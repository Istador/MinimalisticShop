package shop

import shop.xml._
import shop.db._

/**
 * Erstellt eine neue Bestellung
 * - Anzeige des Formulars zum bestätigen und Auswahl der Adressen
 * - Entgegennahme des Formulars und Anzeigen der Bestellung 
 */
object CreateOrder {

  //XML-Ausgabe einer Bestellung 
  private[this] def print(b: Bestellung)(implicit s: Session, o: Out) =
    echo(BestellungX.xml(b)) 

  //Methode die von Shop aufgerufen wird, überprüft Parameter
  def apply(implicit r: Req, s: Session, o: Out): Unit = {
    try {
      //wenn eingeloggt, und Liefer-/Rechnungsadresse übergeben
      if(Login.isLoggedIn && (existsPara("l_new") || existsPara("r_new"))) {
        //Lese Adressen aus Formular aus
        val (liefer, rechnung) = Profil.adressen
        if(liefer==null)
          error("Fehler: keine Lieferadresse angegeben.")
        else if(rechnung==null)
         error("Fehler: keine Rechnungsadresse angegeben.")
        else
          return this(liefer, rechnung)
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
  
  //Liefer- und Rechnungsadresse sind übergeben. Erstelle Bestellung.
  private[this] def apply(l: Adresse, r: Adresse)(implicit s: Session, o: Out): Unit = {
	  val u = Login.getUser
	  val wk = WarenkorbX.get(u)
	  val b = BestellungX.create(u, l, r, wk)
	  if(b == null){
	    error("Fehler: Bestellung konnte nicht erstellt werden.")
	    this()
	  } else if(b.userConfirm) { //Bestätige die erstellte Bestellung
		info("Bestellung angelegt")
		ShowOrder(b) //Erstellte Bestellung anzeigen
	  } else {
	    error
	  }
  }

  //Anzeigen des Formulars falls eingeloggt
  private[this] def apply()(implicit s: Session, o: Out): Unit = {
    try {
      //eingeloggt
      if(Login.isLoggedIn) {
        Login.print
        echo(BestellungX.xmlCreate(Login.getUser))
      }
      //nicht eingeloggt
      else {
        error("Fehler: Diese Seite ist nur für angemeldete Benutzer.")
        Redirect()
      }
    } catch { case _: Throwable => error }
  }
}