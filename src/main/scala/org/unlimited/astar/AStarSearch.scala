/*
package org.unlimited.astar
import scala.collection.mutable.PriorityQueue
import de.lessvoid.nifty.elements.events.ElementShowEvent

case class Coordinate(x: Int, y: Int) {
  
}

class AStarSearch {
  def search(map: SimpleMap, start: Coordinate, end: Coordinate) : List[Coordinate] = {
    type queueItem = (Int, Int, List[Coordinate])
    val queue = new PriorityQueue[queueItem]

    //initialize the priority Queue
    queue.enqueue((0,0,List(start)))

    val currentItem = queue.dequeue()

    map.neigbours(_currentItem._3.head)

    //Check if we already processed the neighbour

    //Check if the neighbour is equal to the end node
    //if yes we are done
    //else
    //  we compute the new queue item, which consists of:
    //  costs from the startnode (costs of the current + distance from the current to the neighbour)
    //  calculate the total path to the end, which is: cost from start to here (we already did this) + estimation to the end
    //  add the coordinate of the neighbour (leading) to the path of the current node (tailing)

    //This has to be optimized (e.g. with the hashmap)
    //check if we already have it in the queue
    //if yes -> we leave the cheapest item of the two in the queue
    //else
    //  we add it to the queue

    
  }
}*/
