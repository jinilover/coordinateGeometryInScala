package org.jinilover.geometry.coordinate

import org.scalatest.{Matchers, FlatSpec}
import GeometryFuncs._
import PolygonFuncs._

class SubtractPolygonsSpec extends FlatSpec with Matchers {
  val box = Box((3, 10), (13, 20))

  it should "subtract 2 polygons, 1 sticks to left, the other sticks to right  " in {
    subtract(box)(
      Box((3, 11), (7, 14)), Box((10, 15), (13, 18))
    ) should be {
      Some(
        Polygon(
          (3, 20), (13, 20), (13, 18), (10, 18),
          (10, 15), (13, 15), (13, 10), (3, 10),
          (3, 11), (7, 11), (7, 14), (3, 14)
        )
      )
    }
  }

  it should "subtract an 'L'" in {
    subtract(box)(
      Polygon(
        (3, 20), (13, 20), (13, 18), (5, 18),
        (5, 10), (3, 10)
      )
    ) should be {
      Some(Polygon((5, 18), (13, 18), (13, 10), (5, 10)))
    }
  }

  it should "subtract 4 boxes on each corner, form a cross" in {
    subtract(box)(
      Box((3, 10), (7, 14)), Box((3, 16), (7, 20)), Box((9, 16), (13, 20)), Box((9, 10), (13, 14))
    ) should be {
      Some(
        Polygon(
          (7, 20), (9, 20), (9, 16), (13, 16),
          (13, 14), (9, 14), (9, 10), (7, 10),
          (7, 14), (3, 14), (3, 16), (7, 16)
        )
      )
    }
  }

  it should "subtract the same size polygon, return None" in {
    subtract(box)(box) should be(None)
  }

  it should "subtract a polygon at bottom left, another that matches 3 edges" in {
    subtract(box)(
      Box((3, 13), (6, 20)),
      Polygon(
        (7, 20), (13, 20), (13, 10), (3, 10),
        (3, 12), (11, 12), (11, 18), (7, 18)
      )
    ) should be {
      Some(
        Polygon(
          (6, 20), (7, 20), (7, 18), (11, 18),
          (11, 12), (3, 12), (3, 13), (6, 13)
        )
      )
    }
  }
}
