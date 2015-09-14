package org.jinilover.geometry.coordinate

import org.scalatest.{Matchers, FlatSpec}
import Geometry._

/**
 * Test if the algo can decide not to match the polygon due to no edge matching 
 */
class CannotMergeSpec extends FlatSpec with Matchers {
  it should "not merge 2 polygon as no edge is matched" in {
    merge {
      Polygon((2, 13), (6, 13), (6, 9), (2, 9))
    } {
      Box((6, 5), (12, 9))
    } should be(None)
  }
}
