package app.fmgp

import zio._
import app.fmgp.geo._
import app.fmgp.meta

import app.fmgp.syntax.logging._
import app.fmgp.syntax.CoordinatesDsl

object dsl extends CoordinatesDsl {
  type Dsl = Has[Dsl.Service]
  //type F[T] = T //This will be object warping the metadata
  type M[T] = app.fmgp.meta.MacroUtils.MetaValue[T] //This will be object warping the metadata

  // Companion object exists to hold service definition and a`lso the live implementation.
  object Dsl {
    trait Service {
      def box(width: Double, height: Double, depth: Double): UIO[Box] =
        ZIO.succeed(Box(width, height, depth))
      def sphere(center: => XYZ, radius: => Double): UIO[Sphere] =
        ZIO.succeed(Sphere(radius, center))
      def shapes(shapes: Shape*): UIO[Shape] =
        ZIO.succeed(ShapeSeq(shapes))
    }

    val live: URLayer[Logging, Dsl] =
      ZLayer.fromService[Logging.Service, Dsl.Service] { (logging: Logging.Service) =>
        new Service {
          // override def box(width: Double, height: Double, depth: Double): IO[IOException, Box] =
          //   ZIO.succeed(Box(width, height, depth)).flatMap(s => logging.log(s.toString).map(_ => s))
        }
      }
  }

  // Accessor Methods
  def box(width: => Double, height: => Double, depth: => Double): RIO[Dsl, Box] =
    ZIO.accessM(_.get.box(width, height, depth))

  def sphere(center: => XYZ, radius: => Double): RIO[Dsl, Sphere] =
    ZIO.accessM(_.get.sphere(center, radius))

  def shapes(shapes: Shape*): RIO[Dsl, Shape] =
    ZIO.accessM(_.get.shapes(shapes: _*))

  // def world(shapes: Shape*) = ZIO.succeed(WorldAddition(shapes))
  // def worldConsole(shapes: Shape*) = console.putStrLn(WorldAddition(shapes).toString)
  // def worldJson(shapes: Shape*) =
  //   import io.circe._, io.circe.syntax._
  //   import app.fmgp.geo.EncoderDecoder.{given}
  //   console.putStrLn(WorldAddition(shapes).asJson.spaces2)

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
