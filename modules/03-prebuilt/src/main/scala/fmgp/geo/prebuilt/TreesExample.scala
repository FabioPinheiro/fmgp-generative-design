package fmgp.geo.prebuilt

import zio._
import scala.math._
import fmgp.geo._
import fmgp.dsl._

//import fmgp.geo.prebuilt.RhythmicGymnasticsPavilionExample._
import fmgp.geo.prebuilt.IsenbergSchoolOfManagementHubExample._

object TreesExample {

  type ZOUT = UIO[fmgp.geo.Shape]

  trait Tree {
    def tree2d(
        base: XYZ,
        length: Double,
        angle: Double,
        deltaAngle: Double,
        reductionFactor: Double,
        iterations: Int = 6,
        leafRadius: Double = 0.1
    ): ZOUT

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
    ): ZOUT

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
    ): ZOUT
  }

  case class TreeLive(random: Random, dsl: Dsl) extends Tree {
    import dsl._

    override def tree2d(
        base: XYZ,
        length: Double,
        angle: Double,
        deltaAngle: Double,
        reductionFactor: Double,
        iterations: Int = 6,
        leafRadius: Double = 0.1
    ): ZOUT =
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
      )

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
    ) = {

      def randomReductionFactor =
        if (minReductionFactor < maxReductionFactor) random.nextDoubleBetween(minReductionFactor, maxReductionFactor)
        else ZIO.succeed(minReductionFactor)
      def randomDeltaAngle =
        if (minDeltaAngle < maxDeltaAngle) random.nextDoubleBetween(minDeltaAngle, maxDeltaAngle)
        else ZIO.succeed(minDeltaAngle)

      for {
        _ <- ZIO.unit
        top = base + Polar(rho = length, phi = angle).asVec
        branch <- line(Seq(base, top))
        leafOrLeftRight <-
          if (length < 0.5 || maxIterations < 1) circle(leafRadius, top) //leaf
          else {
            for {
              leftReductionFactor <- randomReductionFactor
              leftDeltaAngle <- randomDeltaAngle
              left <- tree2dRandom( //left
                top,
                length * leftReductionFactor,
                angle - leftDeltaAngle,
                minDeltaAngle,
                maxDeltaAngle,
                minReductionFactor,
                maxReductionFactor,
                maxIterations = maxIterations - 1,
                leafRadius
              )
              rightReductionFactor <- randomReductionFactor
              rightDeltaAngle <- randomDeltaAngle
              right <- tree2dRandom( //right
                top,
                length * rightReductionFactor,
                angle + rightDeltaAngle,
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

    override def tree3dRandom(
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
    ): ZOUT = {

      def randomReductionFactor =
        if (minReductionFactor < maxReductionFactor) random.nextDoubleBetween(minReductionFactor, maxReductionFactor)
        else ZIO.succeed(minReductionFactor)
      def randomDeltaAngle =
        if (minDeltaAngle < maxDeltaAngle) random.nextDoubleBetween(minDeltaAngle, maxDeltaAngle)
        else ZIO.succeed(minDeltaAngle)

      for {
        _ <- ZIO.unit
        tVec = Polar(rho = length, phi = angle).asVec
        rotateAngle <- random.nextDoubleBetween(0, 2 * Pi)
        newVec = Matrix.rotate(angle = rotateAngle, axisVector = previousVec).dot(tVec.asVec).asVec
        top = base + newVec
        thickness = max(log(maxIterations), 0.5) * 0.05
        branch <- cylinder(base, top, thickness)
        leafOrLeftRight <-
          if (length < 0.5 || maxIterations < 1) sphere(top, leafRadius) //leaf
          else {
            for {
              leftReductionFactor <- randomReductionFactor
              leftDeltaAngle <- randomDeltaAngle
              left <- tree3dRandom( //left
                top,
                length * leftReductionFactor,
                angle - leftDeltaAngle,
                minDeltaAngle,
                maxDeltaAngle,
                minReductionFactor,
                maxReductionFactor,
                maxIterations = maxIterations - 1,
                leafRadius,
                previousVec = newVec,
              )
              rightReductionFactor <- randomReductionFactor
              rightDeltaAngle <- randomDeltaAngle
              right <- tree3dRandom( //right
                top,
                length * rightReductionFactor,
                angle + rightDeltaAngle,
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
  }

  object TreeLive {
    val layer: URLayer[Has[Random] with Has[Dsl], Has[Tree]] =
      (TreeLive(_, _)).toLayer[Tree]
  }

  // Accessor Methods
  def tree2d(
      base: XYZ,
      length: Double,
      angle: Double,
      deltaAngle: Double,
      reductionFactor: Double,
      iterations: Int = 6,
      leafRadius: Double = 0.1
  ): URIO[Has[Tree], fmgp.geo.Shape] = ZIO.serviceWith(
    _.tree2d(base, length, angle, deltaAngle, reductionFactor, iterations, leafRadius)
  )

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
  ): URIO[Has[Tree], fmgp.geo.Shape] = ZIO.serviceWith(
    _.tree2dRandom(
      base,
      length,
      angle,
      minDeltaAngle,
      maxDeltaAngle,
      minReductionFactor,
      maxReductionFactor,
      maxIterations,
      leafRadius
    )
  )

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
  ): URIO[Has[Tree], fmgp.geo.Shape] = ZIO.serviceWith(
    _.tree3dRandom(
      base,
      length,
      angle,
      minDeltaAngle,
      maxDeltaAngle,
      minReductionFactor,
      maxReductionFactor,
      maxIterations,
      leafRadius,
      previousVec
    )
  )

  val program: zio.ZIO[zio.Has[Tree] with zio.Has[Dsl], Throwable, fmgp.geo.Shape] = {
    for {
      a1 <- tree2d(xyz(-10, 0), 5, Pi / 2, Pi / 8, 0.6, iterations = 7)
      a2 <- tree2d(xyz(0, 0), 5, Pi / 2, Pi / 8, 0.8, iterations = 7)
      a3 <- tree2d(xyz(10, 0), 5, Pi / 2, Pi / 6, 0.7, iterations = 7)
      b1 <- tree2dRandom(xyz(-20, 0, -10), 5, Pi / 2, Pi / 16, Pi / 4, 0.6, 0.9)
      b2 <- tree2dRandom(xyz(0, 0, -10), 5, Pi / 2, Pi / 16, Pi / 4, 0.6, 0.9)
      b3 <- tree2dRandom(xyz(20, 0, -10), 5, Pi / 2, Pi / 16, Pi / 4, 0.6, 0.9)
      c1 <- tree3dRandom(xyz(-20, 0, -30), 5, Pi / 2, Pi / 16, Pi / 4, 0.6, 0.9)
      c2 <- tree3dRandom(xyz(0, 0, -30), 5, Pi / 2, Pi / 16, Pi / 4, 0.6, 0.9)
      c3 <- tree3dRandom(xyz(20, 0, -30), 5, Pi / 2, Pi / 16, Pi / 4, 0.6, 0.9)
      ret <- shapes(a1, a2, a3, b1, b2, b3, c1, c2, c3)
    } yield ret
  }
}
