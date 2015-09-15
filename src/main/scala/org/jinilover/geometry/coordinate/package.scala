package org.jinilover.geometry

package object coordinate {
  type POINTS = List[Point]
  type EDGES = List[Edge]
  type JOIN_EDGES = EDGES => EDGES => EDGES => EDGES => Option[EDGES]

  implicit def toPoint(tuple: (Int, Int)): Point =
    Point(tuple._1, tuple._2)

}
