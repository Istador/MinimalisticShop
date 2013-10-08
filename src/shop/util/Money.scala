package shop.util

/**
 * immer bei x.xx5 aufrunden schafft ein ungleichmäßiges Runden.
 * 
 * HALF_EVEN rundet bei x.xy5
 * auf, wenn y gerade
 * ab, wenn y ungerade
 * (oder umgekehrt)
 */
import BigDecimal.RoundingMode.{HALF_EVEN => RM}

//Geld interface
trait Money {
  def value: BigDecimal
  def +(o:Money):Money
  def -(o:Money):Money
  def *(o:Money):Money
  def *(n:BigDecimal):Money
  def /(o:Money):Money
  def /(n:BigDecimal):Money
  def toEuro:Euro
  def toInt:Int
  def toString:String
}

//Begleitobjekte von Geld
trait MoneyObject[+T <: Money] {
  def apply(value:BigDecimal):T
  def toEuro(self:BigDecimal):Euro
  def fromEuro(x:Euro):T
  def unit:String
  def toString(x:BigDecimal):String
  def toInt(x:BigDecimal):Int
  def fromInt(x:Int):Euro
}

abstract class MoneyClass extends Money {
  def mo:MoneyObject[MoneyClass]
  
  def +(o:MoneyClass):MoneyClass = mo(value + o.value)
  def -(o:MoneyClass):MoneyClass = mo(value - o.value)
  def *(o:MoneyClass):MoneyClass = mo(value * o.value)
  def /(o:MoneyClass):MoneyClass = mo(value / o.value)
  
  def *(n:BigDecimal):MoneyClass = mo(value * n) 
  def /(n:BigDecimal):MoneyClass = mo(value / n)
  
  //bei anderem Typen erst umwandeln
  def +(o:Money):MoneyClass = this + mo.fromEuro(o.toEuro)
  def -(o:Money):MoneyClass = this - mo.fromEuro(o.toEuro)
  def *(o:Money):MoneyClass = this * mo.fromEuro(o.toEuro)
  def /(o:Money):MoneyClass = this / mo.fromEuro(o.toEuro)
  
  override def toString:String = mo.toString(value) +" "+ mo.unit
  def toEuro:Euro = mo.toEuro(value)
  def toInt:Int = mo.toInt(value)
}

object EuroObj extends MoneyObject[Euro]{
  def apply(x:BigDecimal) = Euro(x)
  def toEuro(self:BigDecimal) = Euro(self)
  def fromEuro(x:Euro) = x
  def unit:String = "€"
    
  //runde auf 2 Nachkommastellen bei der Ausgabe als String
  def toString(x:BigDecimal):String = x.setScale(2, RM).toString

  def toInt(x:BigDecimal):Int = (x * 100).intValue  
  def fromInt(x:Int):Euro = Euro(BigDecimal(x) / 100) 
}

case class Euro(value:BigDecimal) extends MoneyClass {
  def mo = EuroObj
}