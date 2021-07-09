package app.fmgp.geo

// import munit.Clue.generate
// import scala.scalajs.js.internal.UnitOps.unitOrOps
import munit.Assertions._

object TestUtils {
  def assertClose(output: Double, expected: Double, `+-`: Double = 0.0000000001): Unit =
    assert(math.abs(clue(output) - clue(expected)) < `+-`, s"The elements differ by more than ${`+-`}")

  def assertCloseMatrix(output: Matrix, expected: Matrix, `+-`: Double = 0.0000000001): Unit = {
    val a = output.productIterator.toSeq.asInstanceOf[Seq[Double]]
    val b = expected.productIterator.toSeq.asInstanceOf[Seq[Double]]
    assertEquals(a.length, b.length)
    a.zip(b).foreach { case (aa, bb) => assertClose(aa, bb, `+-`) }
  }
}
