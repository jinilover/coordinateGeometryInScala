package org.jinilover.geometry.coordinate

import org.scalatest.{Matchers, FlatSpec}
import GeometryFuncs._

class SubtractPolygonsSpec extends FlatSpec with Matchers {
  it should "testing " in {
    subtract {
      Box((3, 10), (13, 20))
    }(
        Polygon((3, 14), (7, 14), (7, 11), (3, 11)),
        Polygon((10, 18), (13, 18), (13, 15), (10, 15))
      )
  }
}
