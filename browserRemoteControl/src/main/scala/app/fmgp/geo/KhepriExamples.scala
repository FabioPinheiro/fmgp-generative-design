package app.fmgp.geo
import scala.math._
import scala.util.Random

trait KhepriExamples extends Syntax {

  def cross = {
    line(Seq(xyz(-1, -1), xyz(-1, 0), xyz(1, 0), xyz(1, 1)))
    line(Seq(xyz(-1, 1), xyz(0, 1), xyz(0, -1), xyz(1, -1)))
  }

  def polygon(radius: Double = 1, vertex: Int = 5) = {
    assert(vertex >= 3)
    line((0 to vertex).map(i => pol(radius, 2 * Pi * i / vertex)))
  }

  def rectangle(p1: XYZ, p2: XYZ) = line(Seq(p1, xyz(p1.x, p2.y), p2, xyz(p2.x, p1.y)), closeLine = true)

  /**
    * Drawing Doric Columns
    * The drawing of a Doric column is divided into three parts:
    * shaft, echinus and abacus. Each of these parts has an independent function.
    *
    * @param p column's center base coordinate
    * @param hShaft shaft's height
    * @param rBaseShaft shaft's base radius
    * @param hEchinus echinus' height
    * @param rBaseEchinus echinus' base radius = shaft's top radius
    * @param hAbacus abacus' height
    * @param lAbacus abacus' length = 2*echinus top radius
    */
  def doricColumn2d(
      p: XYZ,
      hShaft: Double,
      rBaseShaft: Double,
      hEchinus: Double,
      rBaseEchinus: Double,
      hAbacus: Double,
      lAbacus: Double
  ) = {
    def shaft(p: XYZ, hShaft: Double, rBase: Double, rTop: Double) =
      line(
        Seq(
          p + xyz(-rTop, hShaft),
          p + xyz(-rBase, 0),
          p + xyz(+rBase, 0),
          p + xyz(+rTop, hShaft)
        ),
        closeLine = true
      )
    def echinus(p: XYZ, hEchinus: Double, rBase: Double, rTop: Double) =
      line(
        Seq(
          p + xyz(-rBase, 0),
          p + xyz(-rTop, hEchinus),
          p + xyz(+rTop, hEchinus),
          p + xyz(+rBase, 0)
        ),
        closeLine = true
      )
    def abacus(p: XYZ, hAbacus: Double, lAbacus: Double) =
      rectangle(p + xyz(-(lAbacus / 2), 0), p + xyz(lAbacus / 2, hAbacus))

    shaft(p, hShaft, rBaseShaft, rBaseEchinus)
    echinus(p + xyz(0, hShaft), hEchinus, rBaseEchinus, lAbacus / 2)
    abacus(p + xyz(0, hShaft + hEchinus), hAbacus, lAbacus)
  }

  def doricColumn3d(
      p: XYZ,
      hShaft: Double,
      rBaseShaft: Double,
      hEchinus: Double,
      rBaseEchinus: Double,
      hAbacus: Double,
      lAbacus: Double
  ) = {
    def shaft(p: XYZ, hShaft: Double, rBase: Double, rTop: Double) =
      coneFrustum(p, rBase, p + Vec(y = hShaft), rTop)
    def echinus(p: XYZ, hEchinus: Double, rBase: Double, rTop: Double) =
      coneFrustum(p, rBase, p + Vec(y = hEchinus), rTop)
    def abacus(p: XYZ, hAbacus: Double, lAbacus: Double) =
      box(p + Vec(-(lAbacus / 2), 0, -(lAbacus / 2)), p + Vec(lAbacus / 2, hAbacus, lAbacus / 2))

    shaft(p, hShaft, rBaseShaft, rBaseEchinus)
    echinus(p + xyz(0, hShaft), hEchinus, rBaseEchinus, lAbacus / 2)
    abacus(p + xyz(0, hShaft + hEchinus), hAbacus, lAbacus)
  }

  def crossOfCones(p: XYZ, rb: Double, rt: Double, l: Double) = {
    coneFrustum(p, rb, p + Vec(x = l), rt)
    coneFrustum(p, rb, p + Vec(y = l), rt)
    coneFrustum(p, rb, p + Vec(z = l), rt)
    coneFrustum(p, rb, p + Vec(x = -l), rt)
    coneFrustum(p, rb, p + Vec(y = -l), rt)
    coneFrustum(p, rb, p + Vec(z = -l), rt)
  }

  def spiralStairs(
      p: XYZ,
      radius: Double,
      height: Double,
      angle: Double,
      stairSize: Double = 1,
      stairs: Int = 10
  ): Unit = {
    assert(stairs >= 1)
    cone(p, radius * 2, p.+(y = height))
    for (i <- 1 to stairs - 1) {
      val hDelta = i * height / stairs
      val p1 = p.+(y = hDelta)
      val p2 = p + Cylindrical(stairSize, i * angle, hDelta).asVec
      cone(p1, radius, p2)
    }
  }

  // ### Tree ###
  def tree2d(
      base: XYZ,
      length: Double,
      angle: Double,
      deltaAngle: Double,
      reductionFactor: Double,
      iterations: Int = 6,
      leafRadius: Double = 0.1
  ): ShapeSeq = {
    val top = base + Polar(rho = length, phi = angle).asVec
    def branch(p0: XYZ, p1: XYZ): Shape = Line(Seq(p0, p1))
    def leaf(p: XYZ): Shape = Circle(leafRadius, top)
    ShapeSeq(branch(base, top)) ++ {
      if (iterations < 1) ShapeSeq(leaf(top))
      else {
        tree2d(
          top,
          length * reductionFactor,
          angle + deltaAngle,
          deltaAngle,
          reductionFactor,
          iterations = iterations - 1,
          leafRadius = leafRadius,
        ) ++ tree2d(
          top,
          length * reductionFactor,
          angle - deltaAngle,
          deltaAngle,
          reductionFactor,
          iterations = iterations - 1,
          leafRadius = leafRadius,
        )
      }
    }
  }

  def tree2Random(
      base: XYZ,
      length: Double,
      angle: Double,
      minDeltaAngle: Double,
      maxDeltaAngle: Double,
      minReductionFactor: Double,
      maxReductionFactor: Double,
      leafRadius: Double = 0.1
  )(implicit random: Random): ShapeSeq = {
    val top = base + Polar(rho = length, phi = angle).asVec
    def branch(p0: XYZ, p1: XYZ): Shape = Line(Seq(p0, p1))
    def leaf(p: XYZ): Shape = Circle(leafRadius, top)
    ShapeSeq(branch(base, top)) ++ {
      if (length < 0.5) ShapeSeq(leaf(top))
      else {
        tree2Random(
          top,
          length * random.between(minReductionFactor, maxReductionFactor),
          angle + random.between(minDeltaAngle, maxDeltaAngle),
          minDeltaAngle,
          maxDeltaAngle,
          minReductionFactor,
          maxReductionFactor,
          leafRadius
        ) ++ tree2Random(
          top,
          length * random.between(minReductionFactor, maxReductionFactor),
          angle - random.between(minDeltaAngle, maxDeltaAngle),
          minDeltaAngle,
          maxDeltaAngle,
          minReductionFactor,
          maxReductionFactor,
          leafRadius
        )
      }
    }
  }

  //def tree3d(

  // ### Trusses ### 6.7.1 Modeling Trusses
  //def truss
  //def DNA double helix

  //### 7.2 Constructive Geometry - union; intersection; subtraction

  //### 7.6 Extrusions

  //def sinusoidalWall ### 7.6.2 Extrusion Along a Path

  //7.7 Gaudí’s Columns
  // ### 8 Transformations ###
  // 8.2 Translation - move(sphere(), vxyz(1, 2, 3))
  // 8.3 Scale - scale(papal_cross(), 3)
  // 8.4 Rotation - rotate(papal_cross(), pi/4)
  // 8.5 Reflection - mirror(cone_frustum(p, rb, p+vz(h/2), rn), p+vz(h/2)) = def hourglass(p, rb, rn, h)
  // 12 Coordinate Space
}
