package shop.db

/**
 * ein einzelnes Adresse-Objekt
 * Adressen sind von Usern getrennt, und werden nie gelöscht, da sie von 
 * Bestellungen referenziert werden. Außerdem kann so eine Adresse doppelt
 * referenziert werden, z.B. wenn Liefer- und Rechnungsadresse identisch sind.
 */
case class Adresse(id: Int, user: Int, adresse: String) extends DBObject[Int]

/**
 * Adressen-Tabelle in Datenbank
 */
object Adressen extends DBTable[Int, Adresse] {
  val TABLE = "shop_adressen"
  val PREF = "adr"

  /**
   * Created die Tabelle in der Datenbank
   */
  def createTable() = sqlUpdate(
    "create table " + TABLE + " ("
      + "adr_id int auto_increment primary key,"
      + "user_id int not null,"
      //+ "constraint foreign key (user_id) references " + Users.TABLE + "(user_id) on update CASCADE on delete CASCADE,"
      + "adr_adresse varchar(255) not null,"
      + "constraint unique(user_id, adr_adresse)"
      + ") engine=InnoDB;")

  /**
   * Erstellt eine neue Adresse, falls sie noch nicht vorhanden ist.
   */
  def create(user: User, adresse: String):Adresse = {
    //erst gucken ob die Adresse schon existiert
    val f = find(user, adresse) 
    if(f != null) return f
    //wenn nicht anlegen
    sqlInsert(
    "insert into " + TABLE + " (user_id, adr_adresse) values (?,?) ;",
    (pst) => {
      pst.setInt(1, user.id)
      pst.setString(2, adresse)
    },
    Adresse(_, user.id, adresse),
    addCache)
  }
  
  /**
   * durchsucht die Adressen eines Users um zu schauen ob sie bereits existiert 
   */
  def find(user: User, adresse: String) = sqlQuery(
      "select * from " + TABLE + " where user_id = ? and adr_adresse = ?;",
      pst => { pst.setInt(1, user.id) ; pst.setString(2, adresse) },
      createObject)
  
  
  /**
   * Erstellt aus dem ResultSet ein Adresse-Objekt
   */
  def createSingleObject(rs: java.sql.ResultSet) = {
    val id = rs.getInt("adr_id")
    val user = rs.getInt("user_id")
    val adresse = rs.getString("adr_adresse");
    Adresse(id, user, adresse)
  }

}