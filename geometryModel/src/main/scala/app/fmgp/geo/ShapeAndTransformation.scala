package app.fmgp.geo

/** Transformations */
sealed trait Transformation {
  def matrix: Matrix
}

final case class TransformMatrix(matrix: Matrix) extends Transformation

/** Shapes */
sealed trait Shape

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
}

case class TransformationShape(
    shape: Shape,
    transformation: Transformation
) extends Shape

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
  //   //FIXME missing transformation
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

/**
  * @param radius - Radius of the torus, from the center of the torus to the center of the tube. Default is 1.
  * @param tube — Radius of the tube. Default is 0.4.
  * @param arc — Central angle. Default is Math.PI * 2.
  * @param radialSegments — Default is 8
  * @param tubularSegments — Default is 6.
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

case class Line(
    vertices: Seq[XYZ]
) extends Shape

case class Circle(
    radius: Double,
    center: XYZ = XYZ.origin,
    fill: Boolean = false
) extends Shape

// ###################
// ### $$$$$$$$$$$ ###
// ###################
object RegularPolygon {
  def size(radius: Double, numberOfSides: Double) = 2 * radius * math.sin(math.Pi / numberOfSides)
  def radius(size: Double, numberOfSides: Double) = size / (2 * math.sin(math.Pi / numberOfSides))
  //Apothem = Radius × cos(Pi/n)  # n=numbner of sides
  //Side = 2 × Apothem × tan(Pi/n) # n=number of sides
}
