package org.jinilover.geometry.coordinate

import org.scalacheck.Gen

object Generator {
  def randomList[T](items: List[T]): Gen[List[T]] =
    items match {
      case Nil => Gen.const(List.empty[T])
      case _ =>
        for {
          n <- Gen.choose(0, items.size - 1)
          (lead, _ :: trail) = items splitAt n
          randomTail <- randomList(lead ::: trail)
        } yield items(n) :: randomTail
    }

}
