package shop

import shop.xml._
import shop.db._

/**
 * Profilseite um Liefer- und Rechnungsadresse ändern zu können
 */
object Profil {

  /**
   * holt die Lieferadresse aus dem Request
   * entweder wird die vorhandene Adresse aus dem Profil zurückgegeben, 
   * oder eine neue Adresse erstellt
   */
  private[this] def liefer(implicit r: Req, s: Session): Adresse = {
    val l_new = getPara("l_new")
    //Lieferadresse vom Profil verwenden
    if(l_new == null || l_new.equals("") || l_new.equals("0"))
      return Login.getUser.liefer
    //neue Lieferadresse erstellen
    val strl = getPara("liefer")
    if (strl != null && strl != "")
      return Adressen.create(Login.getUser, strl)
    return null
  }

  /**
   * holt die Rechnungsadresse aus dem Request
   * entweder wird die vorhandene Adresse aus dem Profil zurückgegeben, 
   * oder eine neue Adresse erstellt
   */
  private[this] def rechnung(implicit r: Req, s: Session): Adresse = {
    val r_new = getPara("r_new")
    //Lieferadresse vom Profil verwenden
    if(r_new == null || r_new.equals("") || r_new.equals("0"))
      return Login.getUser.rechnung
    //neue Lieferadresse erstellen
    val strr = getPara("rechnung")
    if (strr != null && strr != "")
      return Adressen.create(Login.getUser, strr)
    return null
  }

  /**
   * holt die Liefer- und Rechnungsadresse aus dem Request und gibt beide zurück.
   * Behandelt auch den Fall wenn Rechnungs- gleich Lieferadresse sein soll.
   */
  private[shop] def adressen(implicit r: Req, s: Session, o: Out): (Adresse, Adresse) = {
    val l = liefer
    if(existsPara("r_wie_l"))
      return (l, l)
    (l, rechnung)
  }

  //gibt das XML für ein Profil aus
  private[this] def print(u: User)(implicit s: Session, o: Out) =
    echo(UserX.xmlProfile(u))

  //Methode die von Shop aufgerufen wird, überprüft Parameter
  def apply(implicit r: Req, s: Session, o: Out): Unit = {
    try {
      //wenn eingeloggt, und Liefer-/Rechnungsadresse übergeben
      if(Login.isLoggedIn && (existsPara("l_new") || existsPara("r_new"))) {
        //Lese Adressen aus Formular aus
        val (liefer, rechnung) = adressen
        if(liefer==null)
          error("Fehler: keine Lieferadresse angegeben.")
        else if(rechnung==null)
         error("Fehler: keine Rechnungsadresse angegeben.")
        else
          this(liefer, rechnung)
      }
      //Fehlerfall, oder keine Parameter - Formular anzeigen
      this()
    } catch { case _: Throwable => param_error }
  }

  //Methode die von Redirect aufgerufen werden kann
  private[shop] def apply(params: Array[Any])(implicit s: Session, o: Out): Unit = {
    if (params.size == 0)
      this()
    else
      param_exception
  }

  //Liefer und Rechnungsadresse für den User ändern
  private[this] def apply(l: Adresse, r: Adresse)(implicit s: Session, o: Out): Unit = {
	  val u = Login.getUser
	  u.setLiefer(l)
	  u.setRechnung(r)
	  info("Adressen gespeichert")
  }

  //Anzeigen des Formulars falls eingeloggt
  private[this] def apply()(implicit s: Session, o: Out): Unit = {
    try {
      //eingeloggt
      if(Login.isLoggedIn) {
        print(Login.getUser)
      } else { // nicht eingeloggt
        error("Fehler: Diese Seite ist nur für angemeldete Benutzer.")
        Redirect()
      }
    } catch { case _: Throwable => error }
  }
}