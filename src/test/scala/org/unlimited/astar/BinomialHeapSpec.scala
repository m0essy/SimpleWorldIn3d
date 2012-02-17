package org.unlimited.astar

import org.specs2.mutable._
import org.specs2.ScalaCheck
import org.scalacheck._

class BinomialHeapSpec extends Specification with ScalaCheck {
  "BinomialHeap containing a no elements" should {
    val heap = new BinomialHeap[Int]
    "return the None as minimum" in {
      heap.minimum mustEqual None
    }
  }

  "BinomialHeap containing elements" should {
    "return the smallest element as minimum" in {
      Prop.forAll((list: List[Int]) =>
        list != Nil ==> (Some(list.min) mustEqual (BinomialHeap.create(list).minimum))
      )
    }
  }

  "BinomialHeap" should {
    def items[A <% Ordered[A]](heap: BinomialHeap[A]) : List[A] = heap.minimum match {
      case None => Nil
      case Some(value) => value :: items(heap.deleteMin)
    }

    "return the elements sorted" in {
      Prop.forAll((list: List[Int]) =>
        list.sorted mustEqual items(BinomialHeap.create(list))
      )
    }

    "return the elements sorted example" in {
      List(0,0,1,1) mustEqual items(BinomialHeap.create(List(0,1,0,1)))
    }
  }

//  "BinomialHeap" should {
//    "return " in {
//      val listGen = Gen.listOf(Gen.posNum[Int])
//      val subListGen = for {
//        l <- listGen
//        n <- Gen.oneOf(0 to (l.size-1))
//      } yield (l, Gen.pick(n, l))
//    }
//  }
}