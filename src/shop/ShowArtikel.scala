package shop

import shop.xml._
import shop.db._

/**
 * Anzeigen eines einzelnen Artikels
 */
object ShowArtikel {
  
  //Parameter überprüfen
  def apply(implicit r: Req, s: Session, o: Out): Unit = {
    try {
      val a = getPara("a").toInt
      val c = getPara("c")
      //Kategorie übergeben
      if (c != null && c != "")
        this(a, c.toInt)
      //keine Kategorie übergeben -> Root-Kategorie
      else
        this(a, 1)
    } catch { case _: Throwable => param_error }
  }

  //Methode die von Redirect aufgerufen werden kann
  private[shop] def apply(params: Array[Any])(implicit s: Session, o: Out): Unit = {
    if (params.size == 2)
      this(params(0).asInstanceOf[Int], params(1).asInstanceOf[Int])
    else
      param_exception
  }

  //Anzeigen des Artikels
  private[this] def apply(art: Int, cat: Int)(implicit s: Session, o: Out): Unit = {
    try {
      //ob der nutzer eingeloggt ist oder nicht.
      Login.print
      
      //Kategorie-Baum-Navigation
      ShowKategorie.print(cat)
      
      //Artikel selbst
      echo(ArtikelX.xml(art: Artikel))
      
      //zu dieser Seite redirect erlauben
      Redirect(this, art, cat)
    } catch { case _: Throwable => error }
  }
}