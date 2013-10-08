package shop

import shop.xml._
import shop.db._

/**
 * Zeigt eine Übersicht aller bereits erstellten Bestellungen eines Benutzers
 */
object ShowOrders {

  //Ausgabe aller Bestellungen eines Users
  private[this] def print(u: User)(implicit s: Session, o: Out) =
    echo(BestellungX.xml(u))
  
  //Keine Parameter zum überprüfen
  def apply(implicit r: Req, s: Session, o: Out): Unit = {
    this()
  }

  //Methode die von Redirect aufgerufen werden kann
  private[shop] def apply(params: Array[Any])(implicit s: Session, o: Out): Unit = {
    if (params.size == 0)
      this()
    else
      param_exception
  }
  
  private[this] def apply()(implicit s: Session, o: Out):Unit = {
    try {
      //Benutzer ist eingeloggt
      if (Login.isLoggedIn) {
        Login.print
        print(Login.getUser)
      }
      //nicht eingeloggt
      else {
        error("Fehler: Diese Seite ist nur für angemeldete Benutzer.")
        Redirect()
      }
    } catch { case _: Throwable => error }
  }
}