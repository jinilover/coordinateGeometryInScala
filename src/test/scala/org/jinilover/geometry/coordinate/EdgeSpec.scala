package org.jinilover.geometry.coordinate

import org.scalatest.{Matchers, FlatSpec}
import PolygonFuncs._
import EdgeFuncs._
import CommonFuncs._


class EdgeSpec  extends FlatSpec with Matchers {
  it should "calculate a horizontal edge length correctly" in {
    val pt1 = Point(2, 13)
    val pt2 = Point(6, 13)
    edgeLen(Edge(pt1, pt2)) should be(4)
    edgeLen(Edge(pt2, pt1)) should be(4)
  }

  it should "calculate a vertical edge length correctly" in {
    val pt1 = Point(13, 2)
    val pt2 = Point(13, 6)
    edgeLen(Edge(pt1, pt2)) should be(4)
    edgeLen(Edge(pt2, pt1)) should be(4)
  }

  it should "deduce a box's 4 edges correctly" in {
    polygonToEdges {
      Box((2, 9), (6, 13))
    } should be {
      List(
        Edge((2, 13), (6, 13)),
        Edge((6, 13), (6, 9)),
        Edge((6, 9), (2, 9)),
        Edge((2, 9), (2, 13))
      )
    }
  }

  it should "deduce a polygon's edges correctly" in {
    polygonToEdges {
      Polygon(
        (2, 13), (6, 13), (6, 10), (12, 10), (12, 6), (6, 6), (6, 9), (2, 9)
      )
    } should be {
      List(
        Edge((2, 13), (6, 13)),
        Edge((6, 13), (6, 10)),
        Edge((6, 10), (12, 10)),
        Edge((12, 10), (12, 6)),
        Edge((12, 6), (6, 6)),
        Edge((6, 6), (6, 9)),
        Edge((6, 9), (2, 9)),
        Edge((2, 9), (2, 13))
      )
    }
  }

  it should "deduce range overlappe correctly" in {
    rangesOverlapped((9, 13), (6, 10)) should be(true)
    rangesOverlapped((6, 10), (9, 13)) should be(true)
    rangesOverlapped((6, 10), (6, 10)) should be(true)
    rangesOverlapped((6, 10), (5, 13)) should be(true)
    rangesOverlapped((9, 13), (5, 9)) should be(false)
    rangesOverlapped((9, 13), (5, 8)) should be(false)
  }

  it should "determine if edge overlapped" in {
    edgesOverlapped(Edge((6, 13), (6, 9)))(Edge((6, 6), (6, 10))) should be(true)
    edgesOverlapped(Edge((6, 13), (6, 9)))(Edge((6, 10), (6, 6))) should be(true)
    edgesOverlapped(Edge((6, 13), (6, 9)))(Edge((6, 5), (6, 9))) should be(false)
    edgesOverlapped(Edge((2, 8), (6, 8)))(Edge((6, 5), (6, 9))) should be(false)
  }

  it should "deduce 1 overlapping edges a polygon and a box" in {
    overlapEdges {
      List(
        Edge((2, 13), (6, 13)),
        Edge((6, 13), (6, 9)),
        Edge((6, 9), (2, 9)),
        Edge((2, 9), (2, 13))
      )
    } {
      List(
        Edge((6, 10), (12, 10)),
        Edge((12, 10), (12, 6)),
        Edge((12, 6), (6, 6)),
        Edge((6, 6), (6, 10))
      )
    } should be {
      Some((
        List(Edge((6, 13), (6, 9))),
        List(Edge((6, 6), (6, 10)))
        ))
    }

    overlapEdges {
      List(
        Edge((3, 10), (6, 10)),
        Edge((6, 10), (6, 8)),
        Edge((6, 8), (9, 8)),
        Edge((9, 8), (9, 10)),
        Edge((9, 10), (12, 10)),
        Edge((12, 10), (12, 4)),
        Edge((12, 4), (3, 4)),
        Edge((3, 4), (3, 10))
      )
    } {
      List(
        Edge((6, 13), (12, 13)),
        Edge((12, 13), (12, 10)),
        Edge((12, 10), (6, 10)),
        Edge((6, 10), (6, 13))
      )
    } should be {
      Some((
        List(Edge((9, 10), (12, 10))),
        List(Edge((12, 10), (6, 10)))
        ))
    }
  }

  it should "deduce 2 overlapping edges a polygon and a box" in {
    overlapEdges {
      List(
        Edge((3, 17), (9, 17)),
        Edge((9, 17), (9, 14)),
        Edge((9, 14), (6, 14)),
        Edge((6, 14), (6, 11)),
        Edge((6, 11), (3, 11)),
        Edge((3, 11), (3, 17))
      )
    } {
      List(
        Edge((6, 14), (10, 14)),
        Edge((10, 14), (10, 10)),
        Edge((10, 10), (6, 10)),
        Edge((6, 10), (6, 14))
      )
    } should be {
      Some((
        List(Edge((9, 14), (6, 14)), Edge((6, 14), (6, 11))),
        List(Edge((6, 14), (10, 14)), Edge((6, 10), (6, 14)))
        ))
    }

//    var polyEdges = Vector(Edge(Point(9 pts, 14 pts), Point(6 pts, 14 pts)), Edge(Point(6 pts, 14 pts), Point(6 pts, 11 pts)))
//    var boxEdges = Vector(Edge(Point(6 pts, 14 pts), Point(10 pts, 14 pts)), Edge(Point(6 pts, 10 pts), Point(6 pts, 14 pts)))
//    Edge.makeEdgesContinuous(polyEdges) should be(Some(polyEdges))
//    Edge.inMatchOrder(polyEdges)(boxEdges) should be(boxEdges)
//
    overlapEdges {
      List(
        Edge((10, 14), (16, 14)),
        Edge((16, 14), (16, 4)),
        Edge((16, 4), (3, 4)),
        Edge((3, 4), (3, 10)),
        Edge((3, 10), (7, 10)),
        Edge((7, 10), (7, 6)),
        Edge((7, 6), (13, 6)),
        Edge((13, 6), (13, 10)),
        Edge((13, 10), (10, 10)),
        Edge((10, 10), (10, 14))
      )
    } {
      List(
        Edge((7, 10), (10, 10)),
        Edge((10, 10), (10, 6)),
        Edge((10, 6), (7, 6)),
        Edge((7, 6), (7, 10))
      )
    } should be {
      Some((
        List(Edge((7, 10), (7, 6)), Edge((7, 6), (13, 6))),
        List(Edge((7, 6), (7, 10)), Edge((10, 6), (7, 6)))
        ))
    }

//    polyEdges = Vector(Edge(Point(7 pts, 10 pts), Point(7 pts, 6 pts)), Edge(Point(7 pts, 6 pts), Point(13 pts, 6 pts)))
//    boxEdges = Vector(Edge(Point(7 pts, 6 pts), Point(7 pts, 10 pts)), Edge(Point(10 pts, 6 pts), Point(7 pts, 6 pts)))
//    Edge.makeEdgesContinuous(polyEdges) should be(Some(polyEdges))
//    Edge.inMatchOrder(polyEdges)(boxEdges) should be(boxEdges)
  }

  it should "deduce 3 overlapping polyEdges a polygon and a box" in {
    overlapEdges {
      List(
        Edge((3, 10), (6, 10)),
        Edge((6, 10), (6, 8)),
        Edge((6, 8), (10, 8)),
        Edge((10, 8), (10, 10)),
        Edge((10, 10), (14, 10)),
        Edge((14, 10), (14, 5)),
        Edge((14, 5), (3, 5)),
        Edge((3, 5), (3, 10))
      )
    } {
      List(
        Edge((6, 12), (10, 12)),
        Edge((10, 12), (10, 8)),
        Edge((10, 8), (6, 8)),
        Edge((6, 8), (6, 12))
      )
    } should be {
      Some((
        List(Edge((6, 10), (6, 8)), Edge((6, 8), (10, 8)), Edge((10, 8), (10, 10))),
        List(Edge((6, 8), (6, 12)), Edge((10, 8), (6, 8)), Edge((10, 12), (10, 8)))
        ))
    }

//    var polyEdges = Vector(Edge(Point(6 pts, 10 pts), Point(6 pts, 8 pts)), Edge(Point(6 pts, 8 pts), Point(10 pts, 8 pts)), Edge(Point(10 pts, 8 pts), Point(10 pts, 10 pts)))
//    var boxEdges = Vector(Edge(Point(6 pts, 8 pts), Point(6 pts, 12 pts)), Edge(Point(10 pts, 8 pts), Point(6 pts, 8 pts)), Edge(Point(10 pts, 12 pts), Point(10 pts, 8 pts)))
//    Edge.makeEdgesContinuous(polyEdges) should be(Some(polyEdges))
//    Edge.inMatchOrder(polyEdges)(boxEdges) should be(boxEdges)

    overlapEdges {
      List(
        Edge((3, 20), (7, 20)),
        Edge((7, 20), (7, 13)),
        Edge((7, 13), (11, 13)),
        Edge((11, 13), (11, 20)),
        Edge((11, 20), (15, 20)),
        Edge((15, 20), (15, 11)),
        Edge((15, 11), (3, 11)),
        Edge((3, 11), (3, 20))
      )
    } {
      List(
        Edge((7, 17), (11, 17)),
        Edge((11, 17), (11, 13)),
        Edge((11, 13), (7, 13)),
        Edge((7, 13), (7, 17))
      )
    } should be {
      Some((
        List(Edge((7, 20), (7, 13)), Edge((7, 13), (11, 13)), Edge((11, 13), (11, 20))),
        List(Edge((7, 13), (7, 17)), Edge((11, 13), (7, 13)), Edge((11, 17), (11, 13)))
        ))
    }

//    polyEdges = Vector(Edge(Point(7 pts, 20 pts), Point(7 pts, 13 pts)), Edge(Point(7 pts, 13 pts), Point(11 pts, 13 pts)), Edge(Point(11 pts, 13 pts), Point(11 pts, 20 pts)))
//    boxEdges = Vector(Edge(Point(7 pts, 13 pts), Point(7 pts, 17 pts)), Edge(Point(11 pts, 13 pts), Point(7 pts, 13 pts)), Edge(Point(11 pts, 17 pts), Point(11 pts, 13 pts)))
//    Edge.makeEdgesContinuous(polyEdges) should be(Some(polyEdges))
//    Edge.inMatchOrder(polyEdges)(boxEdges) should be(boxEdges)
  }

  it should "deduce 4 overlapping polyEdges a polygon and a box" in {
    overlapEdges {
      List(
        Edge((8, 14), (18, 14)),
        Edge((18, 14), (18, 3)),
        Edge((18, 3), (3, 3)),
        Edge((3, 3), (3, 10)),
        Edge((3, 10), (6, 10)),
        Edge((6, 10), (6, 6)),
        Edge((6, 6), (14, 6)),
        Edge((14, 6), (14, 10)),
        Edge((14, 10), (8, 10)),
        Edge((8, 10), (8, 14))
      )
    } {
      List(
        Edge((6, 10), (14, 10)),
        Edge((14, 10), (14, 6)),
        Edge((14, 6), (6, 6)),
        Edge((6, 6), (6, 10))
      )
    } should be {
      Some((
        List(Edge((6, 10), (6, 6)), Edge((6, 6), (14, 6)), Edge((14, 6), (14, 10)), Edge((14, 10), (8, 10))),
        List(Edge((6, 6), (6, 10)), Edge((14, 6), (6, 6)), Edge((14, 10), (14, 6)), Edge((6, 10), (14, 10)))
        ))
    }

//    var polyEdges = Vector(Edge(Point(6 pts, 10 pts), Point(6 pts, 6 pts)), Edge(Point(6 pts, 6 pts), Point(14 pts, 6 pts)), Edge(Point(14 pts, 6 pts), Point(14 pts, 10 pts)), Edge(Point(14 pts, 10 pts), Point(8 pts, 10 pts)))
//    var boxEdges = Vector(Edge(Point(6 pts, 6 pts), Point(6 pts, 10 pts)), Edge(Point(14 pts, 6 pts), Point(6 pts, 6 pts)), Edge(Point(14 pts, 10 pts), Point(14 pts, 6 pts)), Edge(Point(6 pts, 10 pts), Point(14 pts, 10 pts)))
//    Edge.makeEdgesContinuous(polyEdges) should be(Some(polyEdges))
//    Edge.inMatchOrder(polyEdges)(boxEdges) should be(boxEdges)
  }

  it should "deduce no overlapping polyEdges between a polygon and a box" in {
    overlapEdges {
      List(
        Edge((2, 13), (6, 13)),
        Edge((6, 13), (6, 9)),
        Edge((6, 9), (2, 9)),
        Edge((2, 9), (2, 13))
      )
    } {
      List(
        Edge((6, 9), (12, 9)),
        Edge((12, 9), (12, 5)),
        Edge((12, 5), (6, 5)),
        Edge((6, 5), (6, 9))
      )
    } should be(None)
  }

  it should "deduce invalid overlapping polyEdges (due to incontinuous) between a polygon and a box" in {
    overlapEdges {
      List(
        Edge((3, 20), (7, 20)),
        Edge((7, 20), (7, 13)),
        Edge((7, 13), (11, 13)),
        Edge((11, 13), (11, 20)),
        Edge((11, 20), (15, 20)),
        Edge((15, 20), (15, 11)),
        Edge((15, 11), (3, 11)),
        Edge((3, 11), (3, 20))
      )
    } {
      List(
        Edge((7, 19), (11, 19)),
        Edge((11, 19), (11, 16)),
        Edge((11, 16), (7, 16)),
        Edge((7, 16), (7, 19))
      )
    } should be(None)

    overlapEdges {
      List(
        Edge((3, 20), (11, 20)),
        Edge((11, 20), (11, 18)),
        Edge((11, 18), (5, 18)),
        Edge((5, 18), (5, 11)),
        Edge((5, 11), (13, 11)),
        Edge((13, 11), (13, 14)),
        Edge((13, 14), (11, 4)),
        Edge((11, 4), (11, 16)),
        Edge((11, 16), (14, 16)),
        Edge((14, 16), (14, 9)),
        Edge((14, 9), (3, 9)),
        Edge((3, 9), (3, 20))
      )
    } {
      List(
        Edge((5, 18), (11, 18)),
        Edge((11, 18), (11, 11)),
        Edge((11, 11), (5, 11)),
        Edge((5, 11), (5, 18))
      )
    } should be(None)

    overlapEdges {
      List(
        Edge((7, 21), (12, 21)),
        Edge((12, 21), (12, 11)),
        Edge((12, 11), (7, 11)),
        Edge((7, 11), (7, 13)),
        Edge((7, 13), (10, 13)),
        Edge((10, 13), (10, 15)),
        Edge((10, 15), (7, 15)),
        Edge((7, 15), (7, 17)),
        Edge((7, 17), (10, 17)),
        Edge((10, 17), (10, 19)),
        Edge((10, 19), (7, 19)),
        Edge((7, 19), (7, 21))
      )
    } {
      List(
        Edge((3, 20), (7, 20)),
        Edge((7, 20), (7, 12)),
        Edge((7, 12), (3, 12)),
        Edge((3, 12), (3, 20))
      )
    } should be(None)

    overlapEdges {
      List(
        Edge((8, 14), (18, 14)),
        Edge((18, 14), (18, 3)),
        Edge((18, 3), (3, 3)),
        Edge((3, 3), (3, 10)),
        Edge((3, 10), (6, 10)),
        Edge((6, 10), (6, 6)),
        Edge((6, 6), (14, 6)),
        Edge((14, 6), (14, 10)),
        Edge((14, 10), (8, 10)),
        Edge((8, 10), (8, 14))
      )
    } {
      List(
        Edge((6, 13), (8, 13)),
        Edge((8, 13), (8, 6)),
        Edge((8, 6), (6, 6)),
        Edge((6, 6), (6, 13))
      )
    } should be(None)
  }

}
