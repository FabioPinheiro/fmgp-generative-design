package fmgp.geo

import fmgp.typings.three.object3DMod.Object3D
import fmgp.geo
import zio._
import fmgp.typings.three.eventDispatcherMod.Event

trait Mesher {
  def generateObj3D(shapes: => Seq[geo.Shape]): UIO[Object3D[Event]]
}

// Accessor Methods Inside the Companion Object
object Mesher {
  def generateObj3D(shapes: => Seq[geo.Shape]): URIO[Mesher, Object3D[Event]] =
    ZIO.serviceWithZIO(_.generateObj3D(shapes))
}

class MesherLive extends Mesher {
  def generateObj3D(shapes: => Seq[geo.Shape]): UIO[Object3D[Event]] =
    ZIO.succeed(WorldImprovements.generateObj3D(shapes))
}

object MesherLive {
  val live: Layer[Nothing, Mesher] =
    ZLayer.succeed[Mesher](new MesherLive) // (Tag[Mesher], Tracer.newTrace)
}
