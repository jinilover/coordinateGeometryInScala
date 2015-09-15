package org.jinilover.geometry.coordinate

import org.jinilover.geometry.coordinate.PolygonFuncs._
import org.scalatest.{Matchers, FlatSpec}

class BoundaryConditionSpec extends FlatSpec with Matchers {
  it should ("merge with 2 matching edges to form polygon has a hole, ok since all matching polygon edges are continuous") in {
    combine {
      Polygon(
        (10, 14), (16, 14), (16, 4), (3, 4),
        (3, 10), (7, 10), (7, 6), (13, 6),
        (13, 10), (10, 10)
      )
    } {
      Box((7, 6), (10, 10))
    } should be {
      Some(
        Polygon(
          (10, 14), (16, 14), (16, 4), (3, 4),
          (3, 10), (10, 10), (10, 6), (13, 6),
          (13, 10), (10, 10)
        )
      )
    }
  }

  val _LholePoly =
    Polygon(
      (10, 14), (16, 14), (16, 4), (3, 4),
      (3, 10), (10, 10), (10, 8), (7, 8),
      (7, 6), (13, 6), (13, 10), (10, 10)
    )

  it should ("merge with 1 matching edges to form polygon has a hole, ok since all matching polygon edges are continuous") in {
    combine {
      Polygon(
        (10, 14), (16, 14), (16, 4), (3, 4),
        (3, 10), (7, 10), (7, 6), (13, 6),
        (13, 10), (10, 10)
      )
    } {
      Box((7, 8), (10, 10))
    } should be {
      Some(_LholePoly)
    }
  }

  it should ("merge a polygon has a 'L' hole with a box, it's mergeable or not depends on the box's posiiton") in {
    // use the resulting polygon from the last test
    combine(_LholePoly)(Box((10, 6), (13, 9))) should be(None)

    combine(_LholePoly)(Box((10, 6), (13, 10))) should be(None)

    val doubleEyesPoly =
      Polygon(
        (10, 14), (16, 14), (16, 4), (3, 4),
        (3, 10), (10, 10), (10, 8), (7, 8),
        (7, 6), (10, 6), (10, 8), (13, 8),
        (13, 10), (10, 10)
      )

    combine(_LholePoly) {
      Box((10, 6), (13, 8))
    } should be {
      Some(doubleEyesPoly)
    }

    combine(doubleEyesPoly)(Box((10, 8), (13, 10))) should be(None)

    val singleEyePoly =
      Polygon(
        (10, 14), (16, 14), (16, 4), (3, 4),
        (3, 10), (10, 10), (10, 8), (13, 8),
        (13, 10), (10, 10)
      )

    combine(doubleEyesPoly) {
      Box((7, 6), (10, 8))
    } should be {
      Some(singleEyePoly)
    }

    combine(singleEyePoly) {
      Box((10, 8), (13, 10))
    } should be {
      Some(
        Polygon(
          (10, 14), (16, 14), (16, 4), (3, 4),
          (3, 10), (10, 10)
        )
      )
    }
  }

}
