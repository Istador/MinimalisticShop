package shop

import shop.xml._
import shop.db._

/**
 * Zeigt den Warenkorb eines Benuzers, und behandelt
 * das hinzufügen und Ändern von Artikeln zum Warenkorb.
 *
 * Der Warenkorb befindet sich entweder in der Sessionm, oder, falls der
 * Benutzer eingeloggt ist, in der Datenbank.
 */
object ShowWarenkorb {

  //ob der Session-Warenkorb existiert
  private[shop] def exists(implicit s: Session): Boolean = s.getAttribute("warenkorb") != null

  //gibt den Session-Warenkorb zurück
  private[shop] def getWK(implicit s: Session): Map[Artikel, Int] =
    s.getAttribute("warenkorb") match {
      //vorhanden
      case wk: Map[_, _] => wk.asInstanceOf[Map[Artikel, Int]]
      //noch nicht vorhanden -> leere map erstellen
      case _ => Map[Artikel, Int]()
    }

  //ersetzt den Warenkorb in der Session durch einen neuen
  private[shop] def setWK(wk: Map[Artikel, Int])(implicit s: Session) =
    if (wk == null || wk.isEmpty) s.setAttribute("warenkorb", null)
    else s.setAttribute("warenkorb", wk)

  //setzt die Anzahl im Session-Warenkorb
  private[shop] def setInWK(a: Artikel, n: Int)(implicit s: Session): Unit =
    if (n <= 0)
      setWK(getWK.-(a))
    else
      setWK(getWK.+((a, n)))

  //erhöhe die Anzahl im Warenkorb um 1
  private[shop] def incInWK(a: Artikel)(implicit s: Session): Unit = {
    val wk = getWK
    setWK(wk.+((a, if (wk.contains(a)) wk(a) + 1 else 1)))
  }

  //Parameter überprüfen
  def apply(implicit r: Req, s: Session, o: Out): Unit = {
    try {
      val a = getPara("a") //Artikel
      val n = getPara("n") //Menge

      //kein Artikel übergeben
      if (a == null || a == "")
        this() //Warenkorb anzeigen
      //Artikel ohne Anzahl übergeben
      else if (n == null || n == "")
        this(a.toInt) //inkrementiere um 1
      //Artikel und Anzahl übergeben
      else
        this(a.toInt, n.toInt) //setze auf Anzahl
    } catch { case _: Throwable => param_error }
  }

  //Redirect Methode
  private[shop] def apply(params: Array[Any])(implicit s: Session, o: Out): Unit = {
    if (params.size == 0)
      this()
    else
      param_exception
  }

  //Artikel inkrementieren
  private[this] def apply(art: Int)(implicit s: Session, o: Out): Unit = {
    try {
      //eingeloggt?
      if (Login.isLoggedIn)
        if (art == -1)
          //Warenkorb leeren
          WarenkorbX.get(Login.getUser).empty
        else
          //Warenkorb in Datenbank für diesen Artikel um 1 erhöhen
          WarenkorbX.get(Login.getUser).add(art)
      //nicht eingeloggt
      else if (art == -1)
        //Warenkorb in Session leeren
        setWK(null)
      else
        //Artikel im Warenkorb der Session um 1 erhöhen
        incInWK(art)
      //Warenkorb ausgeben
      this()
    } catch { case _: Throwable => error }
  }

  //Artikel auf Wert setzen
  private[this] def apply(art: Int, n: Int)(implicit s: Session, o: Out): Unit = {
    try {
      //eingeloggt?
      if (Login.isLoggedIn)
        //Warenkorb in Datenbank für diesen Artikel auf n setzen
        WarenkorbX.get(Login.getUser).set(art, n)
      //nicht eingeloggt
      else
        //Artikel im Warenkorb der Session auf n setzen
        setInWK(art, n)
      //Warenkorb ausgeben
      this()
    } catch { case _: Throwable => error }
  }

  //Warenkorb ausgeben
  private[this] def apply()(implicit s: Session, o: Out): Unit = {
    try {
      Login.print
      //eingeloggt?
      if (Login.isLoggedIn)
        o.println(WarenkorbX.xml(Login.getUser))
      //nicht eingeloggt, Warenkorb aus Session
      else
        o.println(<basket>{
          for (wki <- getWK) yield <item amount={ wki._2 }>{ wki._1: Elem }</item>
        }</basket>)
      //Erlaube Redirect zu dieser Seite
      Redirect(this)
    } catch { case _: Throwable => error }
  }
}