package shop

/**
 * Facade Pattern
 * Methoden für die JSP-Seiten zum einfachen aufrufen
 * Die JSP-Seiten übergeben request, session und out in Java, und die Scala-Klassen
 * übernehmen die ganze Logik der Anwendung.
 *
 * Für jede JSP-Seite eine Methode:
 */
object Shop {

  def login(implicit r: Req, s: Session, o: Out, res: Res) = show(Login)
  def logout(implicit r: Req, s: Session, o: Out, res: Res) = show(Logout)
  def profil(implicit r: Req, s: Session, o: Out, res: Res) = show(Profil)
  def register(implicit r: Req, s: Session, o: Out, res: Res) = show(Register)
  def artikel(implicit r: Req, s: Session, o: Out, res: Res) = show(ShowArtikel)
  def kategorie(implicit r: Req, s: Session, o: Out, res: Res) = show(ShowKategorie)
  def bestellen(implicit r: Req, s: Session, o: Out, res: Res) = show(CreateOrder)
  def bestellung(implicit r: Req, s: Session, o: Out, res: Res) = show(ShowOrder)
  def bestellungen(implicit r: Req, s: Session, o: Out, res: Res) = show(ShowOrders)
  def warenkorb(implicit r: Req, s: Session, o: Out, res: Res) = show(ShowWarenkorb)

  //die Funktion show, bekommt nur Objekte übergeben die eine apply-Methode besitzen
  private[this]type Applyable = { def apply(implicit r: Req, s: Session, o: Out): Any }

  /*
   * Die Funktion show ruft die Seitenspezifische apply Methode auf, umgibt dessen 
   * ausgabe mit dem zentralen XML <shop> element und behandelt evtl. unbehandelte 
   * Exceptions.
   */
  private[this] def show(a: Applyable)(implicit req: Req, s: Session, o: Out, res: Res): Unit = {
    res.setCharacterEncoding("UTF-8") //Rückgabe dieser Seite ist UTF-8
    req.setCharacterEncoding("UTF-8") //Formulardaten des Users sind UTF-8

    //Suchparameter q in <shop> packen falls vorhanden 
    var q = getPara("q")
    if (q != null && q != "") q = " q=\"" + q + "\""
    else q = ""

    o.println("<shop xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"shop.xsd\"" + q + ">")
    try {
      a.apply
    } catch { //Fehlerbehandlung
      case e: Throwable => error; echo(e)
    }
    o.println("</shop>")
  }

}