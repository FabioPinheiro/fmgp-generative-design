package app.fmgp

import typings.three.loaderMod.Loader
import typings.three.mod._
import typings.three.webGLRendererMod.WebGLRendererParameters
import typings.three.lineBasicMaterialMod.LineBasicMaterialParameters

import app.fmgp._
import scala.scalajs.js
import js.{undefined => ^}
import js.JSConverters._
import scala.scalajs.js.UndefOrOps
import app.fmgp.geo.LinePath
import app.fmgp.geo.CubicBezierPath
import app.fmgp.geo.MultiPath

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
  val sphereGeom = new SphereGeometry(1.0, 32, 32, ^, ^, ^, ^)

  val parametersLine = js.Dynamic
    .literal("color" -> 0x0000ff)
    .asInstanceOf[LineBasicMaterialParameters]
  val materialLine = new LineBasicMaterial(parametersLine)

  def generateObj3D(world: geo.WorldState): Object3D = world.dimensions match {
    case geo.Dimensions.D2 => generateObj3D(world.shapes)
    case geo.Dimensions.D3 => generateObj3D(world.shapes)
  }

  case class GenerateOP(
      wireframe: Boolean = false,
      material: typings.three.materialMod.Material = geo.SceneGraph.surfaceMat
  ) {
    def withWireframe: GenerateOP = copy(wireframe = true)
    def withMaterial(material: typings.three.materialMod.Material): GenerateOP = copy(material = material)
    def toObj3D(
        geometry: typings.three.bufferGeometryMod.BufferGeometry
    ): Object3D = {
      if (wireframe) {
        val wireframe = new WireframeGeometry(geometry)
        new LineSegments(wireframe).asInstanceOf[Object3D]
      } else new Mesh(geometry, material).asInstanceOf[Object3D]
    }
    def toObj3D(
        geometry: typings.three.geometryMod.Geometry
    ): Object3D = {
      if (wireframe) {
        val wireframe = new WireframeGeometry(geometry)
        new LineSegments(wireframe).asInstanceOf[Object3D]
      } else new Mesh(geometry, material).asInstanceOf[Object3D]
    }
  }

  def multiPath2ShapePath(multiPath: geo.MultiPath): typings.three.shapeMod.Shape = {
    val sss = new typings.three.shapeMod.Shape
    var location: Option[geo.XYZ] = None
    multiPath.paths.map {
      case LinePath(vertices) =>
        vertices match {
          case Nil => //NONE
          case head +: seq =>
            location = Some(
              location
                .filter(_ == head)
                .getOrElse { sss.moveTo(head.x, head.y); head }
            )
            seq.foreach { v =>
              sss.lineTo(v.x, v.y)
              location = Some(v)
            }
        }
      case CubicBezierPath(a, af, bf, b) =>
        location = Some(
          location
            .filter(_ == a)
            .getOrElse { sss.moveTo(a.x, a.y); a }
        )
        sss.bezierCurveTo(af.x, af.y, bf.x, bf.y, b.x, b.y)
        location = Some(b)

      case MultiPath(paths) => ???
    }
    sss
  }

  def multiPath2Path(multiPath: geo.MultiPath): typings.three.pathMod.Path = {
    val sss = new typings.three.pathMod.Path
    var location: Option[geo.XYZ] = None
    multiPath.paths.map {
      case LinePath(vertices) =>
        vertices match {
          case Nil => //NONE
          case head +: seq =>
            location = Some(
              location
                .filter(_ == head)
                .getOrElse { sss.moveTo(head.x, head.y); head }
            )
            seq.foreach { v =>
              sss.lineTo(v.x, v.y)
              location = Some(v)
            }
        }
      case CubicBezierPath(a, af, bf, b) =>
        location = Some(
          location
            .filter(_ == a)
            .getOrElse { sss.moveTo(a.x, a.y); a }
        )
        sss.bezierCurveTo(af.x, af.y, bf.x, bf.y, b.x, b.y)
        location = Some(b)

      case MultiPath(paths) => ???
    }
    sss
  }

  def multiPath2Curve(multiPath: geo.MultiPath): typings.three.curveMod.Curve[typings.three.vector3Mod.Vector3] = {
    val curves = new CurvePath[typings.three.vector3Mod.Vector3]
    multiPath.paths.map {
      case LinePath(vertices) =>
        vertices match {
          case Nil => //NONE
          case head +: seq =>
            seq
              .foldLeft(head) { (a, b) =>
                val lc = new LineCurve3(new Vector3(a.x, a.y, a.z), new Vector3(b.x, b.y, b.z))
                curves.add(lc)
                b
              }
        }
      case CubicBezierPath(a, af, bf, b) =>
        val cbc = new CubicBezierCurve3(
          new Vector3(a.x, a.y, a.z),
          new Vector3(af.x, af.y, af.z),
          new Vector3(bf.x, bf.y, bf.z),
          new Vector3(b.x, b.y, b.z)
        )
        curves.add(cbc)
      case MultiPath(paths) => ???
    }
    curves
  }

  //FIXME change Object3D to typings.three.object3DMod.Object3D and remove the asInstanceOf
  def generateObj3D(shapes: Seq[geo.Shape]): Object3D = {
    def generateShape(shape: geo.Shape, state: GenerateOP): Object3D = shape match {
      case geo.Wireframe(shape) =>
        generateShape(shape, state.withWireframe)
      case geo.ShapeSeq(shapes) =>
        shapes.map(generateShape(_, state)).fold(new Object3D)((z, n) => z.add(n))
      case geo.TransformationShape(shape, transformation) =>
        val aux = generateShape(shape, state)
        aux.applyMatrix4(matrix2matrix(transformation.matrix).multiply(aux.matrix))
        aux

      case box: geo.Box =>
        val obj = state.toObj3D(boxGeom)
        obj.scale.set(box.width, box.height, box.depth)
        obj

      case sphere: geo.Sphere =>
        val obj = state.toObj3D(sphereGeom)
        //obj.scale.set(v.radius, v.radius, v.radius)
        obj.matrixAutoUpdate = false
        val m = geo.Matrix
          .scale(sphere.radius, sphere.radius, sphere.radius)
          .preTranslate(sphere.center.x, sphere.center.y, sphere.center.z)
        obj.applyMatrix4(matrix2matrix(m))
        obj

      case cylinder: geo.Cylinder =>
        val cylinderGeom = new CylinderGeometry(
          radiusTop = cylinder.topRadius,
          radiusBottom = cylinder.bottomRadius,
          height = cylinder.height,
          radiusSegments = cylinder.radialSegments.orUndefined,
          heightSegments = cylinder.heightSegments.orUndefined,
          openEnded = cylinder.openEnded.orUndefined,
          thetaStart = cylinder.thetaStart.orUndefined,
          thetaLength = cylinder.thetaLength.orUndefined
        )
        state.toObj3D(cylinderGeom)

      case torus: geo.Torus =>
        val torusGeom = new TorusGeometry(
          torus.radius,
          torus.tube,
          torus.radialSegments,
          torus.tubularSegments,
          torus.arc
        )
        state.toObj3D(torusGeom)

      case geo.Extrude(multiPath: MultiPath, holes: Seq[MultiPath], options: Option[geo.Extrude.Options]) =>
        val ooo = options
          .map { o =>
            typings.three.extrudeGeometryMod.ExtrudeGeometryOptions(
              //UVGenerator: UVGenerator = null,
              bevelEnabled = if (o.bevelEnabled.isDefined) o.bevelEnabled.get else null,
              bevelOffset = if (o.bevelOffset.isDefined) o.bevelOffset.get else null,
              bevelSegments = if (o.bevelSegments.isDefined) o.bevelSegments.get else null,
              bevelSize = if (o.bevelSize.isDefined) o.bevelSize.get else null,
              bevelThickness = if (o.bevelThickness.isDefined) o.bevelThickness.get else null,
              curveSegments = if (o.curveSegments.isDefined) o.curveSegments.get else null,
              depth = if (o.depth.isDefined) o.depth.get else null,
              extrudePath = o.extrudePath.map(e => multiPath2Curve(e)).orNull,
              steps = if (o.steps.isDefined) o.steps.get else null,
            )
          }
          .getOrElse(typings.three.extrudeGeometryMod.ExtrudeGeometryOptions())

        val jsPath = multiPath2ShapePath(multiPath)
        jsPath.holes = holes.map(e => multiPath2Path(e)).toJSArray
        state.toObj3D(new ExtrudeBufferGeometry(jsPath, ooo))

      case geo.PlaneShape(multiPath, holes: Seq[MultiPath]) =>
        val shape = multiPath2ShapePath(multiPath)
        shape.holes = holes.map(e => multiPath2Path(e)).toJSArray
        val geometry = new ShapeBufferGeometry(shape)
        new Mesh(geometry, geo.SceneGraph.basicMat).asInstanceOf[Object3D]

      case path: geo.MyPath =>
        path match {
          case multiPath: geo.MultiPath =>
            val points = multiPath2Path(multiPath).getPoints()
            val bg = new BufferGeometry().setFromPoints(points.map(e => e))
            new Line(bg, materialLine).asInstanceOf[Object3D]
          case geo.LinePath(vertices) =>
            val geometryLine = new Geometry()
            vertices.foreach(v => geometryLine.vertices.push(new Vector3(v.x, v.y, v.z)))
            new Line(geometryLine, materialLine).asInstanceOf[Object3D]
          case geo.CubicBezierPath(a, af, bf, b) =>
            val curve = new CubicBezierCurve3(
              new Vector3(a.x, a.y, a.z),
              new Vector3(af.x, af.y, af.z),
              new Vector3(bf.x, bf.y, bf.z),
              new Vector3(b.x, b.y, b.z)
            )
            val geometry = new BufferGeometry().setFromPoints(curve.getPoints(50).map(x => x))
            new Line(geometry, materialLine).asInstanceOf[Object3D]
        }
      //new Line(geometryLine, materialLine).asInstanceOf[Object3D]
      case c: geo.Circle =>
        val geometry = new CircleGeometry(c.radius, 32)
        val obj = if (c.fill) {
          state.withMaterial(materialLine).toObj3D(geometry)
        } else {
          geometry.vertices.shift() //Remove center vertex
          new LineLoop(geometry, materialLine).asInstanceOf[Object3D]
        }
        obj.position.set(c.center.x, c.center.y, c.center.z)
        //TODO obj.matrixAutoUpdate = false
        obj
    }

    val tmp: Seq[Object3D] = shapes.map { s => generateShape(s, GenerateOP()) }

    val parent = new Object3D
    tmp.foreach(e => parent.add(e))
    parent
  }
}
