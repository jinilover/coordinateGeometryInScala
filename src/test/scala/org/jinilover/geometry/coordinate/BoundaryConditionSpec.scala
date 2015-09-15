package org.jinilover.geometry.coordinate

import org.jinilover.geometry.coordinate.PolygonFuncs._
import org.scalatest.{Matchers, FlatSpec}

class BoundaryConditionSpec  extends FlatSpec with Matchers {
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
      Some(
        Polygon(
          (10, 14), (16, 14), (16, 4), (3, 4),
          (3, 10), (10, 10), (10, 8), (7, 8),
          (7, 6), (13, 6), (13, 10), (10, 10)
        )
      )
    }
  }

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

}
