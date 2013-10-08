package shop

/**
 * Diese Klasse speichert in der Session die letzte Seite die der Nutzer aufgerufen
 * hat, damit Seiten wie Login / Logout / Register oder bei Fehlermeldungen zu denen
 * zurückgesprungen werden kann.
 *
 * Es werden nicht alle Seiten gespeichert, sondern nur solche, die auch ohne Login
 * zu erreichen sind.
 *
 * Damit eine Seite gespeichert werden kann muss sie eine apply-Methode implementieren,
 * die als Parameter ein Array vom Typ Any hat, der die Parameter der Seite übergibt.
 * Die Parameter werden ebenfalls in der Session gespeichert.
 * Ein Parameter könnte z.B. sein welchen Artikel der User sich angesehen hat.
 */
private[shop] object Redirect {

  //Seite zu der Redirected werden soll muss eine solche apply-methode haben 
  private[shop] type Applyable = { def apply(params: Array[Any])(implicit s: Session, o: Out): Any }

  //speichert die Seite und Parameter, um sie später erneut aufrufen zu können
  private[shop] def apply(page: Applyable, params: Any*)(implicit s: Session): Unit = {
    //Speichere die Referenz (Pointer) zur letzten Seite (Singleton Objekt) im Session-Speicher
    setAttr("last_page", page)
    //Speichere die Parameter der Seite im Session-Speicher als Array
    setAttr("last_page_params", params.toArray)
  }

  //verhindert Endlosrekursion von Redirects, dass die aufgerufene Seite selbst 
  //wieder ein Redirect aufrufen kann
  var once = true

  //holt sich die Seite und die Parameter aus der Session, und ruft sie auf.
  private[this] def loadLastPage(implicit s: Session, o: Out): Unit = {
    once = false
    //letzte Seite aus Session-Speicher holen
    val page = getAttr[Applyable]("last_page")
    //Parameter für die Seite aus Session-Speicher holen
    val params = getAttr[Array[Any]]("last_page_params")
    //Seite mit Parametern aufrufen
    page(params)
    once = true
  }

  //lädt die letzte Seite erneut ein
  private[shop] def apply()(implicit s: Session, o: Out): Unit = {
    //wenn nicht bereits ausgeführt wird
    if (once) { 
      try {
        try {
          loadLastPage
        } catch { case _: Throwable => ShowKategorie(1) } //Fehlerfall -> Startseite
      } catch { case _: Throwable => error("Fehler beim Redirect") } //auch auf Startseite ein Fehler
    } else { error("Fehler beim Redirect") } //wenn versucht wird ein zweites mal umzuleiten
    once = true;
  }
}