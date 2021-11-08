package app.fmgp.geo.prebuilt

import zio._
import scala.math._
import app.fmgp.geo._
import app.fmgp.dsl._

//import app.fmgp.geo.prebuilt.RhythmicGymnasticsPavilionExample._
import app.fmgp.geo.prebuilt.IsenbergSchoolOfManagementHubExample._
import scala.util.Random

object GeoZioExample {

  def tree2d(
      base: XYZ,
      length: Double,
      angle: Double,
      deltaAngle: Double,
      reductionFactor: Double,
      iterations: Int = 6,
      leafRadius: Double = 0.1
  ) =
    tree2Random(
      base = base,
      length = length,
      angle = angle,
      minDeltaAngle = deltaAngle,
      maxDeltaAngle = deltaAngle,
      minReductionFactor = reductionFactor,
      maxReductionFactor = reductionFactor,
      maxIterations = iterations,
      leafRadius = leafRadius,
    )(new scala.util.Random) //REMOVE

  def tree2Random(
      base: XYZ,
      length: Double,
      angle: Double,
      minDeltaAngle: Double,
      maxDeltaAngle: Double,
      minReductionFactor: Double,
      maxReductionFactor: Double,
      maxIterations: Int = 100,
      leafRadius: Double = 0.1
  )(implicit random: Random): ZIO[app.fmgp.dsl.Dsl, Throwable, app.fmgp.geo.Shape] = {
    for {
      _ <- ZIO.unit
      top = base + Polar(rho = length, phi = angle).asVec
      branch <- line(Seq(base, top))
      leafOrLeftRight <-
        if (length < 0.5 || maxIterations < 1) circle(leafRadius, top) //leaf
        else {
          for {
            left <- tree2Random( //left
              top,
              length * (if (minReductionFactor < maxReductionFactor)
                          random.between(minReductionFactor, maxReductionFactor)
                        else minReductionFactor),
              angle - (if (minDeltaAngle < maxDeltaAngle) random.between(minDeltaAngle, maxDeltaAngle)
                       else minDeltaAngle),
              minDeltaAngle,
              maxDeltaAngle,
              minReductionFactor,
              maxReductionFactor,
              maxIterations = maxIterations - 1,
              leafRadius
            )
            right <- tree2Random( //right
              top,
              length * (if (minReductionFactor < maxReductionFactor)
                          random.between(minReductionFactor, maxReductionFactor)
                        else minReductionFactor),
              angle + (if (minDeltaAngle < maxDeltaAngle) random.between(minDeltaAngle, maxDeltaAngle)
                       else minDeltaAngle),
              minDeltaAngle,
              maxDeltaAngle,
              minReductionFactor,
              maxReductionFactor,
              maxIterations = maxIterations - 1,
              leafRadius
            )
            leftRight <- shapes(left, right)
          } yield (leftRight)
        }
      model <- shapes(branch, leafOrLeftRight) //, left, right)
    } yield (model)
  }

  val programTrees: zio.ZIO[app.fmgp.dsl.Dsl, Throwable, app.fmgp.geo.Shape] = {
    implicit val random: scala.util.Random = new scala.util.Random
    random.setSeed(532443)
    zShapes(
      tree2d(xyz(-10, 0), 5, Pi / 2, Pi / 8, 0.6, iterations = 7),
      tree2d(xyz(0, 0), 5, Pi / 2, Pi / 8, 0.8, iterations = 7),
      tree2d(xyz(10, 0), 5, Pi / 2, Pi / 6, 0.7, iterations = 7),
      tree2Random(xyz(-20, 0, -10), 5, Pi / 2, Pi / 16, Pi / 4, 0.6, 0.9),
      tree2Random(xyz(0, 0, -10), 5, Pi / 2, Pi / 16, Pi / 4, 0.6, 0.9),
      tree2Random(xyz(20, 0, -10), 5, Pi / 2, Pi / 16, Pi / 4, 0.6, 0.9),
    )
  }

  val program: zio.ZIO[app.fmgp.dsl.Dsl, Throwable, app.fmgp.geo.Shape] =
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

  def unsafeWorldShapes: Shape = defaultRuntime.unsafeRun(program)
  def unsafeWorld: World = World.addition(unsafeWorldShapes)
}
