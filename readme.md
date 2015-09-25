#Coordinate geometry problem
This is a common coordinate geometry problem be solved in publishing.  The purpose is to find the co-ordinates of a polygon's vertices.

![Alt text](https://github.com/jinilover/images/blob/master/Polygon1.png)

E.g. in the above diagram, it aims to find the co-ordinates of the 6 vertices of the yellow region.  The algorithm is given the outermost rectangle topLeft/bottomRight coordinates and the 8 rectangle topLeft/bottomRight coordinates.  The numbers represent the input order the blue rectangles provided to the algorithm.  This implies the sequence of the rectangles is provided in a random order.  The algorithm needs to "subtract" the blue rectangles from the outermost rectangle to calculate the 6 vertices of the yellow region.

Here is another example.  The algorithm needs to find the co-ordinates of the 10 vertices of the yellow polygon.

![Alt text](https://github.com/jinilover/images/blob/master/Polygon2.png)  

There can be more than one polygon remained after subtraction.  In the following diagram, it calculates the 4 vertices of the 4 yellow polygons.

![Alt text](https://github.com/jinilover/images/blob/master/Polygon3.png)  

##Assumptions:
* The blue rectangles do not form a polygon having a hole.
* The blue rectangles are inside the outermost rectangle.
* Similar to the blue rectangles, the yellow polygon(s) does/do not have a hole.
* There is no overlapping between the blue rectangles

##Coordinates setting
* The top left coordinates is (0, 0).  
* The polygon vertices are presented in a list of (x, y), counter clockwise manner.  The first vertice is the left end of the bottom horizontal line.  E.g. in the first diagram, the yellow region vertices are (3, 11), (17, 11), (17, 3), (0, 3), (0, 9), (3, 9).
* A rectangle vertices are presented by topLeft and bottomRight corner.  E.g. in the first diagram, the blue rectangle #3 vertices are presented as (0, 0), (17, 3).

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
res0: List[org.jinilover.geometry.coordinate.Polygon] = List(Polygon(List(Point(3,11), Point(17,11), Point(17,3), Point(0,3), Point(0,9), Point(3,9))))
```

To simulate the diagram 2, under the REPL, type the following command
```Scala
scala> val page = Box((0, 0), (17, 19))
page: org.jinilover.geometry.coordinate.Box = Box(Point(0,0),Point(17,19))

scala> val boxes = List(
     |         Box((15, 7), (16, 19)), Box((0, 0), (17, 3)),
     |         Box((4, 9), (8, 19)), Box((0, 9), (4, 14)),
     |         Box((8, 7), (11, 19)), Box((16, 3), (17, 19)),
     |         Box((11, 10), (15, 19)), Box((0, 14), (4, 19))
     |       )
boxes: List[org.jinilover.geometry.coordinate.Box] = List(Box(Point(15,7),Point(16,19)), Box(Point(0,0),Point(17,3)), Box(Point(4,9),Point(8,19)), Box(Point(0,9),Point(4,14)), Box(Point(8,7),Point(11,19)), Box(Point(16,3),Point(17,19)), Box(Point(11,10),Point(15,19)), Box(Point(0,14),Point(4,19)))

scala> calculateRemainedSpace(page)(boxes: _*)
res1: List[org.jinilover.geometry.coordinate.Polygon] = List(Polygon(List(Point(11,10), Point(15,10), Point(15,7), Point(16,7), Point(16,3), Point(0,3), Point(0,9), Point(8,9), Point(8,7), Point(11,7))))
```

To simulate the diagram 3, under the REPL, type the following command
```Scala
scala> val page = Box((3, 10), (13, 20))
page: org.jinilover.geometry.coordinate.Box = Box(Point(3,10),Point(13,20))

scala> val boxes = List(
     |         Box((7, 10), (9, 14)), Box((3, 14), (7, 16)),
     |         Box((7, 14), (9, 16)), Box((9, 14), (13, 16)),
     |         Box((7, 16), (9, 20))
     |       )
boxes: List[org.jinilover.geometry.coordinate.Box] = List(Box(Point(7,10),Point(9,14)), Box(Point(3,14),Point(7,16)), Box(Point(7,14),Point(9,16)), Box(Point(9,14),Point(13,16)), Box(Point(7,16),Point(9,20)))

scala> calculateRemainedSpace(page)(boxes: _*)
res2: List[org.jinilover.geometry.coordinate.Polygon] = List(Polygon(List(Point(3,14), Point(7,14), Point(7,10), Point(3,10))), Polygon(List(Point(3,20), Point(7,20), Point(7,16), Point(3,16))), Polygon(List(Point(9,14), Point(13,14), Point(13,10), Point(9,10))), Polygon(List(Point(9,20), Point(13,20), Point(13,16), Point(9,16))))
```
There are 4 polygons found in the result list.

Similar tests are performed in https://github.com/jinilover/coordinateGeometryInScala/blob/master/src/test/scala/org/jinilover/geometry/coordinate/CalculateReminedSpaceSpec.scala

##Analysis, development and testing
This problem can be solved by using different programming paradigms.  Adopting FP makes the application simpler, more elegant and easier to be maintained.  This minimizes the time in implementation.  Most of the time is spent on analyzing the problem and writing test cases.  In the implementation, essential FP features are used such as higher-order functions, functional composition and Monad.  Scalaz is used in a few places to save the time from writing some functional patterns from scratch.
