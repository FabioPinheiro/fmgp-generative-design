package app.fmgp.geo
import scala.math._
import scala.util.Random

trait KhepriExamples extends Syntax {

  def cross = {
    line(Seq(xyz(-1, -1), xyz(-1, 0), xyz(1, 0), xyz(1, 1)))
    line(Seq(xyz(-1, 1), xyz(0, 1), xyz(0, -1), xyz(1, -1)))
  }

  def polygon(radius: Double = 1, vertex: Int = 5) = {
    assert(vertex >= 3)
    line((0 to vertex).map(i => pol(radius, 2 * Pi * i / vertex)))
  }

  def rectangle(p1: XYZ, p2: XYZ) = line(Seq(p1, xyz(p1.x, p2.y), p2, xyz(p2.x, p1.y)), closeLine = true)

  /**
    * Drawing Doric Columns
    * The drawing of a Doric column is divided into three parts:
    * shaft, echinus and abacus. Each of these parts has an independent function.
    *
    * @param p column's center base coordinate
    * @param hShaft shaft's height
    * @param rBaseShaft shaft's base radius
    * @param hEchinus echinus' height
    * @param rBaseEchinus echinus' base radius = shaft's top radius
    * @param hAbacus abacus' height
    * @param lAbacus abacus' length = 2*echinus top radius
    */
  def doricColumn2d(
      p: XYZ,
      hShaft: Double,
      rBaseShaft: Double,
      hEchinus: Double,
      rBaseEchinus: Double,
      hAbacus: Double,
      lAbacus: Double
  ) = {
    def shaft(p: XYZ, hShaft: Double, rBase: Double, rTop: Double) =
      line(
        Seq(
          p + xyz(-rTop, hShaft),
          p + xyz(-rBase, 0),
          p + xyz(+rBase, 0),
          p + xyz(+rTop, hShaft)
        ),
        closeLine = true
      )
    def echinus(p: XYZ, hEchinus: Double, rBase: Double, rTop: Double) =
      line(
        Seq(
          p + xyz(-rBase, 0),
          p + xyz(-rTop, hEchinus),
          p + xyz(+rTop, hEchinus),
          p + xyz(+rBase, 0)
        ),
        closeLine = true
      )
    def abacus(p: XYZ, hAbacus: Double, lAbacus: Double) =
      rectangle(p + xyz(-(lAbacus / 2), 0), p + xyz(lAbacus / 2, hAbacus))

    shaft(p, hShaft, rBaseShaft, rBaseEchinus)
    echinus(p + xyz(0, hShaft), hEchinus, rBaseEchinus, lAbacus / 2)
    abacus(p + xyz(0, hShaft + hEchinus), hAbacus, lAbacus)
  }

  def doricColumn3d(
      p: XYZ,
      hShaft: Double,
      rBaseShaft: Double,
      hEchinus: Double,
      rBaseEchinus: Double,
      hAbacus: Double,
      lAbacus: Double
  ) = {
    def shaft(p: XYZ, hShaft: Double, rBase: Double, rTop: Double) =
      coneFrustum(p, rBase, p + Vec(y = hShaft), rTop)
    def echinus(p: XYZ, hEchinus: Double, rBase: Double, rTop: Double) =
      coneFrustum(p, rBase, p + Vec(y = hEchinus), rTop)
    def abacus(p: XYZ, hAbacus: Double, lAbacus: Double) =
      box(p + Vec(-(lAbacus / 2), 0, -(lAbacus / 2)), p + Vec(lAbacus / 2, hAbacus, lAbacus / 2))

    shaft(p, hShaft, rBaseShaft, rBaseEchinus)
    echinus(p + xyz(0, hShaft), hEchinus, rBaseEchinus, lAbacus / 2)
    abacus(p + xyz(0, hShaft + hEchinus), hAbacus, lAbacus)
  }

  def crossOfCones(p: XYZ, rb: Double, rt: Double, l: Double) = {
    coneFrustum(p, rb, p + Vec(x = l), rt)
    coneFrustum(p, rb, p + Vec(y = l), rt)
    coneFrustum(p, rb, p + Vec(z = l), rt)
    coneFrustum(p, rb, p + Vec(x = -l), rt)
    coneFrustum(p, rb, p + Vec(y = -l), rt)
    coneFrustum(p, rb, p + Vec(z = -l), rt)
  }

  def spiralStairs(
      p: XYZ,
      radius: Double,
      height: Double,
      angle: Double,
      stairSize: Double = 1,
      stairs: Int = 10
  ): Unit = {
    assert(stairs >= 1)
    cone(p, radius * 2, p.+(y = height))
    for (i <- 1 to stairs - 1) {
      val hDelta = i * height / stairs
      val p1 = p.+(y = hDelta)
      val p2 = p + Cylindrical(stairSize, i * angle, hDelta).asVec
      cone(p1, radius, p2)
    }
  }

  // ### Tree ###
  def tree2d(
      base: XYZ,
      length: Double,
      angle: Double,
      deltaAngle: Double,
      reductionFactor: Double,
      iterations: Int = 6,
      leafRadius: Double = 0.1
  ): ShapeSeq = {
    val top = base + Polar(rho = length, phi = angle).asVec
    def branch(p0: XYZ, p1: XYZ): Shape = LinePath(Seq(p0, p1))
    def leaf(p: XYZ): Shape = Circle(leafRadius, top)
    ShapeSeq(branch(base, top)) ++ {
      if (iterations < 1) ShapeSeq(leaf(top))
      else {
        tree2d(
          top,
          length * reductionFactor,
          angle + deltaAngle,
          deltaAngle,
          reductionFactor,
          iterations = iterations - 1,
          leafRadius = leafRadius,
        ) ++ tree2d(
          top,
          length * reductionFactor,
          angle - deltaAngle,
          deltaAngle,
          reductionFactor,
          iterations = iterations - 1,
          leafRadius = leafRadius,
        )
      }
    }
  }

  def tree2Random(
      base: XYZ,
      length: Double,
      angle: Double,
      minDeltaAngle: Double,
      maxDeltaAngle: Double,
      minReductionFactor: Double,
      maxReductionFactor: Double,
      leafRadius: Double = 0.1
  )(implicit random: Random): ShapeSeq = {
    val top = base + Polar(rho = length, phi = angle).asVec
    def branch(p0: XYZ, p1: XYZ): Shape = LinePath(Seq(p0, p1))
    def leaf(p: XYZ): Shape = Circle(leafRadius, top)
    ShapeSeq(branch(base, top)) ++ {
      if (length < 0.5) ShapeSeq(leaf(top))
      else {
        tree2Random(
          top,
          length * random.between(minReductionFactor, maxReductionFactor),
          angle + random.between(minDeltaAngle, maxDeltaAngle),
          minDeltaAngle,
          maxDeltaAngle,
          minReductionFactor,
          maxReductionFactor,
          leafRadius
        ) ++ tree2Random(
          top,
          length * random.between(minReductionFactor, maxReductionFactor),
          angle - random.between(minDeltaAngle, maxDeltaAngle),
          minDeltaAngle,
          maxDeltaAngle,
          minReductionFactor,
          maxReductionFactor,
          leafRadius
        )
      }
    }
  }

  //def tree3d

  // ### Trusses ### 6.7.1 Modeling Trusses
  object Truss {
    private def trussNode(p: XYZ, trussNodeRadius: Double = 0.1): Shape = Sphere(trussNodeRadius, p)
    private def trussStrut(p0: XYZ, p1: XYZ, trussStrutRadius: Double = 0.03) =
      Cylinder.fromVerticesRadius(p0, p1, trussStrutRadius)
    def trussNodes(ps: Seq[XYZ]): ShapeSeq = ps.map(p => trussNode(p))
    def trussStruts(ps: Seq[XYZ], qs: Seq[XYZ]): ShapeSeq =
      ps.zip(qs).map { case (p, q) => trussStrut(p, q) } //truss_bar(p, q)

    def truss(as: Seq[XYZ], bs: Seq[XYZ], cs: Seq[XYZ]): ShapeSeq =
      trussNodes(as) ++ trussNodes(bs) ++ trussNodes(cs) ++
        trussStruts(as, cs) ++ trussStruts(as, bs) ++ trussStruts(bs, cs) ++
        trussStruts(bs, as.drop(1)) ++ trussStruts(bs, cs.drop(1)) ++
        trussStruts(as, as.drop(1)) ++ trussStruts(bs, bs.drop(2)) ++
        trussStruts(cs, cs.drop(1))

    def arcPositions(center: XYZ, radius: Double, phi: Double, psi0: Double, psi1: Double, dpsi: Double): Seq[XYZ] =
      if (psi0 > psi1) Seq.empty
      else {
        (center + Spherical(rho = radius, phi = phi, psi = psi0).asVec) +: arcPositions(
          center,
          radius,
          phi,
          psi0 + dpsi,
          psi1,
          dpsi
        )
        //[p+vsph(r, phi, psi0), arc_positions(p, r, phi, psi0+dpsi, psi1, dpsi)...]
      }

    def arcTruss(center: XYZ, rac: Double, rb: Double, phi: Double, psi0: Double, psi1: Double, l: Double, n: Int) = {
      val dpsi = (psi1 - psi0) / n
      truss(
        arcPositions(center + Polar(l / 2.0, phi + Pi / 2).asVec, rac, phi, psi0, psi1, dpsi),
        arcPositions(center, rb, phi, psi0 + dpsi / 2.0, psi1 - dpsi / 2.0, dpsi),
        arcPositions(center + Polar(l / 2.0, phi - Pi / 2).asVec, rac, phi, psi0, psi1, dpsi)
      )
    }

    def spaceTruss(ptss: Seq[Seq[XYZ]]): ShapeSeq = ptss match {
      case as +: bs +: cs +: next =>
        trussNodes(as) ++
          trussNodes(bs) ++
          trussStruts(as, cs) ++
          trussStruts(as, bs) ++
          trussStruts(bs, cs) ++
          trussStruts(bs, as.drop(1)) ++
          trussStruts(bs, cs.drop(1)) ++
          trussStruts(as, as.drop(1)) ++
          trussStruts(bs, bs.drop(1)) ++ {
          if (ptss.size == 3 /*no nodes left?*/ ) trussNodes(cs) ++ trussStruts(cs, cs.drop(1))
          else spaceTruss(ptss.drop(2)) ++ trussStruts(bs, ptss(3))
        }
    }

    def horizontalTrussPositions(p: XYZ, h: Double, l: Double, n: Int, m: Int): Seq[Seq[XYZ]] = {
      def linearPositions(p: XYZ, l: Double, n: Int): Seq[XYZ] = {
        if (n == 0) Seq.empty else p +: linearPositions(p + Vec(x = l), l, n - 1)
      }
      if (m == 0) Seq(linearPositions(p, l, n))
      else {
        Seq(
          linearPositions(p, l, n),
          linearPositions(p + Vec(l / 2, l / 2, h), l, n - 1),
        ) ++ horizontalTrussPositions(p + Vec(y = l), h, l, n, m - 1)
      }
    }

    def trussPyramid(
        hashtagX: Int,
        hashtagY: Int,
        length: Double = 1,
        height: Double = 1,
        heightFactor: Double = 1
    ): ShapeSeq = {
      val m: Seq[Seq[Seq[XYZ]]] = for (level <- 0 to scala.math.min(hashtagX, hashtagY) - 1) yield {
        (0 to hashtagX - 1 - level).map(x =>
          (0 to hashtagY - 1 - level).map(y =>
            XYZ(
              length * (x + level / 2.0),
              level * height * scala.math.pow(heightFactor, level),
              length * (y + level / 2.0),
            )
          )
        )
      }
      m.flatMap(_.flatMap(_.map(p => trussNode(p)))) ++ {
        (0 to m.length - 1).flatMap { w =>
          (0 to m(w).length - 1).flatMap { j =>
            (0 to m(w)(j).length - 1).flatMap { i =>
              (if (i > 0) Seq(trussStrut(m(w)(j)(i - 1), m(w)(j)(i))) else Seq.empty) ++
                (if (j > 0) Seq(trussStrut(m(w)(j - 1)(i), m(w)(j)(i))) else Seq.empty) ++ {
                if (w == 0) Seq.empty
                else
                  Seq(
                    trussStrut(m(w - 1)(j)(i), m(w)(j)(i)),
                    trussStrut(m(w - 1)(j)(i + 1), m(w)(j)(i)),
                    trussStrut(m(w - 1)(j + 1)(i), m(w)(j)(i)),
                    trussStrut(m(w - 1)(j + 1)(i + 1), m(w)(j)(i)),
                  )
              }
            }
          }
        }
      }
    }
  }

  //### 7.6 Extrusions
  object Heart {
    def path(x: Double = 0, y: Double = 0, size: Double = 1): MultiPath = {
      PathBuilder(XYZ(x, y).scale(size))
        .bezierCurveTo(Vec(x, y).scale(size), Vec(x - 1, y - 5).scale(size), XYZ(x - 5, y - 5).scale(size))
        .bezierCurveTo(Vec(x - 11, y - 5).scale(size), Vec(x - 11, y + 2).scale(size), XYZ(x - 11, y + 2).scale(size))
        .bezierCurveTo(Vec(x - 11, y + 6).scale(size), Vec(x - 8, y + 10.4).scale(size), XYZ(x, y + 14).scale(size))
        .bezierCurveTo(Vec(x + 7, y + 10.4).scale(size), Vec(x + 11, y + 6).scale(size), XYZ(x + 11, y + 2).scale(size))
        .bezierCurveTo(Vec(x + 11, y + 2).scale(size), Vec(x + 11, y - 5).scale(size), XYZ(x + 5, y - 5).scale(size))
        .bezierCurveTo(Vec(x + 2, y - 5).scale(size), Vec(x, y).scale(size), XYZ(x, y).scale(size))
        .build
    }

    def planeShape(
        holes: Seq[MultiPath] = Seq.empty,
        x: Double = 0,
        y: Double = 0,
        size: Double = 1
    ) = PlaneShape(path(x = x, y = y, size = size), holes = holes)

    val extrudePath: MultiPath =
      PathBuilder(XYZ(0, 0, 0)).bezierCurveTo(Vec(0, 0, -5), Vec(0, 0, -10), XYZ(0, 20, -20)).build

    def extrude(
        holes: Seq[MultiPath] = Seq.empty,
        extrudePath: MultiPath = extrudePath,
        x: Double = 0,
        y: Double = 0,
        size: Double = 1
    ) = Extrude(
      path = path(x = x, y = y, size = size),
      holes = holes,
      options = Some(Extrude.Options(extrudePath = Some(extrudePath), steps = Some(50)))
    )
  }

  //def sinusoidalWall ### 7.6.2 Extrusion Along a Path

  //7.7 Gaudí’s Columns
  // ### 8 Transformations ###
  // 8.2 Translation - move(sphere(), vxyz(1, 2, 3))
  // 8.3 Scale - scale(papal_cross(), 3)
  // 8.4 Rotation - rotate(papal_cross(), pi/4)
  // 8.5 Reflection - mirror(cone_frustum(p, rb, p+vz(h/2), rn), p+vz(h/2)) = def hourglass(p, rb, rn, h)
  // 12 Coordinate Space

  object ScalaStairs {
    val path = PathBuilder(XYZ(0, 0)).lineTo(XYZ(3, 0)).lineTo(XYZ(3, 0.01)).lineTo(XYZ(0, 0.01)).build
    //addShape(path)

    val extrudePath = PathBuilder(XYZ(0, 0, 0))
      .lineTo(XYZ(0, 1, 0))
      .lineTo(XYZ(0, 1, -1.2))
      .lineTo(XYZ(0, 2, -1))
      .lineTo(XYZ(0, 2, -2.2))
      .build
    //addShape(extrudePath)
  }
}
