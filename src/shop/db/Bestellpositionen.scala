package shop.db

/**
 * Eine einzelne Bestellposition
 */
case class Bestellposition(best: Bestellung, art: Artikel, price: Money, menge: Int) extends DBObject[(Int, Int)] {
  //zusammengesetzter Primärschlüssel aus Bestellungs-ID und Artikel-ID
  lazy val id = (best.id, art.id)
}

/**
 * Tabelle der Bestellpositionen
 */
object Bestellpositionen extends DBTable[(Int, Int), Bestellposition] {
  val TABLE = "shop_best_art"
  val PREF = "bp"

  /**
   * Created die Tabelle in der Datenbank
   */
  def createTable() = sqlUpdate(
    "create table " + TABLE + " ("
      + "best_id int not null"
      + ",constraint foreign key (best_id) references " + Bestellungen.TABLE + "(best_id) on update CASCADE on delete RESTRICT"
      + ",art_id int not null"
      + ",constraint foreign key (art_id) references " + Artikel.TABLE + "(art_id) on update CASCADE on delete RESTRICT"
      + ",primary key (best_id, art_id)"
      + ",bp_price int not null"
      + ",bp_menge int not null) engine=InnoDB;")

  /**
   * Erstellt aus dem ResultSet ein Bestellpositions-Objekt
   */
  def createSingleObject(rs: java.sql.ResultSet): Bestellposition = {
    val best_id = rs.getInt("best_id")
    val best = Bestellungen.get(best_id)
    val art = Artikel.createSingleObject(rs)
    Artikel.addCache(art)
    val preis = rs.getInt("bp_price")
    val menge = rs.getInt("bp_menge")
    return Bestellposition(best, art, preis, menge)
  }

  /**
   * Gibt alle Bestellpositionen einer Bestellung zurück
   */
  def get(best: Bestellung) = sqlQuery(
    "select * from " + TABLE + " natural join " + Artikel.TABLE + " where best_id = ? ;",
    _.setInt(1, best.id),
    createObjects)

  /**
   * Erstellt mehrere Bestellpoitionen auf einmal
   */
  def create(best: Bestellung, wk: Warenkorb) = sqlInsert[WarenkorbItem, Bestellposition](
    "insert into " + TABLE + " values (?, ?, ?, ?);",
    (pst, wki) => {
      pst.setInt(1, best.id)
      pst.setInt(2, wki.art.id)
      pst.setInt(3, wki.art.price)
      pst.setInt(4, wki.menge)
    },
    (id, wki) => {
      Bestellposition(best, wki.art, wki.art.price, wki.menge)
    },
    wk.articles.toArray: _*)

}