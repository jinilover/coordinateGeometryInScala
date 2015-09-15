package org.jinilover.geometry.coordinate

object CommonFuncs {
  /**
   * shift the items at position where the item satisfy f predicate
   */
  def shiftItems[T](items: List[T])(f: T => Boolean): List[T] = {
    val (lead, trail) = items splitAt (items indexWhere f)
    trail ++ lead
  }

  /**
   * subList are list subset, subList are in the same order as the
   * correspondent in list, return a tuple 
   * containing items before/after subList
   */
  def subtractItems[T](list: List[T])(subList: List[T]): (List[T], List[T]) = {
    val (lead, trail) = list splitAt (list indexWhere (_ == subList.head))
    (lead, trail drop subList.size)
  }

  def firstLastItem[T](list: List[T]): (T, T) =
    (list.head, list.last)

  val sortTuple2: ((Int, Int)) => ((Int, Int)) = t => if (t._1 < t._2) t else (t._2, t._1)

  val rangesOverlapped: ((Int, Int), (Int, Int)) => Boolean = {
    (r1, r2) =>
      val sortedR1 = sortTuple2(r1)
      val sortedR2 = sortTuple2(r2)
      val withinRange: Int => ((Int, Int)) => Boolean = x => r => (r._1 <= x && x < r._2)
      withinRange(sortedR1._1)(sortedR2) || withinRange(sortedR2._1)(sortedR1)
  }

}
