package app.fmgp.geo.webapp

import com.raquo.airstream.state.Var

import app.fmgp.geo.World
import app.fmgp.geo.VisualizerJSLive
import app.fmgp.geo.Visualizer._
import app.fmgp.geo.MesherLive

object AppGlobal {
  val worldVar = Var[World](initial = World.w3DEmpty)

  def onWorldUpdate(world: World): Unit = {
    AppGlobal.worldVar.set(world)
  }

  VisualizerJSLive.callbackHack = onWorldUpdate _ //HACK

  def setWorld(world: World) =
    zio.Runtime.global.unsafeRunToFuture(
      (if (world.shapes.isEmpty) clean else update(world))
        .inject(VisualizerJSLive.live, MesherLive.live)
    )
}
