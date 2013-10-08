package shop

import shop.xml._
import shop.db._

/**
 * Anzeigen einer Kategorie, sowie die Artikel in dieser Kategorie
 */
object ShowKategorie {

  //Kategorie-Baum-Navigation ausgeben  
  private[shop] def print(k: Kategorie)(implicit s: Session, o: Out) =
    echo(KategorieX.xml(k))

  //Parameter überprüfen
  def apply(implicit r: Req, s: Session, o: Out): Unit = {
    try {
      //Kategorieparameter
      var c = 1 //default = alle Kategorien 
      val c_in = getPara("c")
      if (c_in != null && c_in != "") //wurde übergeben
        c = c_in.toInt

      //Suchparameter
      var q = getPara("q")

      this(c, q)
    } catch { case _: Throwable => param_error }
  }

  //Methode die von Redirect aufgerufen werden kann
  private[shop] def apply(params: Array[Any])(implicit s: Session, o: Out): Unit = {
    if (params.size == 2)
      this(params(0).asInstanceOf[Int], params(1).asInstanceOf[String])
    else
      param_exception
  }

  //Anzeigen der Kategorie und dessen Artikel
  private[shop] def apply(cat: Int, q: String = null)(implicit s: Session, o: Out): Unit = {
    try {
      //ob eingeloggt
      Login.print
      
      //Kategorie-Navigation
      ShowKategorie.print(cat)
      
      //ohne suche
      if (q == null || q == "")
        echo(ArtikelX.xml(cat: Kategorie))
      //mit suche
      else
        echo(ArtikelX.xml(cat, q))
      
      //erlaube Redirect zu dieser Seite
      Redirect(this, cat, q)
    } catch { case _: Throwable => error }
  }

}