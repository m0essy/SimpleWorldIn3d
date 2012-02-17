/*
package org.unlimited.astar

import org.apache.ivy.plugins.version.Match
import de.lessvoid.nifty.tools.time.interpolator.LinearTime
import org.specs2.internal.scalaz.ListT.Yield
import org.unlimited.astar.Coordinate

class SimpleMap(mapSize: Int) {
  
  private def inBounds(x: Int, y: Int) : Boolean = (x < mapSize && y < mapSize && x >= 0 && y >= 0)

  def neighbours(c: Coordinate) : List[Coordinate] =
    (for {
      i <- (c.x -1) to (c.x + 1)
      j <- (c.y -1) to (c.y + 1)
      if(inBounds(i,j) && c != Coordinate(i,j))
    } yield Coordinate(i,j)).toList
}

*/
