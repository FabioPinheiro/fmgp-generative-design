package app.fmgp

import typings.three.mod._
import app.fmgp.geo._

sealed trait GeoWarp {
  def generateObj3D: Object3D
  def dimensions: Dimensions
}

object GeoWarp {
  implicit class WorldWarp(val world: World) {
    def warp = WorldWarp(world)
  }
}
case class WorldWarp(world: WorldState) extends GeoWarp {
  lazy private val aux = WorldImprovements.generateObj3D(world)
  def generateObj3D: Object3D = aux
  def dimensions: Dimensions = world.dimensions
}

class DynamicWorldWarp extends GeoWarp {
  override def dimensions: Dimensions = Dimensions.D3
  override def generateObj3D: Object3D = obj

  private var multiBodyWorld: Seq[World] = Seq(World.w3DEmpty)
  private val obj: Object3D = new Object3D()

  def update(world: World) = {
    world match {
      case WorldAddition(shapes) =>
        multiBodyWorld = multiBodyWorld :+ world
        val aux: Object3D = WorldImprovements.generateObj3D(world.shapes)
        obj.add(aux)
      case WorldState(shapes, dimensions) =>
        obj.children.toList.foreach(o => obj.remove(o))
        //toList method is needed because the obj.remove edit over the array we are iterating
        val aux: Object3D = WorldImprovements.generateObj3D(world.shapes)
        obj.add(aux)
    }
  }
}
object DynamicWorldWarp {
  def apply() = new DynamicWorldWarp
}

case class Object3DWarp(
    dimensions: Dimensions,
    parent: Object3D = new Object3D
) extends GeoWarp {
  def generateObj3D: Object3D = parent
  def add(obj: Object3D) = {
    parent.add(obj)
    this
  }
}
