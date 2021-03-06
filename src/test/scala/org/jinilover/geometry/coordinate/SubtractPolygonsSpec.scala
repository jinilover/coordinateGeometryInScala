package org.jinilover.geometry.coordinate

import org.scalatest.{Matchers, FlatSpec}
import GeometryFuncs._
import PolygonFuncs._
import org.scalacheck.{Properties, Prop}
import Prop._
import GeometryFuncs._
import Generator._

class SubtractPolygonsSpec extends Properties("Calculate remained space algorithm") {
  val box = Box((3, 10), (13, 20))

  property("subtract 2 polygons, 1 sticks to left, the other sticks to right") =
    forAll {
      randomList(List(
        Box((3, 11), (7, 14)), Box((10, 15), (13, 18))
      ): POLYGONS)
    } {
      polys => subtract(box)(polys: _*) ==
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
        Polygon(
          (3, 20), (13, 20), (13, 18), (5, 18),
          (5, 10), (3, 10)
        )
      ))
    } {
      polys => subtract(box)(polys: _*) ==
        List(Polygon((5, 18), (13, 18), (13, 10), (5, 10)))
    }

  //similar to last test, subtract 1 more 'L' on the top right corner
  property("subtract an 'L' and 1 more on the top right corner") =
    forAll {
      randomList(List(
        Polygon(
          (3, 20), (13, 20), (13, 18), (5, 18),
          (5, 10), (3, 10)
        ),
        Polygon(
          (7, 15), (13, 15), (13, 13), (9, 13),
          (9, 10), (7, 10)
        )
      ))
    } {
      polys => subtract(box)(polys: _*).toSet ==
        Set(
          Polygon(
            (5, 18), (13, 18), (13, 15), (7, 15),
            (7, 10), (5, 10)
          ),
          Polygon(
            (9, 13), (13, 13), (13, 10), (9, 10)
          )
        )
    }

  property("subtract 4 boxes on each corner, form a cross") =
    forAll {
      randomList(List(
        Box((3, 10), (7, 14)), Box((3, 16), (7, 20)),
        Box((9, 16), (13, 20)), Box((9, 10), (13, 14))
      ): POLYGONS)
    } {
      polys => subtract(box)(polys: _*) ==
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
        Polygon(
          (7, 20), (9, 20), (9, 16), (13, 16),
          (13, 14), (9, 14), (9, 10), (7, 10),
          (7, 14), (3, 14), (3, 16), (7, 16)
        )
      ))
    } {
      polys => subtract(box)(polys: _*).toSet ==
        (Set(
          Box((3, 10), (7, 14)), Box((3, 16), (7, 20)),
          Box((9, 16), (13, 20)), Box((9, 10), (13, 14))
        ): Set[Polygon])
    }

  property("subtract the same size polygon, return None") =
    forAll {
      randomList(List(box): POLYGONS)
    } {
      polys => subtract(box)(polys: _*) == Nil
    }

  property("subtract a polygon at bottom left, another that matches 3 edges") =
    forAll {
      randomList(List(
        Box((3, 13), (6, 20)),
        Polygon(
          (7, 20), (13, 20), (13, 10), (3, 10),
          (3, 12), (11, 12), (11, 18), (7, 18)
        )
      ): POLYGONS)
    } {
      polys => subtract(box)(polys: _*) ==
        List(
          Polygon(
            (6, 20), (7, 20), (7, 18), (11, 18),
            (11, 12), (3, 12), (3, 13), (6, 13)
          )
        )
    }
}
