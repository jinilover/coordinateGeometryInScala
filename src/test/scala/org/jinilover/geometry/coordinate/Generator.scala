package org.jinilover.geometry.coordinate

import org.scalacheck.Gen

object Generator {
  val boxRandomOrder: BOXES => Gen[BOXES] =
    boxes => boxes.foldLeft(Gen.const(Nil: BOXES)) {
      (genBoxes, b) =>
        for {
          bs <- genBoxes
          availBs = boxes diff bs
          n <- Gen.choose(0, availBs.size - 1)
        } yield availBs(n) :: bs
    }

}
