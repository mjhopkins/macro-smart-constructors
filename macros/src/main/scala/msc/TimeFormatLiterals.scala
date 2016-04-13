package msc

import org.joda.time.format.DateTimeFormat

import scala.language.experimental.macros
import scala.reflect.macros.blackbox

object TimeFormatLiterals {
  implicit class TimeFormatContext(sc: StringContext) {
    def format1() = TimeFormat.parse(sc.parts.head)
    def format(): TimeFormat = macro timeFormat_impl
  }

  def timeFormat_impl(c: blackbox.Context)() = {
    import scala.util.{Failure, Success, Try}
    import c.universe._

    c.prefix.tree match {
      case q""" $obj($implicitClass(${p: String})) """ =>
        Try(DateTimeFormat.forPattern(p)) match {
          case Failure(e)     =>
            val pos = c.enclosingPosition
            c.abort(
              pos.withPoint(pos.point + "format".length + 1),
              "Invalid time format: " + e.getMessage
            )
          case Success(value) =>
            val tf = weakTypeOf[TimeFormat].typeSymbol.companion
            q"""  $tf.parse($p) valueOr _root_.scala.sys.error"""
        }
    }

  }
}
