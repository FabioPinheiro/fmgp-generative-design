package fmgp.geo.webapp

import com.raquo.airstream.state.Var

import fmgp.geo.World
import fmgp.geo.VisualizerJSLive
import fmgp.geo.Visualizer._
import fmgp.geo.MesherLive

object AppGlobal {
  val worldVar = Var[World](initial = World.w3DEmpty)

  def onWorldUpdate(world: World): Unit = {
    AppGlobal.worldVar.set(world)
  }

  VisualizerJSLive.callbackHack = onWorldUpdate _ //HACK

  def setWorld(world: World) =
    zio.Runtime.global.unsafeRunToFuture(
      (if (world.shapes.isEmpty) clean else update(world))
        .provide(VisualizerJSLive.live, MesherLive.live)
    )
}
