package msc

import scalaz.\/
import TimeFormatLiterals._

object Test extends App {

  // add a clashing name to test macro hygiene:
  val TimeFormat = 4

  val timeFormat1: TimeFormat = msc.TimeFormat("MMM D YYYY")

// compilation error:
//  val badTimeFormat1 = msc.TimeFormat(":qMMM D YYYY")

  val attempt: String \/ TimeFormat = format1"MMM D YYYY"
  attempt.valueOr(sys.error)

  val timeFormat2 = format"MMM D YYYY"

// compilation error:
//  val badTimeFormat2 = format"MMM Dt YYYY"

  val p = Port(8080)

//   Compilation failure: 0 is out of range
//  val badPort1 = Port(0)

//   Compilation failure: 65536 is out of range
//  val badPort2 = Port(65536)
}
