package shop.xml

import shop.db.{ Artikel => A, Kategorie => K }

/**
 * Erzeugt XML für Artikel
 */
object ArtikelX {
  
  //findet Artikel innerhalb der Kategorie welche den String im Namen enthalten
  def find(k:K, name:String) =
    getAll(k:K).filter{a => a.name.indexOf(name) != -1 }
  
  //findet alle Artikel welche den String im Namen enthalten
  def find(name:String) = A.find(name)
  
  //Artikel mit der ID
  def get(id:Int) = A.get(id)
  def apply(id:Int) = get(id)
  
  //Artikel in der Kategorie
  def get(k:K) = k.articles
  
  //Artikel in der Kategorie und Unterkategorien
  def getAll(k:K) = k.allArticles
  
  //Artikel der Kategorie
  def ofCategory(id:Int) = get(id:K)
  
  //Alle Artikel der Kategorie und Unterkategorien
  def allOfCategory(id:Int) = getAll(id:K)

  /**
   * erzeugt XML für mehrere Artikel (für Kategorie-Seite und Suche)
   * Traversable ist eine Oberklasse für alle Collections über die man mit for
   * iterieren kann (Set, List, Array, etc.)
   */
  def xml(as:Traversable[A]):Elem = <articles>{for(a<-as) yield a:Elem}</articles>
  
  /**
   * XML für einen einzelnen Artikel (einen einzelnen Artikel genauer angucken)
   * enthält im Gegensatz zu sonst die Artikelbeschreibung und die Kategorie des
   * Artikels.
   */
  def xml(a:A):Elem = {
    val x = (a:Elem)
    x.copy(child = (x.child :+ <desc>{a.desc}</desc>) ++ KategorieX.xml(a) )
  }
  
  //XML-Ergebnis der Suche nach einem String
  def xml(name:String):Elem = xml(find(name))
  
  //XML-Ergebnis der Suche nach einem String in einer Kategorie
  def xml(k:K, name:String):Elem = xml(find(k, name))
  
  //XML-Ergebnis aller Artikel einer Kategorie ohne Suche
  def xml(k:K):Elem = xml(getAll(k))
  
}