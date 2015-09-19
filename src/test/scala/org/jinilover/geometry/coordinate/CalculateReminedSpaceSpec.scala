package org.jinilover.geometry.coordinate

import org.scalacheck.{Properties, Prop}
import Prop._
import GeometryFuncs._
import PolygonFuncs._
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
      boxes => calculateRemainedSpace(box)(boxes: _*) ==
        List(
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
      boxes => calculateRemainedSpace(box)(boxes: _*) ==
        List(
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
      boxes => calculateRemainedSpace(box)(boxes: _*) ==
        List(
          Polygon(
            (7, 20), (9, 20), (9, 16), (13, 16),
            (13, 14), (9, 14), (9, 10), (7, 10),
            (7, 14), (3, 14), (3, 16), (7, 16)
          )
        )
    }

  // inverse of last case
  property("subtract a cross, 4 boxes remain on each corner") =
    forAll {
      randomList(List(
        Box((7, 10), (9, 14)), Box((3, 14), (7, 16)),
        Box((7, 14), (9, 16)), Box((9, 14), (13, 16)),
        Box((7, 16), (9, 20))
      ))
    } {
      boxes => calculateRemainedSpace(box)(boxes: _*).toSet ==
        (List(
          Box((3, 10), (7, 14)), Box((3, 16), (7, 20)),
          Box((9, 16), (13, 20)), Box((9, 10), (13, 14))
        ) : List[Polygon]).toSet
    }

  property("subtract the same size polygon, return None") =
    forAll {
      randomList(List(
        Box((3, 10), (8, 15)), Box((3, 15), (8, 20)),
        Box((8, 15), (13, 20)), Box((8, 10), (13, 15))
      ))
    } {
      boxes => calculateRemainedSpace(box)(boxes: _*) == Nil
    }

  property("subtract a polygon at bottom left, another that matches 3 edges") =
    forAll {
      randomList(List(
        Box((3, 13), (6, 20)), Box((7, 18), (13, 20)),
        Box((11, 12), (13, 18)), Box((3, 10), (13, 12))
      ))
    } {
      boxes => calculateRemainedSpace(box)(boxes: _*) ==
        List(
          Polygon(
            (6, 20), (7, 20), (7, 18), (11, 18),
            (11, 12), (3, 12), (3, 13), (6, 13)
          )
        )
    }

  val page = Box((0, 0), (17, 19))

  property("simulate calculation of the yellow region in readme.md's diagram 1") =
    forAll {
      randomList(List(
        Box((6, 11), (9, 19)), Box((15, 11), (17, 19)),
        Box((0, 0), (17, 3)), Box((0, 9), (3, 14)),
        Box((13, 11), (15, 19)), Box((9, 11), (13, 19)),
        Box((3, 11), (6, 19)), Box((0, 14), (3, 19))
      ))
    } {
      boxes => calculateRemainedSpace(page)(boxes: _*) ==
        List(
          Polygon(
            (3, 11), (17, 11), (17, 3), (0, 3),
            (0, 9), (3, 9)
          )
        )
    }

  property("simulate calculation of the yellow region in readme.md's diagram 2") =
    forAll {
      randomList(List(
        Box((15, 7), (16, 19)), Box((0, 0), (17, 3)),
        Box((4, 9), (8, 19)), Box((0, 9), (4, 14)),
        Box((8, 7), (11, 19)), Box((16, 3), (17, 19)),
        Box((11, 10), (15, 19)), Box((0, 14), (4, 19))
      ))
    } {
      boxes => calculateRemainedSpace(page)(boxes: _*) ==
        List(
          Polygon(
            (11, 10), (15, 10), (15, 7), (16, 7),
            (16, 3), (0, 3), (0, 9), (8, 9),
            (8, 7), (11, 7)
          )
        )
    }
}
