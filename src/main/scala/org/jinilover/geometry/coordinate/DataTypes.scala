package org.jinilover.geometry.coordinate

case class Point(x: Int, y: Int)

case class Edge(start: Point, end: Point)

case class Box(tpLeft: Point, btmRight: Point)

case class Polygon(points: Point*)

sealed trait EdgeOrientation

case object H extends EdgeOrientation

case object V extends EdgeOrientation
