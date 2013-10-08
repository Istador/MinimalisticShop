package shop.db

/**
 * ein Bestellungsobjekt
 */
case class Bestellung(id: Int, user: Int, datum: DateTime, price: Money, liefer: Int, rechnung: Int, var status: Int) extends DBObject[Int] {

  //Status ändern
  private[this] def setStatus(newStatus: Int) = {
    status = newStatus
    Bestellungen.update(this, "status", newStatus)
    true
  }

  // 0=angelegt, 1=user-bestätigt, 2=admin-bestätigt, 3=storniert, 4=im Versand, 5=erledigt
  def userConfirm = if (status == 0) setStatus(1) else false
  def adminConfirm = if (status == 1) setStatus(2) else false
  def storniere = if (status == 0 || status == 1) setStatus(3) else false
  def admin_storniere = if (status == 0 || status == 1 || status == 2) setStatus(3) else false
  def versende = if (status == 2) setStatus(4) else false
  def versendet = if (status == 4) setStatus(5) else false

  var _best_positionen: Set[Bestellposition] = null

  /**
   * Bestellpositionen (Atikel, Menge und Preis) der Bestellung, nur
   * anfragen wenn nicht schon bereits gecached.
   */
  def best_positionen: Set[Bestellposition] = {
    if (_best_positionen == null)
      _best_positionen = Bestellpositionen.get(this)
    _best_positionen
  }
}

/**
 * Bestellungen Datanbank-Tabelle
 */
object Bestellungen extends DBTable[Int, Bestellung] {
  val TABLE = "shop_bestellungen"
  val PREF = "best"

  /**
   * Created die Tabelle in der Datenbank
   */
  def createTable() = sqlUpdate("create table " + TABLE + " ( "
    + "best_id int auto_increment primary key"
    + ",user_id int not null"
    + ",constraint foreign key (user_id) references " + Users.TABLE + "(user_id) on update CASCADE on delete RESTRICT"
    + ",best_date date not null"
    + ",best_time time not null"
    + ",best_price int not null"
    + ",best_liefer int not null"
    + ",constraint foreign key (best_liefer) references " + Adressen.TABLE + "(adr_id) on update CASCADE on delete RESTRICT"
    + ",best_rechnung int not null"
    + ",constraint foreign key (best_rechnung) references " + Adressen.TABLE + "(adr_id) on update CASCADE on delete RESTRICT"
    + ",best_status int not null default 0" // 0=angelegt, 1=user-bestätigt, 2=admin-bestätigt, 3=storniert, 4=im Versand, 5=erledigt 
    + ") engine=InnoDB ;")

  /**
   * Erstellt aus dem ResultSet ein Bestellung-Objekt
   */
  def createSingleObject(rs: java.sql.ResultSet): Bestellung = {
    val id = rs.getInt("best_id")
    val user = rs.getInt("user_id")
    val date = rs.getDate("best_date")
    val time = rs.getTime("best_time")
    val preis = rs.getInt("best_price")
    val liefer = rs.getInt("best_liefer")
    val rechnung = rs.getInt("best_rechnung")
    val status = rs.getInt("best_status")
    return Bestellung(id, user, DateTime(date, time), preis, liefer, rechnung, status)
  }

  /**
   * Erstellt eine neue Bestellung
   */
  def create(user: User, liefer: Adresse, rechnung: Adresse, wk: Warenkorb): Bestellung = {
    //nur wenn Warenkorb nicht leer ist
    if (wk.isEmpty) return null

    //Preise aller Artikel (inkl. MwSt) summieren
    var price: Money = 0
    for (wki <- wk.articles)
      price += wki.art.price * wki.menge

    //Versandkostenpauschale von 5€ draufschlagen zuzüglich MwSt
    price += (5.00: Money) * 1.19

    //Bestelldatum
    val date = DateTime()

    //In Datenbank einfügen
    val best = sqlInsert(
      "insert into " + TABLE + " (user_id, best_date, best_time, best_price, best_liefer, best_rechnung) values (?,?,?,?,?,?) ;",
      (pst) => {
        pst.setInt(1, user.id)
        pst.setDate(2, date.date)
        pst.setTime(3, date.time)
        pst.setInt(4, price)
        pst.setInt(5, liefer.id)
        pst.setInt(6, rechnung.id)
      },
      Bestellung(_, user.id, date, price, liefer.id, rechnung.id, 0),
      addCache)

    //Bestellung erfolgreich angelegt
    if (best != null) {
      //Bestellpositionen erstellen
      val bps = Bestellpositionen.create(best, wk)
      best._best_positionen = bps
      //Bestellpositionen erfolgreich erstellt
      if (!bps.isEmpty) {
        //Warenkorb leeren
        wk.empty
      }
    }

    best
  }

  /**
   * Gibt alle Bestellungen eines Users zurück
   */
  def get(user: User) = sqlQuery(
    "select * from " + TABLE + " where user_id = ? ;",
    _.setInt(1, user.id),
    createObjects)

}