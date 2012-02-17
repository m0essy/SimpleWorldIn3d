package org.unlimited.astar

import Option._
import javax.management.remote.rmi._RMIConnection_Stub

class BinomialHeap[A <% Ordered[A]](val trees: List[BinomialTree[A]] = Nil) {
  lazy val minimum: Option[A] =
    trees match {
      case Nil => None
      case _ => Some((trees flatMap {
        t => BinomialTree.root(t)
      }).min)
    }

  def insert(value: A): BinomialHeap[A] = BinomialHeap.insert(value, this)

  def deleteMin() : BinomialHeap[A] = {
    val minimumTree : Node[A] =
      trees.flatMap(BinomialTree.toNode(_)).minBy(n => n.value)

    val left = new BinomialHeap(trees.map(t => if (t == minimumTree) EmptyTree else t).reverse.dropWhile(_ == EmptyTree).reverse)
    val right = new BinomialHeap(minimumTree.children)
    BinomialHeap.merge(left, right)
  }

  def delete(value: A) : BinomialHeap[A] = BinomialHeap.delete(value, this)
}

object BinomialHeap {
  def merge[A <% Ordered[A]](a: BinomialHeap[A], b: BinomialHeap[A]): BinomialHeap[A] = {
    new BinomialHeap[A](mergeHeads(a.trees, b.trees, EmptyTree).reverse.dropWhile(_ == EmptyTree).reverse)
  }

  def create[A <% Ordered[A]](items: Seq[A]) : BinomialHeap[A] =
    items.foldLeft (new BinomialHeap[A]()) ((agg, item) => agg insert item)

  private def mergeHeads[A <% Ordered[A]] (p: List[BinomialTree[A]], q: List[BinomialTree[A]], carry: BinomialTree[A])
    : List[BinomialTree[A]] =
  {
    def splitDefault(l: List[BinomialTree[A]]) : (BinomialTree[A], List[BinomialTree[A]]) = l match {
      case h :: t => (h, t)
      case Nil => (EmptyTree, Nil)
    }
    val (ph, pt) = splitDefault(p)
    val (qh, qt) = splitDefault(q)
    val (newHead, newCarry) = List(carry,ph,qh).filter(_!=EmptyTree) match {
      case Nil => (EmptyTree, EmptyTree)
      case List(t1) => (t1, EmptyTree)
      case List(t1,t2) => (EmptyTree, BinomialTree.merge(t1,t2))
      case List(t1,t2,t3) => (t1, BinomialTree.merge(t2,t3))
    }

    (pt, qt, newCarry) match {
      case (Nil, Nil, EmptyTree) => newHead :: Nil
      case _ => newHead :: mergeHeads(pt, qt, newCarry)
    }
  }

  def insert[A <% Ordered[A]](a: A, b: BinomialHeap[A]): BinomialHeap[A] = {
    val singleItemHeap = new BinomialHeap[A](List(new Node[A](a, Nil)))
    merge(singleItemHeap, b)
  }

  def delete[A <% Ordered[A]](a: A, b:BinomialHeap[A]): BinomialHeap[A] = {
    sys.error("not impl")
  }
}

trait BinomialTree[-A]

object BinomialTree {
  def merge[A <% Ordered[A]](a: BinomialTree[A], b: BinomialTree[A]): BinomialTree[A] =
    (a, b) match {
      case (EmptyTree, EmptyTree) => EmptyTree
      case (tree, EmptyTree) => tree
      case (EmptyTree, tree) => tree
      case (p: Node[A], q: Node[A]) =>
        if (p.value <= q.value)
          p.addSubTree(q)
        else
          q.addSubTree(p)
    }

  def toNode[A](tree: BinomialTree[A]) : Option[Node[A]] = 
    tree match {
      case n: Node[A] => Some(n)
      case _ => None
    }

  def root[A](tree: BinomialTree[A]): Option[A] =
    tree match {
      case n: Node[A] => Some(n.value)
      case _ => None
    }
}

case object EmptyTree extends BinomialTree[Any]

case class Node[A](value: A, children: List[Node[A]]) extends BinomialTree[A] {
  def addSubTree(other: Node[A]) = Node[A](value, children ::: other :: Nil)
}

