package app.fmgp.geo

/** Transformations */
sealed trait Transformation {
  def matrix: Matrix
}

final case class TransformMatrix(matrix: Matrix) extends Transformation

/** Shapes */
sealed trait Shape extends Any

object Shape {
  // We can use Shapeless lib
  //type ShapeT[S] = Shape with TransformationShape[S, Transformation]{type SHAPE = S}

  implicit class ShapeImprovements(val obj: Shape) {
    def transformWith(transform: Matrix): TransformationShape =
      TransformationShape(obj, TransformMatrix(transform))
  }

//   implicit class ShapeImprovements[S <: Shape, T <: Transformation](    val obj: S) {
//     def transformWith(
//         transform: Matrix
//     ): TransformationShape[S, Transformation] =
//       TransformationShape(obj, TransformMatrix(transform))
//   }
  def apply(shapes: Shape*): ShapeSeq = ShapeSeq(shapes)
}

case class TransformationShape(
    shape: Shape,
    transformation: Transformation
) extends Shape

case class Wireframe(shape: Shape) extends Shape
case class ShapeSeq(shapes: Seq[Shape] = Seq.empty) extends Shape with Seq[Shape] {
  override def iterator: Iterator[Shape] = shapes.iterator
  override def apply(i: Int): Shape = shapes(i)
  override def length: Int = shapes.length
}
object ShapeSeq {
  def apply(shape: Shape): ShapeSeq = ShapeSeq(Seq(shape))
  import scala.language.implicitConversions
  implicit def implicitConverter(s: Seq[Shape]): ShapeSeq = ShapeSeq(s)
}

case class Points(val c: Seq[Coordinate3D]) extends Shape //AnyVal with

case class Box(
    width: Double,
    height: Double,
    depth: Double
) extends Shape

object Box {
  def fromOppositeVertex(v1: XYZ, v2: XYZ): Shape = {
    val dimensions = v2 - v1
    val tranlate = v1 + dimensions.asVec.scale(0.5)
    Box(dimensions.x, dimensions.y, dimensions.z)
      .transformWith(Matrix.translate(tranlate.asVec))
  }
}

case class Sphere(
    radius: Double,
    center: XYZ
) extends Shape

case class Cylinder(
    bottomRadius: Double,
    height: Double,
    topRadius: Double,
    radialSegments: Option[Int] = None,
    heightSegments: Option[Int] = None,
    openEnded: Option[Boolean] = None,
    thetaStart: Option[Double] = None,
    thetaLength: Option[Double] = None
) extends Shape

object Cylinder {
  def apply(bottomRadius: Double, height: Double): Cylinder =
    apply(bottomRadius = bottomRadius, height = height, topRadius = bottomRadius)

  // def fromVerticesRadiusNew(base: XYZ, top: XYZ, bottomRadius: Double, topRadius: Option[Double] = None): Shape = {
  //   val eixo: Vec = Vec(from = base, to = top)

  //   //move centor (origem) to another center
  //   val translation = XYZ.midpoint(base, top)

  //   Cylinder(bottomRadius = bottomRadius, height = eixo.length, topRadius = topRadius.getOrElse(bottomRadius))
  //   //TODO missing transformation
  // }

  def fromVerticesRadius(
      bottom: XYZ,
      top: XYZ,
      bottomRadius: Double,
      topRadius: Option[Double] = None,
      radialSegments: Option[Int] = None,
      heightSegments: Option[Int] = None,
      openEnded: Option[Boolean] = None,
      thetaStart: Option[Double] = None,
      thetaLength: Option[Double] = None
  ) = {
    val cylinderAxis = (top - bottom).asVec
    val t = {
      val orientAxis = Matrix.alignFromAxisToAxis(fromAxis = cylinderAxis.normalized, toAxis = Vec(0, 1, 0))
      val midPoint = bottom + cylinderAxis.scale(0.5)
      orientAxis.preTranslate(midPoint.asVec)
    }
    Cylinder(
      bottomRadius = bottomRadius,
      height = cylinderAxis.length,
      topRadius = topRadius.getOrElse(bottomRadius),
      radialSegments = radialSegments,
      heightSegments = heightSegments,
      openEnded = openEnded,
      thetaStart = thetaStart,
      thetaLength = thetaLength
    ).transformWith(t)
  }
}

/** @param radius
  *   - Radius of the torus, from the center of the torus to the center of the tube. Default is 1.
  * @param tube
  *   — Radius of the tube. Default is 0.4.
  * @param arc
  *   — Central angle. Default is Math.PI * 2.
  * @param radialSegments
  *   — Default is 8
  * @param tubularSegments
  *   — Default is 6.
  */
case class Torus(
    radius: Double,
    tube: Double,
    arc: Double = math.Pi * 2,
    radialSegments: Int = 8,
    tubularSegments: Int = 6,
) extends Shape

object Torus {
  def withCenter(center: XYZ, radius: Double, tube: Double, arc: Double = math.Pi * 2) =
    Torus(radius = radius, tube = tube, arc = arc)
      .transformWith(Matrix.translate(center.asVec))
}

// ###########################
// ### PATH & Line & Curve ###
// ###########################
case class Extrude(
    path: MultiPath,
    holes: Seq[MultiPath] = Seq.empty,
    options: Option[Extrude.Options] = None
) extends Shape

object Extrude {
  case class Options(
      //UVGenerator: Option[typings.three.extrudeGeometryMod.UVGenerator] = None,
      bevelEnabled: Option[Boolean] = None,
      bevelOffset: Option[Double] = None,
      bevelSegments: Option[Double] = None,
      bevelSize: Option[Double] = None,
      bevelThickness: Option[Double] = None,
      curveSegments: Option[Double] = None,
      depth: Option[Double] = None,
      extrudePath: Option[MultiPath] = None,
      steps: Option[Double] = None
  )
}

case class PlaneShape(path: MultiPath, holes: Seq[MultiPath] = Seq.empty) extends Shape

case class SurfaceGridShape(points: Array[Array[XYZ]]) extends Shape

sealed trait MyPath extends Shape //val curveSegments: Int = 12

case class LinePath(vertices: Seq[XYZ]) extends MyPath
object LinePath {
  def apply(v1: XYZ, v2: XYZ): LinePath = LinePath(Seq(v1, v2))
}

case class MultiPath(paths: Seq[MyPath]) extends MyPath with Seq[MyPath] {
  override def iterator: Iterator[MyPath] = paths.iterator
  override def apply(i: Int): MyPath = paths(i)
  override def length: Int = paths.length
}
object MultiPath {
  def apply(path: MyPath): MultiPath = MultiPath(Seq(path))
  def apply(): MultiPath = MultiPath(Seq.empty)
  //implicit def implicitConverter(s: Seq[MyPath]): MultiPath = MultiPath(s)
}

case class CubicBezierPath(a: XYZ, af: Vec, bf: Vec, b: XYZ) extends MyPath //add arcLengthDivisions: Double

case class Circle(
    radius: Double,
    center: XYZ = XYZ.origin,
    fill: Boolean = false
) extends Shape //MyPath

case class TriangleShape(t: Triangle[XYZ], n: Triangle[Vec]) extends Shape

case class Triangle[T <: Coordinate](a: T, b: T, c: T) {
  @inline def invert: Triangle[T] = Triangle(a, c, b)
  @inline def asTXYZ = Triangle[XYZ](a.toXYZ, b.toXYZ, c.toXYZ)
  @inline def asTVec = Triangle[Vec](a.asVec, b.asVec, c.asVec)
  @inline def toSeqFloat = Triangle.toSeqFloat(a, b, c)
  @inline def toInvertSeqFloat = Triangle.toInvertSeqFloat(a, b, c)
  def map[W <: Coordinate](f: T => W): Triangle[W] = Triangle(f(a), f(b), f(c))
}
object Triangle {
  // format: off
  @inline def toSeqFloat[T <: Coordinate](a: T, b: T, c: T) = Seq(
    a.x.toFloat, a.y.toFloat, a.z.toFloat, // a
    b.x.toFloat, b.y.toFloat, b.z.toFloat, // b
    c.x.toFloat, c.y.toFloat, c.z.toFloat, // c
  )
  @inline def toInvertSeqFloat[T <: Coordinate](a: T, b: T, c: T) = Seq(
    a.x.toFloat, a.y.toFloat, a.z.toFloat, // a
    c.x.toFloat, c.y.toFloat, c.z.toFloat, // c
    b.x.toFloat, b.y.toFloat, b.z.toFloat, // b
  )
  // format: om
}

// ### Extras Shapes ###
sealed trait ShapeExtras extends Shape

case class TestShape() extends Shape
case class Arrow(to: Vec, from: XYZ = XYZ.origin) extends ShapeExtras
case class Axes(m: Matrix) extends ShapeExtras
case class TextShape(text: String, size: Double) extends ShapeExtras

// /**
//   *
//   * @param position
//   * @param lookAt
//   * @param fov camera frustum vertical field of view.
//   * @param aspect camera frustum aspect ratio.
//   * @param near camera frustum near plane.
//   * @param far camera frustum far plane.
//   */
// case class Frustum(
//     position: XYZ,
//     lookAt: XYZ,
//     fov: Double,
//     aspect: Double,
//     near: Double,
//     far: Double
// ) extends ShapeExtras {
//   def lootTo: Vec = ???
// }

// ###################
// ### $$$$$$$$$$$ ###
// ###################
object RegularPolygon {
  def size(radius: Double, numberOfSides: Double) = 2 * radius * math.sin(math.Pi / numberOfSides)
  def radius(size: Double, numberOfSides: Double) = size / (2 * math.sin(math.Pi / numberOfSides))
  //Apothem = Radius × cos(Pi/n)  # n=numbner of sides
  //Side = 2 × Apothem × tan(Pi/n) # n=number of sides
}

case class PathBuilder(startPoint: XYZ) {
  var currentPoint: XYZ = startPoint
  var multiPath: Seq[MyPath] = Seq.empty

  def build: MultiPath = MultiPath(multiPath.reverse)
  def moveToStart: PathBuilder = moveTo(startPoint)
  def moveTo(xyz: XYZ): PathBuilder = {
    currentPoint = xyz
    this
  }
  def lineTo(traget: XYZ): PathBuilder = {
    multiPath = LinePath(currentPoint, traget) +: multiPath
    currentPoint = traget
    this
  }
  def bezierCurveTo(af: Vec, bf: Vec, tangent: XYZ): PathBuilder = {
    multiPath = CubicBezierPath(a = currentPoint, af = af, bf = bf, b = tangent) +: multiPath
    currentPoint = tangent
    this
  }
}
