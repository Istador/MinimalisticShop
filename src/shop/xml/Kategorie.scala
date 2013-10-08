package shop.xml

import shop.db.{ Artikel => A, Kategorie => K, Kategorien => Ks, KategorieArtikel => KA }

/**
 * Erzeugt XML für Kategorien 
 */
object KategorieX {

  //Kategorie mit der ID
  def get(id:Int):K = Ks.get(id)
  def apply(id:Int) = get(id)
  
  /**
   * XML-Ergebnis für eine einzelne Kategorie mit Eltern-Elementen.
   * (für die aktuell ausgewählte Kategorie)
   */
  def xml(cat:K): Elem =
    <category id={cat.id} name={cat.name}>
      { genParents(cat.parent) }
      { for (c <- cat.childs) yield c:Elem }
    </category>
  
  //XML-Ergebnis für die Kategorieen eines Artikels
  def xml(a:A): Set[Elem] =
    for(c <- KA.get(a)) yield c:Elem

  //erzeugt rekursiv das XML für die Eltern einer Kategorie
  private[this] def genParents(c: K): Elem = c match {
    case null => null 
    case K(id, name, null) => <parent id={id} name={name}/>
    case K(id, name, parent) => <parent id={id} name={name}>{ genParents(parent) }</parent>
  }
  
}