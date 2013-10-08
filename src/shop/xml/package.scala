import BigDecimal.RoundingMode.{HALF_EVEN => RM}

import shop.db.{
  Artikel => A,
  User => U, Users => Us,
  Kategorie => K, Kategorien => Ks, KategorieArtikel => KA,
  Warenkorb => WK, WarenkorbItem => WKI,
  Adresse => Addr, Adressen => Addrs,
  Bestellung => B, Bestellungen => Bs, Bestellposition => BP, Bestellpositionen => BPs
}

package shop{
package object xml { 
  //abkürzende Namen
  type Elem = scala.xml.Elem
  type Node = scala.xml.Node
  def Utility = scala.xml.Utility
  type DateTime = shop.util.DateTime

  
  /**
   * Implizite Methoden - automatische Typumwandlungen
   */
  
  implicit def intToStr(i: Int): String = i.toString
  implicit def boolToStr(b: Boolean): String = b.toString
  implicit def dateTimeToStr(dt: DateTime): String = dt.toString
  //implicit def bigDecimalToString(x:BigDecimal):String = x.setScale(2, RM).toString

  type Money = shop.util.MoneyClass
  implicit def moneyToString(x:Money):String = x.toString
  
  
  implicit def intToArticle(id: Int): A = A.get(id)
  implicit def artToString(a: A): String = (a:Elem).toString
  implicit def artToElem(a: A): Elem = <article id={a.id} name={a.name} price={a.price} hasPicture={a.hasPicture} />
  implicit def artToNode(a: A): Node = a:Elem
  
  implicit def intToCategory(id: Int): K = Ks.get(id)
  implicit def catToString(c: K): String = (c:Elem).toString
  implicit def catToElem(c: K): Elem = <category id={ c.id } name={ c.name }/>
  implicit def catToNode(c: K): Node = c:Elem
  
  implicit def intToUser(id: Int): U = Us.get(id)
  implicit def userToString(u:U):String = (u:Elem).toString
  implicit def userToElem(u:U):Elem = <user id={u.id} loginname={u.loginname} email={u.email} />
  implicit def userToNode(u:U):Node = u:Elem
  
  implicit def intToAddr(id: Int): Addr = Addrs.get(id)
  implicit def addrToString(a: Addr): String = (a:Elem).toString
  implicit def addrToElem(a: Addr): Elem = if(a != null) <address id={a.id}>{a.adresse}</address> else null
  implicit def addrToNode(a: Addr): Node = a:Elem
  
  implicit def intToBest(id: Int):B = Bs.get(id)
  implicit def bestToString(b: B):String = (b:Elem).toString
  implicit def bestToElem(b: B):Elem = <order id={b.id} datetime={b.datum} price={b.price} status={b.status} />
  implicit def bestToNode(b: B):Node = b:Elem
  
  implicit def bestPosToString(bp: BP):String = (bp:Elem).toString
  implicit def bestPosToElem(bp: BP):Elem = <item amount={bp.menge} price={bp.price}>{bp.art:Elem}</item>
  implicit def bestPosToNode(bp: BP):Node = bp:Elem
  
  implicit def intToWk(id: Int):WK = WK.get(id:U)
  implicit def wkToString(wk:WK):String = (wk:Elem).toString
  implicit def wkToElem(wk:WK):Elem = <basket>{for(wki <- wk.articles) yield wki:Elem}</basket>
  implicit def wkToNode(wk:WK):Node = wk:Elem
  
  implicit def wkiToString(wki:WKI):String = (wki:Elem).toString
  implicit def wkiToElem(wki:WKI):Elem = <item amount={wki.menge}>{wki.art:Elem}</item>
  implicit def wkiToNode(wki:WKI):Node = wki:Elem
  
}
}