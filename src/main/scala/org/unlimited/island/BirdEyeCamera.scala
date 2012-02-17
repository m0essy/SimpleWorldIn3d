package org.unlimited.island

import com.jme3._
import math._
import renderer.Camera
class BirdEyeCamera(val camera: Camera, val step: Float) {
  val planeNormal = new Vector3f(0,1,0)
  def forward(value: Float) {
    val planeForward = (camera.getLeft cross planeNormal) mult (value*step)
    camera.setLocation(camera.getLocation add planeForward)
  }

  def backward(value: Float) {
    forward(-value)
  }

  private def turn(value: Float) {
    val rot = new Quaternion().fromAngleAxis(value, planeNormal)
    camera.setRotation(rot mult camera.getRotation)
  }

  def turnLeft(value: Float) { turn(value) }
  def turnRight(value: Float) { turn(-value) }
}








