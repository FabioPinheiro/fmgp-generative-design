package fmgp.geo

import zio._
import typings.three.mod.Group
import typings.three.eventDispatcherMod.Event

trait Visualizer {
  def update(world: => World): UIO[Unit]
  def clean: UIO[Unit]
}

// Accessor Methods Inside the Companion Object
object Visualizer {
  def update(world: => World): URIO[Visualizer, Unit] = ZIO.serviceWithZIO(_.update(world))
  def clean: URIO[Visualizer, Unit] = ZIO.serviceWithZIO(_.clean)
}

case class VisualizerJSLive(mesher: Mesher) extends Visualizer {
  def update(world: => World): UIO[Unit] = {
    UIO(VisualizerJSLive.callbackHack(world)) <&>
      mesher
        .generateObj3D(world.shapes)
        .map(e => VisualizerJSLive.modelToAnimate.add(e))
        .map(_ => ())
  }
  // /.map(e => onWorldUpdate(e))
  def clean: UIO[Unit] =
    UIO(VisualizerJSLive.callbackHack(World.w3DEmpty)) <&>
      UIO {
        VisualizerJSLive.modelToAnimate.children.toList.foreach { o =>
          VisualizerJSLive.modelToAnimate.remove(o)
        } // toList method is needed because the obj.remove edit over the array we are iterating
      }
}

object VisualizerJSLive {
  val modelToAnimate: Group = new Group
  val webGLHelper = new WebGLHelper(topPadding = 64, modelToAnimate)
  def height = webGLHelper.height
  def width = webGLHelper.width

  var callbackHack: World => Unit = (_) => () // FIXME

  lazy val live: URLayer[Mesher, Visualizer] =
    (VisualizerJSLive(_)).toLayer[Visualizer]
}
