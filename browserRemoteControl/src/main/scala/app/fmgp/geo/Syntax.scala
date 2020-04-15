package app.fmgp.geo

object Syntax extends Syntax {
  def addShape[T <: Shape](t: T): T = {
    println(t)
    t
  }
  def clear: Unit = {
    println("Clear all Shapes!")
  }
}

trait Syntax extends BaseSyntax with KhepriSolidPrimitives
trait BaseSyntax {
  def addShape[T <: Shape](t: T): T
  def clear: Unit

  def xyz(x: Double = 0, y: Double = 0, z: Double = 0): XYZ = XYZ(x, y, z)
  def pol = polar _
  def polar(module: Double, argument: Double): XYZ = Polar(module, argument).toXY

  def box(width: Double, height: Double, depth: Double): Box = addShape(Box(width, height, depth))
  def sphere(radius: Double, center: XYZ = XYZ.origin): Sphere = addShape(Sphere(radius, center))
  def cylinder(radius: Double, height: Double): Cylinder = addShape(Cylinder(radius, height))

  def line(vertices: Seq[XYZ], closeLine: Boolean = false): Line =
    addShape(Line(if (closeLine) vertices ++ vertices.headOption else vertices))
  def circle(radius: Double, center: XYZ = XYZ.origin): Circle = addShape(Circle(radius, center))
}

trait KhepriSolidPrimitives extends BaseSyntax {
  def box(v1: XYZ, v2: XYZ): Shape = addShape(Box.fromOppositeVertex(v1, v2))
  def cone(bottom: XYZ, radius: Double, top: XYZ): Shape = addShape(Cylinder.fromVerticesRadius(bottom, top, radius))
//coneFrustum(xyz(11, 1, 0), 2, xyz(10, 0, 5), 1)
  def sphere(center: XYZ, radius: Double): Sphere = sphere(radius, center)
//cylinder(xyz(8, 7, 0), 1, xyz(6, 8, 7))
//regular_pyramid(5, xyz(-2, 1, 0), 1, 0, xyz(2, 7, 7))
//torus(xyz(14, 6, 5), 2, 1)
}
