package shop.db

/**
 * Interface für Datenbank-Objekte. Jedes Objekt repräsentiert ein
 * Datenbank-Tupel und muss zumindest eine Methode 'id' implementieren
 * mit einem beliebigen Typ (z.B. Int, oder (Int, Int) für zusammengesetzte
 * Primärschlüssel).
 *
 * Prinzipiell spricht nichts dagegen einen Primärschlüssel z.B. vom Typ
 * (Int, String, Char) anzulegen.
 *
 * Die ID dient vorallem für das Caching von Objekten und, automatisch erstellten
 * Methoden wie get, delete und update.
 */
trait DBObject[ID] {
  def id: ID
}

/**
 * Abstrakte Klasse für eine Datenbank-Tabelle selbst.
 * erstellt einige Methoden automatisch. Z.B. Caching von Objekten, get,
 * update, delete,
 */
private[db] trait DBTable[ID, DBO >: Null <: DBObject[ID]] {

  /**
   * Prefix von Attribut-Namen dieser Tabelle
   * Sinnvoll um Attribute dieser Tabelle von Attributen anderer Tabellen
   * eindeutig zu unterscheiden, damit bei einem NATURAL JOIN außer den
   * Fremdschlüsseln keine Attribute mit gleichem Namen auftauchen.
   *
   * Muss von konkreten Sub-Klassen implementiert werden.
   */
  def PREF: String

  /**
   * Tabellenname dieser Tabelle in der Datenbank
   *
   * Muss von konkreten Sub-Klassen implementiert werden.
   */
  def TABLE: String

  /**
   * Cache von bereits abgefragten Objekten. Map: ID -> DBObjekt
   */
  private[this] val entryCache = scala.collection.mutable.Map[ID, DBO]()

  /**
   * holt ein Objekt aus dem Cache falls es vorhanden ist
   */
  def getCache(id: ID): DBO = entryCache.get(id) match {
    case Some(e) => e
    case None => null
  }

  /**
   * fügt ein Objekt dem Cache hinzu, falls es noch nicht vorhanden ist.
   */
  def addCache(obj: DBO): Unit = {
    if (obj == null) return
    if (entryCache.contains(obj.id)) return
    entryCache.put(obj.id, obj)
  }

  /**
   * entfernt ein Objekt aus dem Cache
   */
  def removeCache(id: ID): Unit = {
    if (!entryCache.contains(id)) return
    entryCache.remove(id)
  }

  /**
   * entfernt alle Objekte aus dem Cache
   */
  def clearCache(): Unit = entryCache.clear

  /**
   * Methode um die Datenbank-Tabelle zu erstellen.
   *
   * Muss von konkreten Sub-Klassen implementiert werden.
   */
  def createTable: Unit

  /**
   * Entfernt die Tabelle aus der Datenbank
   */
  def dropTable() = {
    sqlUpdate("drop table if exists " + TABLE + " cascade ;")
    clearCache
  }

  /**
   * Methode um ein einzelnes DBObjekt aus einer ResultSet Zeile zu erstellen.
   *
   * Muss von konkreten Sub-Klassen implementiert werden.
   */
  def createSingleObject(rs: java.sql.ResultSet): DBO

  /**
   * Methode die aus einem ResultSet nur die erste Zeile betrachtet
   * und ein einzelnes DBObjekt erstellt und es zum Cache hinzufügt
   */
  def createObject(rs: java.sql.ResultSet): DBO = {
    while (rs.next) {
      val obj = createSingleObject(rs)
      addCache(obj)
      return obj
    }
    null
  }

  /**
   * Diese Methode erstellt für jede Zeile des ResultSets ein DBObjekt
   * und fügt es dem Cache hinzu.
   */
  def createObjects(rs: java.sql.ResultSet): Set[DBO] = {
    var result = Set[DBO]()
    while (rs.next) {
      val obj = createSingleObject(rs)
      addCache(obj)
      result = result + obj
    }
    result
  }

  /**
   * Holt ein DBObjekt mit der ID aus dem Cache, oder aus der Datenbank
   * falls es nicht im Cache ist. Letzteres funktioniert nur für Int-IDs.
   */
  def apply(id: ID) = {
    var c = getCache(id)
    if (c == null && id.isInstanceOf[Int])
      c = get(id.asInstanceOf[Int])
    c
  }

  /**
   * holt ein DBObjekt mit einer Int-ID aus dem Cache, oder fragt dieses aus der
   * Datenbank an.
   */
  def get(id: Int): DBO = {
    //erst gucken ob das angeforderte Objekt schon im Cache ist
    var obj: DBO = getCache(id.asInstanceOf[ID])
    //wenn im Cache -> zurückgeben
    if (obj != null) return obj
    //sonst Datenbankabfrage
    sqlQuery(
      "select * from " + TABLE + " where " + PREF + "_id = ? ;",
      _.setInt(1, id),
      createObject)
  }

  /**
   * Ändert einen Wert in der Datenbank-Tabelle.
   * Funktioniert nur mit einfachen Int-IDs.
   */
  def update[T](obj: DBO, column: String, value: T) = sqlUpdate(
    "update " + TABLE + " set " + PREF + "_" + column + " = ? where " + PREF + "_id = ? ;",
    (pst) => { pst.setObject(1, value); pst.setInt(2, obj.id.asInstanceOf[Int]) })

  /**
   * Entfernt eine Zeile aus der Datenbank-Tabelle.
   * Funktioniert nur mit einfachen Int-IDs.
   */
  def -(id: Int) = delete(id)
  def delete(id: Int) = {
    sqlUpdate("delete from " + TABLE + " where " + PREF + "_id = ? ;", _.setInt(1, id))
    removeCache(id.asInstanceOf[ID])
  }

}