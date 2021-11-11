package app.fmgp.geo.prebuilt

import zio._
import scala.math._
import app.fmgp.geo._
import app.fmgp.dsl._

//import app.fmgp.geo.prebuilt.RhythmicGymnasticsPavilionExample._
import app.fmgp.geo.prebuilt.IsenbergSchoolOfManagementHubExample._
import scala.util.Random

object TreesExample {

  def tree2d(
      base: XYZ,
      length: Double,
      angle: Double,
      deltaAngle: Double,
      reductionFactor: Double,
      iterations: Int = 6,
      leafRadius: Double = 0.1
  ) =
    tree2dRandom(
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

  def tree2dRandom(
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
            left <- tree2dRandom( //left
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
            right <- tree2dRandom( //right
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

  def tree3dRandom(
      base: XYZ,
      length: Double,
      angle: Double,
      minDeltaAngle: Double,
      maxDeltaAngle: Double,
      minReductionFactor: Double,
      maxReductionFactor: Double,
      maxIterations: Int = 10,
      leafRadius: Double = 0.1,
      previousVec: Vec = Vec(0, 1, 0)
  )(implicit random: Random): ZIO[app.fmgp.dsl.Dsl, Throwable, app.fmgp.geo.Shape] = {
    for {
      _ <- ZIO.unit
      tVec = Polar(rho = length, phi = angle).asVec
      newVec = Matrix.rotate(angle = random.between(0, 2 * Pi), axisVector = previousVec).dot(tVec.asVec).asVec
      top = base + newVec
      thickness = max(log(maxIterations), 0.5) * 0.05
      branch <- cylinder(base, top, thickness)
      leafOrLeftRight <-
        if (length < 0.5 || maxIterations < 1) sphere(top, leafRadius) //leaf
        else {
          for {
            left <- tree3dRandom( //left
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
              leafRadius,
              previousVec = newVec,
            )
            right <- tree3dRandom( //right
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
              leafRadius,
              previousVec = newVec,
            )
            leftRight <- shapes(left, right)
          } yield (leftRight)
        }
      model <- shapes(branch, leafOrLeftRight)
    } yield (model)
  }

  val program: zio.ZIO[app.fmgp.dsl.Dsl, Throwable, app.fmgp.geo.Shape] = {
    implicit val random: scala.util.Random = new scala.util.Random
    random.setSeed(532444)
    zShapes(
      tree2d(xyz(-10, 0), 5, Pi / 2, Pi / 8, 0.6, iterations = 7),
      tree2d(xyz(0, 0), 5, Pi / 2, Pi / 8, 0.8, iterations = 7),
      tree2d(xyz(10, 0), 5, Pi / 2, Pi / 6, 0.7, iterations = 7),
      tree2dRandom(xyz(-20, 0, -10), 5, Pi / 2, Pi / 16, Pi / 4, 0.6, 0.9),
      tree2dRandom(xyz(0, 0, -10), 5, Pi / 2, Pi / 16, Pi / 4, 0.6, 0.9),
      tree2dRandom(xyz(20, 0, -10), 5, Pi / 2, Pi / 16, Pi / 4, 0.6, 0.9),
      tree3dRandom(xyz(-20, 0, -30), 5, Pi / 2, Pi / 16, Pi / 4, 0.6, 0.9),
      tree3dRandom(xyz(0, 0, -30), 5, Pi / 2, Pi / 16, Pi / 4, 0.6, 0.9),
      tree3dRandom(xyz(20, 0, -30), 5, Pi / 2, Pi / 16, Pi / 4, 0.6, 0.9),
    )
  }

  def unsafeWorldShapes: Shape = defaultRuntime.unsafeRun(program)
  def unsafeWorld: World = World.addition(unsafeWorldShapes)
}
