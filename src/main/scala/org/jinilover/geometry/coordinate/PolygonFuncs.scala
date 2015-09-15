package org.jinilover.geometry.coordinate

import scala.language.implicitConversions
import com.typesafe.scalalogging._
import CommonFuncs._
import EdgeFuncs._

object PolygonFuncs extends LazyLogging {
  val joinBy1or2Matches: JOIN_EDGES =
    pEdges => pMatches => bEdges => bMatches => {
      val (pLead, pTail) = subtractItems(pEdges)(pMatches)
      val bRemains = {
        val remains = bEdges diff bMatches
        shiftItems(remains)(_ == headOfPEdge(remains))
      }
      val (firstPMatch, lastPMatch) = firstLastItem(pMatches)
      val (firstBMatch, lastBMatch) = firstLastItem(bMatches)
      val conn1 = List(Edge(firstPMatch.start, firstBMatch.end))
      val conn2 = List(Edge(lastBMatch.start, lastPMatch.end))
      val edges = connectEdges(List(pLead, conn1, bRemains, conn2, pTail))
      logger.debug(s"after joinBy ${pMatches.size} matching edges, the result is: $edges")
      edges
    }

  val joinByThreeMatches: JOIN_EDGES =
    pEdges => pMatches => bEdges => bMatches => {
      val (pLead, pTail) = subtractItems(pEdges)(pMatches)
      val bRemain = bEdges diff bMatches
      val pPartialMatches = unequalEdges(pMatches)(bMatches)
      val bPartialMatches = unequalEdges(bMatches)(pMatches)
      val (conn1, conn2) = bPartialMatches.size match {
        case 0 => (List.empty[Edge], List.empty[Edge])
        case 1 =>
          val bMatch = bPartialMatches.head
          val pMatch = pPartialMatches.head
          if (bMatches.last == bMatch)
            (List.empty[Edge], List(Edge(bMatch.start, pMatch.end)))
          else
            (List(Edge(pMatch.start, bMatch.end)), List.empty[Edge])
        case 2 =>
          val (bMatchFirst, bMatchLast) = firstLastItem(bPartialMatches)
          val (pMatchFirst, pMatchLast) = firstLastItem(pPartialMatches)
          (List(Edge(pMatchFirst.start, bMatchFirst.end)), List(Edge(bMatchLast.start, pMatchLast.end)))
      }
      val edges = connectEdges(List(pLead, conn1, bRemain, conn2, pTail))
      logger.debug(s"after joinByThreeMatches, the result is: $edges")
      edges
    }

  val joinByFourMatches: JOIN_EDGES =
    pEdges => pMatches => bEdges => bMatches => {
      val (pLead, pTail) = subtractItems(pEdges)(pMatches)
      val pPartialMatches = unequalEdges(pMatches)(bMatches)
      val bPartialMatches = unequalEdges(bMatches)(pMatches)
      logger.debug(
        s"""
           |joinByFourMatches, pPartialMatches:
           |$pPartialMatches
           |bPartialMatches:
           |$bPartialMatches""".stripMargin)
      val connsFromMatches = pPartialMatches.zip(bPartialMatches) map {
        t =>
          val (pEdge, bEdge) = t
          if (pEdge.end == bEdge.start)
            Edge(pEdge.start, bEdge.end)
          else
            Edge(bEdge.start, pEdge.end)
      }
      val edges = connectEdges(List(pLead, connsFromMatches, pTail))
      logger.debug(s"after joinByFourMatches, the result is: $edges")
      edges
    }

  val boxToPolygon: Box => Polygon = {
    box =>
      val Box(tpLeft, btmRight) = box
      Polygon((tpLeft.x, btmRight.y), btmRight, (btmRight.x, tpLeft.y), tpLeft)
  }

  val polygonToEdges: Polygon => EDGES = {
    polygon =>
      val head :: tail = polygon.points.toList
      val (edges, lastPt) = tail.foldLeft((List.empty[Edge], head)) {
        (z, pt) =>
          val (edges, lastPt) = z
          (Edge(lastPt, pt) :: edges, pt)
      }
      (Edge(lastPt, head) :: edges).reverse
  }

  val boxToEdges: Box => EDGES =
    boxToPolygon andThen polygonToEdges

  import scalaz.Functor
  import scalaz.std.option._

  val formPolygon: EDGES => EDGES => EDGES => EDGES => JOIN_EDGES => Option[Polygon] =
    pEdges => pMatches => bEdges => bMatches => joinByMatch =>
      joinByMatch(pEdges)(pMatches)(bEdges)(bMatches) flatMap {
        edges =>
          val composite =
            shiftPEdges andThen combineColinearEdges andThen holeCheck andThen
              Functor[Option].lift((_: EDGES).map(_.start))
          composite(edges) map (Polygon(_: _*))
      }

  val combine: Polygon => Box => Option[Polygon] =
    polygon => box => {
      val pEdges = polygonToEdges(polygon)
      val bEdges = boxToEdges(box)
      overlapEdges(pEdges)(bEdges) flatMap {
        t =>
          val (pMatches, bMatches) = t
          lazy val toPolygon = formPolygon(pEdges)(pMatches)(bEdges)(bMatches)
          bMatches.size match {
            case 1 => toPolygon(joinBy1or2Matches)
            case 2 => toPolygon(joinBy1or2Matches)
            case 3 => toPolygon(joinByThreeMatches)
            case 4 => toPolygon(joinByFourMatches)
            case _ => None // won't happen, just eliminate non-exhaustive warning
          }
      }
    }
}
