package shop.xml

import shop.db.{Artikel => A, User => U, Users => Us, Warenkorb => WK, WarenkorbItem => WKI}

object WarenkorbX {
  
  //Warenkorb eines Users
  def get(u:U) = WK.get(u)
  
  //Menge eines Artikels im Warenkorb eines Users ändern
  def update(u:U, a:A, menge:Int) = get(u).set(a, menge)
  
  //Menge eines Artikels im Warenkorb eines Users erhöhen
  def increment(u:U, a:A) = get(u).add(a)
  
  //Warenkorb eines Users leeren
  def empty(u:U) = WK.delete(u)
  
  //Warenkorb eines Users importieren (aus session)
  def importMap(u:U, map:Map[A,Int]):WK = WK.importMap(u, map)
  
  //Warenkorb eines Users als XML-Ausgeben
  def xml(u:U) = get(u):Elem
}