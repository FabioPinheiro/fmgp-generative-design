package app.fmgp.geo.prebuilt

import zio._
import scala.math._
import app.fmgp.geo._
import app.fmgp.dsl._

import app.fmgp.geo.prebuilt.RhythmicGymnasticsPavilionExample

object GeoZioExample {

  val program = for {
    _ <- ZIO.unit
    grid = RhythmicGymnasticsPavilionExample
      .damped_sin_roof_pts(u0(), 20, 3, 10, 15, Pi, 0.03, Pi / 50, Pi / 10, 60, 100, 24, 100, 1)
    sg <- surface_grid(grid)
    shape = sg.transformWith(Matrix.rotate(Pi / 2, Vec(1, 0, 0)).postTranslate(0, -100, 0))
  } yield (shape)

  val world: World = World.addition(defaultRuntime.unsafeRun(program))
}
