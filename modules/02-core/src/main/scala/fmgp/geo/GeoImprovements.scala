package fmgp.geo

import fmgp.geo
import typings.three.mod
import scala.scalajs.js.JSConverters._

import scala.scalajs.js.annotation.JSExportTopLevel
import scala.scalajs.js.annotation.JSExport

@JSExportTopLevel("GeoImprovements")
object GeoImprovements {

  def matrix2matrix(m: geo.Matrix): typings.three.matrix4Mod.Matrix4 =
    matrix2matrix(m, new typings.three.matrix4Mod.Matrix4)

  def matrix2matrix(m: geo.Matrix, aux: typings.three.matrix4Mod.Matrix4): typings.three.matrix4Mod.Matrix4 = {
    // format: off
    aux.set(
        m.m00, m.m01, m.m02, m.m03,
        m.m10, m.m11, m.m12, m.m13,
        m.m20, m.m21, m.m22, m.m23,
        m.m30, m.m31, m.m32, m.m33
    )
    // format: on
    aux
  }

  def matrixJs2matrix(m: typings.three.matrix4Mod.Matrix4): geo.Matrix = {
    val aux = m.elements
    // format: off
    geo.Matrix(
        m00 = aux(0), m01= aux(1), m02= aux(2), m03= aux(3),
        m10 = aux(4), m11= aux(5), m12= aux(6), m13= aux(7),
        m20 = aux(8), m21= aux(9), m22= aux(10), m23= aux(11),
        m30 = aux(12), m31= aux(13), m32= aux(14), m33= aux(15)
    )
    // format: on
  }

  @inline def float2ArrayLike(points: Seq[Float]): typings.std.ArrayLike[Double] =
    new scala.scalajs.js.typedarray.Float32Array(points.toJSIterable)
      .asInstanceOf[typings.std.ArrayLike[Double]] // FIXME ... TS

  @inline def arrayLike2Float(array: typings.std.ArrayLike[Double]): Array[Float] =
    array.asInstanceOf[scala.scalajs.js.typedarray.Float32Array].jsIterator.toIterator.toArray

  @inline def float2BufferAttribute(points: Seq[Float]): mod.BufferAttribute =
    new mod.BufferAttribute(float2ArrayLike(points), 3)

}
