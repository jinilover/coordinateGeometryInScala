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

  property("subtract an 'L'") =
    forAll {
      randomList(List(
        Box((5, 18), (13, 20)), Box((3, 10), (5, 20))
      ))
    } {
      boxes =>
        calculateRemainedSpace(box)(boxes: _*) ==
          Some(
            Polygon((5, 18), (13, 18), (13, 10), (5, 10))
          )
    }

  property("subtract 4 boxes on each corner, form a cross") =
    forAll {
      randomList(List(
        Box((3, 10), (7, 14)), Box((3, 16), (7, 20)),
        Box((9, 16), (13, 20)), Box((9, 10), (13, 14))
      ))
    } {
      boxes =>
        calculateRemainedSpace(box)(boxes: _*) ==
          Some(
            Polygon(
              (7, 20), (9, 20), (9, 16), (13, 16),
              (13, 14), (9, 14), (9, 10), (7, 10),
              (7, 14), (3, 14), (3, 16), (7, 16)
            )
          )
    }

  property("subtract the same size polygon, return None") =
    forAll {
      randomList(List(
        Box((3, 10), (8, 15)), Box((3, 15), (8, 20)),
        Box((8, 15), (13, 20)), Box((8, 10), (13, 15))
      ))
    } {
      boxes =>
        calculateRemainedSpace(box)(boxes: _*) == None
    }

  property("subtract a polygon at bottom left, another that matches 3 edges") =
    forAll {
      randomList(List(
        Box((3, 13), (6, 20)), Box((7, 18), (13, 20)),
        Box((11, 12), (13, 18)), Box((3, 10), (13, 12))
      ))
    } {
      boxes =>
        calculateRemainedSpace(box)(boxes: _*) ==
          Some(
            Polygon(
              (6, 20), (7, 20), (7, 18), (11, 18),
              (11, 12), (3, 12), (3, 13), (6, 13)
            )
          )
    }
}
