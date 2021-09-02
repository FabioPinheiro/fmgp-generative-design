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
import com.raquo.laminar.keys.ReactiveEventProp

@JSExportTopLevel("GeoApp")
object GeoApp {
  val topPadding = 64 //76

  def onWorldUpdate(world: World): Unit = {
    AppGlobal.worldVar.set(world)
  }

  lazy val webGLHelper = new WebGLHelper(topPadding = topPadding, onWorldUpdate)

  val geoCanvasHack = div(
    onMountCallback(ctx => {
      webGLHelper.init
      StatsComponent.append(ctx.thisNode.ref)
      webGLHelper.append(ctx.thisNode.ref)
    }),
  )

  val rootElement = geoCanvasHack

  def apply(): HtmlElement =
    rootElement
}
