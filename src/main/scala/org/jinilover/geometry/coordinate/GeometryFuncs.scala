package org.jinilover.geometry.coordinate

import PolygonFuncs._
import EdgeFuncs._
import com.typesafe.scalalogging.LazyLogging

object GeometryFuncs extends LazyLogging {
  def unite(boxes: Box*): POLYGONS = {
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

  def subtract(box: Box)(polySeq: Polygon*): POLYGONS = {
    val polys = polySeq.toList
    polys match {
      case Nil => Nil
      case _ =>
        // bConns are box's fragmented edges after subtracting the portion that matches with polygon edges
        // pConns are the polygons not matching with any box edge
        val (bConns, pConns) = polys.foldLeft(polygonToEdges(box), List.empty[Edge]) {
          (z1, p) => polygonToEdges(p).foldLeft(z1) {
            (z2, pEdge) =>
              val (bRemains, pRemains) = z2
              bRemains filter edgesOverlapped(pEdge) match {
                case bMatch :: _ =>
                  (cutEdges(bMatch)(pEdge) ++ bRemains.filterNot(_ == bMatch), pRemains)
                case _ =>
                  (bRemains, pEdge :: pRemains)
              }
          }
        }

        import scalaz.Kleisli._
        import scalaz.std.list._
        bConns ++ pConns match {
          case Nil => Nil
          case es =>
            val composite =
              kleisli(rearrangeOutOfOrderEdges(bConns)) map (shiftOrderedEdges andThen edgesToStartPts andThen createPolygon)
            composite run es
        }
    }
  }


  /**
   * Entry point to this coordinate geometry application
   * Calculate the coordinate of the remaining space by subtracting boxes from bigBox
   */
  def calculateRemainedSpace(bigBox: Box)(boxes: Box*): POLYGONS =
    subtract(bigBox)(unite(boxes: _*): _*)

}
