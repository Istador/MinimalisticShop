package shop.db

/**
 * Ein Kategorie-Objekt
 */
case class Kategorie(id: Int, name: String, parent: Kategorie) extends DBObject[Int] {

  /**
   * Kind-Kategorien und Artikel werden nur dann geladen wenn sie von der
   * Anwendung gebraucht werden, und dann auch zwischengespeichert, da es
   * sehr teuer ist rekursiv über alle Kinder zu iterieren (weil es jeweils
   * eine neue Datenbankanfrage erfordert).
   */
  private[this] var _childs: Set[Kategorie] = null
  private[this] var _allchilds: Set[Kategorie] = null
  private[this] var _articles: Set[Artikel] = null
  private[this] var _allarticles: Set[Artikel] = null

  /**
   * direkte Unterkategorien dieser Kategorie (ohne deren Unterkategorien)
   */
  def childs = {
    //nur einmal abfragen - zwischenspeichern
    if (_childs == null)
      _childs = Kategorien.getChilds(this)
    _childs
  }

  /**
   * Artikel in dieser Kategorie (ohne die der Unterkategorien)
   */
  def articles = {
    if (_articles == null)
      _articles = KategorieArtikel.get(this)
    _articles
  }

  /**
   * Alle Unterkategorien (auch alle Unterkategorien der Unterkategorien)
   */
  def allChilds: Set[Kategorie] = {
    if (_allchilds == null) {
      val s = scala.collection.mutable.Set[Kategorie]()
      for (child <- childs) { s += child; s ++= child.allChilds }
      _allchilds = s.toSet
    }
    _allchilds
  }

  /**
   * Alle Artikel in dieser Kategorie und deren Unterkategorien. Rekursiv! Teuer!
   */
  def allArticles: Set[Artikel] = {
    //articles.toSet.++(for(c <- childs ; a <- c.allarticles) yield a)
    if (_allarticles == null)
      _allarticles = KategorieArtikel.getAll(allChilds + this)
    _allarticles
  }

  /**
   * Fügt einen Artikel dieser Kategorie hinzu
   */
  def +(art: Artikel) = add(art)
  def add(art: Artikel) = KategorieArtikel.create(this, art)

  /**
   * Entfernt einen Artikel aus dieser Kategorie
   */
  def -(art: Artikel) = remove(art)
  def remove(art: Artikel) = KategorieArtikel.delete(this, art)

}

/**
 * Kategorien-Tabelle in Datenbank
 */
object Kategorien extends DBTable[Int, Kategorie] {
  val TABLE = "shop_category"
  val PREF = "cat"

  /**
   * Created die Tabelle in der Datenbank
   */
  def createTable() = sqlUpdate(
    "create table " + TABLE + " ("
      + "cat_id int auto_increment primary key,"
      + "cat_name varchar(255) not null,"
      + "cat_parent int,"
      + "constraint foreign key (cat_parent) references " + TABLE + "(cat_id) on update CASCADE on delete SET NULL"
      + ") engine=InnoDB;")

  /**
   * Erstellt aus dem ResultSet ein Kategorie-Objekt
   */
  def createSingleObject(rs: java.sql.ResultSet): Kategorie = {
    val id = rs.getInt("cat_id")
    val name = rs.getString("cat_name")
    val parent = rs.getInt("cat_parent");
    return Kategorie(id, name, get(parent))
  }

  /**
   * Erstellt eine neue Kategorie
   */
  def +(name: String, parent: Kategorie = null) = create(name, parent)
  def create(name: String, parent: Kategorie = null) = sqlInsert(
    "insert into " + TABLE + " (cat_name, cat_parent) values (?,?) ;",
    (pst) => {
      pst.setString(1, name)
      if (parent == null) //ohne Parent-Kategorie
        pst.setNull(2, java.sql.Types.INTEGER)
      else //mit Parent-Kategorie
        pst.setInt(2, parent.id)
    },
    Kategorie(_, name, parent),
    addCache)

  /**
   * hole die Unter-Kategorien (Kinder) einer Ober-Kategorie (Parent)
   */
  def getChilds(parent: Kategorie) = sqlQuery(
    "select * from " + TABLE + " where cat_parent = ? ;",
    _.setInt(1, parent.id),
    createObjects)

}

/**
 * Zwischentabelle für die
 * n:m Beziehung zwischen Artikeln und Kategorien
 */
object KategorieArtikel extends DBTable[Int, Kategorie] {
  val TABLE = "shop_cat_art"
  val PREF = ""

  /**
   * Created die Tabelle in der Datenbank
   */
  def createTable() = sqlUpdate(
    "create table " + TABLE + " ("
      + "cat_id int not null,"
      + "constraint foreign key (cat_id) references " + Kategorien.TABLE + "(cat_id) on update CASCADE on delete CASCADE,"
      + "art_id int not null,"
      + "constraint foreign key (art_id) references " + Artikel.TABLE + "(art_id) on update CASCADE on delete CASCADE,"
      + "primary key(cat_id, art_id) ) engine=InnoDB;")

  def createSingleObject(rs: java.sql.ResultSet) = null

  /**
   * Alle Kategorien eines Artikels holen
   */
  def get(art: Artikel) = sqlQuery(
    "select * from " + TABLE + " natural join " + Kategorien.TABLE + " where art_id = ? ;",
    _.setInt(1, art.id),
    Kategorien.createObjects)

  /**
   * Alle Artikel einer einzelnen Kategorie holen
   */
  def get(cat: Kategorie) = sqlQuery(
    "select * from " + TABLE + " natural join " + Artikel.TABLE + " where cat_id = ? ;",
    _.setInt(1, cat.id),
    Artikel.createObjects)

  /**
   * Alle Artikel von mehreren Kategorien
   */
  def getAll(cats: Set[Kategorie]): Set[Artikel] = {
    if (cats.isEmpty) return Set[Artikel]()

    //SQL-Antrage mit einer ID die in einer Liste von IDs existiert
    val sql = ("select * from " + TABLE + " natural join " + Artikel.TABLE
      + " where cat_id IN ("
      //für jede Kategorie ein ? einfügen und mit Kommas trennen
      + Array.fill(cats.size)("?").mkString(", ")
      + ") ;")

    //setter methode, um alle Fragezeichen entsprechend ihrer ID zu setzen
    val set: (java.sql.PreparedStatement) => Unit = (pst) =>
      //for schleife mit Index
      for ((c, i) <- cats.view.zipWithIndex)
        pst.setInt(i + 1, c.id)

    sqlQuery(sql, set, Artikel.createObjects)
  }

  /**
   * Fügt einen Artikel einer Kategorie hinzu
   */
  def +(cat: Kategorie, art: Artikel) = create(cat, art)
  def create(cat: Kategorie, art: Artikel) = sqlUpdate(
    "replace into " + TABLE + " values (?, ?) ;",
    (pst) => { pst.setInt(1, cat.id); pst.setInt(2, art.id) })

  /**
   * Löscht einen Artikel aus einer Kategorie
   */
  def -(cat: Kategorie, art: Artikel) = delete(cat, art)
  def delete(cat: Kategorie, art: Artikel) = sqlUpdate(
    "delete from " + TABLE + " where cat_id = ? and art_id = ? ;",
    (pst) => { pst.setInt(1, cat.id); pst.setInt(2, art.id) })

}






