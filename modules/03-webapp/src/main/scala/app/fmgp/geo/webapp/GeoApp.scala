package app.fmgp.geo.webapp

import scala.util.chaining._
import scala.scalajs.js
import scala.scalajs.js.annotation._
import org.scalajs.dom

import com.raquo.laminar.api.L.{_, given}

import typings.three.loaderMod.Loader
import typings.three.mod.{Shape => _, _}
import typings.three.anon.{X => AnonX}
import typings.three.webGLRendererMod.WebGLRendererParameters
//import typings.statsJs.mod.{^ => Stats}

import app.fmgp._
import app.fmgp.geo._
import app.fmgp.threejs.extras.FlyControls //FirstPersonControls, FlyControls, OrbitControls

@JSExportTopLevel("GeoApp")
object GeoApp {
  val topPadding = 76
  lazy val webGLHelper = new WebGLHelper(topPadding = topPadding)

  val clickObserver = Observer[dom.MouseEvent](onNext = ev => onClickEvent(ev))
  val touchObserver = Observer[dom.TouchEvent](onNext = ev => onTouchEvent(ev))

  val geoCanvasHack = div(
    onMountCallback(ctx => webGLHelper.init),
    //onClick.useCapture --> captureModeClickObserver)
    onClick.useBubbleMode --> clickObserver,
    //onTouch.useBubbleMode --> clickObserver,
  )
  geoCanvasHack.ref.appendChild(webGLHelper.renderer.domElement)
  //webGLHelper.renderer.domElement.style = "z-index:-1; position: fixed; top: 0px; left: 0px;"
  webGLHelper.renderer.domElement.style = "z-index:-1; left: 0px;"

  def onClickEvent(event: dom.MouseEvent) = {
    // calculate mouse position in normalized device coordinates (-1 to +1) for both components
    val x = (event.clientX / dom.window.innerWidth) * 2 - 1
    val y = -((event.clientY - topPadding) / dom.window.innerHeight) * 2 + 1
    WebGLGlobal.uiEvent = Some(AnonX(x, y))
  }
  def onTouchEvent(event: dom.TouchEvent) = {
    val x = (event.touches(0).screenX / dom.window.innerWidth) * 2 - 1
    val y = -((event.touches(0).screenY - topPadding) / dom.window.innerHeight) * 2 + 1
    WebGLGlobal.uiEvent = Some(AnonX(x, y))
  }
  dom.window.addEventListener("click", onClickEvent, false)
  dom.window.addEventListener("ontouch", onTouchEvent, false) //TODO need to test this

  val rootElement = geoCanvasHack
// In most other examples, containerNode will be set to this behind the scenes
  //val containerNode = dom.document.querySelector("#mdoc-html-run0")
  //render(containerNode, rootElement)

  def apply(): HtmlElement =
    rootElement
}
