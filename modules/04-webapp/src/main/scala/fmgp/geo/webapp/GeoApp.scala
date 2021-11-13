package fmgp.geo.webapp

import scala.util.chaining._
import scala.scalajs.js
import scala.scalajs.js.annotation._
import org.scalajs.dom

import com.raquo.laminar.api.L.{_, given}

import typings.three.loaderMod.Loader
import typings.three.mod.{Shape => _, _}
import typings.three.anon.{X => AnonX}
import typings.three.webGLRendererMod.WebGLRendererParameters

import fmgp._
import fmgp.geo._
import fmgp.threejs.extras.FlyControls
import com.raquo.laminar.keys.ReactiveEventProp

@JSExportTopLevel("GeoApp")
object GeoApp {

  // Need to start (autoReconnect is lazy)
  WebsocketJSLive.autoReconnect

  val geoCanvasHack = div(
    onMountCallback(ctx => {
      VisualizerJSLive.webGLHelper.init
      StatsComponent.append(ctx.thisNode.ref)
      VisualizerJSLive.webGLHelper.append(ctx.thisNode.ref)
    })
  )

  val rootElement = geoCanvasHack

  def apply(): HtmlElement = rootElement
}
