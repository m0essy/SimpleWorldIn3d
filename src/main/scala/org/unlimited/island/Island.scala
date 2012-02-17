package org.unlimited.island

import com.jme3._
import asset.AssetManager
import bounding.BoundingBox
import material.Material
import material.RenderState.BlendMode
import math._
import renderer.queue.RenderQueue
import renderer.RenderManager
import scene._
import scene.shape.Box
import shader.VarType
import texture.Texture.WrapMode
import util.BufferUtils

class Island(val terrain: Terrain) (implicit val assetManager: AssetManager, implicit val renderManager: RenderManager) {
  lazy val verticesBuffer = BufferUtils.createFloatBuffer(terrain.vertices: _*)
  val size = terrain.vertexSize
  val cellSize = terrain.cellSize

  private lazy val grid = {
    val gridIndices = new Array[Int](cellSize*cellSize*8)
    (0 to cellSize-1).foreach (y => {
      (0 to cellSize-1).foreach (x => {
        val cell = terrain.cell(x,y)
        cell.fillLineBuffer(gridIndices, cell.idx*8)
      })
    })
    val gridMesh = new Mesh()
    gridMesh.setMode(Mesh.Mode.Lines)
    gridMesh.setBuffer(VertexBuffer.Type.Position, 3, verticesBuffer)
    gridMesh.setBuffer(VertexBuffer.Type.Index,    1, BufferUtils.createIntBuffer(gridIndices: _*))

    val matGrid = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md")
    matGrid.setParam("Color", VarType.Vector4, ColorRGBA.Green)
    val gridGeom = new Geometry("grid", gridMesh)
    gridGeom.setMaterial(matGrid)
    gridGeom.setLocalTranslation(0, 0.001f, 0)
    gridGeom.setModelBound(new BoundingBox())
    gridGeom.updateModelBound()

    gridGeom
  }

  private lazy val mesh = {
    val indices = new Array[Int](cellSize*cellSize*6)

    (0 to cellSize-1).foreach (y => {
      (0 to cellSize-1).foreach (x => {
        val cell = terrain.cell(x,y)
        cell.fillMeshBuffer(indices, cell.idx*6)
      })
    })

    val gridMesh = new Mesh
    gridMesh.setMode(Mesh.Mode.Triangles)
    gridMesh.setBuffer(VertexBuffer.Type.Position, 3, verticesBuffer)
    gridMesh.setBuffer(VertexBuffer.Type.Normal,   3, BufferUtils.createFloatBuffer(terrain.normals: _*))
    gridMesh.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(terrain.textureCoords: _*))
    gridMesh.setBuffer(VertexBuffer.Type.Index,    1, BufferUtils.createIntBuffer(indices: _*))

    val matRock = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md")
    val grass = assetManager.loadTexture("Textures/Terrain/splat/grass.jpg")
    grass.setWrap(WrapMode.Repeat)
    matRock.setTexture("DiffuseMap", grass)

    val meshGeom = new Geometry("island", gridMesh)
    meshGeom.setMaterial(matRock)
    meshGeom.setModelBound(new BoundingBox())
    meshGeom.updateModelBound()

    meshGeom
  }

  private lazy val sea = {
    val seaBox = new Box(new Vector3f(0,0,0), new Vector3f(size, .1f, size))
    val seaMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md")
    val water = assetManager.loadTexture("Textures/Terrain/water.png")
    water.setWrap(WrapMode.Repeat)
    seaMat.setTexture("DiffuseMap", water)
    seaMat.getAdditionalRenderState.setBlendMode(BlendMode.Alpha)
    seaMat.setBoolean("UseAlpha", true)
    val seaGeom = new Geometry("sea", seaBox)
    seaGeom.setMaterial(seaMat)
    seaGeom.setQueueBucket(RenderQueue.Bucket.Transparent)
    seaGeom
  }

  def triggerGrid() = {
    node.hasChild(grid) match {
      case true => node.detachChild(grid)
      case false => node.attachChild(grid)
    }
  }

  lazy val node = {
    val islandNode = new Node()
    islandNode.attachChild(mesh)
    islandNode.attachChild(grid)
    islandNode.attachChild(sea)
    islandNode
  }
}
