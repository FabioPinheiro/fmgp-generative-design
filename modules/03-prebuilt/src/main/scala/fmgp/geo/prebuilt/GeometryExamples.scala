package fmgp.geo.prebuilt

import fmgp.geo._

object GeometryExamples {

  def shapesDemo3D: WorldState = World.w3D(
    Seq(
      Sphere(0.3, XYZ(3, 3, -1)),
      Sphere(0.2, XYZ(2, 2, -1)),
      Sphere(0.1, XYZ(1, 1, -1)),
      //Cylinder(0.2, 2),
      TransformationShape(
        Cylinder(0.2, 2),
        TransformMatrix(Matrix.translate(-1, 0, 1))
      ),
      TransformationShape(
        Cylinder(0.2, 2),
        TransformMatrix(
          Matrix.translate(-1, 0, 1).postRotate(Math.PI / 2, Vec(0, 0, 1))
        )
      ),
      Cylinder.fromVerticesRadius(XYZ(1, 1, 1), XYZ(3, 3, 1), 0.1)
    )
  )

  def shapesDemo2D: WorldState = World.w2D(
    Seq(
      LinePath(
        Seq(XYZ(-8, 0), XYZ(0, 8), XYZ(8, 0), XYZ(0, 4), XYZ(-8, 0))
      ),
      TransformationShape(
        Circle(3),
        TransformMatrix(Matrix.translate(0, -3, 0))
      ),
      TransformationShape(
        Circle(1, XYZ(2, 2)),
        TransformMatrix(Matrix.translate(0, -3, 0))
      )
    )
  )

}
