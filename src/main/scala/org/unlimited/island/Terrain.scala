package org.unlimited.island

import com.jme3._
import math._
class Terrain(val vertexSize: Int) {
  val cellSize = vertexSize - 1
  val vertices = new Array[Vector3f](vertexSize*vertexSize)
  val textureCoords = new Array[Vector2f](vertexSize*vertexSize)
  val normals = new Array[Vector3f](vertexSize*vertexSize)

  (0 to (vertexSize-1)).foreach (y => {
    (0 to (vertexSize-1)).foreach (x => {
      vertices(y*vertexSize + x) = new Vector3f(x,0,y)
//      textureCoords(y*vertexSize + x) = new Vector2f(x.toFloat/(vertexSize-1).toFloat, y.toFloat/(vertexSize-1).toFloat)
      textureCoords(y*vertexSize + x) = new Vector2f(x % 2, y % 2)
    })
  })

  def calculateNormals() {
    (0 to (vertexSize - 1)).foreach(y => {
      (0 to (vertexSize - 1)).foreach(x => {
        normals(y * vertexSize + x) = vertex(x, y).normal
      })
    })
  }

  class Vertex(private val c_x: Int, private val c_y: Int) {
    val idx = c_y*vertexSize + c_x
    val vector = vertices(idx)
    def x = vector.x
    def y = vector.y
    def z = vector.z
    def y_= (value: Float) { vector.y = value }

    lazy val neighbourCells : Seq[Cell] = for {
      x <- Array(c_x-1,c_x)
      y <- Array(c_y-1,c_y)
      if (x > 0 && x < cellSize && y > 0 && y < cellSize)
    } yield cell(x,y)
    lazy val normal = (neighbourCells.foldLeft (Vector3f.ZERO) (_ add _.normal)).normalize()
  }
  def vertex(x: Int, y: Int) = new Vertex(x,y)

  class Cell(private val c_x: Int, private val c_y: Int) {
    val idx = c_y*cellSize + c_x
    val sw = vertex(c_x, c_y)
    val se = vertex(c_x+1, c_y)
    val nw = vertex(c_x, c_y+1)
    val ne = vertex(c_x+1, c_y+1)

    lazy val normal : Vector3f =
      (((sw.vector add se.vector.negate()) cross (nw.vector add se.vector.negate()))
        add
       ((nw.vector add se.vector.negate()) cross (ne.vector add se.vector.negate()))).normalize()

    def fillMeshBuffer(indices: Array[Int], startIdx: Int) {
      indices(startIdx+0) = se.idx
      indices(startIdx+1) = sw.idx
      indices(startIdx+2) = nw.idx
      indices(startIdx+3) = nw.idx
      indices(startIdx+4) = ne.idx
      indices(startIdx+5) = se.idx
    }

    def fillLineBuffer(indices: Array[Int], startIdx: Int) {
      indices(startIdx+0) = sw.idx
      indices(startIdx+1) = se.idx
      indices(startIdx+2) = se.idx
      indices(startIdx+3) = ne.idx
      indices(startIdx+4) = ne.idx
      indices(startIdx+5) = nw.idx
      indices(startIdx+6) = nw.idx
      indices(startIdx+7) = sw.idx
    }
  }
  def cell(x: Int, y: Int) = new Cell(x,y)
}








