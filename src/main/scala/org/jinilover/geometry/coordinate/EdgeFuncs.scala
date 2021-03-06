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
        case es :+ lastEdge => join2Edges(lastEdge)(edge) map (es ++ _)
        case _ => Some(List(edge))
      }

  val appendEdges: EDGES => EDGES => Option[EDGES] =
    es1 => _.foldLeft[Option[EDGES]](Some(es1)) {
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
            } yield (newPs, bs :+ bMatch)
          }
      } flatMap {
        tuple =>
          tuple match {
            case (Nil, Nil) => None
            case (pMatches, bMatches) => Some(pMatches, bMatches.distinct)
            // a box can match with > 1 polygon edge
            // this happen when 3 box edges are equal to the polygon's correspondant
            // and 1 box edge matches with 2 polygon edges
            // and these 5 polygon edges are continuous
          }
      }

  /**
   * the given edges, find the only edge whose start cannot connect to other edges
   */
  val headOfPEdge: EDGES => Edge =
    es => es.filter(e => es.forall(_.end != e.start)).head

  /**
   * pEdges are in order to form a polygon,
   * but it may not start with the lowest horizontal edges,
   * shift the edges s.t. it starts with the lowest horizontal edges.
   */
  val shiftOrderedEdges: EDGES => EDGES =
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
      logger.debug(s"shiftPEdges: firstEdge = $firstEdge, edges = $edges")
      edges
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

  /**
   * The given edges are in order, this function remove any zero length edge 
   */
  val connectEdges: List[EDGES] => Option[EDGES] =
    _.foldLeft[Option[EDGES]](Some(List.empty[Edge])) {
      (z, es) => z.flatMap(appendEdges(_)(es))
    }

  /**
   * Both edges match and in the direction, returns remaining edges after longEdge - shortEdge
   */
  val cutEdges: Edge => Edge => EDGES =
    longEdge => shortEdge =>
      (longEdge, shortEdge) match {
        case _ if sameEdges(longEdge)(shortEdge) => Nil
        case (Edge(longSt, longEd), Edge(shortSt, shortEd)) if longSt == shortSt =>
          List(Edge(shortEd, longEd))
        case (Edge(longSt, longEd), Edge(shortSt, shortEd)) if longEd == shortEd =>
          List(Edge(longSt, shortSt))
        case (Edge(longSt, longEd), Edge(shortSt, shortEd)) =>
          List(Edge(longSt, shortSt), Edge(shortEd, longEd))
      }

  val flipEdge: Edge => Edge = e => Edge(e.end, e.start)

  /**
   * The given edges can form a polygon or more than 1 polygon, 
   * but they are out of order and mixed together if they can form different polygons,
   * separate them which belong to different polygons, within each list, 
   * rearrange them in order
   * firstEdges are es subset, in separating es to different list that form 
   * different polygons, the first edge of each list must exist in firstEdges.
   */
  val rearrangeOutOfOrderEdges: EDGES => EDGES => List[EDGES] =
    firstEdges => es => {
      def recur(inOrder: EDGES)(remains: EDGES): List[EDGES] =
        (inOrder, remains) match {
          case (Nil, Nil) => Nil
          case (_, Nil) => List(inOrder)
          case (last :: (_ :+ first), _) if last.end == first.start =>
            inOrder :: recur(Nil)(remains)
          case (last :: _, _) =>
            val next = remains.filter {
              e => e.start == last.end || e.end == last.end
            }.head
            val newInOrder = (if (next.start == last.end) next else flipEdge(next)) :: inOrder
            recur(newInOrder)(remains filterNot (_ == next))
          case _ =>
            val firstEdge = remains.filter(r => firstEdges exists (_ == r)).head
            recur(List(firstEdge))(remains filterNot (_ == firstEdge))
        }

      recur(Nil)(es) map (_.reverse)
    }

  val edgesToStartPts: EDGES => POINTS = _ map (_.start)

  def orientationDependent[T](edge: Edge)(h: => T)(v: => T): T =
    orient(edge) match {
      case H => h
      case V => v
    }

}
