package shop.xml

import shop.db.{User => U, Users => Us, Adresse => Addr, Adressen => Addrs}

/**
 * Erzeugt XML für User 
 */
object UserX {
  //User mit der ID
  def get(id:Int):U = Us(id)
  def apply(id:Int) = get(id)
  
  //User mit dem Namen
  def apply(loginname:String):U = Us(loginname)
  
  //XML für eine Lieferadresse
  def liefer(a:Addr):Elem = <liefer>{a:Elem}</liefer>
  
  //XML für eine Rechnungsadresse
  def rechnung(a:Addr):Elem = <rechnung>{a:Elem}</rechnung>
  
  //XML für die Lieferadresse eines Users
  def liefer(u:U):Elem = liefer(u.liefer:Addr)
  
  //XML für die Rechnungsadresse eines Users
  def rechnung(u:U):Elem = rechnung(u.rechnung:Addr)
  
  //XML für einen User (auf jeder Seite um zu signalisieren das man eingeloggt ist)
  def xml(u:U): Elem = u
  
  //XML für einen User inklusive Liefer- und Rechnungsadresse (für das Profil)
  def xmlProfile(u:U): Elem = {
    val x = (u:Elem)
    x.copy(child = x.child :+ liefer(u) :+ rechnung(u))
  }
  
  //Registriert einen neuen User
  def register(loginname: String, email: String, pw: String) = Us.create(loginname, email, pw)
}