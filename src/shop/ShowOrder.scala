package shop

import shop.xml._
import shop.db._

/**
 * Zeige eine Bestellung eines Users an, und ermögliche diese zu stornieren
 */
object ShowOrder { 

  //Bestellung ausgeben
  private[this] def print(b: Bestellung)(implicit s: Session, o: Out) =
    echo(BestellungX.xml(b))

  //Parameter überprüfen
  def apply(implicit r: Req, s: Session, o: Out): Unit = {
    try {
      val b = getPara("b").toInt
      val action = getPara("action")
      if (action == null || action.equals("") || action.equals("storno"))
        this(b, action)
      else
        param_exception
    } catch { case _: Throwable => param_error }
  }

  //Methode die von Redirect aufgerufen werden kann
  private[shop] def apply(params: Array[Any])(implicit s: Session, o: Out): Unit = {
    if (params.size == 1)
      this(params(0).asInstanceOf[Bestellung])
    else
      param_exception
  }

  //stornieren wenn gewünscht und anzeigen der Bestellung
  private[shop] def apply(b: Bestellung, action: String = null)(implicit s: Session, o: Out): Unit = {
    try {
      //eingeloggt
      if (Login.isLoggedIn) {
        //wenn dies die Bestellung des Benutzers ist
        if (Login.getUser.id == b.user) {
          //Wenn der Benutzer diese Bestellung stornieren möchte
          if (action != null && action.equals("storno")) {
            //Sorniere
            if (b.storniere)
              info("Ihre Bestellung wurde storniert")
            else
              error("Fehler: Ihre Bestellung konnte nicht storniert werden")
          }
          Login.print
          print(b)
        } else {
          error("Fehler: Dies ist nicht ihre Bestellung")
          Redirect()
        }
      } else {
        error("Fehler: Diese Seite ist nur für angemeldete Benutzer.")
        Redirect()
      }
    } catch { case _: Throwable => error }
  }
}