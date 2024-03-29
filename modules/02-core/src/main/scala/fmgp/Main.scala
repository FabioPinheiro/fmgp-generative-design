package fmgp

import typings.three.loaderMod.Loader
import typings.three.mod.{Shape => _, _}
import typings.three.anon.{X => AnonX}
import typings.three.webGLRendererMod.WebGLRendererParameters
import typings.statsJs.mod.{^ => Stats}

import fmgp.geo._
import fmgp.Utils
import scala.scalajs.js
import scala.scalajs.js.annotation._
import java.awt.geom.Dimension2D

import scala.util.chaining._

//TODO move this file to TEST
object Main {
  val webGLHelper = WebGLHelper(topPadding = 0)

  def main(args: Array[String]): Unit = {
    WebGLTextGlobal.init
    org.scalajs.dom.document.body.appendChild(webGLHelper.renderer.domElement)
    webGLHelper.renderer.domElement.style = "position: fixed; top: 0px; left: 0px;"
    js.timers.setTimeout(1000)(webGLHelper.init) // milliseconds FIXME
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
  var test: org.scalajs.dom.Event = _
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
