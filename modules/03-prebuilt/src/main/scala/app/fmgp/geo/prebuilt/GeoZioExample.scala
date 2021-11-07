package app.fmgp.geo.prebuilt

import scala.scalajs.js.annotation._

import zio._
import scala.math._
import app.fmgp.geo._
import app.fmgp.dsl._

//import app.fmgp.geo.prebuilt.RhythmicGymnasticsPavilionExample._
import app.fmgp.geo.prebuilt.IsenbergSchoolOfManagementHubExample._
import scala.util.Random

@JSExportTopLevel("GeoZioExample")
object GeoZioExample {

  def tree2Random(
      base: XYZ,
      length: Double,
      angle: Double,
      minDeltaAngle: Double,
      maxDeltaAngle: Double,
      minReductionFactor: Double,
      maxReductionFactor: Double,
      leafRadius: Double = 0.1
  )(implicit random: Random): ZIO[app.fmgp.dsl.Dsl, Any, app.fmgp.geo.Shape] = {
    //val top = base + Polar(rho = length, phi = angle).asVec
    //def branch(p0: XYZ, p1: XYZ): Shape = LinePath(Seq(p0, p1))
    //def leaf(p: XYZ): Shape = Circle(leafRadius, top)
    // ShapeSeq(branch(base, top)) ++ {
    val aaa = for {
      _ <- ZIO.unit
      top = base + Polar(rho = length, phi = angle).asVec
      branch <- line(Seq(base, top))
      leaf <- {
        if (length < 0.5) circle(leafRadius, top)
        else
          tree2Random(
            top,
            length * random.between(minReductionFactor, maxReductionFactor),
            angle + random.between(minDeltaAngle, maxDeltaAngle),
            minDeltaAngle,
            maxDeltaAngle,
            minReductionFactor,
            maxReductionFactor,
            leafRadius
          ).flatMap(s =>
            tree2Random(
              top,
              length * random.between(minReductionFactor, maxReductionFactor),
              angle - random.between(minDeltaAngle, maxDeltaAngle),
              minDeltaAngle,
              maxDeltaAngle,
              minReductionFactor,
              maxReductionFactor,
              leafRadius
            ).map(e => ShapeSeq(Seq(e, s)))
          )
      }
      right <- emptyShape
      // right <- {
      //   if (length < 0.5) emptyShape
      //   else
      //     tree2Random(
      //       top,
      //       length * random.between(minReductionFactor, maxReductionFactor),
      //       angle - random.between(minDeltaAngle, maxDeltaAngle),
      //       minDeltaAngle,
      //       maxDeltaAngle,
      //       minReductionFactor,
      //       maxReductionFactor,
      //       leafRadius
      //     )
      // }
      model <- shapes(branch, leaf) //, left, right)
    } yield (model)
    aaa
    // aaa.flatMap(e =>
    //   tree2Random(
    //     top,
    //     length * random.between(minReductionFactor, maxReductionFactor),
    //     angle + random.between(minDeltaAngle, maxDeltaAngle),
    //     minDeltaAngle,
    //     maxDeltaAngle,
    //     minReductionFactor,
    //     maxReductionFactor,
    //     leafRadius
    //   ).map(a => ShapeSeq(Seq(e, a)))
    // )

  }

  implicit val random: scala.util.Random = new scala.util.Random

  val program: zio.ZIO[app.fmgp.dsl.Dsl, Any, app.fmgp.geo.Shape] =
    tree2Random(xyz(-20, 0, -10), 5, Pi / 2, Pi / 16, Pi / 4, 0.6, 0.9)
  // for {
  //   _ <- ZIO.unit
  //   //points = pts_circle(XYZ.origin, r = 2, alfa_init = 0, alfa_end = Pi, n = 10)
  //   isenberg = Isenberg(
  //     XYZ.origin,
  //     ri = 5,
  //     re = 10,
  //     alfa_init = 0,
  //     alfa_proj = Pi,
  //     alfa_end = 3d / 2 * Pi,
  //     n = 50,
  //     slabThickness = 0.02,
  //   )
  //   height = 1.5
  //   floors = ShapeSeq((1 to 5).map(f => isenberg.slabRoof(height * f)))
  //   beams = isenberg.beams(height)
  //   shapes = Shape(
  //     isenberg.slabFloor,
  //     floors,
  //     beams
  //   ).transformWith(Matrix.rotate(-Pi / 2, Vec(x = 1))) //Points(points)
  // } yield (shapes)

  def worldShapes: Shape = defaultRuntime.unsafeRun(program)
  def world: World = World.addition(worldShapes)
}
