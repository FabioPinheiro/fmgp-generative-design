package app.fmgp.geo

import utest._

object MatrixTest extends TestSuite {
  // This is necessary to find test methods in Scala.js
  //scalaJsSupport

  // Basic assertion
  val tests = Tests {

    test("invert") {
      test("(I = I')") {
        assert(Matrix().inverse == Matrix())
      }
      // format: off
      val m1 = Matrix(
         1, 1, 1,-1,
         1, 1,-1, 1,
         1,-1, 1, 1,
        -1, 1, 1, 1
      )
      val m1I = Matrix(
         1/4d, 1/4d, 1/4d,-1/4d,
         1/4d, 1/4d,-1/4d, 1/4d,
         1/4d,-1/4d, 1/4d, 1/4d,
        -1/4d, 1/4d, 1/4d, 1/4d
      )
      val m2 = Matrix(
         1, 0, 0, 1,
         0, 1, 0, 0,
         0, 0, 1, 0,
         0, 0, 0, 1
      )
      val m2I = Matrix(
         1, 0, 0, -1,
         0, 1, 0, 0,
         0, 0, 1, 0,
         0, 0, 0, 1
      )
      val m3 = Matrix(
         1, 2, 0, 0,
         0, 1, 0, 4,
         0, 0, 2, 0,
         1, 0, 4, 0
      )
      val m3I = Matrix(
        0,      0,    -2,    1,
        0.5,    0,    1,     -0.5,
        0,      0,    0.5,   0,
        -0.125, 0.25, -0.25, 0.125
      )
      val m4 = Matrix(
         2, 0, 5, 1,
         1, 2, 3, 5,
         2, 0, 2, 0,
         0, 1, 0, 2
      )
      // format: on
      test("(A inverted = A')") {
        assert(m1.inverse == m1I)
        assert(m2.inverse == m2I)
        assert(m3.inverse == m3I)
      }
      test("(A x A' = I)") {
        assert(m1.inverse.postMultiply(m1) == Matrix())
        assert(m2.inverse.postMultiply(m2) == Matrix())
        assert(m3.inverse.postMultiply(m3) == Matrix())
        assert(m4.inverse.postMultiply(m4) == Matrix())
      }
    }
  }
}
