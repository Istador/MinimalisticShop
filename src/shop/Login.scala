package shop

import shop.xml._
import shop.db._

/**
 * Loggt einen User ein, in dem sein User-Objekt in die Session gespeichert wird
 * 
 * bietet den anderen Seiten außerdem zugriff darauf, um festzustellen ob der
 * User eingeloggt ist, und welcher User das ist.
 * 
 */
object Login { 
   
  //returnt das user-objekt das im Session-Speicher liegt. kann null zurückgeben!
  private[shop] def getUser(implicit s: Session): User = getAttr("user")

  //true: user ist eingelogt, false: user ist nicht eingelogt
  private[shop] def isLoggedIn(implicit s: Session): Boolean = getUser != null

  //Ausgabe des <user>-XML falls der User eingeloggt ist
  private[shop] def print(implicit s: Session, o: Out): Unit =
    if (Login.isLoggedIn) echo(UserX.xml(Login.getUser))
    //else echo(<login/>)

  //Logt den User ein
  private[shop] def apply(implicit r: Req, s: Session, o: Out): Unit = {
    val loginname = getPara("login")
    val pw = getPara("pw")

    //User ist bereits eingeloggt
    if (Login.isLoggedIn)
      error("Fehler: Sie sind bereits eingeloggt.")
    //Login Parameter nicht übergeben
    else if (loginname == null || pw == null || loginname == "" || pw == "")
      return param_error //macht den Redirect selbst
    else {
      //Suche User mit Loginname in Datenbank
      val user = UserX(loginname)

      //User existiert nicht, oder PW falsch
      //in beiden Fällen die selbe Rückgabe zu generieren ist ein wichtiges Sicherheitsmerkmal,
      //da ein Angreifer sonst herausfinden könnte ob ein Benutzeraccount existiert oder nicht.
      if (user == null || !user.checkPassword(pw))
        error("Login Fehler")
      //User existiert und PW richtig
      else {
        //Setze User-Objekt in Session
        setAttr("user", user)
        //Informations-Mitteilung an den User
        info("Sie sind jetzt eingeloggt.")
        //Artikel im Session-Warenkorb?
        //das kann passieren wenn der User im nicht eingeloggtem Zustand Artikel zum
        //Warenkorb hinzufügte.
        if (ShowWarenkorb.exists && !ShowWarenkorb.getWK.isEmpty) {
          info("Ihr lokaler Warenkorb wurde in den ihres Benutzeraccounts übernommen.")
          WarenkorbX.get(user).add(ShowWarenkorb.getWK) //Übertragen zu DB-Warenkorb
          ShowWarenkorb.setWK(null) //Session-Warenkorb leeren
        }
      }
    }

    //zur letzten Seite vor dem Login gehen (entweder jetzt eingeloggt oder mit Fehlermeldung)
    Redirect()
  }
}