package org.jinilover.geometry.coordinate

import org.scalatest.{Matchers, FlatSpec}
import PolygonFuncs._

import scala.language.postfixOps

class CombineByTwoEdgesSpec extends FlatSpec with Matchers {
  it should "combine a poly with a top right big box" in {
    combine {
      Polygon((3, 17), (9, 17), (9, 14), (6, 14), (6, 11), (3, 11))
    } {
      Box((6, 10), (10, 14))
    } should be {
      Some(
        Polygon((3, 17), (9, 17), (9, 14), (10, 14), (10, 10), (6, 10), (6, 11), (3, 11))
      )
    }
  }

  it should "combine a poly with a bottom right big box" in {
    combine {
      Polygon((3, 17), (7, 17), (7, 15), (10, 15), (10, 12), (3, 12))
    } {
      Box((7, 15), (12, 19))
    } should be {
      Some(
        Polygon((7, 19), (12, 19), (12, 15), (10, 15), (10, 12), (3, 12), (3, 17), (7, 17))
      )
    }
  }

  it should "combine a poly with a bottom left big box" in {
    combine {
      Polygon((8, 18), (11, 18), (11, 13), (6, 13), (6, 16), (8, 16))
    } {
      Box((4, 16), (8, 20))
    } should be {
      Some(
        Polygon((4, 20), (8, 20), (8, 18), (11, 18), (11, 13), (6, 13), (6, 16), (4, 16))
      )
    }
  }

  it should "combine a poly with a top left big box" in {
    combine {
      Polygon((3, 20), (11, 20), (11, 14), (7, 14), (7, 17), (3, 17))
    } {
      Box((2, 11), (7, 17))
    } should be {
      Some(
        Polygon((3, 20), (11, 20), (11, 14), (7, 14), (7, 11), (2, 11), (2, 17), (3, 17))
      )
    }
  }

  it should "combine a poly with a top right box to form a rectangle" in {
    combine {
      Polygon((4, 10), (9, 10), (9, 8), (6, 8), (6, 5), (4, 5))
    } {
      Box((6, 5), (9, 8))
    } should be {
      Some(
        Polygon((4, 10), (9, 10), (9, 5), (4, 5))
      )
    }
  }

  it should "combine a poly with a bottom right box to form a rectangle" in {
    combine {
      Polygon((4, 10), (6, 10), (6, 7), (9, 7), (9, 5), (4, 5))
    } {
      Box((6, 7), (9, 10))
    } should be {
      Some(
        Polygon((4, 10), (9, 10), (9, 5), (4, 5))
      )
    }
  }

  it should "combine a poly with a bottom left box to form a rectangle" in {
    combine {
      Polygon((7, 10), (9, 10), (9, 5), (4, 5), (4, 7), (7, 7))
    } {
      Box((4, 7), (7, 10))
    } should be {
      Some(
        Polygon((4, 10), (9, 10), (9, 5), (4, 5))
      )
    }
  }

  it should "combine a poly with a top left box to form a rectangle" in {
    combine {
      Polygon((4, 10), (9, 10), (9, 5), (7, 5), (7, 8), (4, 8))
    } {
      Box((4, 5), (7, 8))
    } should be {
      Some(
        Polygon((4, 10), (9, 10), (9, 5), (4, 5))
      )
    }
  }

  it should "combine a poly with a top right small box" in {
    combine {
      Polygon((4, 10), (9, 10), (9, 8), (6, 8), (6, 5), (4, 5))
    } {
      Box((6, 6), (8, 8))
    } should be {
      Some(
        Polygon((4, 10), (9, 10), (9, 8), (8, 8), (8, 6), (6, 6), (6, 5), (4, 5))
      )
    }
  }

  it should "combine a poly with a bottom right small box" in {
    combine {
      Polygon((4, 10), (6, 10), (6, 7), (9, 7), (9, 5), (4, 5))
    } {
      Box((6, 7), (8, 9))
    } should be {
      Some(
        Polygon((4, 10), (6, 10), (6, 9), (8, 9), (8, 7), (9, 7), (9, 5), (4, 5))
      )
    }
  }

  it should "combine a poly with a bottom left small box" in {
    combine {
      Polygon((7, 10), (9, 10), (9, 5), (4, 5), (4, 7), (7, 7))
    } {
      Box((5, 7), (7, 9))
    } should be {
      Some(
        Polygon((7, 10), (9, 10), (9, 5), (4, 5), (4, 7), (5, 7), (5, 9), (7, 9))
      )
    }
  }

  it should "combine a poly with a top left small box" in {
    combine {
      Polygon((4, 10), (9, 10), (9, 5), (7, 5), (7, 7), (4, 7))
    } {
      Box((5, 6), (7, 7))
    } should be {
      Some(
        Polygon((4, 10), (9, 10), (9, 5), (7, 5), (7, 6), (5, 6), (5, 7), (4, 7))
      )
    }
  }

}
