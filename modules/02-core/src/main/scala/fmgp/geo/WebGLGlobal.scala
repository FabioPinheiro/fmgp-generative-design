package fmgp.geo

import typings.three.loaderMod.Loader
import typings.three.mod.{Shape => _, _}
import typings.three.webGLRendererMod.WebGLRendererParameters
import typings.three.fontLoaderMod.Font
import typings.three.fontLoaderMod.FontLoader
import typings.three.flyControlsMod.FlyControls

import fmgp.Websocket

import fmgp.Log
import scala.scalajs.js.annotation.JSExportTopLevel
import scala.scalajs.js.annotation.JSExport

@JSExportTopLevel("WebGLTextGlobal")
object WebGLTextGlobal {
  var textFont: Font = _
  val loader = new FontLoader()
  def init = Log.info(s"### WebGLTextGlobal.init ###")
  loader.load(
    "https://raw.githubusercontent.com/mrdoob/three.js/dev/examples/fonts/gentilis_regular.typeface.json", // "fonts/helvetiker_bold.typeface.json",
    (f: Font) => textFont = f
  )
  init

  @JSExport
  var scene: Scene = _
}

class WebGLGlobal {
  val debugUI = false
  var scene: Scene = _
  var sceneUI: Scene = _
  var animateFrameId: Option[Int] = None
  var cameraUI: Option[Camera] = None
  var controls: Option[FlyControls] = None

  val uiElements: scala.collection.mutable.HashMap[Int, InteractiveMesh] = scala.collection.mutable.HashMap.empty
  def addUiElement(o: InteractiveMesh) = {
    uiElements.put(o.id.toInt, o)
    sceneUI.add(o.mesh); if (debugUI) scene.add(o.mesh.clone(true))
  }

  val raycaster = new Raycaster()

}
