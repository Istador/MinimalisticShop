package shop.util

import java.sql.DriverManager

/*
 * Stellt eine java.sql.Connection zum Server her, und bietet einen zentralen 
 * Punkt von dem aus sie aufgerufen werden kann
 */
object Conn {
  var c: Conn = null

  def apply(server: String, port: String, db: String, user: String, pw: String): java.sql.Connection = {
    this.synchronized {
      if (c == null) c = new Conn(server, port, db, user, pw)
      c.getCon
    }
  }

  def apply(): java.sql.Connection = this.synchronized {
    this("localhost", "3306", "dv18", "dv18", "cNr%")
  }

  def close() = this.synchronized { if (c != null) { c.close; c = null } }
}

class Conn(server: String, port: String, db: String, user: String, pw: String) {
  Class.forName("com.mysql.jdbc.Driver")
  var con: java.sql.Connection = null

  private[this] def connect = { con = DriverManager.getConnection("jdbc:mysql://" + server + ":" + port + "/" + db, user, pw) }
  def close = { con.close(); con = null }
  def getCon = { if (con == null || con.isClosed) { connect }; con }
}