package app.fmgp.syntax

import app.fmgp.geo.{XYZ, Vec, Polar, Cylindrical, Spherical}

trait CoordinatesDsl {
  def u0() = XYZ.origin
  def xyz(x: Double = 0, y: Double = 0, z: Double = 0): XYZ = XYZ(x, y, z)
  def vxyz(x: Double = 0, y: Double = 0, z: Double = 0): Vec = Vec(x, y, z)
  def pol = polar _
  def polar(module: Double, argument: Double): XYZ = Polar(rho = argument, phi = module).toXYZ
  def cyl = cylindrical _
  def cylindrical(rho: Double, phi: Double, y: Double) = Cylindrical(rho = rho, phi = phi, y = y)
  def sph = spherical _
  def spherical(rho: Double, phi: Double, psi: Double) = Spherical(rho = rho, phi = phi, psi = psi)
}
