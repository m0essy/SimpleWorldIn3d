package org.unlimited.island

import com.jme3._
import app.SimpleApplication
import bounding.BoundingBox
import collision.CollisionResults
import input.controls.{ActionListener, MouseButtonTrigger, AnalogListener, KeyTrigger}
import input.{MouseInput, KeyInput}
import light.{DirectionalLight, AmbientLight}
import material.Material
import material.RenderState.BlendMode
import math._
import scene._
import shader.VarType
import scala.util.Random
import java.lang.Math
import util.{SkyFactory, BufferUtils}

object Main extends App {
  override def main(args: Array[String]) {
    new Main().start()
  }
}

class Main extends SimpleApplication {
  private val that = this
  object Implicits {
    implicit val currentAssetManager = that.assetManager
    implicit val currentRenderManager = that.renderManager
  }
  def randomTerrainMesh(size: Int, numHills: Int, radius: Float, flatness: Float) = {
    val grid = new Terrain(size)
    val rSqr = radius*radius
    (1 to numHills).foreach(_ => {
      val cx = Random.nextFloat() * size
      val cy = Random.nextFloat() * size
      ((Math.floor(cx-radius).toInt) to (Math.ceil(cx+radius).toInt)).foreach(x => {
        ((Math.floor(cy-radius).toInt) to (Math.ceil(cy+radius).toInt)).foreach(y => {
          if (x >=0 && x < size && y >= 0 && y < size) {
            val dx = Math.abs(cx-x)
            val dy = Math.abs(cy-y)
            val distSqr = dx*dx + dy*dy
            if (distSqr < rSqr) {
              val v = grid.vertex(x,y)
              v.y = v.y + Math.sqrt(rSqr - distSqr).toFloat * flatness
            }
          }
        })
      })
    })

    grid.calculateNormals()

    import Implicits._
    new Island(grid)
  }

  def simpleInitApp() {
    rootNode.attachChild(
      SkyFactory.createSky(assetManager, "Textures/Sky/Bright/BrightSky.dds", false))
    val island = randomTerrainMesh(64, 100, 5f, .1f)
    rootNode.attachChild(island.node)
    val ambient = new AmbientLight()
    ambient.setColor(ColorRGBA.DarkGray)
    rootNode.addLight(ambient)

    val sun = new DirectionalLight()
    sun.setDirection(new Vector3f(1, -1, 0).normalize())
    sun.setColor(ColorRGBA.White)

    flyCam.setEnabled(false)
    rootNode.addLight(sun)

    inputManager.addMapping("Forward", new KeyTrigger(KeyInput.KEY_W))
    inputManager.addMapping("Backward", new KeyTrigger(KeyInput.KEY_S))
    inputManager.addMapping("Turn Left", new KeyTrigger(KeyInput.KEY_A))
    inputManager.addMapping("Turn Right", new KeyTrigger(KeyInput.KEY_D))
    inputManager.addMapping("Trigger Grid", new KeyTrigger(KeyInput.KEY_G))

    inputManager.addMapping("Pick Target", new MouseButtonTrigger(MouseInput.BUTTON_LEFT))

    val birdEye = new BirdEyeCamera(cam, 10)

    cam.setLocation(new Vector3f(0, 10, 0))
    cam.setAxes(new Quaternion().fromAngleAxis((Math.PI/6.0).toFloat, new Vector3f(1, 0, 0).normalize()))

    inputManager.addListener(new AnalogListener {
      def onAnalog(eventName: String, value: Float, tpf: Float) {
        (eventName match {
          case "Forward" => birdEye.forward _
          case "Backward" => birdEye.backward _
          case "Turn Left" => birdEye.turnLeft _
          case "Turn Right" => birdEye.turnRight _
        })(value)
      }
    }, "Forward", "Backward", "Turn Left", "Turn Right")

    val selectionHandler = new SelectionHandler(island, rootNode)
    inputManager.addListener(new ActionListener {
      def onAction(eventName: String, pressed: Boolean, tpf: Float) {
        if (pressed) {
          (eventName match {
            case "Pick Target" => selectionHandler.pickCurrentTarget()
          })
        }
      }
    }, "Pick Target")

    inputManager.addListener(new ActionListener {
      def onAction(eventName: String, pressed: Boolean, tpf: Float) {
        if (pressed) {
          (eventName match {
            case "Trigger Grid" => island.triggerGrid()
          })
        }
      }
    }, "Trigger Grid")

    inputManager.setCursorVisible(true)
  }

  class SelectionHandler(val island: Island, val rootNode: Node) {
    var selectedCell : Geometry = null

    def setSelectedCell(x: Int, y: Int) {
      def createGeometry() = {
        val mesh = new Mesh()
        val indices = new Array[Int](6)
        island.terrain.cell(x,y).fillMeshBuffer(indices, 0)
        mesh.setMode(Mesh.Mode.Triangles)
        mesh.setBuffer(VertexBuffer.Type.Position, 3, island.verticesBuffer)
        mesh.setBuffer(VertexBuffer.Type.Index,    1, BufferUtils.createIntBuffer(indices: _*))

        val mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md")
        mat.setParam("Color", VarType.Vector4, new ColorRGBA(1,0,0,.5f))
        mat.getAdditionalRenderState.setBlendMode(BlendMode.Alpha)
        val geometry = new Geometry("selectedCell", mesh)
        geometry.setMaterial(mat)
        geometry.setLocalTranslation(0, 0.001f, 0)
        geometry.setModelBound(new BoundingBox())
        geometry.updateModelBound()
        geometry
      }
      if (selectedCell != null) rootNode.detachChild(selectedCell)
      selectedCell = createGeometry()
      rootNode.attachChild(selectedCell)
    }

    def clearSelectedCell() {
      if (selectedCell != null) {
        rootNode.detachChild(selectedCell)
        selectedCell = null
      }
    }

    def pickCurrentTarget() {
      val results = new CollisionResults()

      val screenCoord = inputManager.getCursorPosition
      val projectedScreenCoord = cam.getWorldCoordinates(screenCoord, 0f).clone()
      val rayDirection = cam.getWorldCoordinates(screenCoord, 1f) subtract projectedScreenCoord
      val ray = new Ray(projectedScreenCoord, rayDirection)

      rootNode.getChild("island").collideWith(ray, results)
      if (results.size() > 0) {
        val collision = results.getCollision(0)
        val contact = collision.getContactPoint
        setSelectedCell(contact.getX.toInt, contact.getZ.toInt)
      } else {
        clearSelectedCell()
      }
    }
  }
}


