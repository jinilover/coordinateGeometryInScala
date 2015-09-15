package org.jinilover.geometry.coordinate

import org.jinilover.geometry.coordinate.PolygonFuncs._
import org.scalatest.{Matchers, FlatSpec}

class MergeFourEdgesSpec extends FlatSpec with Matchers {
  it should "1 box edge not covered by the polygon's correspondant, the 'opening' faces the the bottom" in {
    combine {
      Polygon(
        (8, 15), (13, 15), (13, 4), (3, 4),
        (3, 10), (6, 10), (6, 7), (10, 7),
        (10, 10), (8, 10)
      )
    } {
      Box((6, 7), (10, 10))
    } should be {
      Some(
        Polygon(
          (8, 15), (13, 15), (13, 4), (3, 4),
          (3, 10), (8, 10)
        )
      )
    }

    // same as previous, this time the polygon's left horizontal is lower than the opening
    combine {
      Polygon(
        (8, 15), (13, 15), (13, 4), (3, 4),
        (3, 11), (6, 11), (6, 7), (10, 7),
        (10, 10), (8, 10)
      )
    } {
      Box((6, 7), (10, 10))
    } should be {
      Some(
        Polygon(
          (8, 15), (13, 15), (13, 4), (3, 4),
          (3, 11), (6, 11), (6, 10), (8, 10)
        )
      )
    }
  }

  it should "1 box edge not covered by the polygon's correspondant, the 'opening' faces the the left" in {
    combine {
      Polygon(
        (6, 15), (13, 15), (13, 4), (3, 4),
        (3, 10), (6, 10), (6, 7), (10, 7),
        (10, 12), (6, 12)
      )
    } {
      Box((6, 7), (10, 12))
    } should be {
      Some(
        Polygon(
          (6, 15), (13, 15), (13, 4), (3, 4),
          (3, 10), (6, 10)
        )
      )
    }

    // same as previous, this time the polygon's bottom vertical is lefter than the opening
    combine {
      Polygon(
        (7, 15), (13, 15), (13, 4), (3, 4),
        (3, 10), (6, 10), (6, 7), (10, 7),
        (10, 12), (7, 12)
      )
    } {
      Box((6, 7), (10, 12))
    } should be {
      Some(
        Polygon(
          (7, 15), (13, 15), (13, 4), (3, 4),
          (3, 10), (6, 10), (6, 12), (7, 12)
        )
      )
    }
  }

  it should "1 box edge not covered by the polygon's correspondant, the 'opening' faces the the top" in {
    combine {
      Polygon(
        (3, 20), (13, 20), (13, 14), (10, 14),
        (10, 18), (5, 18), (5, 14), (8, 14),
        (8, 11), (3, 11)
      )
    } {
      Box((5, 14), (10, 18))
    } should be {
      Some(
        Polygon(
          (3, 20), (13, 20), (13, 14), (8, 14),
          (8, 11), (3, 11)
        )
      )
    }

    // same as previous, this time the polygon's left horizontal is higher than the opening
    combine {
      Polygon(
        (3, 20), (13, 20), (13, 13), (10, 13),
        (10, 18), (5, 18), (5, 14), (8, 14),
        (8, 11), (3, 11)
      )
    } {
      Box((5, 14), (10, 18))
    } should be {
      Some(
        Polygon(
          (3, 20), (13, 20), (13, 13), (10, 13),
          (10, 14), (8, 14), (8, 11), (3, 11)
        )
      )
    }
  }

  it should "1 box edge not covered by the polygon's correspondant, the 'opening' faces the the right" in {
    combine {
      Polygon(
        (3, 20), (13, 20), (13, 16), (8, 16),
        (8, 18), (5, 18), (5, 14), (8, 14),
        (8, 11), (3, 11)
      )
    } {
      Box((5, 14), (8, 18))
    } should be {
      Some(
        Polygon(
          (3, 20), (13, 20), (13, 16), (8, 16),
          (8, 11), (3, 11)
        )
      )
    }

    // same as previous, this time the polygon's top vertical is righter than the opening
    combine {
      Polygon(
        (3, 20), (13, 20), (13, 16), (8, 16),
        (8, 18), (5, 18), (5, 14), (9, 14),
        (9, 11), (3, 11)
      )
    } {
      Box((5, 14), (8, 18))
    } should be {
      Some(
        Polygon(
          (3, 20), (13, 20), (13, 16), (8, 16),
          (8, 14), (9, 14), (9, 11), (3, 11)
        )
      )
    }
  }

  it should "the box exposes the corn in the top right" in {
    combine {
      Polygon(
        (3, 20), (13, 20), (13, 15), (10, 16),
        (10, 18), (5, 18), (5, 14), (8, 14),
        (8, 11), (3, 11)
      )
    } {
      Box((5, 14), (10, 18))
    } should be {
      Some(
        Polygon(
          (3, 20), (13, 20), (13, 15), (10, 16),
          (10, 14), (8, 14), (8, 11), (3, 11)
        )
      )
    }
  }

  it should "the box exposes the corn in the bottom right" in {
    combine {
      Polygon(
        (3, 10), (6, 10), (6, 7), (5, 7),
        (5, 4), (8, 4), (8, 5), (11, 5),
        (11, 2), (3, 2)
      )
    } {
      Box((5, 4), (8, 7))
    } should be {
      Some(
        Polygon(
          (3, 10), (6, 10), (6, 7), (8, 7),
          (8, 5), (11, 5), (11, 2), (3, 2)
        )
      )
    }
  }

  it should "the box exposes the corn in the bottom left" in {
    combine {
      Polygon(
        (8, 15), (13, 15), (13, 4), (3, 4),
        (3, 10), (6, 10), (6, 7), (10, 7),
        (10, 12), (8, 12)
      )
    } {
      Box((6, 7), (10, 12))
    } should be {
      Some(
        Polygon(
          (8, 15), (13, 15), (13, 4), (3, 4),
          (3, 10), (6, 10), (6, 12), (8, 12)
        )
      )
    }
  }

  it should "the box exposes the corn in the top left" in {
    combine {
      Polygon(
        (3, 20), (15, 20), (15, 11), (9, 11),
        (9, 14), (12, 14), (12, 17), (6, 17),
        (6, 15), (3, 15)
      )
    } {
      Box((6, 14), (12, 17))
    } should be {
      Some(
        Polygon(
          (3, 20), (15, 20), (15, 11), (9, 11),
          (9, 14), (6, 14), (6, 15), (3, 15)
        )
      )
    }
  }

  it should "the indentation faces top" in {
//    combine {
//      Polygon(
//        (3, 10), (11, 10), (11, 3), (8, 3),
//        (8, 5), (9, 5), (9, 8), (5, 8),
//        (5, 5), (6, 5), (6, 3), (3, 3)
//      )
//    } {
//      Box((5, 5), (9, 8))
//    } should be {
//      Some(
//        Polygon(
//          (3, 10), (11, 10), (11, 3), (8, 3),
//          (8, 5), (6, 5), (6, 3), (3, 3)
//        )
//      )
//    }
  }

  it should "the indentation faces right" in {
//    combine {
//      Polygon(
//        (3, 20), (12, 20), (12, 16), (10, 16),
//        (10, 18), (5, 18), (5, 12), (10, 12),
//        (10, 14), (12, 14), (12, 10), (3, 10)
//      )
//    } {
//      Box((5, 12), (10, 18))
//    } should be {
//      Some(
//        Polygon(
//          (3, 20), (12, 20), (12, 16), (10, 16),
//          (10, 14), (12, 14), (12, 10), (3, 10)
//        )
//      )
//    }
  }

  it should "the indentation faces bottom" in {


  }

  it should "the indentation faces left" in {


  }
}
