package shop.db

/**
 * ein einzelnes Artikel-Objekt
 */
case class Artikel(id: Int, name: String, desc: String, price: Money, var hasPicture: Boolean) extends DBObject[Int] {
  //methode um zu speichern, dass der Artikel ein Bild besitzt
  def setPicture = { hasPicture = true; Artikel.update(this, "picture", true) }
}

/**
 * Artikel-Tabelle in Datenbank
 */
object Artikel extends DBTable[Int, Artikel] {
  val TABLE = "shop_articles"
  val PREF = "art"

  /**
   * Created die Tabelle in der Datenbank
   */
  def createTable() = sqlUpdate(
    "create table " + TABLE + " ("
      + "art_id int auto_increment primary key,"
      + "art_name varchar(255) not null,"
      + "art_desc text null,"
      + "art_price int not null,"
      + "art_picture bool not null default 0"
      + ") engine=InnoDB ;")

  /**
   * Erstellt aus dem ResultSet ein Artikel-Objekt
   */
  def createSingleObject(rs: java.sql.ResultSet): Artikel = {
    val id = rs.getInt("art_id")
    val name = rs.getString("art_name")
    val desc = rs.getString("art_desc")
    val price = rs.getInt("art_price")
    val hasPicture = rs.getBoolean("art_picture")
    return Artikel(id, name, desc, price, hasPicture)
  }

  /**
   * Erstellt einen neuen Artikel
   */
  def +(name: String, desc: String, price: Money) = create(name, desc, price)
  def create(name: String, desc: String, price: Money) = sqlInsert(
    "insert into " + TABLE + " (art_name, art_desc, art_price) values (?,?,?) ;",
    (pst) => {
      pst.setString(1, name)
      pst.setString(2, desc)
      pst.setInt(3, price)
    },
    Artikel(_, name, desc, price, false),
    addCache)

  /**
   * Gibt alle Artikel zurück, dessen Name den String enthält
   */
  def find(name: String) = sqlQuery(
    "select * from " + TABLE + " where art_name like ? ;",
    _.setString(1, "%" + name + "%"),
    createObjects)

}