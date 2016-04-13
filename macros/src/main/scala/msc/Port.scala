package msc

import scalaz.\/
import scalaz.syntax.either._
import language.experimental.macros
import scala.reflect.macros.blackbox

final class Port private(val value: Int) extends AnyVal

object Port {
  def parse(p: Int): String \/ Port =
    if (1024 <= p && p < 65536) new Port(p).right
    else s"$p is out of range".left


  def apply(i: Int): Port = macro impl

  def impl(c: blackbox.Context)(i: c.Tree) = {
    import c.universe._

    def fail(s: String) = {
      val pos = c.enclosingPosition
      c.abort(pos.withPoint(pos.point + 1), s)
    }

    i match {
      case Literal(Constant(p: Int)) =>
        Port.parse(p) valueOr fail

        q"""
           _root_.msc.Port.parse($p) valueOr _root_.scala.sys.error
         """
      case other =>
        fail(other + " is not an Int literal. Use Port.parse instead.")
    }
  }
}
