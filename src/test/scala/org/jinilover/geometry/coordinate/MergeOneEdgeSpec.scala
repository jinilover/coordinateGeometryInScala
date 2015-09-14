package org.jinilover.geometry.coordinate

import org.scalatest.{Matchers, FlatSpec}
import Geometry._

import scala.language.postfixOps

class MergeOneEdgeSpec extends FlatSpec with Matchers {
  it should "have merge a poly with a box with 1 edge matched" in {
    merge {
      Polygon((2, 13), (6, 13), (6, 9), (2, 9))
    } {
      Box((6, 6), (12, 10))
    } should be {
      Some(Polygon((2, 13), (6, 13), (6, 10), (12, 10), (12, 6), (6, 6), (6, 9), (2, 9)))
    }
  }

  it should "have form a 'T' clockwise 90deg" in {
    val expectedPoly = Polygon((9, 13), (12, 13), (12, 6), (9, 6), (9, 8), (6, 8), (6, 11), (9, 11))

    merge {
      Polygon((9, 13), (12, 13), (12, 6), (9, 6))
    } {
      Box((6, 8), (9, 11))
    } should be(Some(expectedPoly))

    // swap the roles of the previous test case
    merge {
      Polygon((6, 11), (9, 11), (9, 8), (6, 8))
    } {
      Box((9, 6), (12, 13))
    } should be(Some(expectedPoly))
  }

  it should "have form a 'L' pointing to left" in {
    val expectedPoly = Polygon((2, 11), (8, 11), (8, 7), (5, 7), (5, 9), (2, 9))

    merge {
      Polygon((2, 11), (5, 11), (5, 9), (2, 9))
    } {
      Box((5, 7), (8, 11))
    } should be(Some(expectedPoly))

    // swap the roles of the previous test case
    merge {
      Polygon((5, 11), (8, 11), (8, 7), (5, 7))
    } {
      Box((2, 9), (5, 11))
    } should be(Some(expectedPoly))
  }

  it should "have form a 'L'" in {
    val expectedPoly = Polygon((2, 11), (8, 11), (8, 8), (5, 8), (5, 6), (2, 6))
    merge {
      Polygon((2, 11), (5, 11), (5, 6), (2, 6))
    } {
      Box((5, 8), (8, 11))
    } should be(Some(expectedPoly))

    // swap the roles of the previous test case
    merge {
      Polygon((5, 11), (8, 11), (8, 8), (5, 8))
    } {
      Box((2, 6), (5, 11))
    } should be(Some(expectedPoly))
  }

  it should "form a 'L' clockwise 90deg" in {
    val expectedPoly = Polygon((2, 11), (5, 11), (5, 10), (7, 10), (7, 8), (2, 8))
    merge {
      Polygon((2, 11), (5, 11), (5, 8), (2, 8))
    } {
      Box((5, 8), (7, 10))
    } should be(Some(expectedPoly))

    // swap the roles of the previous test case
    merge {
      Polygon((5, 10), (7, 10), (7, 8), (5, 8))
    } {
      Box((2, 8), (5, 11))
    } should be(Some(expectedPoly))
  }

  it should "form a 'L' pointing to left, counterclockwise 90deg" in {
    val expectedPoly = Polygon((5, 11), (8, 11), (8, 6), (2, 6), (2, 8), (5, 8))
    merge {
      Polygon((2, 8), (8, 8), (8, 6), (2, 6))
    } {
      Box((5, 8), (8, 11))
    } should be(Some(expectedPoly))

    // swap the roles of the previous test case
    merge {
      Polygon((5, 11), (8, 11), (8, 8), (5, 8))
    } {
      Box((2, 6), (8, 8))
    } should be(Some(expectedPoly))
  }

  it should "2 polygons stands side by side to form a new rectangle" in {
    val expectedPoly = Polygon((5, 11), (13, 11), (13, 7), (5, 7))
    merge {
      Polygon((5, 11), (9, 11), (9, 7), (5, 7))
    } {
      Box((9, 7), (13, 11))
    } should be(Some(expectedPoly))

    // swap the roles of the previous test case
    merge {
      Polygon((9, 11), (13, 11), (13, 7), (9, 7))
    } {
      Box((5, 7), (9, 11))
    } should be(Some(expectedPoly))
  }

  it should "1 polygon sits on another to form a new rectangle" in {
    val expectedPoly = Polygon((2, 11), (6, 11), (6, 3), (2, 3))
    merge {
      Polygon((2, 7), (6, 7), (6, 3), (2, 3))
    } {
      Box((2, 7), (6, 11))
    } should be(Some(expectedPoly))

    // swap the roles of the previous test case
    merge {
      Polygon((2, 11), (6, 11), (6, 7), (2, 7))
    } {
      Box((2, 3), (6, 7))
    } should be(Some(expectedPoly))
  }

}
