package shop.util

import java.util.{ Date => JD }
import java.sql.{ Date => D }
import java.sql.{ Time => T }

/**
 * Singleton-Objekt für die DateTime-Klasse
 */
object DateTime {

  //jetziger Zeitpunkt
  def apply(): DateTime = {
    val now = new JD()
    DateTime(new D(now.getTime), new T(now.getTime))
  }

  //sortierung
  implicit def dateTimeOrdering: Ordering[DateTime] = 
    Ordering.fromLessThan(_ isAfter _)

}

/**
 * DateTime Klasse die Date und Time zusammen als ein Objekt zusammenfasst
 */
case class DateTime(date: D, time: T) {
  override def toString(): String = date.toString() + " " + time.toString()

  def isAfter(other: DateTime):Boolean = 
    date.after(other.date) || (!date.before(other.date) && time.after(other.time))
  
  def isBefore(other: DateTime): Boolean =
    date.before(other.date) || (!date.after(other.date) && time.before(other.time))
}