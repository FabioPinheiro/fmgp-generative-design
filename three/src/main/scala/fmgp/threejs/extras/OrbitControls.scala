package fmgp.threejs.extras

import fmgp.threejs.{Camera, Vector3}
import org.scalajs.dom.raw.{Event, HTMLElement}

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSImport}

@js.native
@JSImport("three/examples/jsm/controls/OrbitControls", "OrbitControls", "Three.OrbitControls")
class OrbitControls(camera: Camera, element: HTMLElement) extends js.Object {

  def this(camera: Camera) = this(camera, null) // scalastyle:ignore

  // API
  var enabled: Boolean = js.native
  var target: Vector3 = js.native

  var minDistance: Double = js.native
  var maxDistance: Double = js.native

  var minZoom: Double = js.native
  var maxZoom: Double = js.native

  var minPolarAngle: Double = js.native
  var maxPolarAngle: Double = js.native

  var minAzimuthAngle: Double = js.native
  var maxAzimuthAngle: Double = js.native

  var enableDamping: Boolean = js.native
  var dampingFactor: Double = js.native

  var enableZoom: Boolean = js.native
  var zoomSpeed: Double = js.native

  var enableRotate: Boolean = js.native
  var rotateSpeed: Double = js.native

  var enablePan: Boolean = js.native
  var panSpeed: Double = js.native
  var screenSpacePanning: Boolean = js.native
  var keyPanSpeed: Double = js.native

  var autoRotate: Boolean = js.native
  var autoRotateSpeed: Double = js.native

  var enableKeys: Boolean = js.native
  //keys: { LEFT: Double = js.native UP: Double = js.native RIGHT: Double = js.native BOTTOM: Double = js.native };
  //mouseButtons: { LEFT: MOUSE; MIDDLE: MOUSE; RIGHT: MOUSE;  };
  //touches: { ONE: TOUCH; TWO: TOUCH };

  def rotateLeft(angle: Double): Unit = js.native
  def rotateUp(angle: Double): Unit = js.native
  def panLeft(distance: Double): Unit = js.native
  def panUp(distance: Double): Unit = js.native
  def pan(deltaX: Double, deltaY: Double): Unit = js.native
  def dollyIn(dollyScale: Double): Unit = js.native
  def dollyOut(dollyScale: Double): Unit = js.native

  def update(): Unit = js.native
  def reset(): Unit = js.native
  def dispose(): Unit = js.native
  def getPolarAngle(): Double = js.native
  def getAzimuthalAngle(): Double = js.native

  // EventDispatcher mixins
  def addEventListener(`type`: String, listener: Any => Unit): Unit = js.native
  def hasEventListener(`type`: String, listener: Any => Unit): Boolean = js.native
  def removeEventListener(`type`: String, listener: Any => Unit): Unit = js.native
  def dispatchEvent(event: Event): Unit = js.native

//  def onMouseDown(event: dom.MouseEvent): Unit = js.native
//  def onMouseUp(event: dom.MouseEvent): Unit = js.native
//  def onMouseWheel(event: dom.MouseEvent): Unit = js.native
//  def onMouseMove(event: dom.MouseEvent): Unit = js.native
//  def onKeyDown(event: dom.KeyboardEvent): Unit = js.native
//  def onKeyUp(event: dom.KeyboardEvent): Unit = js.native

}
