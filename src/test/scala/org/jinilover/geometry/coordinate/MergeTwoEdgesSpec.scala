package org.jinilover.geometry.coordinate

import org.scalatest.{Matchers, FlatSpec}
import Geometry._

import scala.language.postfixOps

class MergeTwoEdgesSpec extends FlatSpec with Matchers {
  
  it should "have merge a poly with a top right big box" in {
//    merge {
//      Polygon((3, 17), (9, 17), (9, 14), (6, 14), (6, 11), (3, 11))
//    } {
//      Box((6, 10), (10, 14))
//    } should be {
//      Some(
//        Polygon((3, 17), (9, 17), (9, 14), (10, 14), (10, 10), (6, 10), (6, 11), (3, 11))
//      )
//    }
  }

  it should "have merge a poly with a bottom right big box" in {


  }

  it should "have merge a poly with a bottom left big box" in {


  }

  it should "have merge a poly with a top left big box" in {


  }

  it should "have merge a poly with a top right box to form a rectangle" in {


  }

  it should "have merge a poly with a bottom right box to form a rectangle" in {


  }

  it should "have merge a poly with a bottom left box to form a rectangle" in {


  }

  it should "have merge a poly with a top left box to form a rectangle" in {


  }

  it should "have merge a poly with a top right small box" in {


  }

  it should "have merge a poly with a bottom right small box" in {


  }

  it should "have merge a poly with a bottom left small box" in {


  }

  it should "have merge a poly with a top left small box" in {


  }

}
