package shop.db

/**
 * Klasse: User
 * Kunden des Shops
 */
case class User(id: Int, loginname: String, email: String, pw: Array[Byte], var liefer: Int, var rechnung: Int) extends DBObject[Int] {
  /**
   * returnt true wenn das passwort mit dem in der Datenbank übereinstimmt, false sonst
   */
  def checkPassword(pw: String) =
    Users.checkPassword(this, pw)

  /**
   * Löscht diesen Benutzer aus der Datenbank
   */
  def delete() = Users.delete(id)

  /**
   * Ändert die Lieferadresse
   */
  def setLiefer(a: Adresse) = {
    liefer = a.id
    Users.update(this, "liefer", a.id)
  }

  /**
   * Ändert die Rechnunsadresse
   */
  def setRechnung(a: Adresse) = {
    rechnung = a.id
    Users.update(this, "rechnung", a.id)
  }
}

/**
 * Users-Tabelle der Datenbak
 */
object Users extends DBTable[Int, User] {
  val TABLE = "shop_users"
  val PREF = "user"

  /**
   * Hasht ein Passwort mittels BCrypt (2^10 Runden) und erstellt davon ein
   * SHA-512 Hash.
   *
   * Da Hashfunktionen wie SHA oder MD5 so erstellt wurden, dass sie schnell
   * berechnet werden können, ist es einem Angreifer möglich systematisch
   * schnell millionen von Hashes zu berechnen und eine Rainbow-Tabelle zu
   * erzeugen. Um das zu verhindern verlangsamt BCrypt die Hasherzeugung für
   * Passwörter, in dem es in 2^10 Runden immer wieder einen Hash erzeugt.
   * Nur so lässt sich das erzeugen einer Rainbowtabelle massiv verlangsamen.
   *
   * Der Username seedet einen PRNG, und der PRNG erzeugt den Salt für den
   * BCrypt-Hashalgorithmus.
   *
   * Dadurch erhalten verschiedene Benutzer auch bei identischem Passwort einen
   * anderem Hash. Andernfalls wäre es einem Angreifer einfach möglich identische
   * Passwörter in der Tabelle ausfindig zu machen.
   * Dadurch muss ein Angreifer für jeden Nutzer eine eigene Rainbow-Tabelle
   * erzeugen.
   *
   * BCrypt und das Seeden mit Benutzernamen macht den Aufwand für einen Angreifer
   * um an die Passwörter zu kommen extrem hoch.
   */
  private def createPassword(loginname: String, pw: String) =
    shop.util.bcrypt.Crypt.bcrypt(pw, loginname)

  /**
   * returnt true wenn das Passwort mit dem aus der Datenbank übereinstimmt, false sonst
   */
  def checkPassword(user: User, pw: String) =
    java.util.Arrays.equals(createPassword(user.loginname, pw), user.pw)

  /**
   * Created die Tabelle in der Datenbank
   */
  def createTable() = {
    sqlUpdate("create table " + TABLE + " ( "
      + "user_id int auto_increment primary key"
      + ",user_loginname varchar(255) unique not null"
      + ",user_email varchar(255) not null"
      + ",user_pw binary(64) not null"
      + ",user_liefer int"
      + ",constraint foreign key (user_liefer) references " + Adressen.TABLE + "(adr_id) on update CASCADE on delete SET NULL"
      + ",user_rechnung int"
      + ",constraint foreign key (user_rechnung) references " + Adressen.TABLE + "(adr_id) on update CASCADE on delete SET NULL"
      + ") engine=InnoDB ;")
    //Foreign-Key von Adresse -> User hier, und nicht bei Erzeugung der Adresse, aufgrund einer zyklischen Abhängigkeit
    sqlUpdate("alter table " + Adressen.TABLE + " add constraint `fk_user` foreign key (user_id) references " + Users.TABLE + "(user_id) on update CASCADE on delete CASCADE ;")
  }

  /**
   * Entfernen des Foreign Key vor dem Drop-Table, da beide Tabellen Co-Abhängig
   */
  override def dropTable() = {
    try { sqlUpdate("alter table " + Adressen.TABLE + " drop KEY fk_user;") } catch { case _: Throwable => }
    try { sqlUpdate("alter table " + Adressen.TABLE + " drop FOREIGN KEY fk_user;") } catch { case _: Throwable => }
    super.dropTable
  }

  /**
   * Erstellt aus dem ResultSet ein User-Objekt
   */
  def createSingleObject(rs: java.sql.ResultSet): User = {
    val id = rs.getInt("user_id")
    val name = rs.getString("user_loginname")
    val email = rs.getString("user_email")
    val pw = rs.getBytes("user_pw")
    val liefer = rs.getInt("user_liefer")
    val rechnung = rs.getInt("user_rechnung")
    return User(id, name, email, pw, liefer, rechnung)
  }

  /**
   * Erstellt einen neuen User (Register)
   */
  def +(loginname: String, email: String, pw: String) = create(loginname, email, pw)
  def create(loginname: String, email: String, pw: String): User =
    create(loginname, email, createPassword(loginname, pw))

  private[this] def create(loginname: String, email: String, pw: Array[Byte]) = sqlInsert(
    "insert into " + TABLE + " (user_loginname, user_email, user_pw) values (?,?,?) ;",
    (pst) => {
      pst.setString(1, loginname)
      pst.setString(2, email)
      pst.setBytes(3, pw)
    },
    User(_, loginname, email, pw, 0, 0),
    addCache)

  /**
   * gibt den User mit dem Namen zurück
   */
  def apply(loginname: String) = get(loginname)
  def get(loginname: String): User = sqlQuery(
    "select * from " + TABLE + " where user_loginname = ? ;",
    _.setString(1, loginname),
    createObject)

}