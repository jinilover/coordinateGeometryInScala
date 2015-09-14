package org.jinilover.geometry.coordinate

import scala.language.implicitConversions
import scala.math.abs
import com.typesafe.scalalogging._
import org.slf4j.LoggerFactory

case class Point(x: Int, y: Int)

case class Edge(start: Point, end: Point)

case class Box(tpLeft: Point, btmRight: Point)

case class Polygon(points: Point*)

object Geometry extends LazyLogging {
  //  type X = Int
  //  type Y = Int
  //  type PointFP = (X, Y)
  //  type EdgeStartFP = PointFP
  //  type EdgeEndFP = PointFP
  //  type TopLeftFP = PointFP
  //  type BottomRightFP = PointFP
  //  type EdgeFP = (EdgeStartFP, EdgeEndFP)
  //  type BoxFP = (TopLeftFP, BottomRightFP)
  //  type PolygonFP = List[PointFP]

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

  //  val calculateLen: EdgeFP => (PointFP => Int) => Int =
  //    edge => getCoord => abs(getCoord(edge.start) - getCoord(edge.end))

  val edgeLen: Edge => Int = {
    edge =>
      val calculate: (Point => Int) => Int = coordVal => abs(coordVal(edge.start) - coordVal(edge.end))
      orientationDependent(edge)(calculate(_.x))(calculate(_.y))
  }

  val edgesOverlapped: Edge => Edge => Boolean = {
    e1 => e2 =>
      lazy val matched: (Int, Int) => ((Int, Int), (Int, Int)) => Boolean =
        (orthogon1, orthogon2) => (r1, r2) => orthogon1 == orthogon2 && rangesOverlapped(sortTuple2(r1), sortTuple2(r2))
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
      val withinRange: Int => ((Int, Int)) => Boolean = x => r => (r._1 <= x && x < r._2)
      withinRange(r1._1)(r2) || withinRange(r2._1)(r1)
  }

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
      //      (e1, e2) match {
      //        case _ if e1.end != e2.start => None
      //        case _ if orient(e1) == orient(e2) => Some(List(EdgeFP(e1.start, e2.end)))
      //        case _ if e1.start == e1.end => Some(List(e2))
      //        case _ if e2.start == e2.end => Some(List(e1))
      //        case _ => Some(List(e1, e2))
      //      }
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

  val overlapEdges: EDGES => EDGES => Option[(EDGES, EDGES)] =
    polyEdges => boxEdges =>
      polyEdges.foldLeft[Option[(EDGES, EDGES)]](Some(Nil, Nil)) {
        (z, pEdge) =>
          val bMatches = boxEdges filter (edgesOverlapped(pEdge))
          if (bMatches.isEmpty) z
          else
          // bMatches.size should be 1 becaise a polygon edge overlaps with at most 1 box edge
            for {
              zVal <- z
              (ps, bs) = zVal
              newPs <- appendEdge(ps)(pEdge)
            } yield (newPs, bs :+ bMatches(0))
      } flatMap {
        tuple => if (tuple._1.isEmpty) None else Some(tuple)
      }

  /**
   * pEdges is a polygon or box edges which are joined continuously, 
   * subEdges is pEdges subset, which are also joined continuously,
   * Re-join (pEdges - subEdges) in a continuous manner
   */
  val makeEdgesContinuous: EDGES => EDGES => EDGES =
    pEdges => subEdges => {
      subtractEdges(pEdges)(subEdges) match {
        case (Nil, tail) => tail
        case (lead, Nil) => lead
        case (lead, tail) => tail ++ lead
      }
    }
  
  val subtractEdges: EDGES => EDGES => (EDGES, EDGES) =
    pEdges => subEdges => {
      val (lead, trail) = pEdges splitAt (pEdges indexWhere (_ == subEdges.head))
      (lead, trail drop subEdges.size)
    }

  /**
   * pEdges is a polygon or box edges which are joined continuously, 
   * but it may not start with the lowest horizontal edges,
   * shift the edges s.t. it starts with the lowest horizontal edges.
   */
  val makePEdgesCounterClockWise: EDGES => EDGES =
    pEdges => {
      val firstEdge = minItem(pEdges) {
        (e1, e2) =>
          (e1.start, e2.start) match {
            case (pt1, pt2) if pt1.y > pt2.y => true
            case (pt1, pt2) if pt1.y == pt2.y => pt1.x < pt2.x
            case _ => false
          }
      }
      val (lead, trail) = pEdges splitAt (pEdges.indexWhere(_ == firstEdge))
      val edges = trail ++ lead
      logger.debug(s"pEdgesCounterClockWise: firstEdge = $firstEdge, edges = $edges")
      edges
    }

  /**
   * remove 0 length edge, replace 2 colinear edges to 1 
   */
  val reduceEdges: EDGES => EDGES =
    _.foldRight(List.empty[Edge]) {
      (e, z) =>
        z match {
          case hd :: tl if sameOrient(e)(hd) => Edge(e.start, hd.end) :: tl
          case _ => e :: z
        }
    }

  def minItem[T](list: List[T])(f: (T, T) => Boolean): T =
    list reduceLeft {
      (x1, x2) => if (f(x1, x2)) x1 else x2
    }

  val joinBy1or2Matches: JOIN_EDGES =
    pEdges => pMatches => bEdges => bMatches => {
      val firstPMatch = pMatches.head
      val lastPMatch = pMatches.reverse.head
      val firstBMatch = bMatches.head
      val lastBMatch = bMatches.reverse.head
      val (pLead, pTail) = subtractEdges(pEdges)(pMatches)
      val bRemainHd :: bRemainTl = makeEdgesContinuous(bEdges)(bMatches)
      val firstConnect = Edge(firstPMatch.start, firstBMatch.end)
      val secondConnect = Edge(lastBMatch.start, lastPMatch.end)

      logger.debug(
        s"""
         |joinBy ${pMatches.size} matching edges
         |firstPMatch: $firstPMatch
         |lastPMatch: $lastPMatch
         |firstBMatch: $firstBMatch
         |lastBMatch: $lastBMatch
         |pLead: $pLead
         |pTail: $pTail
         |bRemainHd: $bRemainHd
         |bRemainTl: $bRemainTl
         |firstConnect: $firstConnect
         |secondConnect: $secondConnect
       """.stripMargin

      )

      // connect edges in correct order: 
      // pLead, firstConnect, bRemainHd, bRemainTl, secondConnect, pTail
      val edges = for {
        edges1 <- appendEdge(pLead)(firstConnect)
        edges2 <- appendEdge(edges1)(bRemainHd)
        edges3 <- appendEdge(edges2 ++ bRemainTl)(secondConnect)
        edges4 <- (pTail match {
          case x :: xs => appendEdge(edges3)(x) map (_ ++ xs)
          case _ => Some(edges3)
        })
      } yield edges4
      logger.debug(s"after joinBy ${pMatches.size} matching edges, the result is: $edges")
      edges
    }

  //  val joinByTwoMatches: JOIN_EDGES =
  //    pEdges => pMatches => bEdges => bMatches => ???

  val joinByThreeMatches: JOIN_EDGES =
    pEdges => pMatches => bEdges => bMatches => ???

  val joinByFourMatches: JOIN_EDGES =
    pEdges => pMatches => bEdges => bMatches => ???

  val formPolygon: EDGES => EDGES => EDGES => EDGES => JOIN_EDGES => Option[Polygon] =
    pEdges => pMatches => bEdges => bMatches => joinByMatch =>
      joinByMatch(pEdges)(pMatches)(bEdges)(bMatches) flatMap {
        edges =>
          val vertices =
            (makePEdgesCounterClockWise andThen reduceEdges andThen (_.map(_.start)))(edges)
          Some(Polygon(vertices: _*))
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
