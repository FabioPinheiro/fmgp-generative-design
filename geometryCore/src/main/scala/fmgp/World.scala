package fmgp

import fmgp._
import fmgp.geo._
import scala.scalajs.js
import fmgp.threejs.Object3D
import fmgp.threejs.Three._
import fmgp.threejs.Matrix4

object WorldImprovements {

  def matrix2matrix(m: geo.Matrix): Matrix4 = {
    val aux = new Matrix4
    // format: off
    aux.set(
      m.m00, m.m01, m.m02, m.m03,
      m.m10, m.m11, m.m12, m.m13,
      m.m20, m.m21, m.m22, m.m23,
      m.m30, m.m31, m.m32, m.m33
    )
    // format: on
    aux
  }

  val boxGeom = new threejs.BoxGeometry(1, 1, 1) //c.width, c.height, c.depth)
  val cylinderGeom = new threejs.CylinderGeometry(1.0, 1.0, 1.0, 32)
  val sphereGeom = new threejs.SphereGeometry(1.0, 32, 32)

  val parametersLine = js.Dynamic
    .literal("color" -> 0x0000ff)
    .asInstanceOf[threejs.LineBasicMaterialParameters]
  val materialLine = new threejs.LineBasicMaterial(parametersLine)

  def generateObj3D(world: World): Object3D = world.dimensions match {
    case Dimensions.D2 => generateObj3D(world.shapes)
    case Dimensions.D3 => generateObj3D(world.shapes)
  }

  def generateObj3D(shapes: Seq[Shape]): Object3D = {
    def generateShape(shape: Shape): Object3D = shape match {
      case TransformationShape(shape, transformation) =>
        val aux = generateShape(shape)
        val m = matrix2matrix(transformation.matrix).multiply(aux.matrix)
        aux.applyMatrix(m)
        aux

      case box: Box =>
        val obj = new threejs.Mesh(boxGeom, SceneGraph.solidMat)
        obj.scale.set(box.width, box.height, box.depth)
        obj

      case sphere: Sphere =>
        val obj = new threejs.Mesh(sphereGeom, SceneGraph.solidMat)
        //obj.scale.set(v.radius, v.radius, v.radius)
        obj.matrixAutoUpdate = false
        val m = geo.Matrix
          .scale(sphere.radius, sphere.radius, sphere.radius)
          .preTranslate(sphere.center.x, sphere.center.y, sphere.center.z)
        obj.applyMatrix(matrix2matrix(m))
        obj

      case cylinder: Cylinder =>
        val obj = new threejs.Mesh(cylinderGeom, SceneGraph.solidMat)
        obj.scale.set(cylinder.radius, cylinder.height, cylinder.radius)
        obj

      case line: Line =>
        val geometryLine = new threejs.Geometry()
        line.vertices.foreach(v =>
          geometryLine.vertices.push(new threejs.Vector3(v.x, v.y, v.z))
        )
        new threejs.Line(geometryLine, materialLine)

      case c: Circle =>
        val geometry = new threejs.CircleGeometry(c.radius, 32)
        val obj = if (c.fill) {
          new threejs.Mesh(geometry, materialLine)
        } else {
          geometry.vertices.shift() //Remove center vertex
          new threejs.LineLoop(geometry, materialLine)
        }
        obj.position.set(c.center.x, c.center.y, c.center.z)
        //TODO obj.matrixAutoUpdate = false
        obj
    }

    val tmp: Seq[Object3D] = shapes.map { s =>
      generateShape(s)
    }

    val parent = new Object3D
    tmp.foreach(parent.add)
    parent
  }
}
