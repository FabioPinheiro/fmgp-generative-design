package fmgp

import zio._
import fmgp.geo._
import fmgp.meta

//import fmgp.logging._
import fmgp.syntax.CoordinatesDsl

object dsl extends CoordinatesDsl {

  trait Dsl {
    def emptyShape: UIO[Shape]
    def zShapes(shapesSeq: URIO[Dsl, Shape]*): URIO[Dsl, Shape]
    def box(width: Double, height: Double, depth: Double): UIO[Box]
    def sphere(center: => XYZ, radius: => Double): UIO[Sphere]
    def shapes(shapes: Shape*): UIO[Shape]
    def cylinder(radius: Double, height: Double): UIO[Cylinder]
    def cylinder(bottom: XYZ, top: XYZ, bottomRadius: Double): UIO[TransformationShape]
    def line(vertices: Seq[XYZ], closeLine: Boolean = false): UIO[LinePath]
    def circle(radius: Double, center: XYZ = XYZ.origin): UIO[Circle]
    def surface_grid(points: Seq[Seq[XYZ]]): UIO[SurfaceGridShape]
  }

  case class DslLive() extends Dsl {
    def emptyShape: UIO[Shape] = ZIO.succeed(ShapeSeq(Seq()))
    def zShapes(shapesSeq: URIO[Dsl, Shape]*): URIO[Dsl, Shape] = {
      shapesSeq.reduce((a, b) => a.zipPar(b).flatMap(e => shapes(e._1, e._2)))
    }

    def box(width: Double, height: Double, depth: Double): UIO[Box] =
      ZIO.succeed(Box(width, height, depth))
    def sphere(center: => XYZ, radius: => Double): UIO[Sphere] =
      ZIO.succeed(Sphere(radius, center))
    def shapes(shapes: Shape*): UIO[Shape] =
      ZIO.succeed(ShapeSeq(shapes))

    def cylinder(radius: Double, height: Double): UIO[Cylinder] =
      ZIO.succeed(Cylinder(radius, height))
    def cylinder(bottom: XYZ, top: XYZ, bottomRadius: Double): UIO[TransformationShape] =
      ZIO.succeed(Cylinder.fromVerticesRadius(bottom, top, bottomRadius))

    def line(vertices: Seq[XYZ], closeLine: Boolean = false): UIO[LinePath] =
      ZIO.succeed(LinePath(if (closeLine) vertices ++ vertices.headOption else vertices))
    def circle(radius: Double, center: XYZ = XYZ.origin): UIO[Circle] =
      ZIO.succeed(Circle(radius, center))
    def surface_grid(points: Seq[Seq[XYZ]]): UIO[SurfaceGridShape] =
      ZIO.succeed(SurfaceGridShape(points.map(_.toArray).toArray))
  }
  object DslLive {
    val layer: ULayer[Dsl] = (() => DslLive()).toLayer[Dsl]
  }

  // Accessor Methods
  def emptyShape: URIO[Dsl, Shape] =
    ZIO.serviceWithZIO(_.emptyShape)

  def zShapes(shapesSeq: URIO[Dsl, Shape]*): URIO[Dsl, Shape] =
    ZIO.serviceWithZIO(_.zShapes(shapesSeq: _*))

  def box(width: => Double, height: => Double, depth: => Double): URIO[Dsl, Box] =
    ZIO.serviceWithZIO(_.box(width, height, depth))

  def sphere(center: => XYZ, radius: => Double): URIO[Dsl, Sphere] =
    ZIO.serviceWithZIO(_.sphere(center, radius))

  def shapes(shapes: Shape*): URIO[Dsl, Shape] =
    ZIO.serviceWithZIO(_.shapes(shapes: _*))

  def cylinder(radius: Double, height: Double): URIO[Dsl, Cylinder] =
    ZIO.serviceWithZIO(_.cylinder(radius, height))

  def cylinder(bottom: XYZ, top: XYZ, bottomRadius: Double): URIO[Dsl, TransformationShape] =
    ZIO.serviceWithZIO(_.cylinder(bottom, top, bottomRadius))

  def line(vertices: Seq[XYZ], closeLine: Boolean = false): URIO[Dsl, LinePath] =
    ZIO.serviceWithZIO(_.line(vertices, closeLine))

  def circle(radius: Double, center: XYZ = XYZ.origin): URIO[Dsl, Circle] =
    ZIO.serviceWithZIO(_.circle(radius, center))

  def surface_grid(points: Seq[Seq[XYZ]]): URIO[Dsl, SurfaceGridShape] =
    ZIO.serviceWithZIO(_.surface_grid(points))

  // Macros Methods
  import scala.quoted.*
  import meta.MacroUtils.getMetaImpl
  import meta.MacroUtils.MetaValue

  inline def _box(width: Double, height: Double, depth: Double): RIO[Dsl, MetaValue[Box]] =
    ${ _boxImpl('width, 'height, 'depth) }

  private def _boxImpl(
      widthExpr: Expr[Double],
      heightExpr: Expr[Double],
      depthExpr: Expr[Double]
  )(using Quotes): Expr[RIO[Dsl, (MetaValue[Box])]] =
    '{ box($widthExpr, $heightExpr, $depthExpr).map(o => ${ getMetaImpl() }.withValue[Box](o)) }
}
