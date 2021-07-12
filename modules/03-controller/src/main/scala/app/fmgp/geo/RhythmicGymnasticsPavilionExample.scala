package app.fmgp.geo

import scala.math._

/** @see
  *   [[http://web.ist.utl.pt/renata.castelo.branco/RCB/programming/gymnastics/GymPav_Pluto.jl.html]]
  */
trait RhythmicGymnasticsPavilionUtils extends Syntax {

  /** sinusoidal funtion
    * @param a
    *   is the amplitude
    * @param omega
    *   is the frequency
    * @param fi
    *   is the phase
    */
  def sinusoidal(a: Double, omega: Double, fi: Double, x: Double) =
    a * sin(omega * x + fi)

  def sin_array_y(p: XYZ, a: Double, omega: Double, fi: Double, dist: Double, n: Int) =
    division(0, dist, n).map(i => p + vxyz(i, sinusoidal(a, omega, fi, i), 0))

  /** damped_sin_wave
    * @param a
    *   is the initial amplitude (the highest peak)
    * @param d
    *   is the decay constant
    * @param omega
    *   is the angular frequency
    */
  def damped_sin_wave(a: Double, d: Double, omega: Double, x: Double) = a * exp(-(d * x)) * sin(omega * x)

  def damped_sin_array_z(p: XYZ, a: Double, d: Double, omega: Double, dist: Double, n: Int) =
    division(0, dist, n).map(i => p + vxyz(i, 0, damped_sin_wave(a, d, omega, i)))

  def damped_sin_roof_pts(
      p: XYZ,
      h: Double,
      a_x: Double,
      a_y_min: Double,
      a_y_max: Double,
      fi: Double,
      decay: Double,
      om_x: Double,
      om_y: Double,
      dist_x: Double,
      dist_y: Double,
      n_x: Int,
      n_y: Int,
      d_i: Double, // d_i is the distance between the pavilion starting point and the beginning of the dumped sine curve
  ) = {
    def f(x: Double, y: Double) =
      if (y <= d_i)
        p + vxyz(
          x,
          -sin(y / d_i * (1 * Pi)),
          y * h / d_i + sin(x / dist_x * Pi) * sinusoidal(a_x, om_x, fi - y * Pi / dist_y, x) * (y * a_x / d_i)
        )
      else
        p + vxyz(
          x,
          y,
          h + sin(x / dist_x * Pi) * sinusoidal(a_x, om_x, fi - y * Pi / dist_y, x) + damped_sin_wave(
            a_y_max - (a_y_max - a_y_min) / dist_x * x,
            decay,
            om_y,
            y
          )
        )
    map_division(f, 0, dist_x, n_x, 0, dist_y, n_y, true)
  }
}

trait RhythmicGymnasticsPavilionExample extends Syntax with RhythmicGymnasticsPavilionUtils {

  //surface_grid(damped_sin_roof_pts(u0(), 20, 3, 10, 15, pi, 0.03, pi/50, pi/10, 60, 100, 120, 800))

}
