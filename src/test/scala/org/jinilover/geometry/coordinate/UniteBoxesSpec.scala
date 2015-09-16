package org.jinilover.geometry.coordinate

import org.scalacheck.{Properties, Gen, Prop}
import Prop._
import GeometryFuncs._
import Generator._

class UniteBoxesSpec extends Properties("Unite boxes algorithm") {
  property("always combine the 3 specific boxes to the same single polygon regardless of the order") =
    forAll {
      randomList(List(
        Box((3, 16), (6, 20)), Box((6, 12), (9, 18)), Box((9, 9), (12, 15))
      ))
    } {
      boxes =>
        unite(boxes: _*) == List(
          Polygon(
            (3, 20), (6, 20), (6, 18), (9, 18),
            (9, 15), (12, 15), (12, 9), (9, 9),
            (9, 12), (6, 12), (6, 16), (3, 16)
          )
        )
    }

  property("always combine the 4 specific boxes to the same 2 polygons regardless of the order") =
    forAll {
      randomList(List(
        Box((3, 16), (6, 20)), Box((6, 12), (9, 18)), Box((9, 9), (12, 15)), Box((9, 21), (12, 24))
      ))
    } {
      boxes =>
        unite(boxes: _*).toSet == Set(
          Polygon(
            (3, 20), (6, 20), (6, 18), (9, 18),
            (9, 15), (12, 15), (12, 9), (9, 9),
            (9, 12), (6, 12), (6, 16), (3, 16)
          ),
          Polygon(
            (9, 24), (12, 24), (12, 21), (9, 21)
          )
        )
    }

  property("always combine the 8 specific boxes to the rectangle regardless of the order") =
    forAll {
      randomList(List(
        Box((3, 10), (7, 20)), Box((7, 10), (9, 12)), Box((7, 14), (9, 16)), Box((9, 10), (13, 15)),
        Box((9, 15), (13, 20)), Box((7, 12), (9, 14)), Box((7, 18), (9, 20)), Box((7, 16), (9, 18))
      ))
    } {
      boxes =>
        unite(boxes: _*) == List(
          Polygon(
            (3, 20), (13, 20), (13, 10), (3, 10)
          )
        )
    }

  property("publishing scenario 1, always combine the 4 specific boxes to the same polygon regardless of the order") =
    forAll {
      randomList(List(
        Box((0, 15), (1, 20)), Box((5, 15), (6, 20)), Box((1, 15), (4, 20)), Box((0, 1), (6, 15))
      ))
    } {
      boxes =>
        unite(boxes: _*) == List(
          Polygon(
            (0, 20), (4, 20), (4, 15), (5, 15),
            (5, 20), (6, 20), (6, 1), (0, 1)
          )
        )
    }

  property("publishing scenario 2, always combine the 5 specific boxes to the same polygon regardless of the order") =
    forAll {
      randomList(List(
        Box((0, 0), (5, 10)), Box((0, 10), (4, 15)), Box((3, 15), (6, 23)), Box((4, 10), (6, 15)),
        Box((5, 0), (6, 10))
      ))
    } {
      boxes =>
        unite(boxes: _*) == List(
          Polygon(
            (3, 23), (6, 23), (6, 0), (0, 0),
            (0, 15), (3, 15)
          )
        )
    }

  property("publishing scenario 3, always combine the 5 specific boxes to the same polygon regardless of the order") =
    forAll {
      randomList(List(
        Box((0, 0), (5, 10)), Box((5, 0), (6, 5)), Box((0, 10), (4, 15)), Box((3, 15), (6, 23)),
        Box((4, 10), (6, 15)), Box((5, 5), (6, 10))
      ))
    } {
      boxes =>
        unite(boxes: _*) == List(
          Polygon(
            (3, 23), (6, 23), (6, 0), (0, 0),
            (0, 15), (3, 15)
          )
        )
    }

  property("publishing scenario 4, always combine the 6 specific boxes to the same polygon regardless of the order") =
    forAll {
      randomList(List(
        Box((3, 17), (4, 23)), Box((2, 0), (6, 13)), Box((4, 13), (6, 17)), Box((2, 13), (4, 17)),
        Box((0, 0), (2, 17)), Box((4, 17), (5, 23))
      ))
    } {
      boxes =>
        unite(boxes: _*) == List(
          Polygon(
            (3, 23), (5, 23), (5, 17), (6, 17),
            (6, 0), (0, 0), (0, 17), (3, 17)
          )
        )
    }
}
