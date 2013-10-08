package shop

import java.sql.{ PreparedStatement => PST, ResultSet => RS }

package object db {
  //Connection-Objekt, der allen Klassen eine gemeinsame SQL-Connection anbietet
  private[this] def Conn = shop.util.Conn
  
  //ein PreparedStatement erstellen
  private[this] def prepareStatement(sql: String) = Conn().prepareStatement(sql)
  
  //ein PreparedStatement erstellen, mit dem erstellten Auto Increment Schlüssel als Rückgabe
  private[this] def insertStatement(sql: String) = Conn().prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)
  
  type DateTime = shop.util.DateTime //Klasse über kürzeren Namen ansprechen
  def DateTime = shop.util.DateTime  //Referenz auf das Singleton-Objekt
  
  type Euro = shop.util.Euro
  def Euro = shop.util.EuroObj
  type Money = shop.util.MoneyClass
  
  //Implizite Umwandlungen
  implicit def bigDecimalToMoney(x:BigDecimal):Money = Euro(x)
  implicit def intToMoney(x:Int):Money = Euro.fromInt(x)
  implicit def doubleToMoney(x:Double):Money = Euro(BigDecimal(x))
  def stringToMoney(x:String):Money = Euro(BigDecimal(x))
  implicit def moneyToInt(x:Money):Int = x.toInt
  implicit def moneyToString(x:Money):String = x.toString
  
  //Insert für ein einzelnes Objekt
  private[db] def sqlInsert[Out >: Null](sql: String, set: (PST) => Unit = (_) => {}, get: (Int) => Out = (_: Int) => { null }, post: (Out) => Unit = (_: Out) => {}): Out = {
    var result: Out = null
    //Prepare Statement Objekt aus SQL-String mit ? erstellen
    val pst = insertStatement(sql)
    //übergebene Funktion 'set' aufrufen, um ? mit Werten zu füllen
    set(pst)
    try {
      //SQL-Befehl ausführen
      pst.executeUpdate
      //Auto-Increment ID
      val rs = pst.getGeneratedKeys
      //übergebene Funktion 'get' aufrufen, um aus dem ResultSet ein beliebiges
      //Objekt vom generischem Typ Out zu erstellen
      if (rs.next) result = get(rs.getInt(1))
      //ResultSet schließen
      rs.close
    } catch { case _: Throwable => }
    //PreparedStatement schließen
    pst.close
    //Postproduktion (optional Operationen auf das neue Objekt ausführen)
    post(result)
    //das von der Funktion 'get' erzeugte Objekt zurückgeben
    result
  }

  //Insert für mehrere Objekte der selben Tabelle in einer SQL-Abfrage
  private[db] def sqlInsert[In, Out >: Null](sql: String, set: (PST, In) => Unit, get: (Int, In) => Out, parms: In*): Set[Out] = {
    //Prepare Statement Objekt aus SQL-String mit ? erstellen 
    val pst = insertStatement(sql)
    //für alle zu erstellende Objekte
    for (p <- parms) {
      //übergebene Funktion 'set' aufrufen, um ? mit Werten zu füllen
      set(pst, p)
      //Insert mit mehreren VALUES
      pst.addBatch
    }

    //SQL-Befehl ausführen
    val rs = pst.executeBatch

    var i = 0 //das wievielte Objekt, Laufvariable
    //für alle erzeugten IDs
    val result = (for (i <- 0 until rs.size) yield {
      //übergebene Funktion 'get' aufrufen, um aus dem ResultSet ein beliebiges
      //Objekt vom generischem Typ Out zu erstellen
      get(rs(i), parms(i))
    })

    //PreparedStatement schließen
    pst.close
    //das von der Funktion 'get' erzeugte Objekt zurückgeben
    result.toSet
  }

  //Abfrage mehrerer Elemente einer Tabelle
  private[db] def sqlQuery[T >: Null](sql: String, set: (PST) => Unit = (_: PST) => {}, get: (RS) => T = (_: RS) => null) = {
    //Prepare Statement Objekt aus SQL-String mit ? erstellen 
    val pst = prepareStatement(sql)
    //übergebene Funktion 'set' aufrufen, um ? mit Werten zu füllen
    set(pst)
    //SQL-Befehl ausführen
    val rs = pst.executeQuery
    //übergebene Funktion 'get' aufrufen, um aus dem ResultSet ein beliebiges 
    //Objekt vom generischem Typ T zu erstellen
    val result: T = get(rs)
    //ResultSet und PreparedStatement schließen
    rs.close
    pst.close
    //das von der Funktion 'get' erzeugte Objekt zurückgeben
    result
  } 

  //Create Table / Update / Delete
  private[db] def sqlUpdate(sql: String, set: (PST) => Unit = (_: PST) => _): Unit = {
    //Prepare Statement Objekt aus SQL-String mit ? erstellen 
    val pst = prepareStatement(sql)
    //übergebene Funktion 'set' aufrufen, um ? mit Werten zu füllen
    set(pst)
    //SQL-Befehl ausführen
    pst.executeUpdate
    //PreparedStatement schließen
    pst.close
  }

}