package app.fmgp.threejs.extras

import typings.three.loaderMod.Loader
import typings.three.mod._
import org.scalajs.dom.raw.{Event, HTMLElement}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

/**
  * FaceNormalsHelper
  * Renders arrows to visualize an object's face normals. Requires that face normals have been specified on all faces or calculated with computeFaceNormals.
  * Note that this only works with the objects whose geometry is an instance of Geometry. For BufferGeometry use a VertexNormalsHelper instead.
  */
@js.native
@JSImport(
  "three/examples/jsm/helpers/FaceNormalsHelper",
  "FaceNormalsHelper",
  "Three.FaceNormalsHelper"
)
class FaceNormalsHelper(`object`: Geometry, size: Double, color: Color, linewidth: Double)
    extends LineSegments[typings.three.geometryMod.Geometry, typings.three.materialMod.Material] {
  def update(): Unit = js.native
}
