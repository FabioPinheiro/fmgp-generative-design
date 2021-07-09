package app.fmgp.geo

import app.fmgp.geo.TestUtils._
//import utest._

//object MatrixTest extends TestSuite {
class MatrixTest extends munit.FunSuite {

  // This is necessary to find test methods in Scala.js
  //scalaJsSupport

  // #######################
  // ### Basic assertion ###
  // #######################

  test("invert(I = I')") {
    assertEquals(Matrix().inverse, Matrix())
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
    assertEquals(m1.inverse, m1I)
    assertEquals(m2.inverse, m2I)
    assertEquals(m3.inverse, m3I)
  }
  test("(A x A' = I)") {
    assertEquals(m1.inverse.postMultiply(m1), Matrix())
    assertEquals(m2.inverse.postMultiply(m2), Matrix())
    assertEquals(m3.inverse.postMultiply(m3), Matrix())
    assertEquals(m4.inverse.postMultiply(m4), Matrix())
  }

  // ###############
  // ### extract ###
  // ###############
  val aux = Vec(111, 333, 555)
  val (sx, sy, sz) = (0.2, 2, 10)
  val (rx, ry, rz) = (0.3, 0.5, 1.4)
  val m11 = Matrix
    .translate(aux)
    .postRotate(rz, Vec(0, 0, 1))
    .postRotate(ry, Vec(0, 1, 0))
    .postRotate(rx, Vec(1, 0, 0))
    .postScale(sx, sy, sz)

  test("Translation") {
    assert(m11.extractTranslation == aux.asXYZ)
  }
  test("Scale") {
    val (x, y, z) = m11.extractScale
    assertClose(x, sx)
    assertClose(y, sy)
    assertClose(z, sz)
  }
  test("Rotation") {
    val expected = Matrix()
      .postRotate(rz, Vec(0, 0, 1))
      .postRotate(ry, Vec(0, 1, 0))
      .postRotate(rx, Vec(1, 0, 0))
    val output = m11.extractRotation
    assertCloseMatrix(output, expected)
  }

  test("Yaw & Pitch & Roll") {
    val (yaw, pitch, roll) = m11.extractYawPitchRoll
    assertClose(yaw, rz)
    assertClose(pitch, ry)
    assertClose(roll, rx)
  }
}
