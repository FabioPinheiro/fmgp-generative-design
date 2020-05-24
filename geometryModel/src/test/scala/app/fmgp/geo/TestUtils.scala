package app.fmgp.geo

object TestUtils {
  def assertClose(output: Double, expected: Double, `+-`: Double = 0.0000000001): Unit =
    assert(math.abs(output - expected) < `+-`)

  def assertCloseMatrix(output: Matrix, expected: Matrix, `+-`: Double = 0.0000000001): Unit = {
    val a = Matrix.unapply(output).get.productIterator.toSeq.asInstanceOf[Seq[Double]]
    val b = Matrix.unapply(expected).get.productIterator.toSeq.asInstanceOf[Seq[Double]]
    assert(a.length == b.length)
    a.zip(b).foreach { case (aa, bb) => assertClose(aa, bb, `+-`) }
  }
}
