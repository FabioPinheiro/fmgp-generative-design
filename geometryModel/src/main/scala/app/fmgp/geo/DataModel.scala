package app.fmgp.geo

object Dimensions {
  sealed trait D {
    def isD3: Boolean = false
    def isD2: Boolean = false
  }
  object D2 extends D {
    override def isD2: Boolean = true
  }
  object D3 extends D {
    override def isD3: Boolean = true
  }
  //case class Unrecognized(i: Int) extends D
}

sealed trait World {
  def shapes: ShapeSeq
}

//final case class WorldReset(shapes: ShapeSeq = Seq.empty) extends World
final case class WorldAddition(shapes: ShapeSeq) extends World
final case class WorldState(shapes: ShapeSeq, dimensions: Dimensions.D) extends World

object World {
  def addition(shapes: ShapeSeq): WorldAddition = WorldAddition(shapes)
  def w2D(shapes: ShapeSeq): WorldState = WorldState(shapes, dimensions = Dimensions.D2)
  def w3D(shapes: ShapeSeq): WorldState = WorldState(shapes, dimensions = Dimensions.D3)
  def w3DEmpty: WorldState = w3D(Seq.empty)
}

sealed trait Coordinate {
  def x: Double
  def y: Double
  def z: Double
  @inline def toXYZ = XYZ(x, y, z)
  @inline def asVec: Vec = Vec(x, y, z)
}

sealed trait Coordinate2D extends Coordinate {
  @inline def toXY0: XYZ = { assert(z == 0); XYZ(x, y, 0) }
  @inline def toX0Z: XYZ = { assert(z == 0); XYZ(x, 0, y) }
  @inline def forceX0Z: XYZ = XYZ(x, 0, z)
  @inline def forceXY0: XYZ = XYZ(x, y, 0)
}
sealed trait Coordinate3D extends Coordinate2D

/**
  * XYZ
  *
  * @param x
  * @param y
  * @param z
  */
final case class XYZ(x: Double, y: Double, z: Double = 0) extends Coordinate3D {
  @inline override def toXYZ: XYZ = this

  def toPolar = {
    assert(z == 0)
    Polar.fromXY(x, y)
  }

  def scale(s: Double): XYZ = XYZ(x * s, y * s, z * s)

  def +(other: XYZ) = XYZ(x + other.x, y + other.y, z + other.z)
  def +(x: Double = 0, y: Double = 0, z: Double = 0): XYZ = this + XYZ(x, y, z)
  def +(v: Vec): XYZ = this + v.asXYZ

  def -(other: XYZ) = XYZ(x - other.x, y - other.y, z - other.z)
  def -(x: Double = 0, y: Double = 0, z: Double = 0): XYZ = this - XYZ(x, y, z)
  def -(v: Vec): XYZ = this - v.asXYZ

  def pointPlusVector(vec: Vec) = this + vec
  def pointMinusPoint(other: XYZ) = this - other
}

object XYZ {
  def origin: XYZ = XYZ(0, 0, 0)
  def midpoint(p1: XYZ, p2: XYZ): XYZ =
    XYZ((p1.x + p2.x) / 2, (p1.y + p2.y) / 2, (p1.z + p2.z) / 2)
}

/**
  * ### 2D ###
  *
  * @param module 'ρ' (Rho) - module*size is also called the radius vector
  * @param argument 'ϕ' (Phi) - argument is also called the polar angle
  */
final case class Polar(rho: Double, phi: Double) extends Coordinate2D {
  def argument = rho
  def module = phi

  def x = rho * Math.cos(phi)
  def y = rho * Math.sin(phi)
  def z = 0 //FIXME
}

object Polar {
  type Pol = Polar

  /** Node the Z coordinate is discasted */
  def fromXYZ(xyz: XYZ): Polar = fromXY(xyz.x, xyz.y)
  def fromXY(x: Double, y: Double): Polar =
    Polar(Math.sqrt(x * x + y * y), Math.atan(y / x))
}

/** Cylindrical Coordinate among the Y-axis */
final case class Cylindrical(rho: Double, phi: Double, y: Double) extends Coordinate3D {
  def x = rho * Math.cos(phi)
  def z = rho * Math.sin(phi)
}

object Cylindrical {
  type Cyl = Cylindrical
  def fromXYZ(xyz: XYZ): Cylindrical = fromXYZ(xyz.x, xyz.y, xyz.z)
  def fromXYZ(x: Double, y: Double, z: Double): Cylindrical =
    Cylindrical(Math.sqrt(x * x + z * z), Math.atan(z / x), y)
}

final case class Spherical(rho: Double, phi: Double, psi: Double) extends Coordinate3D {
  def x = rho * Math.sin(psi) * Math.cos(phi)
  def y = rho * Math.sin(psi) * Math.sin(phi)
  def z = rho * Math.cos(psi)
}
object Spherical {
  type Sph = Spherical
  def fromXYZ(xyz: XYZ): Spherical = fromXYZ(xyz.x, xyz.y, xyz.z)
  def fromXYZ(x: Double, y: Double, z: Double): Spherical =
    Spherical(Math.sqrt(x * x + y * y + z * z), Math.atan(y / x), Math.atan(x * x + y * y) / z)
}

final case class Vec(x: Double = 0, y: Double = 0, z: Double = 0) extends Coordinate {
  @inline override def asVec: Vec = this

  @inline def asXYZ: XYZ = toXYZ
  @inline def unary_- = Vec(-x, -y, -z)

  def +(other: Vec) = Vec(x + other.x, y + other.y, z + other.z)
  def -(other: Vec) = Vec(x - other.x, y - other.y, z - other.z)

  /** Returns the vector dividied by the given scalar. */
  @inline def /(s: Double): Vec = {
    val f = 1 / s
    Vec(x * f, y * f, z * f)
  }

  /** The dot * product of two vectors. */
  @inline def *(v: Vec): Double = x * v.x + y * v.y + z * v.z
  @inline def dot(v: Vec): Double = this * v

  /** Returns the vector scaled by the given scalar. */
  @inline def *(s: Double): Vec = Vec(x * s, y * s, z * s)
  @inline def scale(s: Double): Vec = this * s
  @inline def scale(byX: Double = 1, byY: Double = 1, byZ: Double = 1) =
    Vec(x * byX, y * byY, z * byZ)

  /** Returns the cross product of two vectors. */
  @inline def ⨯(v: Vec): Vec =
    Vec(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x)

  /** Returns the cross product of two vectors. */
  @inline def cross(v: Vec): Vec = this ⨯ v

  @inline def magSqr = x * x + y * y + z * z

  /** Returns the magnitude (length) of this vector. */
  @inline def magnitude = math.sqrt(magSqr)

  @inline def length = magnitude

  /** Returns the normalized vector. */
  @inline def normalized = this / magnitude
  @inline def norm = this / magnitude
}
object Vec {
  def origin: Vec = Vec(0, 0, 0)
  def apply(from: XYZ, to: XYZ) = to.pointMinusPoint(from).asVec
  def cross(a: XYZ, b: XYZ, c: XYZ) = (b - a).asVec.cross((c - a).asVec) //(B - A) x (C - A)
}

// final case class Axis(origin: XYZ, abscissa: Vec, ordinate: Vec) {
//   lazy val applicate = xVec.cross(yVec)
// }

// object Axis {
//   def apply(origin: XYZ, abscissa: Vec, ordinate: Vec):Axis = {
//     new Axis(abscissa = )
//   }
//   def byXYZVector(XYZ: XYZ, vec: Vec) = Axis(XYZ, vec.normalized,)
//   val rgb = Axis(XYZ(0, 0, 0), Vec(1, 0, 0), Vec(0, 1, 0))
// }

// case class Matrix(m: Array[Array[Double]] = Array.ofDim[Double](4, 4)) {
//   def multiply(m: Matrix): Matrix = ???
//   def preTranslate(x: Double, y: Double, z: Double): Matrix = ???

// }
// object Matrix {
//   def scale(x: Double, y: Double, z: Double): Matrix = ???
// }

/**
  * Matrix:
  * 00 | 01| 02| 03
  * 10 | 11| 12| 13
  * 20 | 21| 22| 23
  * 30 | 31| 32| 33
  */
// format: off
final case class Matrix(
  m00: Double = 1, m01: Double = 0, m02: Double = 0, m03: Double = 0,
  m10: Double = 0, m11: Double = 1, m12: Double = 0, m13: Double = 0,
  m20: Double = 0, m21: Double = 0, m22: Double = 1, m23: Double = 0,
  m30: Double = 0, m31: Double = 0, m32: Double = 0, m33: Double = 1
) {
  // format: on
  override def toString: String = {
    val a = Array(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33)
    (for {
      i <- 0 to 3
      t = for (j <- 0 to 3; n = a(i * 4 + j)) yield n
    } yield t.mkString("[", ", ", "]")).mkString("Matrix(", " ; ", ")")
  }

  @inline def center: XYZ = XYZ(m03, m13, m23) //dot(Vex(0, 0, 0))

  @inline def *(v: Vec): XYZ = XYZ(
    x = m00 * v.x + m01 * v.y + m02 * v.z + /*w * v.w*/ m03,
    y = m10 * v.x + m11 * v.y + m12 * v.z + /*w * v.w*/ m13,
    z = m20 * v.x + m21 * v.y + m22 * v.z + /*w * v.w*/ m23,
    //w = m30 * v.x + m31 * v.y + m32 * v.z + w * v.w
  )
  @inline def dot(v: Vec): XYZ = this * v

  @inline def postMultiply(b: Matrix): Matrix = Matrix(
    m00 = b.m00 * m00 + b.m10 * m01 + b.m20 * m02 + b.m30 * m03,
    m01 = b.m01 * m00 + b.m11 * m01 + b.m21 * m02 + b.m31 * m03,
    m02 = b.m02 * m00 + b.m12 * m01 + b.m22 * m02 + b.m32 * m03,
    m03 = b.m03 * m00 + b.m13 * m01 + b.m23 * m02 + b.m33 * m03,
    /**/
    m10 = b.m00 * m10 + b.m10 * m11 + b.m20 * m12 + b.m30 * m13,
    m11 = b.m01 * m10 + b.m11 * m11 + b.m21 * m12 + b.m31 * m13,
    m12 = b.m02 * m10 + b.m12 * m11 + b.m22 * m12 + b.m32 * m13,
    m13 = b.m03 * m10 + b.m13 * m11 + b.m23 * m12 + b.m33 * m13,
    /**/
    m20 = b.m00 * m20 + b.m10 * m21 + b.m20 * m22 + b.m30 * m23,
    m21 = b.m01 * m20 + b.m11 * m21 + b.m21 * m22 + b.m31 * m23,
    m22 = b.m02 * m20 + b.m12 * m21 + b.m22 * m22 + b.m32 * m23,
    m23 = b.m03 * m20 + b.m13 * m21 + b.m23 * m22 + b.m33 * m23,
    /**/
    m30 = b.m00 * m30 + b.m10 * m31 + b.m20 * m32 + b.m30 * m33,
    m31 = b.m01 * m30 + b.m11 * m31 + b.m21 * m32 + b.m31 * m33,
    m32 = b.m02 * m30 + b.m12 * m31 + b.m22 * m32 + b.m32 * m33,
    m33 = b.m03 * m30 + b.m13 * m31 + b.m23 * m32 + b.m33 * m33
  )

  @inline def preMultiply(b: Matrix): Matrix = Matrix(
    m00 = m00 * b.m00 + m10 * b.m01 + m20 * b.m02 + m30 * b.m03,
    m01 = m01 * b.m00 + m11 * b.m01 + m21 * b.m02 + m31 * b.m03,
    m02 = m02 * b.m00 + m12 * b.m01 + m22 * b.m02 + m32 * b.m03,
    m03 = m03 * b.m00 + m13 * b.m01 + m23 * b.m02 + m33 * b.m03,
    /**/
    m10 = m00 * b.m10 + m10 * b.m11 + m20 * b.m12 + m30 * b.m13,
    m11 = m01 * b.m10 + m11 * b.m11 + m21 * b.m12 + m31 * b.m13,
    m12 = m02 * b.m10 + m12 * b.m11 + m22 * b.m12 + m32 * b.m13,
    m13 = m03 * b.m10 + m13 * b.m11 + m23 * b.m12 + m33 * b.m13,
    /**/
    m20 = m00 * b.m20 + m10 * b.m21 + m20 * b.m22 + m30 * b.m23,
    m21 = m01 * b.m20 + m11 * b.m21 + m21 * b.m22 + m31 * b.m23,
    m22 = m02 * b.m20 + m12 * b.m21 + m22 * b.m22 + m32 * b.m23,
    m23 = m03 * b.m20 + m13 * b.m21 + m23 * b.m22 + m33 * b.m23,
    /**/
    m30 = m00 * b.m30 + m10 * b.m31 + m20 * b.m32 + m30 * b.m33,
    m31 = m01 * b.m30 + m11 * b.m31 + m21 * b.m32 + m31 * b.m33,
    m32 = m02 * b.m30 + m12 * b.m31 + m22 * b.m32 + m32 * b.m33,
    m33 = m03 * b.m30 + m13 * b.m31 + m23 * b.m32 + m33 * b.m33
  )

  def preTranslate(x: Double, y: Double, z: Double): Matrix =
    preMultiply(Matrix.translate(x, y, z))
  def preTranslate(v: Vec): Matrix = preTranslate(v.x, v.y, v.z)
  def postTranslate(x: Double, y: Double, z: Double): Matrix =
    postMultiply(Matrix.translate(x, y, z))
  def postTranslate(v: Vec): Matrix = postTranslate(v.x, v.y, v.z)

  def preRotate(radians: Double, axisVector: Vec): Matrix =
    preMultiply(Matrix.rotate(radians, axisVector))
  def postRotate(radians: Double, axisVector: Vec): Matrix =
    postMultiply(Matrix.rotate(radians, axisVector))
  def preScale(xFactor: Double, yFactor: Double, zFactor: Double): Matrix =
    preMultiply(Matrix.scale(xFactor, yFactor, zFactor))
  def postScale(xFactor: Double, yFactor: Double, zFactor: Double): Matrix =
    postMultiply(Matrix.scale(xFactor, yFactor, zFactor))

  // format: off
  def transpose = Matrix(
    m00, m10, m20, m30,
    m01, m11, m21, m31,
    m02, m12, m22, m32,
    m03, m13, m23, m33,
  )
  // format: on

  def inverse = {

    //        n23 * n34 * n42 - n24 * n33 * n42 + n24 * n32 * n43 - n22 * n34 * n43 - n23 * n32 * n44 + n22 * n33 * n44
    val t00 = m12 * m23 * m31 - m13 * m22 * m31 + m13 * m21 * m32 - m11 * m23 * m32 - m12 * m21 * m33 + m11 * m22 * m33
    //        n14 * n33 * n42 - n13 * n34 * n42 - n14 * n32 * n43 + n12 * n34 * n43 + n13 * n32 * n44 - n12 * n33 * n44
    val t01 = m03 * m22 * m31 - m02 * m23 * m31 - m03 * m21 * m32 + m01 * m23 * m32 + m02 * m21 * m33 - m01 * m22 * m33
    //	      n13 * n24 * n42 - n14 * n23 * n42 + n14 * n22 * n43 - n12 * n24 * n43 - n13 * n22 * n44 + n12 * n23 * n44
    val t02 = m02 * m13 * m31 - m03 * m12 * m31 + m03 * m11 * m32 - m01 * m13 * m32 - m02 * m11 * m33 + m01 * m12 * m33
    //	      n14 * n23 * n32 - n13 * n24 * n32 - n14 * n22 * n33 + n12 * n24 * n33 + n13 * n22 * n34 - n12 * n23 * n34
    val t03 = m03 * m12 * m21 - m02 * m13 * m21 - m03 * m11 * m22 + m01 * m13 * m22 + m02 * m11 * m23 - m01 * m12 * m23

    val det = m00 * t00 + m10 * t01 + m20 * t02 + m30 * t03

    if (det == 0) Matrix(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
    else {
      val detInv = 1 / det
      Matrix(
        t00 * detInv,
        t01 * detInv,
        t02 * detInv,
        t03 * detInv,
        //
        (m13 * m22 * m30 - m12 * m23 * m30 - m13 * m20 * m32 + m10 * m23 * m32 + m12 * m20 * m33 - m10 * m22 * m33) * detInv,
        (m02 * m23 * m30 - m03 * m22 * m30 + m03 * m20 * m32 - m00 * m23 * m32 - m02 * m20 * m33 + m00 * m22 * m33) * detInv,
        (m03 * m12 * m30 - m02 * m13 * m30 - m03 * m10 * m32 + m00 * m13 * m32 + m02 * m10 * m33 - m00 * m12 * m33) * detInv,
        (m02 * m13 * m20 - m03 * m12 * m20 + m03 * m10 * m22 - m00 * m13 * m22 - m02 * m10 * m23 + m00 * m12 * m23) * detInv,
        //
        (m11 * m23 * m30 - m13 * m21 * m30 + m13 * m20 * m31 - m10 * m23 * m31 - m11 * m20 * m33 + m10 * m21 * m33) * detInv,
        (m03 * m21 * m30 - m01 * m23 * m30 - m03 * m20 * m31 + m00 * m23 * m31 + m01 * m20 * m33 - m00 * m21 * m33) * detInv,
        (m01 * m13 * m30 - m03 * m11 * m30 + m03 * m10 * m31 - m00 * m13 * m31 - m01 * m10 * m33 + m00 * m11 * m33) * detInv,
        (m03 * m11 * m20 - m01 * m13 * m20 - m03 * m10 * m21 + m00 * m13 * m21 + m01 * m10 * m23 - m00 * m11 * m23) * detInv,
        //
        (m12 * m21 * m30 - m11 * m22 * m30 - m12 * m20 * m31 + m10 * m22 * m31 + m11 * m20 * m32 - m10 * m21 * m32) * detInv,
        (m01 * m22 * m30 - m02 * m21 * m30 + m02 * m20 * m31 - m00 * m22 * m31 - m01 * m20 * m32 + m00 * m21 * m32) * detInv,
        (m02 * m11 * m30 - m01 * m12 * m30 - m02 * m10 * m31 + m00 * m12 * m31 + m01 * m10 * m32 - m00 * m11 * m32) * detInv,
        (m01 * m12 * m20 - m02 * m11 * m20 + m02 * m10 * m21 - m00 * m12 * m21 - m01 * m10 * m22 + m00 * m11 * m22) * detInv,
      )
    }

  }
}
object Matrix {
  // format: off
  @inline def translate(x: Double, y: Double, z: Double): Matrix = Matrix(
    1, 0, 0, x,
    0, 1, 0, y,
    0, 0, 1, z,
    0, 0, 0, 1
  )
  // format: on
  @inline def translate(v: Vec): Matrix = translate(v.x, v.y, v.z)

  /**
    * Returns the rotation matrix about the given angle and axis.
    *
    * @param angle the angle to rotate, in radians.
    * @param x the x-component of the axis vector to rotate around
    * @param y the y-component of the axis vector to rotate around
    * @param z the z-component of the axis vector to rotate around
    */
  @inline def rotate(angle: Double, x: Double, y: Double, z: Double): Matrix = rotate(angle, Vec(x, y, z))

  /**
    * Returns the rotation matrix about the given angle and axis.
    *
    * @param angle the angle to rotate, in radians.
    * @param axisVector the axis vector to rotate around.
    */
  @inline def rotate(angle: Double, axisVector: Vec): Matrix = {
    val s = Math.sin(angle)
    val c = Math.cos(angle)
    val t = 1 - c
    val norm = axisVector.normalized //must be normalized.
    val x = norm.x
    val y = norm.y
    val z = norm.z
    // format: off
    Matrix( 
      x*x*t + c   , x*y*t - z*s , x*z*t+y*s , 0,
      y*x*t + z*s , y*y*t + c   , y*z*t-x*s , 0,
      x*z*t - y*s , y*z*t + x*s , z*z*t + c , 0,
      0           , 0           , 0         , 1
    ) // format: on
  }

  // format: off
  @inline def scale(x: Double, y: Double, z: Double): Matrix = Matrix( 
    x, 0, 0, 0,
    0, y, 0, 0,
    0, 0, z, 0,
    0, 0, 0, 1
  )
  // format: on

  //def multiply(m1: Matrix4, m2: Matrix4) = m1.clone().multiply(m2)
//  def basis(xVector: Vector, yVector: Vector, zVector: Vector, origin: XYZ = XYZ.origin) = {
//    val tmp = new Matrix4
//    tmp.makeBasis(xVector.toVector3, yVector.toVector3, zVector.toVector3)
//    tmp.setPosition(origin.toVector3)
//    Matrix(tmp)
//  }

  /**
    * Based on http://www.euclideanspace.com/maths/geometry/rotations/conversions/angleToMatrix/index.htm
    */
  def axisCosSinAngle(axisVector: Vec, cosAngle: Double, sinAngle: Double) = {
    val (a, c, s) = (axisVector, cosAngle, sinAngle)
    val t = 1.0 - c

    val tmp1 = a.x * a.y * t
    val tmp2 = a.z * s
    val tmp3 = a.x * a.z * t
    val tmp4 = a.y * s
    val tmp5 = a.y * a.z * t
    val tmp6 = a.x * s

    // format: off
    Matrix(
      c + a.x * a.x * t , tmp1 + tmp2       , tmp3 - tmp4       , 0.0,
      tmp1 - tmp2       , c + a.y * a.y * t , tmp5 + tmp6       , 0.0,
      tmp3 + tmp4       , tmp5 - tmp6       , c + a.z * a.z * t , 0.0,
      0.0               , 0.0               , 0.0               , 1.0
    )
    // format: on
  }
  def alignFromAxisToAxis(fromAxis: Vec, toAxis: Vec) = {
    val dot = fromAxis * toAxis
    val cross = fromAxis.cross(toAxis) //(new Vector3()).crossVectors(fromAxis.toVector3, toAxis.toVector3)
    val crossLength = cross.length
    //TODO Improve vector colinearity check. Use approximate equality.
    val areColinear = crossLength == 0.0
    val matrix = if (areColinear) {
      val axis = Vec(1.0, 0.0, 0.0)
      if (dot < 0.0) axisCosSinAngle(axis, -1.0, 0.0)
      else axisCosSinAngle(axis, 1.0, 0.0)
    } else {
      val axis = cross.scale(1.0 / crossLength)
      axisCosSinAngle(axis, dot, crossLength)
    }
    matrix
  }

  def lookAt(from: XYZ, to: XYZ, tmp: Vec = Vec(0, 1, 0)): Matrix = {
    val forward = (from.asVec.-(to.asVec)).norm
    val tmpNorm = Vec(0, 1, 0).norm
    val right = tmpNorm.cross(forward).norm
    val up = forward.cross(right)
    // format: off
    Matrix(
      right.x, up.x, forward.x, from.x,
      right.y, up.y, forward.y, from.y,
      right.z, up.z, forward.z, from.z,
      0, 0, 0, 1 //TODO FIXME should this be '1' ?
    )
     // format: on
  }

  def makePerspective(left: Double, right: Double, top: Double, bottom: Double, near: Double, far: Double) = {
    val x = 2 * near / (right - left)
    val y = 2 * near / (top - bottom)

    val a = (right + left) / (right - left)
    val b = (top + bottom) / (top - bottom)
    val c = -(far + near) / (far - near)
    val d = -2 * far * near / (far - near)

    // format: off
    new Matrix (
      x, 0, a, 0,
      0, y, b, 0,
      0, 0, c, d,
      0, 0,-1, 0
    )
    // format: on
  }
}
