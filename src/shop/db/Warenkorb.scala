package shop.db

/**
 * Klasse um mehrere Warenkorb-Items als ganzes zu behandeln
 */
case class Warenkorb(user: User, var articles: Set[WarenkorbItem]) {

  //Warenkorb ist leer
  def isEmpty = articles.isEmpty

  //leere den Warenkorb
  def empty = {
    //Warenkorbartikel aus der Datenbank entfernen
    Warenkorb.delete(user);
    //entferne aus dem Cache, damit die alte Anzahl nicht wiederverwendete wird 
    for (wki <- articles)
      Warenkorb.removeCache((user.id, wki.art.id))
    articles = Set[WarenkorbItem]()
  }

  //mehrere Artikel zum Warenkorb hinzufügen
  def add(map: Map[Artikel, Int]): Unit =
    for ((a, n) <- map) add(a, n)

  //einen Artikel zum Warenkorb hinzufügen 
  def add(art: Artikel, n: Int = 1): Unit = if (n >= 1) {
    var wki = Warenkorb((user.id, art.id))
    if (wki != null)
      wki.increment(n)
    else if (n >= 1) {
      Warenkorb.update(user, art, n)
      wki = WarenkorbItem(user.id, art, n)
      articles = articles.+(wki)
      Warenkorb.addCache(wki)
    }
  }

  //Anzahl des Artikels im Warenkorb ändern
  def set(art: Artikel, n: Int) = if (n >= 0) {
    var wki = Warenkorb((user.id, art.id))
    if (wki != null)
      wki.set(n)
    if (n <= 0) {
      articles = articles - wki
      Warenkorb.removeCache((user.id, art.id))
    } else {
      Warenkorb.update(user, art, n)
      wki = WarenkorbItem(user.id, art, n)
      articles = articles.+(wki)
      Warenkorb.addCache(wki)
    }
  }

}

/**
 * Warenkorb-Items in Datenbank
 */
case class WarenkorbItem(user: Int, art: Artikel, var menge: Int) extends DBObject[(Int, Int)] {

  override def toString: String = "(" + art.name + "=>" + menge + ")"

  //zusammengesetzter Primärschlüssel
  lazy val id = (user, art.id)

  //erhöhe die Anzahl Artikel
  def increment(n: Int = 1) = if (n >= 1) {
    menge += n
    Warenkorb.update(Users.get(user), art, menge)
  }

  //setze die Anzahl Artikel
  def set(n: Int) = if (n >= 0) {
    menge = n
    Warenkorb.update(Users.get(user), art, menge)
  }
}

/**
 * Warenkorb-Tabelle in Datenbank
 */
object Warenkorb extends DBTable[(Int, Int), WarenkorbItem] {
  val TABLE = "shop_warenkorb"
  val PREF = "wk"

  /**
   * Created die Tabelle in der Datenbank
   */
  def createTable() = sqlUpdate("create table " + TABLE + " ("
    + "user_id int not null,"
    + "constraint foreign key (user_id) references " + Users.TABLE + "(user_id) on update CASCADE on delete CASCADE,"
    + "art_id int not null,"
    + "constraint foreign key (art_id) references " + Artikel.TABLE + "(art_id) on update CASCADE on delete CASCADE,"
    + "primary key (user_id, art_id),"
    + "wk_menge int not null) engine=InnoDB;")

  /**
   * Erstellt aus dem ResultSet ein WarenkorbItem-Objekt
   */
  def createSingleObject(rs: java.sql.ResultSet): WarenkorbItem = {
    val user_id = rs.getInt("user_id")
    val art = Artikel.createSingleObject(rs)
    Artikel.addCache(art)
    val menge = rs.getInt("wk_menge");
    WarenkorbItem(user_id, art, menge)
  }

  /**
   * gibt den Warenkorb eines Users zurück
   */
  def get(user: User): Warenkorb =
    Warenkorb(user, sqlQuery(
      "select * from " + TABLE + " natural join " + Artikel.TABLE + " where user_id = ? ;",
      (pst) => { pst.setInt(1, user.id) },
      createObjects))

  /**
   * Erstellt aus der übergebenen Map den Warenkorb (beim Registrieren)
   */
  def importMap(user: User, map: Map[Artikel, Int]): Warenkorb = {
    if (map.isEmpty) return null
    val wk = sqlInsert[(Artikel, Int), WarenkorbItem](
      "insert into " + TABLE + " (user_id, art_id, wk_menge) values (?,?,?) ;",
      (pst, wki) => {
        pst.setInt(1, user.id)
        pst.setInt(2, wki._1.id)
        pst.setInt(3, wki._2)
      },
      (id, wki) => {
        val e = WarenkorbItem(user.id, wki._1, wki._2)
        addCache(e)
        e
      },
      map.toArray: _*)
    Warenkorb(user, wk)
  }

  /**
   * Hinzufügen/Aktualisieren/Löschen eines Artikels aus dem Warenkorb
   */
  def update(user: User, art: Artikel, menge: Int) = {
    if (menge <= 0) //löschen
      delete(user, art)
    else { //replace = update or insert
      sqlUpdate(
        "replace into " + TABLE + " values (?, ?, ?) ;",
        (pst) => {
          pst.setInt(1, user.id)
          pst.setInt(2, art.id)
          pst.setInt(3, menge)
        })
    }
  }

  /**
   * Löscht den Artikel aus dem Warenkorb
   */
  private[this] def delete(user: User, art: Artikel) = sqlUpdate(
    "delete from " + TABLE + " where user_id = ? and art_id = ? ;",
    (pst) => { pst.setInt(1, user.id); pst.setInt(2, art.id) })

  /**
   * Löscht alle Artikel eines Users aus seinem Warenkorb
   */
  def delete(user: User) =
    sqlUpdate("delete from " + TABLE + " where user_id = ? ;", _.setInt(1, user.id))

}