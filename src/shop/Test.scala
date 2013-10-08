package shop

import shop.db._
import shop.xml._

/**
 * Test-Klasse zum testen der Datenbank
 * extends App bedeutet dass dieses Objekt von der Konsole ausgeführt werden kann
 * (enthält eine static void main-Methode)
 */
object Test extends App {
  
  Tables.drop 
  Tables.create
  Tables.init
  
  val u = UserX.register("ladiges", "robin.ladiges@web.de", "password")
  val addr = Adressen.create(u, "Krupunder Weg 6\nD-22523\nHamburg")
  u.setLiefer(addr)
  u.setRechnung(addr)
  
  val a1 = ArtikelX.find("Harry Potter").fold(null)((a,x)=>x)
  val a2 = ArtikelX.find("Cola").fold(null)((a,x)=>x)
  WarenkorbX.update(u, a1, 1)
  WarenkorbX.update(u, a2, 5)
  
  println(u)
  println(a1)
  println(a2)
  
  val wk = WarenkorbX.get(u)
  
  println(wk)
  
  println()
  
  //Bestellung erstellen
  val best = BestellungX.create(u, addr, addr, wk)
  println("Bestellung "+best.id+" vom "+best.datum.toString+":")
  for(bsp <- best.best_positionen){
    println("\t"+bsp.art.name+"\t"+bsp.menge+" x "+bsp.price+"\t= "+(bsp.price * bsp.menge))
  }
  println("Versandkosten: 5.00 €")
  println("Gesamtpreis: "+best.price)
  
  println()
  
  println(KategorieX.xml(3:Kategorie))
  println(ArtikelX.xml(1:Artikel))
  println(ArtikelX.xml("Potter"))
  println(ArtikelX.xml(1, "tt"))

  println(ArtikelX.xml(3:Kategorie))
  
  //println(ShowArtikel(7, 6)(null, null))
  
}