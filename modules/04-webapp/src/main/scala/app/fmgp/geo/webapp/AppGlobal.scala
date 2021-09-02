package app.fmgp.geo.webapp

//import com.raquo.laminar.api.L._
import com.raquo.airstream.state.Var

import app.fmgp.geo.World
import app.fmgp.geo.WebGLHelper

object AppGlobal {
  val worldVar = Var[World](initial = World.w3DEmpty)

  def onWorldUpdate(world: World): Unit = {
    AppGlobal.worldVar.set(world)
  }

  lazy val webGLHelper = new WebGLHelper(topPadding = 64, onWorldUpdate)

  def setWorld(world: World) = webGLHelper.webGLGlobal.masterWorld.update(world)
}
