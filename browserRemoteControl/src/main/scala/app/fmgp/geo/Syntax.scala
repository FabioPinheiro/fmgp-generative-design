package app.fmgp.geo

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

trait Syntax extends BaseSyntax with KhepriSolidPrimitives
trait BaseSyntax {
  var wireframeMode: Boolean = false
  def addShape[T <: Shape](t: T): T = addShape(t = t, wireframeMode = wireframeMode)
  def addShape[T <: Shape](t: T, wireframeMode: Boolean): T
  def clear: Unit = { wireframeMode = false }

  def xyz(x: Double = 0, y: Double = 0, z: Double = 0): XYZ = XYZ(x, y, z)
  def pol = polar _
  def polar(module: Double, argument: Double): XYZ = Polar(rho = argument, phi = module).toXYZ
  def cyl = cylindrical _
  def cylindrical(rho: Double, phi: Double, y: Double) = Cylindrical(rho = rho, phi = phi, y = y)
  def sph = spherical _
  def spherical(rho: Double, phi: Double, psi: Double) = Spherical(rho = rho, phi = phi, psi = psi)

  def box(width: Double, height: Double, depth: Double): Box = addShape(Box(width, height, depth))
  def sphere(radius: Double, center: XYZ = XYZ.origin): Sphere = addShape(Sphere(radius, center))
  def cylinder(radius: Double, height: Double): Cylinder = addShape(Cylinder(radius, height))

  def line(vertices: Seq[XYZ], closeLine: Boolean = false): LinePath =
    addShape(LinePath(if (closeLine) vertices ++ vertices.headOption else vertices))
  def circle(radius: Double, center: XYZ = XYZ.origin): Circle = addShape(Circle(radius, center))
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
