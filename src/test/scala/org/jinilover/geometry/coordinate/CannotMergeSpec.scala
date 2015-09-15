package org.jinilover.geometry.coordinate

import org.scalatest.{Matchers, FlatSpec}
import PolygonFuncs._

/**
 * Test if the algo can decide not to match the polygon due to no edge matching 
 */
class CannotMergeSpec extends FlatSpec with Matchers {
  it should "no matching edge" in {
    combine {
      Polygon((2, 13), (6, 13), (6, 9), (2, 9))
    } {
      Box((6, 5), (12, 9))
    } should be(None)
  }

  it should "all box edges are matched, but the polygon's matching edges are not continuous" in {
    combine {
      Polygon(
        (3, 20), (11, 20), (11, 18), (5, 18),
        (5, 11), (13, 11), (13, 14), (11, 14),
        (11, 16), (14, 16), (14, 9), (3, 9))
    } {
      Box((5, 11), (11, 18))
    } should be(None)
  }

  it should "2 box edges are matched, but the polygon's matching edges are not continuous" in {
    combine {
      Polygon(
        (3, 20), (7, 20), (7, 13), (11, 13),
        (11, 20), (15, 20), (15, 11), (13, 11))
    } {
      Box((7, 16), (11, 19))
    } should be(None)
  }

  it should ("not merge, matched by 1 edge, but the formed polygon has a hole") in {
    combine {
      Polygon(
        (10, 14), (16, 14), (16, 4), (3, 4),
        (3, 10), (7, 10), (7, 6), (13, 6),
        (13, 10), (10, 10)
      )
    } {
      Box((7, 8), (10, 10))
    } should be(None)
  }

  it should "2 box edges are matched and the polygon's are continuous, but the formed polygon has a hole" in {
    combine {
      Polygon(
        (10, 14), (16, 14), (16, 4), (3, 4),
        (3, 10), (7, 10), (7, 6), (13, 6),
        (13, 10), (10, 10)
      )
    } {
      Box((7, 6), (10, 10))
    } should be(None)
  }
}
