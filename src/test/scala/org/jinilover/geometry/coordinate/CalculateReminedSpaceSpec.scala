package org.jinilover.geometry.coordinate

import org.scalacheck.{Properties, Prop}
import Prop._
import GeometryFuncs._
import Generator._

/**
 * reuse test cases in SubtractPolygonsSpec, but this time tests calculateRemainedSpace
 * which is an end-end test
 */
class CalculateReminedSpaceSpec extends Properties("Calculate remained space algorithm") {
  val box = Box((3, 10), (13, 20))

  property("subtract 2 boxes, 1 sticks to left, the other sticks to right") =
    forAll {
      randomList(List(
        Box((3, 11), (7, 14)), Box((10, 15), (13, 18))
      ))
    } {
      boxes =>
        calculateRemainedSpace(box)(boxes: _*) ==
          Some(
            Polygon(
              (3, 20), (13, 20), (13, 18), (10, 18),
              (10, 15), (13, 15), (13, 10), (3, 10),
              (3, 11), (7, 11), (7, 14), (3, 14)
            )
          )
    }
}
