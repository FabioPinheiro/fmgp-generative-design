package app.fmgp.geo

/**
  * BezierCurves
  * I would love to see the BezierCurves of matrix fly on GPU
  * BE
  */
object BezierCurves {
  @inline def fc0(t: Double) = math.pow(1 - t, 3)
  @inline def fc1(t: Double) = math.pow(1 - t, 2) * t * 3
  @inline def fc2(t: Double) = math.pow(t, 2) * (1 - t) * 3
  @inline def fc3(t: Double) = math.pow(t, 3)

  /**
    * @param t must be between 0 and 1
    */
  @inline def cubic(t: Double, p0: Double, p1: Double, p2: Double, p3: Double): Double =
    p0 * fc0(t) + p1 * fc1(t) + p2 * fc2(t) + p3 * fc3(t)
  @inline def cubic(t: Double): (Double, Double, Double, Double) => Double = {
    val v0 = fc0(t)
    val v1 = fc1(t)
    val v2 = fc2(t)
    val v3 = fc3(t)
    (p0: Double, p1: Double, p2: Double, p3: Double) => v0 * p0 + v1 * p1 + v2 * p2 + v3 * p3
  }

  /** TODO quadratic bezier curves can be optimized */
  @inline def quadratic(t: Double, p0: Double, p12: Double, p3: Double): Double = cubic(t, p0, p12, p12, p3)

  @inline def linear(t: Double, v0: Double, v1: Double): Double = //cubic(t, p0, p0, p3, p3)
    (1 - t) * v0 + t * v1

  // ### Coordinate ###
  def cubic(t: Double, p0: XYZ, p1: XYZ, p2: XYZ, p3: XYZ): XYZ =
    p0.scale(fc0(t)) + p1.scale(fc1(t)) + p2.scale(fc2(t)) + p3.scale(fc3(t))

  def cubic(t: Double, p0: XYZ, p1: Vec, p2: Vec, p3: XYZ): XYZ =
    p0.scale(fc0(t)) + p0.+(p1).scale(fc1(t)) + p3.+(p2).scale(fc2(t)) + p3.scale(fc3(t))

  // ### Matrix ###
  def linear(t: Double, m0: Matrix, m1: Matrix): Matrix = (m0, m1) match {
    case (
        Matrix(a00, a01, a02, a03, a10, a11, a12, a13, a20, a21, a22, a23, a30, a31, a32, a33),
        Matrix(b00, b01, b02, b03, b10, b11, b12, b13, b20, b21, b22, b23, b30, b31, b32, b33)
        ) =>
      Matrix(
        linear(t, a00, b00),
        linear(t, a01, b01),
        linear(t, a02, b02),
        linear(t, a03, b03),
        linear(t, a10, b10),
        linear(t, a11, b11),
        linear(t, a12, b12),
        linear(t, a13, b13),
        linear(t, a20, b20),
        linear(t, a21, b21),
        linear(t, a22, b22),
        linear(t, a23, b23),
        linear(t, a30, b30),
        linear(t, a31, b31),
        linear(t, a32, b32),
        linear(t, a33, b33),
      )
  }

  def quadratic(t: Double, m0: Matrix, m1: Matrix, m2: Matrix): Matrix = (m0, m1, m2) match {
    case (
        Matrix(a00, a01, a02, a03, a10, a11, a12, a13, a20, a21, a22, a23, a30, a31, a32, a33),
        Matrix(b00, b01, b02, b03, b10, b11, b12, b13, b20, b21, b22, b23, b30, b31, b32, b33),
        Matrix(c00, c01, c02, c03, c10, c11, c12, c13, c20, c21, c22, c23, c30, c31, c32, c33)
        ) =>
      Matrix(
        quadratic(t, a00, b00, c00),
        quadratic(t, a01, b01, c01),
        quadratic(t, a02, b02, c02),
        quadratic(t, a03, b03, c03),
        quadratic(t, a10, b10, c10),
        quadratic(t, a11, b11, c11),
        quadratic(t, a12, b12, c12),
        quadratic(t, a13, b13, c13),
        quadratic(t, a20, b20, c20),
        quadratic(t, a21, b21, c21),
        quadratic(t, a22, b22, c22),
        quadratic(t, a23, b23, c23),
        quadratic(t, a30, b30, c30),
        quadratic(t, a31, b31, c31),
        quadratic(t, a32, b32, c32),
        quadratic(t, a33, b33, c33),
      )
  }

  def cubic(t: Double, m0: Matrix, m1: Matrix, m2: Matrix, m3: Matrix): Matrix = (m0, m1, m2, m3) match {
    case (
        Matrix(a00, a01, a02, a03, a10, a11, a12, a13, a20, a21, a22, a23, a30, a31, a32, a33),
        Matrix(b00, b01, b02, b03, b10, b11, b12, b13, b20, b21, b22, b23, b30, b31, b32, b33),
        Matrix(c00, c01, c02, c03, c10, c11, c12, c13, c20, c21, c22, c23, c30, c31, c32, c33),
        Matrix(d00, d01, d02, d03, d10, d11, d12, d13, d20, d21, d22, d23, d30, d31, d32, d33)
        ) =>
      val fff = cubic(t)
      Matrix(
        fff(a00, b00, c00, d00),
        fff(a01, b01, c01, d01),
        fff(a02, b02, c02, d02),
        fff(a03, b03, c03, d03),
        fff(a10, b10, c10, d10),
        fff(a11, b11, c11, d11),
        fff(a12, b12, c12, d12),
        fff(a13, b13, c13, d13),
        fff(a20, b20, c20, d20),
        fff(a21, b21, c21, d21),
        fff(a22, b22, c22, d22),
        fff(a23, b23, c23, d23),
        fff(a30, b30, c30, d30),
        fff(a31, b31, c31, d31),
        fff(a32, b32, c32, d32),
        fff(a33, b33, c33, d33),
      )
  }
  def cubic(t: Double, p0: Matrix, p1: Vec, p2: Vec, p3: Matrix): Matrix = {
    val aux = p0.center.scale(fc0(t)) +
      p0.center.+(p1).scale(fc1(t)) +
      p3.center.+(p2).scale(fc2(t)) +
      p3.center.scale(fc3(t))
    val aaa = Matrix().postTranslate(aux.asVec)
    val n00 = linear(t, p0.m00, p3.m00)
    val n01 = linear(t, p0.m01, p3.m01)
    val n02 = linear(t, p0.m02, p3.m02)
    val n10 = linear(t, p0.m10, p3.m10)
    val n11 = linear(t, p0.m11, p3.m11)
    val n12 = linear(t, p0.m12, p3.m12)
    val n20 = linear(t, p0.m20, p3.m20)
    val n21 = linear(t, p0.m21, p3.m21)
    val n22 = linear(t, p0.m22, p3.m22)
    aaa.copy(m00 = n00, m01 = n01, m02 = n02, m10 = n10, m11 = n11, m12 = n12, m20 = n20, m21 = n21, m22 = n22)
  }
  def cubic(t: Double, p0: Matrix, p1: Double, p2: Double, p3: Matrix): Matrix = {
    val aux = p0.center.scale(fc0(t)) +
      p0.dot(Vec(p1, 0, 0)).+(Vec(p1, 0, 0)).scale(fc1(t)) +
      p3.dot(Vec(-p2, 0, 0)).+(Vec(-p2, 0, 0)).scale(fc2(t)) +
      p3.center.scale(fc3(t))
    val aaa = Matrix().postTranslate(aux.asVec)
    val n00 = linear(t, p0.m00, p3.m00)
    val n01 = linear(t, p0.m01, p3.m01)
    val n02 = linear(t, p0.m02, p3.m02)
    val n10 = linear(t, p0.m10, p3.m10)
    val n11 = linear(t, p0.m11, p3.m11)
    val n12 = linear(t, p0.m12, p3.m12)
    val n20 = linear(t, p0.m20, p3.m20)
    val n21 = linear(t, p0.m21, p3.m21)
    val n22 = linear(t, p0.m22, p3.m22)
    aaa.copy(m00 = n00, m01 = n01, m02 = n02, m10 = n10, m11 = n11, m12 = n12, m20 = n20, m21 = n21, m22 = n22)
  }
}
//we can make this fly on GPU!!
