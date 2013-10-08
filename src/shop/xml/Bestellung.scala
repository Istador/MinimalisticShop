package shop.xml

import shop.db.{ User => U, Warenkorb => WK, Adresse => Addr, Bestellung => B, Bestellungen => Bs }

/**
 * Erzeugt XML für Bestellungen 
 */
object BestellungX {
  //ersetzt in der Adresse die Zeilenumbrüche mit <br/>
  def addr(a:Addr):Elem = if(a != null) <address id={a.id}>{a.adresse.split("\n").map(scala.xml.Text(_):scala.xml.NodeSeq).reduce(_ ++ <br /> ++ _)}</address> else null
  
  //Lieferadresse
  def liefer(a:Addr):Elem = <liefer>{addr(a)}</liefer>
  
  //Rechnungsadresse
  def rechnung(a:Addr):Elem = <rechnung>{addr(a)}</rechnung>
  
  //Bestellung mit der ID
  def get(id:Int):B = Bs.get(id)
  def apply(id:Int) = get(id)
  
  //Alle Bestellungen des Users
  def get(u:U):Set[B] = Bs.get(u)
  
  //XML-Ergebnis aller Bestellungen eines Users sortiert nach Datum
  def xml(u:U):Elem = <orders>{for(b <-get(u).toIndexedSeq.sortBy(_.datum)) yield b:Elem}</orders>
  
  /**
   * XML-Ergebnis einer einzelnen Bestellung, mit den Bestellpositionen, sowie
   * der Liefer- und Rechnungsadresse der Bestellung.
   */
  def xml(b:B):Elem = {
    val x = (b:Elem)
    x.copy(child = x.child.++:((for(bp <- b.best_positionen) yield bp:Elem).toArray).+:(rechnung(b.rechnung)).+:(liefer(b.liefer)) )
  }
  
  /**
   * XML-Ergebnis für die Seite um eine neue Bestellung zu erstellen.
   * Enthält die aktuellen Warenkorb-Items, sowie die 
   * Liefer- und Rechnungsadresse des Users.
   */
  def xmlCreate(u:U):Elem = <createorder>{UserX.liefer(u)}{UserX.rechnung(u)}{WarenkorbX.xml(u)}</createorder>
  
  /**
   * Erstellt eine neue Bestellung
   */
  def create(user:U, liefer:Addr, rechnung:Addr, wk:WK) = 
    Bs.create(user, liefer, rechnung, wk)
}