package app.fmgp

import typings.three.loaderMod.Loader
import typings.three.mod.{Math => ThreeMath, _}
import typings.three.webGLRendererMod.WebGLRendererParameters
import typings.three.lineBasicMaterialMod.LineBasicMaterialParameters

import app.fmgp._
import scala.scalajs.js
import js.{undefined => ^}

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

  val boxGeom = new BoxGeometry(1, 1, 1, ^, ^, ^) //c.width, c.height c.depth
  val cylinderGeom = new CylinderGeometry(1.0, 1.0, 1.0, 32, ^, ^, ^, ^)
  val sphereGeom = new SphereGeometry(1.0, 32, 32, ^, ^, ^, ^)

  val parametersLine = js.Dynamic
    .literal("color" -> 0x0000ff)
    .asInstanceOf[LineBasicMaterialParameters]
  val materialLine = new LineBasicMaterial(parametersLine)

  def generateObj3D(world: geo.WorldState): Object3D = world.dimensions match {
    case geo.Dimensions.D2 => generateObj3D(world.shapes)
    case geo.Dimensions.D3 => generateObj3D(world.shapes)
  }

  def generateObj3D(shapes: Seq[geo.Shape]): Object3D = {
    def generateShape(shape: geo.Shape): Object3D = shape match {
      case geo.TransformationShape(shape, transformation) =>
        val aux = generateShape(shape)
        val m = matrix2matrix(transformation.matrix).multiply(aux.matrix)
        aux.applyMatrix(m)
        aux

      case box: geo.Box =>
        val obj = new Mesh(boxGeom, geo.SceneGraph.solidMat).asInstanceOf[Object3D]
        obj.scale.set(box.width, box.height, box.depth)
        obj

      case sphere: geo.Sphere =>
        val obj = new Mesh(sphereGeom, geo.SceneGraph.solidMat).asInstanceOf[Object3D]
        //obj.scale.set(v.radius, v.radius, v.radius)
        obj.matrixAutoUpdate = false
        val m = geo.Matrix
          .scale(sphere.radius, sphere.radius, sphere.radius)
          .preTranslate(sphere.center.x, sphere.center.y, sphere.center.z)
        obj.applyMatrix(matrix2matrix(m))
        obj

      case cylinder: geo.Cylinder =>
        val obj = new Mesh(cylinderGeom, geo.SceneGraph.solidMat).asInstanceOf[Object3D]
        obj.scale.set(cylinder.radius, cylinder.height, cylinder.radius)
        obj

      case line: geo.Line =>
        val geometryLine = new Geometry()
        line.vertices.foreach(v => geometryLine.vertices.push(new Vector3(v.x, v.y, v.z)))
        new Line(geometryLine, materialLine).asInstanceOf[Object3D]

      case c: geo.Circle =>
        val geometry = new CircleGeometry(c.radius, 32)
        val obj = if (c.fill) {
          new Mesh(geometry, materialLine).asInstanceOf[Object3D]
        } else {
          geometry.vertices.shift() //Remove center vertex
          new LineLoop(geometry, materialLine).asInstanceOf[Object3D]
        }
        obj.position.set(c.center.x, c.center.y, c.center.z)
        //TODO obj.matrixAutoUpdate = false
        obj
    }

    val tmp: Seq[Object3D] = shapes.map { s => generateShape(s) }

    val parent = new Object3D
    tmp.foreach(e => parent.add(e))
    parent
  }
}
