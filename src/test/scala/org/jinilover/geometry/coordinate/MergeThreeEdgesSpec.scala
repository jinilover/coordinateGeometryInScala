package org.jinilover.geometry.coordinate

import org.jinilover.geometry.coordinate.Geometry._
import org.scalatest.{Matchers, FlatSpec}

class MergeThreeEdgesSpec extends FlatSpec with Matchers {
  it should "have merge a poly with a bottom box to form 'T'" in {
    merge {
      Polygon(
        (3, 10), (6, 10), (6, 8), (10, 8),
        (10, 10), (14, 10), (14, 5), (3, 5)
      )
    } {
      Box((6, 8), (10, 12))
    } should be {
      Some(
        Polygon(
          (6, 12), (10, 12), (10, 10), (14, 10),
          (14, 5), (3, 5), (3, 10), (6, 10)
        )
      )
    }
  }

  it should "have merge a poly with a bottom box to form 'T' clockwise 90deg" in {
    merge {
      Polygon(
        (9, 20), (13, 20), (13, 8), (9, 8),
        (9, 12), (11, 12), (11, 16), (9, 16)
      )
    } {
      Box((5, 12), (11, 16))
    } should be {
      Some(
        Polygon(
          (9, 20), (13, 20), (13, 8), (9, 8),
          (9, 12), (5, 12), (5, 16), (9, 16)
        )
      )
    }
  }

  it should "have merge a poly with a bottom box to form 'T' inverted" in {
    merge {
      Polygon(
        (3, 10), (12, 10), (12, 5), (10, 5),
        (10, 7), (6, 7), (6, 5), (3, 5)
      )
    } {
      Box((6, 2), (10, 7))
    } should be {
      Some(
        Polygon(
          (3, 10), (12, 10), (12, 5), (10, 5),
          (10, 2), (6, 2), (6, 5), (3, 5)
        )
      )
    }
  }

  it should "have merge a poly with a bottom box to form 'T' counter clockwise 90deg" in {
    merge {
      Polygon(
        (3, 20), (11, 20), (11, 17), (8, 17),
        (8, 14), (11, 14), (11, 11), (3, 11)
      )
    } {
      Box((8, 14), (13, 17))
    } should be {
      Some(
        Polygon(
          (3, 20), (11, 20), (11, 17), (13, 17),
          (13, 14), (11, 14), (11, 11), (3, 11)
        )
      )
    }
  }

}
