package org.jinilover.geometry.coordinate

import org.scalacheck.Gen

object Generator {
  def boxRandomOrder[T](items: List[T]): Gen[List[T]] =
    items.foldLeft(Gen.const(Nil: List[T])) {
      (genBoxes, b) =>
        for {
          bs <- genBoxes
          availBs = items diff bs
          n <- Gen.choose(0, availBs.size - 1)
        } yield availBs(n) :: bs
    }

}
