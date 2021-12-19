package fmgp.threejs.extras

import typings.three.loaderMod.Loader
import typings.three.mod._
import org.scalajs.dom.raw.{Event, HTMLElement}

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSImport}

/** FlyControls
  * @param object
  *   the camera to be controlled.
  * @param domElement
  *   the HTMLDOMElement used to listen for mouse / touch events. This must be passed in the constructor; changing it
  *   here will not set up new event listeners.
  */
@js.native
@JSImport(
  "three/examples/jsm/controls/FlyControls",
  "FlyControls",
  // "Three.FlyControls"
)
class FlyControls(`object`: Camera, domElement: HTMLElement) extends js.Object {

  def this(`object`: Camera) = this(`object`, null) // scalastyle:ignore

  // API
  /** If set to true, the camera automatically moves forward (and does not stop) when initially translated. Default is
    * false.
    */
  var autoForward: Boolean = js.native

  /** If set to true, you can only look around by performing a drag interaction. Default is false. */
  var dragToLook: Boolean = js.native

  /** The movement speed. Default is 1. */
  var movementSpeed: Number = js.native

  /** The rotation speed. Default is 0.005. */
  var rollSpeed: Number = js.native

  /** Should be called if the controls is no longer required. */
  def dispose: Unit = js.native

  /** delta: Time delta value. Updates the controls. Usually called in the animation loop. */
  def update(delta: Number): Unit = js.native

}
