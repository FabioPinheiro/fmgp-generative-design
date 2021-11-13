package fmgp.geo

import typings.three.mod.Object3D
import fmgp.geo
import zio._

trait Mesher {
  def generateObj3D(shapes: => Seq[geo.Shape]): UIO[Object3D]
}

// Accessor Methods Inside the Companion Object
object Mesher {
  def generateObj3D(shapes: => Seq[geo.Shape]): URIO[Has[Mesher], Object3D] =
    ZIO.serviceWith(_.generateObj3D(shapes))
}

class MesherLive extends Mesher {
  def generateObj3D(shapes: => Seq[geo.Shape]): UIO[Object3D] =
    UIO(WorldImprovements.generateObj3D(shapes))
}

object MesherLive {
  val live: Layer[Nothing, Has[Mesher]] = ZLayer.succeed[Mesher](new MesherLive) //(Tag[Mesher], Tracer.newTrace)
}
