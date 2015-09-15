package org.jinilover.geometry.coordinate

import scala.language.implicitConversions
import scala.math.abs
import com.typesafe.scalalogging._

case class Point(x: Int, y: Int)

case class Edge(start: Point, end: Point)

case class Box(tpLeft: Point, btmRight: Point)

case class Polygon(points: Point*)

object Geometry extends LazyLogging {
  type POINTS = List[Point]
  type EDGES = List[Edge]
  type JOIN_EDGES = EDGES => EDGES => EDGES => EDGES => Option[EDGES]

  implicit def toPoint(tuple: (Int, Int)): Point =
    Point(tuple._1, tuple._2)

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

  val sortTuple2: ((Int, Int)) => ((Int, Int)) = t => if (t._1 < t._2) t else (t._2, t._1)

  val rangesOverlapped: ((Int, Int), (Int, Int)) => Boolean = {
    (r1, r2) =>
      val sortedR1 = sortTuple2(r1)
      val sortedR2 = sortTuple2(r2)
      val withinRange: Int => ((Int, Int)) => Boolean = x => r => (r._1 <= x && x < r._2)
      withinRange(sortedR1._1)(sortedR2) || withinRange(sortedR2._1)(sortedR1)
  }

  /**
   * same edges if they have the same start/end even they point to opposite direction
   */
  val sameEdges: Edge => Edge => Boolean =
    e1 => e2 =>
      (e1, e2) match {
        case (Edge(s1, ed1), Edge(s2, ed2)) =>
          s1 == s2 && ed1 == ed2 || s1 == ed2 && ed1 == s2
      }

  /**
   * from es1, return those edges which are not equal (start/end swapped are equal) to any of es2
   */
  val unequalEdges: EDGES => EDGES => EDGES =
    es1 => es2 => es1.filter(e1 => es2.forall(e2 => !sameEdges(e1)(e2)))

  def orientationDependent[T](edge: Edge)(h: => T)(v: => T): T =
    orient(edge) match {
      case H => h
      case V => v
    }

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
          else
          // bMatches.size should be 1 because a polygon edge overlaps with at most 1 box edge
            for {
              zVal <- z
              (ps, bs) = zVal
              newPs <- appendEdge(ps)(pEdge)
            } yield (newPs, bs :+ bMatches.head)
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
   * shift the items at position where the item satisfy f predicate 
   */
  def shiftItems[T](items: List[T])(f: T => Boolean): List[T] = {
    val (lead, trail) = items splitAt (items indexWhere f)
    trail ++ lead
  }

  /**
   * subItems are items subset. 
   */
  def subtractItems[T](items: List[T])(subItems: List[T]): List[T] =
    items filter (x => !(subItems contains x))

  /**
   * subItems are items subset, subItems items are in the same order as the
   * correspondent in items
   */
  def subtractContinuousItems[T](items: List[T])(subItems: List[T]): (List[T], List[T]) = {
    val (lead, trail) = items splitAt (items indexWhere (_ == subItems.head))
    (lead, trail drop subItems.size)
  }

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

  val holeCheck: EDGES => Option[EDGES] =
    es => Some(es) // TODO

  /**
   * combine 2 colinear edges to 1
   */
  val reduceEdges: EDGES => EDGES =
    _.foldRight(List.empty[Edge]) {
      (e, z) =>
        z match {
          case hd :: tl if sameOrient(e)(hd) => Edge(e.start, hd.end) :: tl
          case _ => e :: z
        }
    }

  def firstLastItem[T](list: List[T]): (T, T) =
    (list.head, list.last)

  val connectEdges: List[EDGES] => Option[EDGES] =
    _.foldLeft[Option[EDGES]](Some(List.empty[Edge])) {
      (z, es) => z.flatMap(appendEdges(_)(es))
    }

  val joinBy1or2Matches: JOIN_EDGES =
    pEdges => pMatches => bEdges => bMatches => {
      val (pLead, pTail) = subtractContinuousItems(pEdges)(pMatches)
      val bRemains = {
        val remains = subtractItems(bEdges)(bMatches)
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
      val (pLead, pTail) = subtractContinuousItems(pEdges)(pMatches)
      val bRemain = subtractItems(bEdges)(bMatches)
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
      val (pLead, pTail) = subtractContinuousItems(pEdges)(pMatches)
      val pPartialMatches = unequalEdges(pMatches)(bMatches)
      val bPartialMatches = unequalEdges(bMatches)(pMatches)
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

  import scalaz.Functor
  import scalaz.std.option._

  val formPolygon: EDGES => EDGES => EDGES => EDGES => JOIN_EDGES => Option[Polygon] =
    pEdges => pMatches => bEdges => bMatches => joinByMatch =>
      joinByMatch(pEdges)(pMatches)(bEdges)(bMatches) flatMap {
        edges =>
          val compositeF =
            shiftPEdges andThen reduceEdges andThen holeCheck andThen
              Functor[Option].lift((_: EDGES).map(_.start))
          compositeF(edges) map (Polygon(_: _*))
      }

  val merge: Polygon => Box => Option[Polygon] =
    polygon => box => {
      val pEdges = polygonToEdges(polygon)
      val bEdges = boxToEdges(box)
      overlapEdges(pEdges)(bEdges) flatMap {
        t =>
          val (pMatches, bMatches) = t
          lazy val toPolygon = formPolygon(pEdges)(pMatches)(bEdges)(bMatches)
          pMatches.size match {
            case 1 => toPolygon(joinBy1or2Matches)
            case 2 => toPolygon(joinBy1or2Matches)
            case 3 => toPolygon(joinByThreeMatches)
            case 4 => toPolygon(joinByFourMatches)
            case _ => None // won't happen, just eliminate non-exhaustive warning
          }
      }
    }
}

sealed trait EdgeOrientation

case object H extends EdgeOrientation

case object V extends EdgeOrientation
