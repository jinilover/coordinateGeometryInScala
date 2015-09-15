package org.jinilover.geometry.coordinate

import com.typesafe.scalalogging.LazyLogging

import scala.math.abs
import CommonFuncs._

object EdgeFuncs extends LazyLogging {
  val orient: Edge => EdgeOrientation =
    edge => if (edge.start.x == edge.end.x) V else H

  val sameOrient: Edge => Edge => Boolean =
    e1 => e2 => orient(e1) == orient(e2)

  val edgeLen: Edge => Int = {
    edge =>
      val calculate: (Point => Int) => Int = coordVal => abs(coordVal(edge.start) - coordVal(edge.end))
      orientationDependent(edge)(calculate(_.x))(calculate(_.y))
  }

  val edgesOverlapped: Edge => Edge => Boolean = {
    e1 => e2 =>
      lazy val matched: (Int, Int) => ((Int, Int), (Int, Int)) => Boolean =
        (orthogon1, orthogon2) => (r1, r2) => orthogon1 == orthogon2 && rangesOverlapped(r1, r2)
      if (sameOrient(e1)(e2))
        orientationDependent(e1) {
          matched(e1.start.y, e2.start.y)(edgeXs(e1), edgeXs(e2))
        } {
          matched(e1.start.x, e2.start.x)(edgeYs(e1), edgeYs(e2))
        }
      else
        false
  }

  val edgeYs: Edge => (Int, Int) = e => (e.start.y, e.end.y)

  val edgeXs: Edge => (Int, Int) = e => (e.start.x, e.end.x)

  /**
   * same edges if they have the same start/end even they point to opposite direction
   */
  val sameEdges: Edge => Edge => Boolean =
    e1 => e2 =>
      (e1, e2) match {
        case (Edge(start1, end1), Edge(start2, end2)) =>
          start1 == start2 && end1 == end2 || start1 == end2 && end1 == start2
      }

  /**
   * from es1, return those edges which are not equal (start/end swapped are equal) to any of es2
   */
  val unequalEdges: EDGES => EDGES => EDGES =
    es1 => es2 => es1.filter(e1 => es2.forall(e2 => !sameEdges(e1)(e2)))

  /**
   * join 2 edges, return None if they can't be join, return Some(List[EdgeFP]) where
   * the list size is 1 if these 2 edges are colinear
   */
  val join2Edges: Edge => Edge => Option[EDGES] =
    e1 => e2 =>
      (e1, e2) match {
        case _ if e1.end != e2.start => None
        case _ if e1.start == e1.end => Some(List(e2))
        case _ if e2.start == e2.end => Some(List(e1))
        case _ => Some(List(e1, e2))
      }

  val appendEdge: EDGES => Edge => Option[EDGES] =
    edges => edge =>
      edges match {
        case es :+ e => join2Edges(e)(edge) map (es ++ _)
        case _ => Some(List(edge))
      }

  val appendEdges: EDGES => EDGES => Option[EDGES] =
    es1 => es2 =>
      es2.foldLeft[Option[EDGES]](Some(es1)) {
        (z, e) => z.flatMap(appendEdge(_)(e))
      }

  val overlapEdges: EDGES => EDGES => Option[(EDGES, EDGES)] =
    polyEdges => boxEdges =>
      polyEdges.foldLeft[Option[(EDGES, EDGES)]](Some(Nil, Nil)) {
        (z, pEdge) =>
          val bMatches = boxEdges filter (edgesOverlapped(pEdge))
          if (bMatches.isEmpty) z
          else {
            // bMatches.size should be 1 because a polygon edge overlaps with at most 1 box edge
            val bMatch = bMatches.head
            for {
              zVal <- z
              (ps, bs) = zVal
              newPs <- appendEdge(ps)(pEdge)
            } yield (newPs, if (bs contains bMatch) bs else bs :+ bMatch)
          }
      } flatMap {
        tuple =>
          tuple match {
            case (Nil, Nil) => None
            case _ => Some(tuple)
          }
      }

  /**
   * the given edges, find the only edge whose start cannot connect to other edges
   */
  val headOfPEdge: EDGES => Edge =
    es => es.filter(e => es.forall(_.end != e.start)).head

  /**
   * pEdges is a polygon edges which are joined continuously,
   * but it may not start with the lowest horizontal edges,
   * shift the edges s.t. it starts with the lowest horizontal edges.
   */
  val shiftPEdges: EDGES => EDGES =
    pEdges => {
      val firstEdge = pEdges reduceLeft {
        (e1, e2) =>
          (e1.start, e2.start) match {
            case (pt1, pt2) if pt1.y > pt2.y => e1
            case (pt1, pt2) if pt1.y == pt2.y && pt1.x < pt2.x => e1
            case _ => e2
          }
      }
      val edges = shiftItems(pEdges)(_ == firstEdge)
      logger.debug(s"pEdgesCounterClockWise: firstEdge = $firstEdge, edges = $edges")
      edges
    }

  /**
   * check if the well-formed edges made a "hole"
   */
  val holeCheck: EDGES => Option[EDGES] =
    _.foldRight[Option[EDGES]](Some(List.empty[Edge])) {
      (e, z) =>
        z flatMap {
          zVal =>
            if (zVal exists (edge => edge.start == e.start && !sameEdges(edge)(e)))
              None
            else
              Some(e :: zVal)
        }
    }

  /**
   * combine any 2 colinear edges to 1
   */
  val combineColinearEdges: EDGES => EDGES =
    _.foldRight(List.empty[Edge]) {
      (e, z) =>
        z match {
          case hd :: tl if sameOrient(e)(hd) => Edge(e.start, hd.end) :: tl
          case _ => e :: z
        }
    }

  val connectEdges: List[EDGES] => Option[EDGES] =
    _.foldLeft[Option[EDGES]](Some(List.empty[Edge])) {
      (z, es) => z.flatMap(appendEdges(_)(es))
    }

  def orientationDependent[T](edge: Edge)(h: => T)(v: => T): T =
    orient(edge) match {
      case H => h
      case V => v
    }

}
