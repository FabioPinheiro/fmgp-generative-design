package app.fmgp

import typings.three.loaderMod.Loader
import typings.three.mod.{Shape => _, _}
import typings.three.anon.{X => AnonX}
import typings.three.webGLRendererMod.WebGLRendererParameters
import typings.statsJs.mod.{^ => Stats}

import app.fmgp.geo._
import app.fmgp.Utils
import org.scalajs.dom
import scala.scalajs.js
import scala.scalajs.js.annotation._
import java.awt.geom.Dimension2D
import app.fmgp.geo.{Object3DWarp, GeoWarp, WorldWarp, DynamicWorldWarp}
import org.scalajs.dom.raw.{Event, Element}

//import js.{undefined => ^}
//import org.scalajs.logging.Logger

import scala.util.chaining._

object Main {
  def onClickEvent(event: dom.MouseEvent) = {
    // calculate mouse position in normalized device coordinates (-1 to +1) for both components
    val x = (event.clientX / dom.window.innerWidth) * 2 - 1
    val y = -(event.clientY / dom.window.innerHeight) * 2 + 1
    WebGLGlobal.uiEvent = Some(AnonX(x, y))
  }
  def onTouchEvent(event: dom.TouchEvent) = {
    val x = (event.touches(0).screenX / dom.window.innerWidth) * 2 - 1
    val y = -(event.touches(0).screenY / dom.window.innerHeight) * 2 + 1
    WebGLGlobal.uiEvent = Some(AnonX(x, y))
  }
  dom.window.addEventListener("click", onClickEvent, false)
  dom.window.addEventListener("ontouch", onTouchEvent, false) //TODO need to test this

  val webGLHelper = WebGLHelper(topPadding = 0)

  def main(args: Array[String]): Unit = {
    WebGLGlobal.init
    dom.document.body.appendChild(webGLHelper.renderer.domElement)
    webGLHelper.renderer.domElement.style = "position: fixed; top: 0px; left: 0px;"
    js.timers.setTimeout(1000)(webGLHelper.init) //milliseconds FIXME
    ()
  }

}

//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//

trait FooOptions extends js.Object {
  val a: Int
  val b: String
  val c: js.UndefOr[Boolean]
}
@JSExportTopLevel("Fabio")
object Fabio {

  /** $m_Lfmgp_Fabio$().test */
  @JSExport
  var test: Event = _
  @JSExport
  var any: Any = _

  /** (new $c_Lfmgp_Fabio$).f() */
  @JSExport
  def f() = {
    println("F:")
    test
  }

  @JSExport
  def foo(options: FooOptions): String = {
    val a = options.a
    val b = options.b
    val c = options.c.getOrElse(false)
    // do something with a, b, c
    s"$a $b $c"
  }
}
