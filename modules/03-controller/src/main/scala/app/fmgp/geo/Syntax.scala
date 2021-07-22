package app.fmgp.geo

import app.fmgp.syntax.CoordinatesDsl
object Syntax extends Syntax {
  override def addShape[T <: Shape](t: T, wireframeMode: Boolean): T = {
    println(s"wireframeMode=$wireframeMode: $t")
    t
  }
  override def clear: Unit = {
    println("Clear all Shapes!")
    super.clear
  }
}

trait Syntax extends CoordinatesDsl with BaseSyntax with KhepriUtils with KhepriSolidPrimitives

/** Khepri's defined functions
  * @see
  *   [[https://github.com/aptmcl/Khepri.jl/blob/master/src/Utils.jl]]
  */
trait KhepriUtils {
  def division(t0: Double, t1: Double, n: Int, include_last: Boolean = true): Seq[Double] =
    val step = (t1 - t0) / n
    (if (include_last)(0.to(n)) else (0.until(n))).map(e => t0 + step * e)

  def map_division[T](f: (Double) => T, t0: Double, t1: Double, n: Int, include_last: Boolean = true): Seq[T] =
    division(t0, t1, n, include_last).map(f)
  def map_division[T](
      f: (Double, Double) => T,
      u0: Double,
      u1: Double,
      nu: Int,
      v0: Double,
      v1: Double,
      nv: Int,
      include_last: Boolean
  ): Seq[Seq[T]] =
    division(u0, u1, nu, include_last)
      .map(u => division(v0, v1, nv, include_last).map(v => f(u, v)))
}

trait BaseSyntax {
  var wireframeMode: Boolean = false
  def addShape[T <: Shape](t: T): T = addShape(t = t, wireframeMode = wireframeMode)
  def addShape[T <: Shape](t: T, wireframeMode: Boolean): T
  def clear: Unit = { wireframeMode = false }

  def box(width: Double, height: Double, depth: Double): Box = addShape(Box(width, height, depth))
  def sphere(radius: Double, center: XYZ = XYZ.origin): Sphere = addShape(Sphere(radius, center))
  def cylinder(radius: Double, height: Double): Cylinder = addShape(Cylinder(radius, height))

  def line(vertices: Seq[XYZ], closeLine: Boolean = false): LinePath =
    addShape(LinePath(if (closeLine) vertices ++ vertices.headOption else vertices))
  def circle(radius: Double, center: XYZ = XYZ.origin): Circle = addShape(Circle(radius, center))
  def surface_grid(points: Seq[Seq[XYZ]]) = addShape(SurfaceGridShape(points.map(_.toArray).toArray))
}

trait KhepriSolidPrimitives extends BaseSyntax {
  def box(v1: XYZ, v2: XYZ): Shape = addShape(Box.fromOppositeVertex(v1, v2))
  def cone(bottom: XYZ, radius: Double, top: XYZ): Shape =
    coneFrustum(bottom = bottom, bottomRadius = radius, top = top, topRadius = radius)
  def coneFrustum(bottom: XYZ, bottomRadius: Double, top: XYZ, topRadius: Double) =
    addShape(Cylinder.fromVerticesRadius(bottom, top, bottomRadius = bottomRadius, topRadius = Some(topRadius)))
  def sphere(center: XYZ, radius: Double): Sphere = sphere(radius, center)
  def cylinder = cone _
  def regularPyramid(radialSegments: Int, bottom: XYZ, size: Double, height: Double, top: XYZ) = addShape(
    Cylinder.fromVerticesRadius(
      bottom = bottom,
      top = top,
      bottomRadius = RegularPolygon.radius(size, radialSegments),
      topRadius = Some(0),
      radialSegments = Some(radialSegments)
    )
  )
  def torus(center: XYZ, radius: Double, tube: Double): Shape =
    addShape(Torus.withCenter(center = center, radius = radius, tube = tube))
}
