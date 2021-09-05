package app.fmgp.syntax

/** Khepri's defined functions
  * @see
  *   [[https://github.com/aptmcl/Khepri.jl/blob/master/src/Utils.jl]]
  */
trait KhepriSyntax {
  def division(t0: Double, t1: Double, n: Int, include_last: Boolean = true): Seq[Double] =
    val step = (t1 - t0) / n
    (if (include_last)(0.to(n)) else (0.until(n))).map(e => t0 + step * e)

  def map_division[T](f: (Double) => T, t0: Double, t1: Double, n: Int, include_last: Boolean = true): Seq[T] =
    division(t0, t1, n, include_last).map(f)
  def map_division[T](
      f: (Double, Double) => T,
      u0: Double,
      u1: Double,
      nu: Int,
      v0: Double,
      v1: Double,
      nv: Int,
      include_last: Boolean
  ): Seq[Seq[T]] =
    division(u0, u1, nu, include_last)
      .map(u => division(v0, v1, nv, include_last).map(v => f(u, v)))
}
