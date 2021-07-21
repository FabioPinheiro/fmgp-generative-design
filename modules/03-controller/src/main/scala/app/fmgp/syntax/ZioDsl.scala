package app.fmgp.syntax

import zio._
import app.fmgp.geo._

object ZioDsl {
  def box(width: Double, height: Double, depth: Double) =
    ZIO.succeed(Box(width, height, depth))

  def sphere(center: => XYZ, radius: => Double) =
    ZIO.succeed(Sphere(radius, center))

  def shapes(shapes: Shape*) =
    ZIO.succeed(ShapeSeq(shapes))

  def world(shapes: Shape*) = ZIO.succeed(WorldAddition(shapes))
  def worldConsole(shapes: Shape*) = console.putStrLn(WorldAddition(shapes).toString)
  def worldJson(shapes: Shape*) =
    import io.circe._, io.circe.syntax._
    import app.fmgp.geo.EncoderDecoder.{given}
    console.putStrLn(WorldAddition(shapes).asJson.spaces2)

}
