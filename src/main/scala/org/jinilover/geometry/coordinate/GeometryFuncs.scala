package org.jinilover.geometry.coordinate

import PolygonFuncs._
import  EdgeFuncs._

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
            combineBoxesToPolygon(remainedBoxes)
          }.toList.flatten
      }

    boxes.toList match {
      case b :: bs => combineBoxesToPolygon(bs)(b)
      case _ => Nil
    }
  }

  def subtract(box: Box)(polySeq: Polygon*): Option[Polygon] = {
    val polys = polySeq.toList
    polys match {
      case Nil => Some(box)
      case _ =>
        // bConns are box's fragmented edges after subtracting the portion that matches with polygon edges
        // similarly, pConns are the polygons' fragmented edges
        val (bConns, pConns) = polys.foldLeft(polygonToEdges(box), List.empty[Edge]) {
          (z1, p) => polygonToEdges(p).foldLeft(z1) {
            (z2, pEdge) =>
              val (bRemains, pRemains) = z2
              bRemains filter edgesOverlapped(pEdge) match {
                case bMatch :: _ => ???
                case _ => (bRemains, pEdge :: pRemains)
              }
          }
        }
        
        bConns ++ pConns match {
          case Nil => None
          case es => 
            val composite = 
              rearrangeOutOfOrderEdges andThen combineColinearEdges andThen (_.map(_.start)) andThen (pts => Polygon(pts: _*))
            Some(composite(es))
        }
    }
  }
}
