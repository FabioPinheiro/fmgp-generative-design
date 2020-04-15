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
    radius: Double,
    height: Double
) extends Shape

object Cylinder {
  def fromVerticesRadiusNew(base: XYZ, top: XYZ, radius: Double): Shape = {
    val eixo: Vec = Vec(from = base, to = top)

    //move centor (origem) to another center
    val translation = XYZ.midpoint(base, top)

    Cylinder(radius = radius, height = eixo.length)
    //FIXME missing transformation
  }

  def fromVerticesRadius(bottom: XYZ, top: XYZ, radius: Double) = {
    val cylinderAxis = (top - bottom).asVec
    val orientAxis = Matrix.alignFromAxisToAxis(
      fromAxis = cylinderAxis.normalized,
      toAxis = Vec(0, 1, 0)
    )
    val midPoint = bottom + cylinderAxis.scale(0.5)

    //println(s"cylinderAxis:$cylinderAxis ; ${cylinderAxis.normalized}; $orientAxis")
    Cylinder(radius, cylinderAxis.length)
      .transformWith(orientAxis.preTranslate(midPoint.asVec))
  }
}

case class Line(
    vertices: Seq[XYZ]
) extends Shape

case class Circle(
    radius: Double,
    center: XYZ = XYZ.origin,
    fill: Boolean = false
) extends Shape
