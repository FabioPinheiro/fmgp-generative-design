package app.fmgp.geo

import scala.scalajs.js
import scala.scalajs.js.annotation._
import fmgp.geo._

@JSExportTopLevel("GeometryExamples")
object GeometryExamples {

  @JSExport
  def atomium: Seq[Shape] = Atomium.atomium(0.8, 3.0, 0.3)

  @JSExport
  def atomiumWorld: WorldState = World.w3D(atomium)

  @JSExport
  def shapesDemo: WorldState = World.w3D(
    Seq(
      Sphere(0.3, XYZ(3, 3 + 8, -1)),
      Sphere(0.2, XYZ(2, 2 + 8, -1)),
      Sphere(0.1, XYZ(1, 1 + 8, -1)),
      //Cylinder(0.2, 2),
      TransformationShape(
        Cylinder(0.2, 2),
        TransformMatrix(Matrix.translate(-1, +8, 1))
      ),
      TransformationShape(
        Cylinder(0.2, 2),
        TransformMatrix(
          Matrix.translate(-1, +8, 1).postRotate(Math.PI / 2, Vec(0, 0, 1))
        )
      ),
      Cylinder.byVerticesRadius(XYZ(1, 1 + 8, 1), XYZ(3, 3 + 8, 1), 0.1)
    )
  )

  @JSExport
  def shapesDemo2D: WorldState = World.w2D(
    Seq(
      Line(Seq(XYZ(-8, 0), XYZ(0, 8), XYZ(8, 0), XYZ(0, 4), XYZ(-8, 0))),
      TransformationShape(
        Circle(3),
        TransformMatrix(Matrix.translate(0, -10, 0))
      ),
      TransformationShape(
        Circle(1, XYZ(2, 2)),
        TransformMatrix(Matrix.translate(0, -10, 0))
      )
    )
  )

}
