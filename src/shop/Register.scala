package shop

import shop.xml._
import shop.db._

/**
 * Erstellt einen neuen User
 * - Anzeige des Formulars zum Eingeben der benötigten Daten
 * - Entgegennahme des Formulars, Erstellen des Users, Redirect zur letzten Seite 
 */
object Register {

  //sind die Formulardaten gültig?
  private[this] def isValid(loginname: String, pw: String, email: String)(implicit o: Out): Boolean = {
    //wurden nicht übergeben 
    if (loginname == null && pw == null && email == null)
      return false

    //mind. ein Feld ist leer
    if (loginname == null || pw == null || email == null || loginname == "" || pw == "" || email == "") {
      error("Fehler: nicht alle Felder ausgefüllt.")
      return false
    }
    
    //Passwort ist nicht lang genug
    if (pw.length < 5) {
      error("Fehler: Passwort muss mindestens 5 Zeichen lang sein.")
      return false
    }
    
    //E-Mail ist ungültig
    if (!email.matches("^\\S+@\\S+\\.\\S+$")) {
      error("Fehler: keine gültige E-Mail-Adresse.")
      return false
    }
    
    //alles OK
    return true
  }

  //Registriert einen neuen User
  def apply(implicit r: Req, s: Session, o: Out): Unit = {

    //Eingeloggter User versucht sich zu registrieren
    if(Login.isLoggedIn) {
      error("Fehler: Sie sind bereits registriert.")
      Redirect()
      return
    }

    val loginname = getPara("login")
    val pw = getPara("pw")
    val email = getPara("email")

    //Formulardaten prüfen
    if (isValid(loginname, pw, email)) {
      //versuche zu registrieren
      val user = UserX.register(loginname, email, pw)
      if (user == null) {
        error("Fehler bei der Registrierung.")
      } else { //erfolgreich registriert

        //Warenkorb aus der Session in DB verschieben falls vorhanden
        if (ShowWarenkorb.exists) {
          WarenkorbX.importMap(user, ShowWarenkorb.getWK) //in db importieren
          ShowWarenkorb.setWK(null) //aus session entfernen
        }

        info("Sie wurden erfolgreich registriert.")

        setAttr("user", user) //einloggen
        //zur letzten Seite vor dem Register gehen
        Redirect()
        return
      }
    }

    //zeige Registrier-Formular
    echo(<register loginname={ loginname } email={ email }/>)
  }
}