import scala.xml.{Elem, Node, Utility}
import javax.servlet.http.HttpServletResponse

package object shop { 
  
  //abkürzende Typnamen für das ganze Package definieren
  type Session = javax.servlet.http.HttpSession
  type Req = javax.servlet.http.HttpServletRequest
  type Res = javax.servlet.http.HttpServletResponse
  type Out = javax.servlet.jsp.JspWriter;
  
  //entfernt Whitespaces aus dem XML
  private[this] def trim(x:Elem):Node = Utility.trim(x)
  private[this] def trim(x:Node):Node = Utility.trim(x)
  
  //XML to String
  private[this] implicit def xmlElemToString(x:Elem):String = trim(x).toString
  private[this] implicit def xmlNodeToString(x:Node):String = trim(x).toString
  
  //holt ein gespeichertes Datum aus der Session falls vorhanden, sonst null
  def getAttr[T](key:String)(implicit s:Session) = 
    s.getAttribute(key).asInstanceOf[T]
  
  //speichert ein Datum in der Session
  def setAttr[T](key:String, o:T)(implicit s:Session) = 
    s.setAttribute(key,o)
  
  //entfernt ein Datum aus der Session (so dass, wenn es angefragt wird null zurückkommt)
  def remAttr[T](key:String)(implicit s:Session) = 
    s.removeAttribute(key)
  
  //holt einen GET- oder POST-Parameter aus dem Request
  def getPara(name:String)(implicit r:Req) = 
    r.getParameter(name)
  
  //prüft ob ein GET- oder POST-Parameter übergeben wurde
  def existsPara(name:String)(implicit r:Req) = 
    r.getParameter(name) != null 
  
  //Ausgabe: Unbekannter Fehler, und Redirect zur letzten Seite
  private[shop] def error(implicit s:Session, o:Out):Unit = { echo(default_error) ; Redirect() }
  //Ausgabe: Parameter Fehler, und Redirect zur letzten Seite
  private[shop] def param_error(implicit s:Session, o:Out):Unit = { echo(parameter_error) ; Redirect() }
  //wirft gezielt eine Runtime-Exception
  private[shop] def param_exception:Unit = throw new RuntimeException("Parameter Fehler")
  
  //Ausgabe einer XML-Fehlermeldung, ohne Redirect
  private[shop] def error(msg:String)(implicit o:Out):Unit = echo(mk_error(msg))
  //Ausgabe einer XML-Informationsmeldung, ohne Redirect
  private[shop] def info(msg:String)(implicit o:Out):Unit = echo(mk_info(msg))
  //Ausgabe einer XML-Warnung, ohne Redirect
  private[shop] def warn(msg:String)(implicit o:Out):Unit = echo(mk_warn(msg))
  
  //Methoden welche die entsprechenden XML-Elemente erzeugen
  private[this] def mk_info(msg:String):scala.xml.Elem = <info msg={msg}/>
  private[this] def mk_warn(msg:String):scala.xml.Elem = <warn msg={msg}/>
  private[this] def mk_error(msg:String):scala.xml.Elem = <error msg={msg}/>
   
  //übliche/häufige Fehlermeldungen zwischenspeichern, und nicht immer neu erzeugen
  //das lazy bedeutet dass es erst erzeugt wird und speicher benötigt, wenn es das erste mal aufgerufen wird
  private[this] lazy val default_error:scala.xml.Elem = mk_error("Unbekannter Fehler")
  private[this] lazy val parameter_error:scala.xml.Elem = mk_error("Parameter Fehler")
  
  //Ausgabe von XML im HTTP-Response
  private[shop] def echo(str:scala.xml.Node)(implicit o:Out):Unit = o.println(trim(str))
  //Ausgabe von XML im HTTP-Response
  private[shop] def echo(str:scala.xml.Elem)(implicit o:Out):Unit = o.println(trim(str))
  //Ausgabe von Exceptions als XML im HTTP-Response
  private[shop] def echo(e:Throwable)(implicit o:Out):Unit = error(e.getMessage + " : <br/>\n" +  e.getStackTraceString)
}