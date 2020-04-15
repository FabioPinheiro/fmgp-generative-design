package app.fmgp.geo
import scala.math._

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
  def doricColumn(
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

}
