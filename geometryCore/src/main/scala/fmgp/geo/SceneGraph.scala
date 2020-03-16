package fmgp.geo

import typings.three.loaderMod.Loader
import typings.three.mod.{Math => ThreeMath, Color => ColorT, _}

import scala.collection.mutable
import scala.scalajs.js

object SceneGraph
    extends Color
    with DefaultMaterials
    with Extrusion
    with MakeMatrixTransformedObject3D
    with PolygonSurface

/** @see [[https://github.com/pafalium/gd-web-env/blob/master/src/SceneGraph/color.js]] */
trait Color {
  def randomColor() = {
    val c = new ColorT()
    c.setHSL(Math.random(), 0.8, 0.5)
    c
  }

  def colorByHSL(hue: Double, saturation: Double, lightness: Double) = {
    val c = new ColorT()
    c.setHSL(hue, saturation, lightness)
    c
  }

  val color = js.Dynamic.literal(
    "random" -> randomColor _,
    "hsl" -> colorByHSL _
  )
}

/** @see [[https://github.com/pafalium/gd-web-env/blob/master/src/SceneGraph/default-materials.js]] */
trait DefaultMaterials {
  val solidMat = new MeshPhongMaterial()
  val surfaceMat = {
    val aux = new MeshPhongMaterial()
    aux.side = DoubleSide
    aux
  }
}

/** @see [[https://github.com/pafalium/gd-web-env/blob/master/src/SceneGraph/extrusion.js]] */
trait Extrusion //TODO

/** @see [[https://github.com/pafalium/gd-web-env/blob/master/src/SceneGraph/makeMatrixTransformedObject3D.js]] */
trait MakeMatrixTransformedObject3D {
  def makeMatrixTransformedObject3D(matrix: Matrix4) = {
    val o = new Object3D()
    o.matrixAutoUpdate = false
    o.matrix = matrix
    o
  }
}

/** @see [[https://github.com/pafalium/gd-web-env/blob/master/src/SceneGraph/polygon-surface.js]] */
trait PolygonSurface extends DefaultMaterials {

  private def polygonSurfaceGeometry(vertices: js.Array[Vector3]) = {
    val geom = new Geometry()
    geom.vertices = vertices.map(e => e: typings.three.vector3Mod.Vector3) // set vertices
    val v0 = 0 // set faces (GL_TRIANGLE_FAN)

    (1 to vertices.length).map(v1 => geom.faces.push(new Face3(v0, v1, v1 + 1)))

    geom.computeFaceNormals() // set normals
    geom.computeVertexNormals() // set normals
    geom.computeBoundingSphere() // set bounding sphere
    geom
  }

  private def toThreePolygonSurface(result: {
    def args: { def vertices: js.Array[Vector3] }
  }) {
    val vertices = result.args.vertices
    val geom = polygonSurfaceGeometry(vertices)
    new Mesh(geom, surfaceMat)
  }
  def polygonSurface = toThreePolygonSurface _
}

case class Primitive(name: String, args: Int)
case class Registry(
    primitives: mutable.Map[String, () => Primitive] = mutable.Map[String, () => Primitive]()
) {
  def provide(name: String, value: () => Primitive) =
    primitives.put(name, value)
  def defPrimitive(name: String, agrs: Seq[String]) =
    () => Primitive(name, agrs.size)
  def defPrimitiveAndProvide(name: String, agrs: Seq[String]) = {
    val aux = defPrimitive(name, agrs)
    provide(name, aux)
    aux
  }
}
