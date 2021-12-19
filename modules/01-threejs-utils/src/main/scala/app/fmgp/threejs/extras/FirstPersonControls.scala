package fmgp.threejs.extras

import typings.three.loaderMod.Loader
import typings.three.mod._
import org.scalajs.dom.raw.{Event, HTMLElement}

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSImport}

/** FirstPersonControls
  * @param object
  *   the camera to be controlled.
  * @param domElement
  *   the HTMLDOMElement used to listen for mouse / touch events. This must be passed in the constructor; changing it
  *   here will not set up new event listeners.
  */
@js.native
@JSImport(
  "three/examples/jsm/controls/FirstPersonControls",
  "FirstPersonControls",
  // "Three.FirstPersonControls"
)
class FirstPersonControls(`object`: Camera, domElement: HTMLElement) extends js.Object {

  def this(`object`: Camera) = this(`object`, null) // scalastyle:ignore

  // API
  /** Whether or not it's possible to look around. Default is true. */
  var activeLook: Boolean = js.native

  /** Whether or not the camera is automatically moved forward. Default is false. */
  var autoForward: Boolean = js.native

  /** Whether or not looking around is vertically constrained by [.verticalMin, .verticalMax]. Default is false. */
  var constrainVertical: Boolean = js.native

  /** Whether or not the controls are enabled. Default is true. */
  var enabled: Boolean = js.native

  /** Determines how much faster the camera moves when it's y-component is near .heightMax. Default is 1. */
  var heightCoef: Number = js.native

  /** Upper camera height limit used for movement speed adjusment. Default is 1. */
  var heightMax: Number = js.native

  /** Lower camera height limit used for movement speed adjusment. Default is 0. */
  var heightMin: Number = js.native

  /** Whether or not the camera's height influences the forward movement speed. Default is false. Use the properties
    * .heightCoef, .heightMin and .heightMax for configuration.
    */
  var heightSpeed: Boolean = js.native

  /** Whether or not it's possible to vertically look around. Default is true. */
  var lookVertical: Boolean = js.native

  /** The look around speed. Default is 0.005. */
  var lookSpeed: Number = js.native

  /** Whether or not the mouse is pressed down. Read-only property. */
  val mouseDragOn: Boolean = js.native

  /** The movement speed. Default is 1. */
  var movementSpeed: Number = js.native

  /** How far you can vertically look around, upper limit. Range is 0 to Math.PI radians. Default is Math.PI. */
  var verticalMax: Number = js.native

  /** How far you can vertically look around, lower limit. Range is 0 to Math.PI radians. Default is 0. */
  var verticalMin: Number = js.native

  /** Should be called if the application window is resized. */
  def handleResize: Unit = js.native

  /** Ensures the controls orient the camera towards the defined target position.
    * @param vector
    *   a vector representing the target position.
    */
  def lookAt(vector: Vector3): FirstPersonControls = js.native

  /** Ensures the controls orient the camera towards the defined target position. Optionally, the x, y, z components of
    * the world space position.
    */
  def lookAt(x: Double, y: Double, z: Double): FirstPersonControls = js.native

  /** Updates the controls. Usually called in the animation loop.
    * @param delta:
    *   Time delta value.
    */
  def update(delta: Number): Unit = js.native
}
