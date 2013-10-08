package shop

import shop.xml._
import shop.db._

/**
 * Loggt den User aus. Dazu wird das User-Objekt aus der Session entfernt. 
 */
object Logout {

  //Logt den User aus
  def apply(implicit r: Req, s: Session, o: Out): Unit = {
    
    if (Login.isLoggedIn){	//User ist eingeloggt
      remAttr("user")			//Entferne User-Objekt aus Session -> ausloggen
      info("Sie sind jetzt ausgeloggt.")
    }
    else					//User ist nicht eingeloggt
      error("Fehler: Sie sind nicht eingelogt.")	//Fehlermeldung

    //zur letzten Seite vor dem Logout gehen
    Redirect()
  }
}