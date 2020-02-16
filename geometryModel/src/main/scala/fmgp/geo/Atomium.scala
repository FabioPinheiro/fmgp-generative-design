package fmgp.geo

import fmgp.geo._

object Atomium {

  def atomiumSpheres(cs: Seq[XYZ], r: Double) =
    cs.map(c => Sphere(r, c))

  def atomiumTube(p1: XYZ, p2: XYZ, r: Double) =
    Cylinder.byVerticesRadius(p1, p2, r)

  def atomiumTubes(c0: XYZ, upCs: Seq[XYZ], downCs: Seq[XYZ], r: Double) = {
    (upCs ++ downCs).map((c0, _)) ++
      upCs.zip(downCs) ++
      upCs.zip(upCs.drop(1) :+ upCs.head) ++
      downCs.zip(downCs.drop(1) :+ downCs.head)
  }.map { case (p1, p2) => atomiumTube(p1, p2, r) }

  def atomiumFrame(sphereR: Double, frameW: Double, tubeR: Double) = {
    val c0 = XYZ.origin
    val c1 = XYZ(-frameW, -frameW, +frameW)
    val c2 = XYZ(+frameW, -frameW, +frameW)
    val c3 = XYZ(+frameW, +frameW, +frameW)
    val c4 = XYZ(-frameW, +frameW, +frameW)
    val c5 = XYZ(-frameW, -frameW, -frameW)
    val c6 = XYZ(+frameW, -frameW, -frameW)
    val c7 = XYZ(+frameW, +frameW, -frameW)
    val c8 = XYZ(-frameW, +frameW, -frameW)

    val s = atomiumSpheres(Seq(c0, c1, c2, c3, c4, c5, c6, c7, c8), sphereR)
    val t = atomiumTubes(c0, Seq(c1, c2, c3, c4), Seq(c5, c6, c7, c8), tubeR)
    Seq(s, t)
  }

  def atomium(sphereR: Double, frameW: Double, tubeR: Double): Seq[Shape] =
    atomiumFrame(sphereR, frameW, tubeR).flatten

}
