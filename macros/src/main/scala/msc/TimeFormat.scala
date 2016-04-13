package msc

import org.joda.time.format.DateTimeFormat

import scala.language.experimental.macros
import scala.reflect.macros.blackbox
import scala.util.Success
import scalaz.\/

final class TimeFormat private(val format: String) extends AnyVal {
  override def toString = s"TimeFormat($format)"
}

object TimeFormat {
  def parse(pattern: String) =
    \/.fromTryCatchNonFatal(DateTimeFormat.forPattern(pattern))
      .bimap(
        e => e.getMessage,
        _ => new TimeFormat(pattern)
      )

  def apply(pattern: String): TimeFormat = macro impl

  def impl(c: blackbox.Context)(pattern: c.Tree) = {
    import c.universe._

    def fail(msg: String) = {
      val pos = c.enclosingPosition
      // position at argument
      c.abort(pos.withPoint(pos.point + 2), msg)
    }

    pattern match {
      case Literal(Constant(s: String)) =>
        parse(s) valueOr fail

        val tf = weakTypeOf[TimeFormat].typeSymbol.companion

        q"""
            $tf.parse($pattern) valueOr _root_.scala.sys.error
        """

      /* Alternatively, this is another way to reference TimeFormat in a hygienic way
         i.e. that won't be broken by clashing imports or definitions
       */

      //        q"""
      //            _root_.smart_constructors.TimeFormat.parse($pattern) valueOr sys.error
      //        """
      case other =>
        fail(s"$other is not a String literal. Use TimeFormat.parse instead.")
    }
  }
}


