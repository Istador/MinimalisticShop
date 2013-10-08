package shop.db

/**
 * Methoden um mit allen Tabellen auf einmal zu arbeiten
 * Alle Tabellen erstellen, löschen, initialisieren, cache leeren
 */
object Tables {
  
  /**
   * Erstellt alle Tabellen in der richtigen Reihenfolge
   */
  def create = {
    Adressen.createTable
    Users.createTable
    Artikel.createTable
    Warenkorb.createTable
    Kategorien.createTable
    KategorieArtikel.createTable
    Bestellungen.createTable
    Bestellpositionen.createTable
  }

  /**
   * Leert alle Caches von allen Tabellen
   */
  def clearCaches = {
    Adressen.clearCache
    Users.clearCache
    Artikel.clearCache
    Warenkorb.clearCache
    Kategorien.clearCache
    KategorieArtikel.clearCache
    Bestellungen.clearCache
    Bestellpositionen.clearCache
  }
  
  /**
   * Löscht alle Tabellen in der richtigen Reihenfolge
   */
  def drop = {
    Bestellpositionen.dropTable
    Bestellungen.dropTable
    KategorieArtikel.dropTable
    Kategorien.dropTable
    Warenkorb.dropTable
    Artikel.dropTable
    Users.dropTable
    Adressen.dropTable
  }
  
  /**
   * Füllt die Tabellen mit Kategorien und Artikel
   */
  def init = {
    val cRoot = Kategorien.+("Alle Kategorien")
      val cSonstiges = Kategorien.+("Sonstiges", cRoot)
        cSonstiges + Artikel.+("Windeln", "Wasserdicht...", 2.00)
      val cFood = Kategorien.+("Lebensmittel", cRoot)
        cFood + Artikel.+("Waffeln", "4er Packung leckerer Waffeln.", 0.67)
        val cDrinks = Kategorien.+("Getränke", cFood)
          cDrinks + Artikel.+("Wasser", "Gesundes Quellwasser 1,5l", 0.49)
          cDrinks + Artikel.+("Coca Cola", "Gesunde Coca Cola Classic 1l", 0.89)
        val cMeat = Kategorien.+("Fleisch", cFood)
          cMeat + Artikel.+("Putenschnitzel", "Das beste vom Tier. 120g", 1.49)
          cMeat + Artikel.+("Steak", "Ein herzhaftes Rindersteak. 250g", 4.99)
      val cBooks = Kategorien.+("Bücher", cRoot)
        val aDesignPatterns = Artikel.+("Design Patterns", "von der Gang of Four (Gamma, Helm, Johnson, Vlissides).", 59.99)
        val aPotter = Artikel.+("Harry Potter und der Stein der Weisen", "von Joanne K. Rowling.", 10.00)
        val cBestBooks = Kategorien.+("Bestseller", cBooks)  
          cBestBooks + aDesignPatterns
          cBestBooks + aPotter
        val cTextBooks = Kategorien.+("Fachbücher", cBooks)
          cTextBooks + aDesignPatterns
          cTextBooks + Artikel.+("Understanding Cryptography", "von Christof Paar und Jan Pelzl", 37.40)
        val cFantasyBooks = Kategorien.+("Fantasy", cBooks)
          cFantasyBooks + aPotter
          cFantasyBooks + Artikel.+("Die Zwerge", "von Markus Heitz", 9.99)
      val cElektro = Kategorien.+("Elektronik", cRoot)
        cElektro + Artikel.+("NIC", "10/100/1000 Network Interface Card.", 9.99)
        cElektro + Artikel.+("MP3-Player", "20 GB MP3-Player", 29.99)
        val cGames = Kategorien.+("Videospiele", cElektro)
          val aPortal = Artikel.+("Portal 2","Entkommen Sie den Testkammern.",20.00) 
          val aSkyrim = Artikel.+("Skyrim","Elder Scrolls V.",35.48)
          val cPC = Kategorien.+("PC-Spiele", cGames)
            cPC + aPortal
            cPC + aSkyrim
            cPC + Artikel.+("Gothic", "Klassisches deutsches RPG von Piranha Bytes.", 4.99)
          val cPS3 = Kategorien.+("PS3-Spiele", cGames)
            cPS3 + aPortal
            cPS3 + aSkyrim
            cPS3 + Artikel.+("God of War 3", "Stürmen Sie als Kratos den Olymp und nehmen Rache an den Göttern selbst.", 64.99)
    
    //diese Artikel besitzen ein Bild
    Artikel(3).setPicture
    Artikel(7).setPicture
    Artikel(9).setPicture
    Artikel(10).setPicture
    Artikel(13).setPicture
    Artikel(15).setPicture
  }
}