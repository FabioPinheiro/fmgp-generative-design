package app.fmgp.geo.prebuilt

import zio._
import scala.math._
import app.fmgp.geo._
import app.fmgp.dsl._

//import app.fmgp.geo.prebuilt.RhythmicGymnasticsPavilionExample._
import app.fmgp.geo.prebuilt.IsenbergSchoolOfManagementHubExample._
import scala.util.Random

object GeoZioExample {

  val program: zio.ZIO[Has[Dsl], Throwable, app.fmgp.geo.Shape] =
    for {
      _ <- ZIO.unit
      //points = pts_circle(XYZ.origin, r = 2, alfa_init = 0, alfa_end = Pi, n = 10)
      isenberg = Isenberg(
        XYZ.origin,
        ri = 5,
        re = 10,
        alfa_init = 0,
        alfa_proj = Pi,
        alfa_end = 3d / 2 * Pi,
        n = 50,
        slabThickness = 0.02,
      )
      height = 1.5
      floors = ShapeSeq((1 to 5).map(f => isenberg.slabRoof(height * f)))
      beams = isenberg.beams(height)
      shapes = Shape(
        isenberg.slabFloor,
        floors,
        beams
      ).transformWith(Matrix.rotate(-Pi / 2, Vec(x = 1))) //Points(points)
    } yield (shapes)

}
