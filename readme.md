#Coordinate geometry problem
This is a common coordinate geometry problem be solved in publishing.  The purpose is to find the co-ordinates of a polygon's vertices.

![Alt text](https://github.com/jinilover/images/blob/master/Polygon1.png)

E.g. in the above diagram, it aims to find the co-ordinates of the 6 vertices of the yellow region.  The algorithm is given the outermost rectangle topLeft/bottomRight coordinates and the 8 rectangle topLeft/bottomRight coordinates.  The numbers represent the input order the blue rectangles provided to the algorithm.  This implies the sequence of the rectangles is provided in a random order.  The algorithm needs to "subtract" the blue rectangles from the outermost rectangle to calculate the 6 vertices of the yellow region.

Here is another example.  

![Alt text](https://github.com/jinilover/images/blob/master/Polygon2.png)  

This time the algorithm needs to find the co-ordinates of the 10 vertices of the yellow polygon.

Assumptions:
* The blue rectangles do not form a polygon having a hole.
* After subtracting the blue rectangles, there should be either no remained space or a single polygon.
* There is no overlapping between the blue rectangles

##Coordinates setting
* The top left coordinates is (0, 0).  
* The polygon vertices are presented in a list of (x, y), counter clock wise mannger.  The first vertice is the left (starting) point of the lowest horizontal line.  E.g. in the first diagram, the yellow region vertices are (3, 11), (17, 11), (17, 3), (0, 3), (0, 9), (3, 9).
* A rectangle vertices are presented by topLeft and bottomRight corner.  E.g. in the first diagram, the blue rectangle 3 topLeft/bottomRight are (0, 0), (17, 3).

##API usage
To simulate the diagram 1, under the REPL, type the following command
```Scala
scala> import org.jinilover.geometry.coordinate._
import org.jinilover.geometry.coordinate._

scala> import org.jinilover.geometry.coordinate.GeometryFuncs._
import org.jinilover.geometry.coordinate.GeometryFuncs._

scala> val page = Box((0, 0), (17, 19))
page: org.jinilover.geometry.coordinate.Box = Box(Point(0,0),Point(17,19))

scala> val boxes = List(
     |         Box((6, 11), (9, 19)), Box((15, 11), (17, 19)),
     |         Box((0, 0), (17, 3)), Box((0, 9), (3, 14)),
     |         Box((13, 11), (15, 19)), Box((9, 11), (13, 19)),
     |         Box((3, 11), (6, 19)), Box((0, 14), (3, 19))
     |       )
boxes: List[org.jinilover.geometry.coordinate.Box] = List(Box(Point(6,11),Point(9,19)), Box(Point(15,11),Point(17,19)), Box(Point(0,0),Point(17,3)), Box(Point(0,9),Point(3,14)), Box(Point(13,11),Point(15,19)), Box(Point(9,11),Point(13,19)), Box(Point(3,11),Point(6,19)), Box(Point(0,14),Point(3,19)))

scala> calculateRemainedSpace(page)(boxes: _*)
res0: Option[org.jinilover.geometry.coordinate.Polygon] = Some(Polygon(List(Point(3,11), Point(17,11), Point(17,3), Point(0,3), Point(0,9), Point(3,9))))

```
To simulate the diagram 1, under the REPL, type the following command
```Scala
scala> val boxes = List(
     |         Box((15, 7), (16, 19)), Box((0, 0), (17, 3)),
     |         Box((4, 9), (8, 19)), Box((0, 9), (4, 14)),
     |         Box((8, 7), (11, 19)), Box((16, 3), (17, 19)),
     |         Box((11, 10), (15, 19)), Box((0, 14), (4, 19))
     |       )
boxes: List[org.jinilover.geometry.coordinate.Box] = List(Box(Point(15,7),Point(16,19)), Box(Point(0,0),Point(17,3)), Box(Point(4,9),Point(8,19)), Box(Point(0,9),Point(4,14)), Box(Point(8,7),Point(11,19)), Box(Point(16,3),Point(17,19)), Box(Point(11,10),Point(15,19)), Box(Point(0,14),Point(4,19)))

scala> calculateRemainedSpace(page)(boxes: _*)
res1: Option[org.jinilover.geometry.coordinate.Polygon] = Some(Polygon(List(Point(11,10), Point(15,10), Point(15,7), Point(16,7), Point(16,3), Point(0,3), Point(0,9), Point(8,9), Point(8,7), Point(11,7))))
```
Similar tests are performed in https://github.com/jinilover/coordinateGeometryInScala/blob/master/src/test/scala/org/jinilover/geometry/coordinate/CalculateReminedSpaceSpec.scala

##Mathematical analysis, development and testing
Most of the time is spent on the graph book drawing the edges and polygon and writing test cases.  Mathemtical problem is a good domain for applying FP.  Since FP is used, the application is written in simpler and elegant way than using other programming paradigm.
