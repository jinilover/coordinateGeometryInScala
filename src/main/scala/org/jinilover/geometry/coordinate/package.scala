package org.jinilover.geometry

package object coordinate {
  type POINTS = List[Point]
  type BOXES = List[Box]
  type EDGES = List[Edge]
  type POLYGONS = List[Polygon]
  type JOIN_EDGES = EDGES => EDGES => EDGES => EDGES => (List[EDGES], String)

  implicit def toPoint(tuple: (Int, Int)): Point =
    Point(tuple._1, tuple._2)

}
