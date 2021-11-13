package fmgp.geo

import zio._
import typings.three.mod.Object3D

trait Visualizer {
  def update(world: => World): UIO[Unit]
  def clean: UIO[Unit]
}

// Accessor Methods Inside the Companion Object
object Visualizer {
  def update(world: => World): URIO[Has[Visualizer], Unit] = ZIO.serviceWith(_.update(world))
  def clean: URIO[Has[Visualizer], Unit] = ZIO.serviceWith(_.clean)
}

case class VisualizerJSLive(mesher: Mesher) extends Visualizer {
  // onWorldUpdate: (world: World) => Unit

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
        } //toList method is needed because the obj.remove edit over the array we are iterating
      }
}

object VisualizerJSLive {
  val modelToAnimate: Object3D = new Object3D()
  val webGLHelper = new WebGLHelper(topPadding = 64, modelToAnimate)
  var callbackHack: World => Unit = (_) => () //FIXME

  lazy val live: URLayer[Has[Mesher], Has[Visualizer]] =
    (VisualizerJSLive(_)).toLayer[Visualizer]
}
