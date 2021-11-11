package app.fmgp.geo.webapp

import com.raquo.laminar.api.L._

import app.fmgp.geo._
import app.fmgp.geo.prebuilt.{
  Atomium,
  GeometryExamples,
  OldSyntaxGeometryExamples,
  RhythmicGymnasticsPavilionExample,
  GeoZioExample,
  TreesExample,
}
import scala.concurrent.Future

val workWorld: Future[World] = {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
  app.fmgp.dsl.defaultRuntime.unsafeRunToFuture(GeoZioExample.program).future.map(s => World.addition(s))
}

val treesWorld: Future[World] = {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
  app.fmgp.dsl.defaultRuntime.unsafeRunToFuture(TreesExample.program).future.map(s => World.addition(s))
}

enum WorldExamplesOption(
    val www: Future[World],
    val icon: (
      com.raquo.domtypes.generic.Modifier[
        com.raquo.laminar.nodes.ReactiveHtmlElement[org.scalajs.dom.html.Element]
      ]
    )
) {
  case Clean extends WorldExamplesOption(Future.successful(World.w3DEmpty), "delete")

  case Default extends WorldExamplesOption(workWorld, "work")
  case Atomium3D extends WorldExamplesOption(Future.successful(Atomium.atomiumWorld.asAddition), MyIcons.atomiumSVG)
  case ShapesDemo2D extends WorldExamplesOption(Future.successful(GeometryExamples.shapesDemo2D.asAddition), "token")
  case ShapesDemo3D extends WorldExamplesOption(Future.successful(GeometryExamples.shapesDemo3D.asAddition), "token")
  case Cross2D extends WorldExamplesOption(Future.successful(OldSyntaxGeometryExamples.world(_.cross)), "token")

  case Polygon2D
      extends WorldExamplesOption(Future.successful(OldSyntaxGeometryExamples.world(_.polygon(10, 5))), "token") //FIXME
  case Rectangle2D
      extends WorldExamplesOption(
        Future.successful(OldSyntaxGeometryExamples.world(_.rectangle(XYZ(-1, -1, 0), XYZ(1, 1, 0)))),
        "token"
      )
  case DoricColumn2d
      extends WorldExamplesOption(
        Future.successful(OldSyntaxGeometryExamples.world { e =>
          e.doricColumn2d(XYZ(0, 0), 9, 0.5, 0.4, 0.3, 0.3, 1.0)
          e.doricColumn2d(XYZ(3, 0), 7, 0.5, 0.4, 0.6, 0.6, 1.6)
          e.doricColumn2d(XYZ(6, 0), 9, 0.7, 0.5, 0.3, 0.2, 1.2)
          e.doricColumn2d(XYZ(9, 0), 8, 0.4, 0.3, 0.2, 0.3, 1.0)
          e.doricColumn2d(XYZ(12, 0), 5, 0.5, 0.4, 0.3, 0.1, 1.0)
          e.doricColumn2d(XYZ(15, 0), 6, 0.8, 0.3, 0.2, 0.4, 1.4)
        }),
        "token"
      )

  case DoricColumn3d
      extends WorldExamplesOption(
        Future.successful(OldSyntaxGeometryExamples.world { e =>
          e.doricColumn3d(XYZ(0, 0), 9, 0.5, 0.4, 0.3, 0.3, 1.0)
          e.doricColumn3d(XYZ(3, 0), 7, 0.5, 0.4, 0.6, 0.6, 1.6)
          e.doricColumn3d(XYZ(6, 0), 9, 0.7, 0.5, 0.3, 0.2, 1.2)
          e.doricColumn3d(XYZ(9, 0), 8, 0.4, 0.3, 0.2, 0.3, 1.0)
          e.doricColumn3d(XYZ(12, 0), 5, 0.5, 0.4, 0.3, 0.1, 1.0)
          e.doricColumn3d(XYZ(15, 0), 6, 0.8, 0.3, 0.2, 0.4, 1.4)
        }),
        "token"
      )

  case SpiralStairs
      extends WorldExamplesOption(
        Future.successful(OldSyntaxGeometryExamples.world { e =>
          import scala.math._
          e.spiralStairs(XYZ(0, 0, 0), 0.1, 3, Pi / 6, 1, 10)
        // e.spiralStairs(XYZ(0, 40, 0), 1.5, 5, Pi / 9)
        // e.spiralStairs(XYZ(0, 80, 0), 0.5, 6, Pi / 8)
        }),
        "token"
      )

  case Tree2D extends WorldExamplesOption(treesWorld, "park")
  case ArcTruss
      extends WorldExamplesOption(
        Future.successful(OldSyntaxGeometryExamples.world { e =>
          import scala.math._
          e.addShape(e.Truss.arcTruss(e.xyz(0, 0, 0), 10, 9, 0, -Pi / 2, Pi / 2, 1.0, 20))
          e.addShape(e.Truss.arcTruss(e.xyz(0, 5, 0), 8, 9, 0, -Pi / 3, Pi / 3, 2.0, 20))
          e.addShape(e.Truss.spaceTruss(e.Truss.horizontalTrussPositions(e.xyz(0, 0, 0), 1, 1, 9, 10)))
          e.addShape(e.Truss.trussPyramid(10, 10, 1, 1, 0.98))
        }),
        "token"
      )
  case Heart3D
      extends WorldExamplesOption(
        Future.successful(OldSyntaxGeometryExamples.world { e =>
          //e.addShape(e.Heart.path(x = 0, y = 0))
          //e.addShape(e.Heart.planeShape(x = 0, y = 0))
          //e.addShape(e.Heart.planeShape(x = 0, y = 0, size = 0.5))
          //e.addShape(e.Heart.planeShape(holes = Seq(e.Heart.path(x = 0, y = 1, size = 0.5)), x = 0, y = 0, size = 1))
          e.addShape(e.Heart.extrude(holes = Seq(e.Heart.path(x = 0, y = 1, size = 0.5)), x = 0, y = 0, size = 1))
        }),
        "favorite_border"
      )

  case TestShape3D
      extends WorldExamplesOption(
        Future.successful(OldSyntaxGeometryExamples.world { e =>
          import e._
          addShape(TestShape())
        }),
        "grade"
      )
  case TextShape3D
      extends WorldExamplesOption(
        Future.successful(OldSyntaxGeometryExamples.world { e => e.addShape(TextShape("Hello World!", 3)) }),
        "title"
      )
  case SurfaceGridShape3D
      extends WorldExamplesOption(
        Future.successful {
          val roofProgram = RhythmicGymnasticsPavilionExample.roof.map {
            import scala.math._
            _.transformWith(Matrix.rotate(Pi / 2, Vec(1, 0, 0)).postTranslate(0, -100, 0))
          }
          World.addition(app.fmgp.dsl.defaultRuntime.unsafeRun(roofProgram))
        },
        "token"
      )

  def makeLi =
    li(
      className("mdc-list-item"),
      role("menuitem"),
      span(className("mdc-list-item__ripple")),
      i(className("material-icons mdc-list-item__graphic"), icon), //FIXME icon make a make a clone of icon
      // span(
      //   className("mdc-list-item__graphic mdc-menu__selection-group-icon"),
      //   i(aria.label("Atomium"), atomiumSVG)
      // ),
      span(className("mdc-list-item__text"), this.toString),
      onClick --> setWorldObserver(www),
    )

  def setWorldObserver(world: Future[World]) = {
    Observer[org.scalajs.dom.MouseEvent](onNext = ev => world.value.map(_.get).map(AppGlobal.setWorld)) //FIXME .get
  }
}
