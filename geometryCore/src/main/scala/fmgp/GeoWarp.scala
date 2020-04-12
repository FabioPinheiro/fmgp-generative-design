package app.fmgp

import typings.three.mod._
import app.fmgp.geo._

sealed trait GeoWarp {
  def generateObj3D: Option[Object3D]
  def dimensions: Dimensions.D
}

object GeoWarp {
  implicit class WorldWarp(val world: World) {
    def warp = WorldWarp(world)
  }
}

case class NoneWarp() extends GeoWarp {
  def generateObj3D: Option[Object3D] = None
  def dimensions: Dimensions.D = Dimensions.D3
}

case class WorldWarp(world: WorldState) extends GeoWarp {
  lazy private val aux = WorldImprovements.generateObj3D(world)
  def generateObj3D: Option[Object3D] = Some(aux)
  def dimensions: Dimensions.D = world.dimensions
}

class DynamicWorldWarp extends GeoWarp {
  override def dimensions: Dimensions.D = Dimensions.D3
  override def generateObj3D: Option[Object3D] = Some(tmp)

  private var multiBodyWorld: Seq[World] = Seq(World.w3DEmpty)
  private val tmp: Object3D = new Object3D()

  def world = ??? //FIXME
  def world_=(w: WorldState): Unit = ??? //FIXME

  def merge(w: World) = {
    multiBodyWorld = multiBodyWorld :+ w
    val aux: Object3D = WorldImprovements.generateObj3D(w.shapes)
    tmp.add(aux)
  }
}
object DynamicWorldWarp {
  def apply() = new DynamicWorldWarp
}

case class Object3DWarp(
    dimensions: Dimensions.D,
    parent: Object3D = new Object3D
) extends GeoWarp {
  def generateObj3D: Option[Object3D] = Some(parent)
  def add(obj: Object3D) = {
    parent.add(obj)
    this
  }
}
