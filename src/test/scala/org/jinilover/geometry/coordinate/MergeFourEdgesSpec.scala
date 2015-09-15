package org.jinilover.geometry.coordinate

import org.jinilover.geometry.coordinate.PolygonFuncs._
import org.scalatest.{Matchers, FlatSpec}

class MergeFourEdgesSpec extends FlatSpec with Matchers {
  it should "1 box edge not covered the polygon's correspondant, the 'opening' faces the the bottom" in {
    combine {
      Polygon(
        (8, 15), (13, 15), (13, 4), (3, 4),
        (3, 10), (6, 10), (6, 7), (10, 7),
        (10, 10), (8, 10)
      )
    } {
      Box((6, 7), (10, 10))
    } should be {
      Some(
        Polygon(
          (8, 15), (13, 15), (13, 4), (3, 4),
          (3, 10), (8, 10)
        )
      )
    }

    // same as previous, this time the polygon's left horizontal is lower than the opening
    combine {
      Polygon(
        (8, 15), (13, 15), (13, 4), (3, 4),
        (3, 11), (6, 11), (6, 7), (10, 7),
        (10, 10), (8, 10)
      )
    } {
      Box((6, 7), (10, 10))
    } should be {
      Some(
        Polygon(
          (8, 15), (13, 15), (13, 4), (3, 4),
          (3, 11), (6, 11), (6, 10), (8, 10)
        )
      )
    }
  }
}
