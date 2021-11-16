package fmgp.geo

import typings.three.loaderMod.Loader
import typings.three.mod._
import typings.three.webGLRendererMod.WebGLRendererParameters
import typings.three.lineBasicMaterialMod.LineBasicMaterialParameters
import typings.three.eventDispatcherMod.Event
import typings.three.constantsMod.BuiltinShaderAttributeName
import typings.three.textGeometryMod.TextGeometry

import fmgp._
import scala.scalajs.js
import js.{undefined => ^}
import js.JSConverters._
import scala.scalajs.js.UndefOrOps
import fmgp.geo.LinePath
import fmgp.geo.CubicBezierPath
import fmgp.geo.MultiPath
import fmgp.geo.{Vec, XYZ}

import scala.util.chaining._

import scala.scalajs.js.annotation.JSExportTopLevel
import scala.scalajs.js.annotation.JSExport

@JSExportTopLevel("WorldImprovements")
object WorldImprovements {

  @JSExport
  var any: js.Any = _

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
  def matrixJs2matrix(m: typings.three.matrix4Mod.Matrix4): geo.Matrix = {
    val aux = m.elements
    // format: off
    geo.Matrix(
      m00 = aux(0), m01= aux(1), m02= aux(2), m03= aux(3),
      m10 = aux(4), m11= aux(5), m12= aux(6), m13= aux(7),
      m20 = aux(8), m21= aux(9), m22= aux(10), m23= aux(11),
      m30 = aux(12), m31= aux(13), m32= aux(14), m33= aux(15)
    )
    // format: on
  }

  @inline def float2ArrayLike(points: Seq[Float]): typings.std.ArrayLike[Double] =
    new scala.scalajs.js.typedarray.Float32Array(points.toJSIterable)
      .asInstanceOf[typings.std.ArrayLike[Double]] //FIXME ... TS

  @inline def arrayLike2Float(array: typings.std.ArrayLike[Double]): Array[Float] =
    array.asInstanceOf[scala.scalajs.js.typedarray.Float32Array].jsIterator.toIterator.toArray

  @inline def float2BufferAttribute(points: Seq[Float]): BufferAttribute =
    new BufferAttribute(float2ArrayLike(points), 3)

  val boxGeom = new BoxGeometry(1, 1, 1, ^, ^, ^) //c.width, c.height c.depth
  val sphereGeom = new SphereGeometry(1.0, 32, 32, ^, ^, ^, ^)

  def parametersLine(color: Double, width: Double) =
    js.Dynamic
      .literal("color" -> color, "linewidth" -> width)
      .asInstanceOf[LineBasicMaterialParameters]

  val materialLine = new LineBasicMaterial(parametersLine(0x999999, 1))
  val materialLineRed = new LineBasicMaterial(parametersLine(0xff0000, 3))
  val materialLineGreen = new LineBasicMaterial(parametersLine(0x00ff00, 3))
  val materialLineBlue = new LineBasicMaterial(parametersLine(0x0000ff, 3))

  def generateObj3D(world: geo.WorldState): Object3D[Event] = world.dimensions match {
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
    ): Object3D[Event] = {
      if (wireframe) {
        val wireframe = new WireframeGeometry(geometry)
        new LineSegments(wireframe).asInstanceOf[Object3D[Event]]
      } else new Mesh(geometry, material).asInstanceOf[Object3D[Event]]
    }
    def toObj3D(
        geometry: typings.three.geometryMod.Geometry
    ): Object3D[Event] = {
      if (wireframe) {
        val wireframe = new WireframeGeometry(geometry)
        new LineSegments(wireframe).asInstanceOf[Object3D[Event]]
      } else new Mesh(geometry, material).asInstanceOf[Object3D[Event]]
    }
  }

  def multiPath2ShapePath(multiPath: geo.MultiPath): typings.three.shapeMod.Shape = {
    val sss = new typings.three.shapeMod.Shape
    var location: Option[XYZ] = None
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
    var location: Option[XYZ] = None
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

  def multiPath2Curve(
      multiPath: geo.MultiPath
  ): typings.three.curvePathMod.CurvePath[typings.three.vector3Mod.Vector3] = {
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
  def generateObj3D(shapes: Seq[geo.Shape]): Object3D[Event] = {
    def generateShape(shape: geo.Shape, state: GenerateOP): Object3D[Event] = shape match {
      case geo.Wireframe(shape) =>
        generateShape(shape, state.withWireframe)
      case geo.ShapeSeq(shapes) =>
        shapes.map(generateShape(_, state)).fold(new Object3D[Event])((z, n) => z.add(n))
      case geo.TransformationShape(shape, transformation) =>
        val aux = generateShape(shape, state)
        aux.applyMatrix4(matrix2matrix(transformation.matrix).multiply(aux.matrixWorld))
        aux

      case geo.Points(points) =>
        val aux = new Float32BufferAttribute(
          float2ArrayLike(points.flatMap(p => Seq(p.x.toFloat, p.y.toFloat, p.z.toFloat))),
          3
        )
        val geometryPoint = new BufferGeometry().tap(_.setAttribute(BuiltinShaderAttributeName.position, aux))
        new Points(geometryPoint, geo.SceneGraph.pointMat).asInstanceOf[Object3D[Event]]

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
          radialSegments = cylinder.radialSegments.map(_.toDouble).orUndefined,
          heightSegments = cylinder.heightSegments.map(_.toDouble).orUndefined,
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
            val aux = js.Dynamic.literal().asInstanceOf[typings.three.extrudeGeometryMod.ExtrudeGeometryOptions]
            if (o.bevelEnabled.isDefined) aux.bevelEnabled = o.bevelEnabled.get
            if (o.bevelOffset.isDefined) aux.bevelOffset = o.bevelOffset.get
            if (o.bevelSegments.isDefined) aux.bevelSegments = o.bevelSegments.get
            if (o.bevelSize.isDefined) aux.bevelSize = o.bevelSize.get
            if (o.bevelThickness.isDefined) aux.bevelThickness = o.bevelThickness.get
            if (o.curveSegments.isDefined) aux.curveSegments = o.curveSegments.get
            if (o.depth.isDefined) aux.depth = o.depth.get
            o.extrudePath.map(e => multiPath2Curve(e)).map(e => aux.extrudePath = e)
            if (o.steps.isDefined) aux.steps = o.steps.get
            aux
          }
          .getOrElse(typings.three.extrudeGeometryMod.ExtrudeGeometryOptions())

        val jsPath = multiPath2ShapePath(multiPath)
        jsPath.holes = holes.map(e => multiPath2Path(e)).toJSArray
        state.toObj3D(new ExtrudeBufferGeometry(jsPath, ooo))

      case geo.PlaneShape(multiPath, holes: Seq[MultiPath]) =>
        val shape = multiPath2ShapePath(multiPath)
        shape.holes = holes.map(e => multiPath2Path(e)).toJSArray
        val geometry = new ShapeBufferGeometry(shape)
        new Mesh(geometry, geo.SceneGraph.basicMat).asInstanceOf[Object3D[Event]]

      case geo.SurfaceGridShape(points: Array[Array[XYZ]]) =>
        def xyzMatrix2Triangles(a: Array[Array[XYZ]]) = {
          val yLength = a.length - 1
          val xLength = a.head.length - 1
          for (y <- 0 until yLength; x <- 0 until xLength)
            //println(s"${(y,x)}${(y,x+1)}${(y+1,x)}  ${(y,x+1)}${(y+1,x)}${(y+1,x+1)}")
            yield {
              geo.Triangle.toSeqFloat(a(y)(x), a(y)(x + 1), a(y + 1)(x)) ++
                geo.Triangle.toSeqFloat(a(y)(x + 1), a(y + 1)(x + 1), a(y + 1)(x))
            }
        }.flatten
        val t = xyzMatrix2Triangles(points)
        val geometry = new BufferGeometry()
          .tap(_.setAttribute(BuiltinShaderAttributeName.position, float2BufferAttribute(t)))
          //.tap(_.setAttribute("normal", float2BufferAttribute(n.toSeqFloat)))
          .tap(_.computeVertexNormals())

        //val op = state.withMaterial(geo.SceneGraph.surfaceNormalMat)
        state.toObj3D(geometry)

      case path: geo.MyPath =>
        path match {
          case multiPath: geo.MultiPath =>
            val points = multiPath2Curve(multiPath).getPoints()
            val bg = new BufferGeometry().setFromPoints(points.map(e => e))
            new Line(bg, materialLine).asInstanceOf[Object3D[Event]]
          case geo.LinePath(vertices) =>
            val geometryLine = new BufferGeometry()
              .tap(
                _.setAttribute(
                  BuiltinShaderAttributeName.position,
                  float2BufferAttribute(vertices.map(e => Seq(e.x.toFloat, e.y.toFloat, e.z.toFloat)).flatten)
                )
              )
            new Line(geometryLine, materialLine).asInstanceOf[Object3D[Event]]
          case geo.CubicBezierPath(a, af, bf, b) =>
            val curve = new CubicBezierCurve3(
              new Vector3(a.x, a.y, a.z),
              new Vector3(af.x, af.y, af.z),
              new Vector3(bf.x, bf.y, bf.z),
              new Vector3(b.x, b.y, b.z)
            )
            val geometry = new BufferGeometry().setFromPoints(curve.getPoints(50).map(x => x))
            new Line(geometry, materialLine).asInstanceOf[Object3D[Event]]
        }
      case c: geo.Circle =>
        val geometry = new CircleGeometry(c.radius, 32)
        val obj = if (c.fill) {
          state.withMaterial(materialLine).toObj3D(geometry)
        } else {
          val positionHack = geometry
            .getAttribute(BuiltinShaderAttributeName.position)
            .asInstanceOf[typings.three.bufferAttributeMod.BufferAttribute]
          val ccc: Seq[Float] = arrayLike2Float(positionHack.array).toIndexedSeq.drop(3) // drop one point (group of 3)
          val geometryAux = new BufferGeometry()
            .tap(_.setAttribute(BuiltinShaderAttributeName.position, float2BufferAttribute(ccc)))
          new LineLoop(geometryAux, materialLine).asInstanceOf[Object3D[Event]] //same as Line (we have the last point)
        }
        obj.position.set(c.center.x, c.center.y, c.center.z)
        obj

      case geo.TriangleShape(t: geo.Triangle[XYZ], n: geo.Triangle[Vec]) =>
        val geometry = new BufferGeometry()
          .tap(_.setAttribute(BuiltinShaderAttributeName.position, float2BufferAttribute(t.toSeqFloat)))
          .tap(_.setAttribute(BuiltinShaderAttributeName.normal, float2BufferAttribute(n.toSeqFloat)))
        //.tap(_.computeVertexNormals())

        //val op = state.withMaterial(geo.SceneGraph.surfaceNormalMat)
        state.toObj3D(geometry)

      case geo.Arrow(to: Vec, from: XYZ) =>
        val obj = new Object3D[Event]
        val mid = to.-(to.-(from.asVec).norm * 0.3).toXYZ
        obj.add(generateShape(geo.Cylinder.fromVerticesRadius(from, mid, 0.03), state))
        obj.add(
          generateShape(
            geo.Cylinder.fromVerticesRadius(mid.toXYZ, to.toXYZ, 0.1, Some(0)),
            state
          )
        )
        obj
      case geo.Axes(m: geo.Matrix) =>
        val ccc = {
          val op = state.withMaterial(geo.SceneGraph.surfaceMat)
          generateShape(geo.Sphere(0.1, XYZ.origin), op)
        }
        val rrr = {
          val op = state.withMaterial(geo.SceneGraph.surfaceMatWithColor(0xff0000))
          generateShape(geo.Arrow(Vec(1, 0, 0)), op)
        }
        val ggg = {
          val op = state.withMaterial(geo.SceneGraph.surfaceMatWithColor(0x00ff00))
          generateShape(geo.Arrow(Vec(0, 1, 0)), op)
        }
        val bbb = {
          val op = state.withMaterial(geo.SceneGraph.surfaceMatWithColor(0x0000ff))
          generateShape(geo.Arrow(Vec(0, 0, 1)), op)
        }

        val obj = new Object3D[Event]
        obj.add(ccc)
        obj.add(rrr)
        obj.add(ggg)
        obj.add(bbb)
        obj.applyMatrix4(matrix2matrix(m))
        obj

      case geo.TextShape(text: String, size: Double) =>
        val textParameters = js.Dynamic.literal().asInstanceOf[typings.three.textGeometryMod.TextGeometryParameters]
        textParameters.font = WebGLTextGlobal.textFont
        textParameters.size = size
        textParameters.height = 0
        textParameters.curveSegments = 12
        val geometry = new TextGeometry(text, textParameters)
        val basicMarerial = new typings.three.meshBasicMaterialMod.MeshBasicMaterial()
        basicMarerial.color = new typings.three.colorMod.Color(0x444444)
        val mesh = new typings.three.mod.Mesh(geometry, basicMarerial)
        mesh.asInstanceOf[Object3D[Event]]

      case geo.TestShape() =>
        val line = geo.LinePath(vertices = (10 to 16).map(i => XYZ(i, 0, 0)))

        def stairsMatrixTransformation(steps: Int, stepsHeight: Double = 1, stepsAngle: Double = 0.1) =
          Seq(geo.Matrix.rotate(0, Vec(0, 1, 0)).postTranslate(Vec(5, 0, 0))) +:
            (0 to steps)
              .map { index =>
                Seq(
                  geo.Matrix
                    .rotate(stepsAngle * (index + 0.2), Vec(0, 1, 0))
                    .postTranslate(Vec(5, (index * stepsHeight), 0)),
                  geo.Matrix
                    .rotate(stepsAngle * index, Vec(0, 1, 0))
                    .postTranslate(Vec(5, (index + 1) * stepsHeight, 0)),
                )
              }
        val (topSideVertices, innerSideSurface, outerSideSurface) = stairsMatrixTransformation(steps = 30)
          .pipe { mmm =>
            def getVerticesAndNormal(a: Seq[XYZ], b: Seq[XYZ]): Seq[XYZ] = {
              (a, b) match {
                case (a1 +: a2 +: aa, b1 +: b2 +: bb) =>
                  Seq(a1, a2, b1, b1, a2, b2) ++ getVerticesAndNormal(a2 +: aa, b2 +: bb)
                case _ => Seq.empty
              }
            }
            val topSide = mmm.flatten
              .dropRight(1)
              .map(m => line.vertices.map(p => m.dot(p.asVec)))
              .pipe(aux =>
                aux
                  .zip(aux.drop(1))
                  .flatMap(e => getVerticesAndNormal(e._1, e._2))
                  .flatMap(e => Seq(e.x.toFloat, e.y.toFloat, e.z.toFloat))
              )
            def sideTriangles(v: Vec) =
              mmm
                .drop(1)
                .map {
                  case m1 +: m2 +: Nil => (m1.dot(v).pipe(_.copy(y = 0)), m1.dot(v), m2.dot(v))
                  case m1 +: Nil       => (m1.dot(v).pipe(_.copy(y = 0)), m1.dot(v), m1.dot(v))
                }
                //.dropRight(1)
                .pipe(aux =>
                  aux
                    .zip(aux.drop(1))
                    .flatMap { case (a, b) =>
                      Seq(
                        geo.Triangle(a._1, a._2, b._1),
                        geo.Triangle(b._2, b._1, a._2),
                        geo.Triangle(a._2, a._3, b._2)
                      )
                    }
                )
            (topSide, sideTriangles(line.vertices.head.asVec), sideTriangles(line.vertices.last.asVec).map(_.invert))
          }

        //val aaaa: String & js.Object = "position"
        val topSideGeometry = new BufferGeometry()
          .tap(_.setAttribute(BuiltinShaderAttributeName.position, float2BufferAttribute(topSideVertices)))
          .tap(_.computeVertexNormals())

        val innerSideGeometry = new BufferGeometry()
          .tap(
            _.setAttribute(
              BuiltinShaderAttributeName.position,
              float2BufferAttribute(innerSideSurface.pipe {
                _.flatMap(_.toSeqFloat)
              })
            )
          )
          .tap(
            _.setAttribute(
              BuiltinShaderAttributeName.normal,
              float2BufferAttribute(innerSideSurface.pipe {
                _.flatMap(_.map(_.forceX0Z).toSeqFloat)
              })
            )
          )

        val outerSideGeometry = new BufferGeometry()
          .tap(
            _.setAttribute(
              BuiltinShaderAttributeName.position,
              float2BufferAttribute(outerSideSurface.pipe {
                _.flatMap(_.toSeqFloat)
              })
            )
          )
          .tap(
            _.setAttribute(
              BuiltinShaderAttributeName.normal,
              float2BufferAttribute(outerSideSurface.pipe {
                _.flatMap(_.map(_.forceX0Z).toSeqFloat)
              })
            )
          )

        // ### OBJ ###
        val op = state.withMaterial(geo.SceneGraph.surfaceNormalMat)
        val obj = op.toObj3D(topSideGeometry)
        obj.add(op.toObj3D(innerSideGeometry))
        obj.add(op.toObj3D(outerSideGeometry))

        val to = XYZ(2, 0, -1)
        obj.add(generateShape(geo.Sphere(0.2, to), state))
        Seq(XYZ(3, 0, 0), XYZ(0, 0, -3), XYZ(0, 0, -2), XYZ(0, 0, -1), XYZ(0, 3, -1)).map { from =>
          val m = geo.Matrix.lookAt(from, to)
          obj.add(generateShape(geo.Axes(m), state))
        }

        obj
    }

    val tmp: Seq[Object3D[Event]] = shapes.map { s => generateShape(s, GenerateOP()) }

    val parent = new Object3D[Event]
    tmp.foreach(e => parent.add(e))
    parent
  }
}

// def multiPath2Curves(
//     multiPath: geo.MultiPath
// ): Seq[typings.three.curveMod.Curve[typings.three.vector3Mod.Vector3]] = {
//   multiPath.paths.flatMap {
//     case LinePath(vertices) =>
//       vertices.zip(vertices.drop(1)).map {
//         case (XYZ(ax, ay, az), XYZ(bx, by, bz)) =>
//           new LineCurve3(
//             new Vector3(ax, ay, az),
//             new Vector3(bx, by, bz)
//           )
//       }
//     case CubicBezierPath(a, af, bf, b) =>
//       Seq(
//         new CubicBezierCurve3(
//           new Vector3(a.x, a.y, a.z),
//           new Vector3(af.x, af.y, af.z),
//           new Vector3(bf.x, bf.y, bf.z),
//           new Vector3(b.x, b.y, b.z)
//         )
//       )
//     case another: MultiPath => multiPath2Curves(another)
//   }
// }

// case geo.Extrude(multiPath: MultiPath, holes: Seq[MultiPath], options: Option[geo.Extrude.Options]) =>
//   val optionsSeq: Seq[typings.three.extrudeGeometryMod.ExtrudeGeometryOptions] =
//     options
//       .map { o =>
//         o.extrudePath
//           .map(extrudeMultiPath =>
//             multiPath2Curves(extrudeMultiPath).map(curve =>
//               typings.three.extrudeGeometryMod.ExtrudeGeometryOptions(
//                 bevelEnabled = if (o.bevelEnabled.isDefined) o.bevelEnabled.get else null,
//                 bevelOffset = if (o.bevelOffset.isDefined) o.bevelOffset.get else null,
//                 bevelSegments = if (o.bevelSegments.isDefined) o.bevelSegments.get else null,
//                 bevelSize = if (o.bevelSize.isDefined) o.bevelSize.get else null,
//                 bevelThickness = if (o.bevelThickness.isDefined) o.bevelThickness.get else null,
//                 curveSegments = if (o.curveSegments.isDefined) o.curveSegments.get else null,
//                 depth = if (o.depth.isDefined) o.depth.get else null,
//                 extrudePath = curve,
//                 steps = if (o.steps.isDefined) o.steps.get else null,
//               )
//             )
//           )
//           .getOrElse(
//             Seq(
//               typings.three.extrudeGeometryMod.ExtrudeGeometryOptions(
//                 bevelEnabled = if (o.bevelEnabled.isDefined) o.bevelEnabled.get else null,
//                 bevelOffset = if (o.bevelOffset.isDefined) o.bevelOffset.get else null,
//                 bevelSegments = if (o.bevelSegments.isDefined) o.bevelSegments.get else null,
//                 bevelSize = if (o.bevelSize.isDefined) o.bevelSize.get else null,
//                 bevelThickness = if (o.bevelThickness.isDefined) o.bevelThickness.get else null,
//                 curveSegments = if (o.curveSegments.isDefined) o.curveSegments.get else null,
//                 depth = if (o.depth.isDefined) o.depth.get else null,
//                 extrudePath = null,
//                 steps = if (o.steps.isDefined) o.steps.get else null,
//               )
//             )
//           )
//       }
//       .getOrElse(Seq(typings.three.extrudeGeometryMod.ExtrudeGeometryOptions()))
//   val jsPath = multiPath2ShapePath(multiPath)
//   jsPath.holes = holes.map(e => multiPath2Path(e)).toJSArray
//   optionsSeq
//     .map(ooo => state.toObj3D(new ExtrudeBufferGeometry(jsPath, ooo)))
//     .fold(new Object3D)((z, n) => z.add(n))
