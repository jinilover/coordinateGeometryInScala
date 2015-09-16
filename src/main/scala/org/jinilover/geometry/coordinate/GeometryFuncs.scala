package org.jinilover.geometry.coordinate

import PolygonFuncs._

object GeometryFuncs {
  def unite(boxes: Box*): List[Polygon] = {
    import scalaz.std.option.optionLast
    import scalaz.std.list._
    import scalaz.std.tuple._
    import scalaz.syntax.monoid._
    import scalaz.LastOption
    import scalaz.Tags._

    lazy val combineBoxesToPolygon: BOXES => Polygon => POLYGONS =
      origBoxes => origPoly => {
        val (newPoly, remainedBoxes) = origBoxes.foldLeft(
          (Last(Some(origPoly)): LastOption[Polygon], mzero[BOXES])
        ) {
          (z, box) =>
            z |+| Last.unwrap(z._1).flatMap(combine(_)(box)).fold {
              (z._1, List(box))
            } { 
              p => (Last(Some(p)), Nil)
            }
        }

        if (origBoxes == remainedBoxes)
          origPoly :: unite(remainedBoxes: _*)
        else
          Last.unwrap(newPoly).map {
            combineBoxesToPolygon(remainedBoxes
            )
          }.toList.flatten
      }

    boxes.toList match {
      case b :: bs => combineBoxesToPolygon(bs)(boxToPolygon(b))
      case _ => Nil
    }
  }
}
