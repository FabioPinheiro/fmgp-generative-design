package app.fmgp.geo

// import munit.Clue.generate
// import scala.scalajs.js.internal.UnitOps.unitOrOps
import munit.Assertions._
import io.circe._, io.circe.parser._, io.circe.syntax._

object TestUtils {
  inline def assertClose(output: Double, expected: Double, `+-`: Double = 0.0000000001): Unit =
    assert(math.abs(clue(output) - clue(expected)) < `+-`, s"The elements differ by more than ${`+-`}")

  inline def assertCloseMatrix(output: Matrix, expected: Matrix, `+-`: Double = 0.0000000001): Unit = {
    val a = output.productIterator.toSeq.asInstanceOf[Seq[Double]]
    val b = expected.productIterator.toSeq.asInstanceOf[Seq[Double]]
    assertEquals(a.length, b.length)
    a.zip(b).foreach { case (aa, bb) => assertClose(aa, bb, `+-`) }
  }

  //circe json
  inline def assertEncoder[A](a: A, s: String)(using Encoder[A]) =
    assertEquals(a.asJson.noSpaces.replaceAll("\\.0", ""), s)

  inline def assertDecode[T](str: String, expected: T)(using Decoder[T]) =
    decode[T](str) match {
      case Left(e)  => fail(s"Decode fail with $e")
      case Right(e) => assertEquals(e, expected)
    }
}
