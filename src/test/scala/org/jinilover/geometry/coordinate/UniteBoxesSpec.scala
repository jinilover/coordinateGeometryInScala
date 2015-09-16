package org.jinilover.geometry.coordinate

import org.scalacheck.{Properties, Gen, Prop}
import Prop._
import GeometryFuncs._
import UniteBoxesSpec._

class UniteBoxesSpec extends Properties("Unite boxes algorithm") {
  property("combine 3 boxes combine to 1 single polygon, the result is the same regardless of the combining order") =
    forAll {
      boxRandomOrder(List(Box((3, 16), (6, 20)), Box((6, 12), (9, 18)), Box((9, 9), (12, 15))))
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

  property("combine 4 boxes and 1 of them cannot be combined, the result is always the same 2 polygons regardless of the combining order") =
    forAll {
      boxRandomOrder(List(
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

  property("combine 8 boxes to 1 square, the result is the same regardless of the combining order") =
    forAll {
      boxRandomOrder(List(
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
}

object UniteBoxesSpec {
  val boxRandomOrder: BOXES => Gen[BOXES] =
    boxes => boxes.foldLeft(Gen.const(Nil: BOXES)) {
      (genBoxes, b) =>
        for {
          bs <- genBoxes
          availBs = boxes diff bs
          n <- Gen.choose(0, availBs.size - 1)
        } yield availBs(n) :: bs
    }
}
