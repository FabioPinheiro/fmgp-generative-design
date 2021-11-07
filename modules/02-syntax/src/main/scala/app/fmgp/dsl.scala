package app.fmgp

import zio._
import app.fmgp.geo._
import app.fmgp.meta

import app.fmgp.logging._
import app.fmgp.syntax.CoordinatesDsl

object dsl extends CoordinatesDsl {
  type Dsl = Has[Dsl.Service]

  def defaultRuntime = Runtime(Dsl.liveService, zio.internal.Platform.default)

  // Companion object exists to hold service definition and a`lso the live implementation.
  object Dsl {
    trait Service {
      def box(width: Double, height: Double, depth: Double): UIO[Box] =
        ZIO.succeed(Box(width, height, depth))
      def sphere(center: => XYZ, radius: => Double): UIO[Sphere] =
        ZIO.succeed(Sphere(radius, center))
      def shapes(shapes: Shape*): UIO[Shape] =
        ZIO.succeed(ShapeSeq(shapes))

      def cylinder(radius: Double, height: Double): UIO[Cylinder] =
        ZIO.succeed(Cylinder(radius, height))
      def line(vertices: Seq[XYZ], closeLine: Boolean = false): UIO[LinePath] =
        ZIO.succeed(LinePath(if (closeLine) vertices ++ vertices.headOption else vertices))
      def circle(radius: Double, center: XYZ = XYZ.origin): UIO[Circle] =
        ZIO.succeed(Circle(radius, center))
      def surface_grid(points: Seq[Seq[XYZ]]): UIO[SurfaceGridShape] =
        ZIO.succeed(SurfaceGridShape(points.map(_.toArray).toArray))
    }

    val live: ULayer[Dsl] = ZLayer.succeed(new Service {})
    val liveService: Dsl = Has(new Service {})
  }

  // Accessor Methods
  def emptyShape: RIO[Dsl, Shape] = ZIO.succeed(ShapeSeq(Seq()))

  def box(width: => Double, height: => Double, depth: => Double): RIO[Dsl, Box] =
    ZIO.accessM(_.get.box(width, height, depth))

  def sphere(center: => XYZ, radius: => Double): RIO[Dsl, Sphere] =
    ZIO.accessM(_.get.sphere(center, radius))

  def shapes(shapes: Shape*): RIO[Dsl, Shape] =
    ZIO.accessM(_.get.shapes(shapes: _*))

  def cylinder(radius: Double, height: Double): RIO[Dsl, Cylinder] =
    ZIO.accessM(_.get.cylinder(radius, height))

  def line(vertices: Seq[XYZ], closeLine: Boolean = false): RIO[Dsl, LinePath] =
    ZIO.accessM(_.get.line(vertices, closeLine))

  def circle(radius: Double, center: XYZ = XYZ.origin): RIO[Dsl, Circle] =
    ZIO.accessM(_.get.circle(radius, center))

  def surface_grid(points: Seq[Seq[XYZ]]): RIO[Dsl, SurfaceGridShape] =
    ZIO.accessM(_.get.surface_grid(points))

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
