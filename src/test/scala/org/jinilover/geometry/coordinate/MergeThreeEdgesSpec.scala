package org.jinilover.geometry.coordinate

import org.jinilover.geometry.coordinate.Geometry._
import org.scalatest.{Matchers, FlatSpec}

class MergeThreeEdgesSpec extends FlatSpec with Matchers {
  it should "merge to form 'T'" in {
    merge {
      Polygon(
        (3, 10), (6, 10), (6, 8), (10, 8),
        (10, 10), (14, 10), (14, 5), (3, 5)
      )
    } {
      Box((6, 8), (10, 12))
    } should be {
      Some(
        Polygon(
          (6, 12), (10, 12), (10, 10), (14, 10),
          (14, 5), (3, 5), (3, 10), (6, 10)
        )
      )
    }
  }

  it should "merge to form 'T' clockwise 90deg" in {
    merge {
      Polygon(
        (9, 20), (13, 20), (13, 8), (9, 8),
        (9, 12), (11, 12), (11, 16), (9, 16)
      )
    } {
      Box((5, 12), (11, 16))
    } should be {
      Some(
        Polygon(
          (9, 20), (13, 20), (13, 8), (9, 8),
          (9, 12), (5, 12), (5, 16), (9, 16)
        )
      )
    }
  }

  it should "merge to form 'T' inverted" in {
    merge {
      Polygon(
        (3, 10), (12, 10), (12, 5), (10, 5),
        (10, 7), (6, 7), (6, 5), (3, 5)
      )
    } {
      Box((6, 2), (10, 7))
    } should be {
      Some(
        Polygon(
          (3, 10), (12, 10), (12, 5), (10, 5),
          (10, 2), (6, 2), (6, 5), (3, 5)
        )
      )
    }
  }

  it should "merge to form 'T' counter clockwise 90deg" in {
    merge {
      Polygon(
        (3, 20), (11, 20), (11, 17), (8, 17),
        (8, 14), (11, 14), (11, 11), (3, 11)
      )
    } {
      Box((8, 14), (13, 17))
    } should be {
      Some(
        Polygon(
          (3, 20), (11, 20), (11, 17), (13, 17),
          (13, 14), (11, 14), (11, 11), (3, 11)
        )
      )
    }
  }

  it should "merge with a bottom box to form a rectangle, i.e. all matched edges same lengths as polygon's correspondants" in {
    merge {
      Polygon(
        (3, 10), (6, 10), (6, 8), (10, 8),
        (10, 10), (14, 10), (14, 5), (3, 5)
      )
    } {
      Box((6, 8), (10, 10))
    } should be {
      Some(
        Polygon((3, 10), (14, 10), (14, 5), (3, 5))
      )
    }
  }

  it should "merge with a left box to form a rectangle, i.e. all matched edges same lengths as polygon's correspondants" in {
    merge {
      Polygon(
        (9, 20), (13, 20), (13, 8), (9, 8),
        (9, 12), (11, 12), (11, 16), (9, 16)
      )
    } {
      Box((9, 12), (11, 16))
    } should be {
      Some(
        Polygon((9, 20), (13, 20), (13, 8), (9, 8))
      )
    }
  }

  it should "merge with a top box to form a rectangle, i.e. all matched edges same lengths as polygon's correspondants" in {
    merge {
      Polygon(
        (3, 10), (12, 10), (12, 5), (10, 5),
        (10, 7), (6, 7), (6, 5), (3, 5)
      )
    } {
      Box((6, 5), (10, 7))
    } should be {
      Some(
        Polygon((3, 10), (12, 10), (12, 5), (3, 5))
      )
    }
  }

  it should "merge with a right box to form a rectangle, i.e. all matched edges same lengths as polygon's correspondants" in {
    merge {
      Polygon(
        (3, 20), (11, 20), (11, 17), (8, 17),
        (8, 14), (11, 14), (11, 11), (3, 11)
      )
    } {
      Box((8, 14), (11, 17))
    } should be {
      Some(
        Polygon((3, 20), (11, 20), (11, 11), (3, 11))
      )
    }
  }

  it should "similar to merge to form 'T' except the right poly horizontal edge levels with the box's" in {
    merge {
      Polygon(
        (3, 10), (6, 10), (6, 8), (10, 8),
        (10, 12), (14, 12), (14, 5), (3, 5)
      )
    } {
      Box((6, 8), (10, 12))
    } should be {
      Some(
        Polygon(
          (6, 12), (14, 12), (14, 5), (3, 5), (3, 10), (6, 10)
        )
      )
    }
  }

  it should "similar to merge to form 'T' except the left poly horizontal edge levels with the box's" in {
    merge {
      Polygon(
        (3, 12), (6, 12), (6, 8), (10, 8),
        (10, 10), (14, 10), (14, 5), (3, 5)
      )
    } {
      Box((6, 8), (10, 12))
    } should be {
      Some(
        Polygon(
          (3, 12), (10, 12), (10, 10), (14, 10), (14, 5), (3, 5)
        )
      )
    }
  }

  it should "similar to merge to form 'T' clockwise 90deg except the bottom poly vertical edge levels with the box's" in {
    merge {
      Polygon(
        (5, 20), (13, 20), (13, 8), (9, 8),
        (9, 12), (11, 12), (11, 16), (5, 16)
      )
    } {
      Box((5, 12), (11, 16))
    } should be {
      Some(
        Polygon(
          (5, 20), (13, 20), (13, 8), (9, 8), (9, 12), (5, 12)
        )
      )
    }
  }

  it should "similar to merge to form 'T' clockwise 90deg except the top poly vertical edge levels with the box's" in {
    merge {
      Polygon(
        (9, 20), (13, 20), (13, 8), (5, 8),
        (5, 12), (11, 12), (11, 16), (9, 16)
      )
    } {
      Box((5, 12), (11, 16))
    } should be {
      Some(
        Polygon(
          (9, 20), (13, 20), (13, 8), (5, 8), (5, 16), (9, 16)
        )
      )
    }
  }

  it should "similar to merge to form 'T' inverted except the right poly horizontal edge levels with the box's" in {
    merge {
      Polygon(
        (3, 10), (12, 10), (12, 2), (10, 2),
        (10, 7), (6, 7), (6, 5), (3, 5)
      )
    } {
      Box((6, 2), (10, 7))
    } should be {
      Some(
        Polygon(
          (3, 10), (12, 10), (12, 2), (6, 2), (6, 5), (3, 5)
        )
      )
    }
  }

  it should "similar to merge to form 'T' inverted except the left poly horizontal edge levels with the box's" in {
    merge {
      Polygon(
        (3, 10), (12, 10), (12, 5), (10, 5),
        (10, 7), (6, 7), (6, 2), (3, 2)
      )
    } {
      Box((6, 2), (10, 7))
    } should be {
      Some(
        Polygon(
          (3, 10), (12, 10), (12, 5), (10, 5), (10, 2), (3, 2)
        )
      )
    }
  }

  it should "similar to merge to form 'T' counter clockwise 90deg except the bottom poly vertical edge levels with the box's" in {
    merge {
      Polygon(
        (3, 20), (13, 20), (13, 17), (8, 17),
        (8, 14), (11, 14), (11, 11), (3, 11)
      )
    } {
      Box((8, 14), (13, 17))
    } should be {
      Some(
        Polygon(
          (3, 20), (13, 20), (13, 14), (11, 14), (11, 11), (3, 11)
        )
      )
    }
  }

  it should "similar to merge to form 'T' counter clockwise 90deg except the top poly vertical edge levels with the box's" in {
    merge {
      Polygon(
        (3, 20), (11, 20), (11, 17), (8, 17),
        (8, 14), (13, 14), (13, 11), (3, 11)
      )
    } {
      Box((8, 14), (13, 17))
    } should be {
      Some(
        Polygon(
          (3, 20), (11, 20), (11, 17), (13, 17), (13, 11), (3, 11)
        )
      )
    }
  }

  it should "similar to merge to form 'T' except both poly horizontal edges lower than the box's" in {
    merge {
      Polygon(
        (3, 13), (6, 13), (6, 8), (10, 8),
        (10, 13), (14, 13), (14, 5), (3, 5)
      )
    } {
      Box((6, 8), (10, 12))
    } should be {
      Some(
        Polygon(
          (3, 13), (6, 13), (6, 12), (10, 12),
          (10, 13), (14, 13), (14, 5), (3, 5)
        )
      )
    }
  }

  it should "similar to merge to form 'T' clockwise 90deg except both poly vertical edges lefter than the box's" in {
    merge {
      Polygon(
        (4, 20), (13, 20), (13, 8), (4, 8),
        (4, 12), (11, 12), (11, 16), (4, 16)
      )
    } {
      Box((5, 12), (11, 16))
    } should be {
      Some(
        Polygon(
          (4, 20), (13, 20), (13, 8), (4, 8),
          (4, 12), (5, 12), (5, 16), (4, 16)
        )
      )
    }
  }

  it should "similar merge to form 'T' inverted except both poly horizontal edges higher than the box's" in {
    merge {
      Polygon(
        (3, 10), (12, 10), (12, 1), (10, 1),
        (10, 7), (6, 7), (6, 1), (3, 1)
      )
    } {
      Box((6, 2), (10, 7))
    } should be {
      Some(
        Polygon(
          (3, 10), (12, 10), (12, 1), (10, 1),
          (10, 2), (6, 2), (6, 1), (3, 1)
        )
      )
    }
  }

  it should "similar to merge to form 'T' counter clockwise 90deg except both poly vertical edges righter than the box's" in {
    merge {
      Polygon(
        (3, 20), (14, 20), (14, 17), (8, 17),
        (8, 14), (14, 14), (14, 11), (3, 11)
      )
    } {
      Box((8, 14), (13, 17))
    } should be {
      Some(
        Polygon(
          (3, 20), (14, 20), (14, 17), (13, 17),
          (13, 14), (14, 14), (14, 11), (3, 11)
        )
      )
    }
  }

}
