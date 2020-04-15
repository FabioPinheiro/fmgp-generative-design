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

trait Syntax {
  def addShape[T <: Shape](t: T): T
  def clear: Unit
  def xyz(x: Double = 0, y: Double = 0, z: Double = 0): XYZ = XYZ(x, y, z)

  def box(width: Double, height: Double, depth: Double): Box = addShape(Box(width, height, depth))
  def sphere(radius: Double, center: XYZ = XYZ.origin): Sphere = addShape(Sphere(radius, center))
  def cylinder(radius: Double, height: Double): Cylinder = addShape(Cylinder(radius, height))
  //def line(vertices: XYZ*): Line = addShape(Line(vertices))
  def line(vertices: Seq[XYZ]): Line = addShape(Line(vertices))
  def circle(radius: Double, center: XYZ = XYZ.origin): Circle = addShape(Circle(radius, center))
}
