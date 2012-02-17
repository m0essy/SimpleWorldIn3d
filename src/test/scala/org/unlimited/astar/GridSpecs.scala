package org.unlimited.astar

import org.specs2.mutable._
import org.unlimited.island.Terrain

class GridSpecs extends Specification {
  "Terrain" should {
    val gridSize = 4
    val grid = new Terrain(gridSize)

    "expose the index of a vertex" in {
      grid.vertex(1,2).idx should beEqualTo(2*gridSize+1)
    }

    "expose its vertices as indexed values" in {
      grid.vertex(1,2).y = 1.0f
      grid.vertices(2*gridSize+1).y should beEqualTo(1.0f)
      grid.vertices(2*gridSize+1).x should beEqualTo(1.0f)
      grid.vertices(2*gridSize+1).z should beEqualTo(2.0f)
    }

    "expose the index of a cell" in {
      grid.cell(1,2).idx should beEqualTo(2*(gridSize-1)+1)
    }

    "expose the cells as indexed values" in {
      grid.cell(1,2).sw.y = 2.0f
      grid.cell(1,2).nw.y = 3.0f
      grid.cell(1,2).se.y = 4.0f
      grid.cell(1,2).ne.y = 5.0f
      grid.vertices(2*gridSize+1).y should beEqualTo(2.0f)
      grid.vertices(3*gridSize+1).y should beEqualTo(3.0f)
      grid.vertices(2*gridSize+2).y should beEqualTo(4.0f)
      grid.vertices(3*gridSize+2).y should beEqualTo(5.0f)
    }
  }
}