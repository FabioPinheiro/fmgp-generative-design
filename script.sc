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
wireframeMode = true

doricColumn3d(xyz(0, 0), 9, 0.5, 0.4, 0.3, 0.3, 1.0)
doricColumn3d(xyz(3, 0), 7, 0.5, 0.4, 0.6, 0.6, 1.6)
doricColumn3d(xyz(6, 0), 9, 0.7, 0.5, 0.3, 0.2, 1.2)
doricColumn3d(xyz(9, 0), 8, 0.4, 0.3, 0.2, 0.3, 1.0)
doricColumn3d(xyz(12, 0), 5, 0.5, 0.4, 0.3, 0.1, 1.0)
doricColumn3d(xyz(15, 0), 6, 0.8, 0.3, 0.2, 0.4, 1.4)
