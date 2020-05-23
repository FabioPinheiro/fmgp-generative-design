/** Run this scipt.sc
  * sbt> browserRemoteControl/console
  * :load script.sc
  * app.fmgp.Main.start()
  * val iii = app.fmgp.Main.server.get; import iii.GeoSyntax._
  */
clear

// sphere(1, xyz())
// sphere(0.1, xyz(1))
// sphere(0.1, xyz(1.2))
// sphere(0.1, xyz(1.4))
// sphere(0.1, xyz(1.6))
// sphere(0.1, xyz(1.8))
// sphere(0.1, xyz(2))
// sphere(0.1, xyz(2, 0.2))
// sphere(0.1, xyz(2, 0.4))
// sphere(0.1, xyz(2, 0.6))
// sphere(0.1, xyz(2, 0.8))
// sphere(0.1, xyz(2, 1.0))
// sphere(0.1, xyz(2, 1.0, 0.2))
// sphere(0.1, xyz(2, 1.0, 0.4))
// sphere(0.1, xyz(2, 1.0, 0.6))
// sphere(0.1, xyz(2, 1.0, 0.8))
// sphere(0.1, xyz(2, 1.0, 1.0))

// // cross
// polygon(vertex = 3)
// polygon(vertex = 5)
// polygon(vertex = 10)

// // # 3.7 Parametrization of Geometric Figures
// doricColumn2d(xyz(0, 0), 9, 0.5, 0.4, 0.3, 0.3, 1.0)
// doricColumn2d(xyz(3, 0), 7, 0.5, 0.4, 0.6, 0.6, 1.6)
// doricColumn2d(xyz(6, 0), 9, 0.7, 0.5, 0.3, 0.2, 1.2)
// doricColumn2d(xyz(9, 0), 8, 0.4, 0.3, 0.2, 0.3, 1.0)
// doricColumn2d(xyz(12, 0), 5, 0.5, 0.4, 0.3, 0.1, 1.0)
// doricColumn2d(xyz(15, 0), 6, 0.8, 0.3, 0.2, 0.4, 1.4)

// // # Primitive solids in Khepri. (3.10.1 Predefined Solids)

// box(xyz(2, 1, 1), xyz(3, 4, 5))
// cone(xyz(6, 0, 0), 1, xyz(8, 1, 5))
// coneFrustum(xyz(11, 1, 0), 1, xyz(10, 0, 5), 0.2)
// sphere(xyz(8, 4, 5), 2)
// cylinder(xyz(8, 7, 0), 1, xyz(6, 8, 7))
// regularPyramid(5, xyz(-2, 1, 0), 1, 0, xyz(2, 7, 7))
// torus(xyz(14, 6, 5), 2, 1)

// crossOfCones(XYZ(1, 2, 3), 0.2, 1, 2)
//wireframeMode = true

// doricColumn3d(xyz(0, 0), 9, 0.5, 0.4, 0.3, 0.3, 1.0)
// doricColumn3d(xyz(3, 0), 7, 0.5, 0.4, 0.6, 0.6, 1.6)
// doricColumn3d(xyz(6, 0), 9, 0.7, 0.5, 0.3, 0.2, 1.2)
// doricColumn3d(xyz(9, 0), 8, 0.4, 0.3, 0.2, 0.3, 1.0)
// doricColumn3d(xyz(12, 0), 5, 0.5, 0.4, 0.3, 0.1, 1.0)
// doricColumn3d(xyz(15, 0), 6, 0.8, 0.3, 0.2, 0.4, 1.4)

//spiralStairs(xyz(0, 0, 0), 0.1, 3, Pi / 6, 1, 10)
//spiralStairs(xyz(0, 40, 0), 1.5, 5, Pi / 9)
//spiralStairs(xyz(0, 80, 0), 0.5, 6, Pi / 8)

//tree2d(xyz(0, 0), 20, Pi / 2, Pi / 8, 0.7, iterations = 7)

//addShape(tree2d(xyz(-10, 0), 5, Pi / 2, Pi / 8, 0.6, iterations = 7))
//addShape(tree2d(xyz(0, 0), 5, Pi / 2, Pi / 8, 0.8, iterations = 7))
//addShape(tree2d(xyz(10, 0), 5, Pi / 2, Pi / 6, 0.7, iterations = 7))

//implicit val random: scala.util.Random = new scala.util.Random
//random.setSeed(532443)
// addShape(tree2Random(xyz(-20, 0), 5, Pi / 2, Pi / 16, Pi / 4, 0.6, 0.9))
// addShape(tree2Random(xyz(0, 0), 5, Pi / 2, Pi / 16, Pi / 4, 0.6, 0.9))
// addShape(tree2Random(xyz(20, 0), 5, Pi / 2, Pi / 16, Pi / 4, 0.6, 0.9))

// addShape(
//   truss(
//     Seq(
//       xyz(0, -1, 0),
//       xyz(1, -1.1, 0),
//       xyz(2, -1.4, 0),
//       xyz(3, -1.6, 0),
//       xyz(4, -1.5, 0),
//       xyz(5, -1.3, 0),
//       xyz(6, -1.1, 0),
//       xyz(7, -1, 0)
//     ),
//     Seq(
//       xyz(0.5, 0, 0.5),
//       xyz(1.5, 0, 1),
//       xyz(2.5, 0, 1.5),
//       xyz(3.5, 0, 2),
//       xyz(4.5, 0, 1.5),
//       xyz(5.5, 0, 1.1),
//       xyz(6.5, 0, 0.8)
//     ),
//     Seq(
//       xyz(0, 1, 0),
//       xyz(1, 1.1, 0),
//       xyz(2, 1.4, 0),
//       xyz(3, 1.6, 0),
//       xyz(4, 1.5, 0),
//       xyz(5, 1.3, 0),
//       xyz(6, 1.1, 0),
//       xyz(7, 1, 0)
//     )
//   )
// )

//addShape(arcTruss(xyz(0, 0, 0), 10, 9, 0, -Pi / 2, Pi / 2, 1.0, 20))
//addShape(arcTruss(xyz(0, 5, 0), 8, 9, 0, -Pi / 3, Pi / 3, 2.0, 20))
//addShape(spaceTruss(horizontalTrussPositions(xyz(0, 0, 0), 1, 1, 9, 10)))
//addShape(Truss.trussPyramid(10, 10, 1, 1, 0.98))

//addShape(Heart.path(x = 0, y = 0))
//addShape(Heart.planeShape(x = 0, y = 0))
//addShape(Heart.planeShape(x = 0, y = 0, size = 0.5))
//addShape(Heart.planeShape(holes = Seq(Heart.path(x = 0, y = 1, size = 0.5)), x = 0, y = 0, size = 1))
//addShape(Heart.extrude(holes = Seq(Heart.path(x = 0, y = 1, size = 0.5)), x = 0, y = 0, size = 1))
// addShape(extrudePath)

// val path = PathBuilder(XYZ(0, 0)).lineTo(XYZ(0, 3)).lineTo(XYZ(0.01, 3)).lineTo(XYZ(0.01, 0)).build
// addShape(path)
// val extrudePath = {
//   PathBuilder(XYZ(0, 0, 0))
//     .lineTo(XYZ(0, 1, 0))
//     .lineTo(XYZ(0, 1, -1.2))
//     .lineTo(XYZ(0, 2, -1))
//     .lineTo(XYZ(0, 2, -2.2))
//     .lineTo(XYZ(0, 3, -2))
//     .lineTo(XYZ(0, 3, -3.2))
//     .build
// }

// addShape(extrudePath)

// addShape(
//   Extrude(path = path, options = Some(Extrude.Options(extrudePath = Some(extrudePath), steps = Some(100))))
// )

{
  val m00 = Axes(m0.m.postTranslate(Vec(10, 5, -10)))
  val aaa = m0.m.postTranslate(Vec(4, 0, 0)).postRotate(1, Vec(1, 0, 0)).postRotate(1, Vec(0, 1, 0))
  val bbb = m00.m.postTranslate(Vec(-4, 0, 0)).postRotate(1, Vec(1, 0, 0)).postRotate(1, Vec(0, 1, 0))
  addShape(Axes(aaa))
  addShape(Axes(bbb))
  draw(aux.map(t => BezierCurves.cubic(t, m0.m, aaa, bbb, m00.m)))

  def draw(mmm: Seq[Matrix]) = {
    addShape(Axes(mmm.head))
    addShape(Axes(mmm.last))
    addShape(ShapeSeq(mmm /*.drop(1).dropRight(1)*/.map(m => Axes(m))))
    val ppp = mmm.map(m => (m.dot(Vec(0, 1, 0)).asVec, Seq(m.dot(Vec(0, 0, -1)), m.dot(Vec(0, 0, 1)))))
    //addShape(Points(ppp.map(_._2).flatten))
    //addShape(Points(ppp.map(_._1.toXYZ)))
    addShape(ShapeSeq(ppp.zip(ppp.drop(1)).flatMap {
      case (a, b) =>
        val t1 = TriangleShape(Triangle(a._2(0), a._2(1), b._2(1)), Triangle(a._1, a._1, b._1))
        val t2 = TriangleShape(Triangle(a._2(0), b._2(1), b._2(0)), Triangle(a._1, b._1, b._1))
        Seq(t1, t2)
    }))
  }
  //addShape(LinePath(mmm.map(_.center)))
  //addShape(ShapeSeq(mmm /*.drop(1).dropRight(1)*/.map(m => Axes(m))))

}

//addShape(TestShape())
