#Coordinate geometry problem
This is a common coordinate geometry problem be solved in publishing.  The purpose is to find the co-ordinates of a polygon's vertices.

![Alt text](https://github.com/jinilover/images/blob/master/Polygon1.png)

E.g. in the above diagram, it aims to find the co-ordinates of the 6 vertices of the yellow polygon.  The algorithm is given the topLeft/bottomRight coordinates of the outermost rectangle and the topLeft/bottomRight coordinates of the 8 rectangles.  The numbers represent the input order the blue rectangles provided to the algorithm.  This implies the sequence of the rectangles is provided in a random order.  The algorithm needs to "subtract" the blue rectangles from the outermost rectangle to calculate the 6 vertices of the yellow polygon.

Here is another example.  

![Alt text](https://github.com/jinilover/images/blob/master/Polygon2.png)  

This time the algorithm needs to find the co-ordinates of the 10 vertices of the yellow polygon.

Assumptions:
* The blue rectangles do not form a polygon having a hole.
* After subtracting the blue rectangles, there should be either no remained space or a single polygon.
* There is overlapping between the blue rectangles

TODO: add more information about the basic ideas, how to use the api to calculate the space ...