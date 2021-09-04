package app.fmgp.geo.webapp

import com.raquo.laminar.api.L._

import app.fmgp.geo._
import app.fmgp.geo.prebuilt.Atomium
import app.fmgp.geo.prebuilt.GeometryExamples
import app.fmgp.geo.prebuild.OldSyntaxGeometryExamples

enum WorldExamplesOption(
    val www: app.fmgp.geo.World,
    val icon: (
      com.raquo.domtypes.generic.Modifier[
        com.raquo.laminar.nodes.ReactiveHtmlElement[org.scalajs.dom.html.Element]
      ]
    )
) {
  case Clean
      extends WorldExamplesOption(
        World.w3DEmpty,
        "delete"
      )
  case Atomium3D
      extends WorldExamplesOption(
        Atomium.atomiumWorld.asAddition,
        MyIcons.atomiumSVG
      )
  case ShapesDemo2D
      extends WorldExamplesOption(
        GeometryExamples.shapesDemo2D.asAddition,
        "token"
      )
  case ShapesDemo3D
      extends WorldExamplesOption(
        GeometryExamples.shapesDemo3D.asAddition,
        "token"
      )
  case Cross2D
      extends WorldExamplesOption(
        OldSyntaxGeometryExamples.world(_.cross),
        "token"
      )

  case Polygon2D
      extends WorldExamplesOption(
        OldSyntaxGeometryExamples.world(_.polygon(10, 5)), //FIXME
        "token"
      )
  case Rectangle2D
      extends WorldExamplesOption(
        OldSyntaxGeometryExamples.world(_.rectangle(XYZ(-1, -1, 0), XYZ(1, 1, 0))),
        "token"
      )
  case DoricColumn2d
      extends WorldExamplesOption(
        OldSyntaxGeometryExamples.world { e =>
          e.doricColumn2d(XYZ(0, 0), 9, 0.5, 0.4, 0.3, 0.3, 1.0)
          e.doricColumn2d(XYZ(3, 0), 7, 0.5, 0.4, 0.6, 0.6, 1.6)
          e.doricColumn2d(XYZ(6, 0), 9, 0.7, 0.5, 0.3, 0.2, 1.2)
          e.doricColumn2d(XYZ(9, 0), 8, 0.4, 0.3, 0.2, 0.3, 1.0)
          e.doricColumn2d(XYZ(12, 0), 5, 0.5, 0.4, 0.3, 0.1, 1.0)
          e.doricColumn2d(XYZ(15, 0), 6, 0.8, 0.3, 0.2, 0.4, 1.4)
        },
        "token"
      )

  case DoricColumn3d
      extends WorldExamplesOption(
        OldSyntaxGeometryExamples.world { e =>
          e.doricColumn3d(XYZ(0, 0), 9, 0.5, 0.4, 0.3, 0.3, 1.0)
          e.doricColumn3d(XYZ(3, 0), 7, 0.5, 0.4, 0.6, 0.6, 1.6)
          e.doricColumn3d(XYZ(6, 0), 9, 0.7, 0.5, 0.3, 0.2, 1.2)
          e.doricColumn3d(XYZ(9, 0), 8, 0.4, 0.3, 0.2, 0.3, 1.0)
          e.doricColumn3d(XYZ(12, 0), 5, 0.5, 0.4, 0.3, 0.1, 1.0)
          e.doricColumn3d(XYZ(15, 0), 6, 0.8, 0.3, 0.2, 0.4, 1.4)
        },
        "token"
      )

  case SpiralStairs
      extends WorldExamplesOption(
        OldSyntaxGeometryExamples.world { e =>
          import scala.math._
          e.spiralStairs(XYZ(0, 0, 0), 0.1, 3, Pi / 6, 1, 10)
        // e.spiralStairs(XYZ(0, 40, 0), 1.5, 5, Pi / 9)
        // e.spiralStairs(XYZ(0, 80, 0), 0.5, 6, Pi / 8)
        },
        "token"
      )

  case Tree2D
      extends WorldExamplesOption(
        OldSyntaxGeometryExamples.world { e =>
          import scala.math._
          e.addShape(e.tree2d(e.xyz(-10, 0), 5, Pi / 2, Pi / 8, 0.6, iterations = 7))
          e.addShape(e.tree2d(e.xyz(0, 0), 5, Pi / 2, Pi / 8, 0.8, iterations = 7))
          e.addShape(e.tree2d(e.xyz(10, 0), 5, Pi / 2, Pi / 6, 0.7, iterations = 7))

          implicit val random: scala.util.Random = new scala.util.Random
          random.setSeed(532443)
          e.addShape(e.tree2Random(e.xyz(-20, 0, -10), 5, Pi / 2, Pi / 16, Pi / 4, 0.6, 0.9))
          e.addShape(e.tree2Random(e.xyz(0, 0, -10), 5, Pi / 2, Pi / 16, Pi / 4, 0.6, 0.9))
          e.addShape(e.tree2Random(e.xyz(20, 0, -10), 5, Pi / 2, Pi / 16, Pi / 4, 0.6, 0.9))
        },
        "token"
      )
  case ArcTruss
      extends WorldExamplesOption(
        OldSyntaxGeometryExamples.world { e =>
          import scala.math._
          e.addShape(e.Truss.arcTruss(e.xyz(0, 0, 0), 10, 9, 0, -Pi / 2, Pi / 2, 1.0, 20))
          e.addShape(e.Truss.arcTruss(e.xyz(0, 5, 0), 8, 9, 0, -Pi / 3, Pi / 3, 2.0, 20))
          e.addShape(e.Truss.spaceTruss(e.Truss.horizontalTrussPositions(e.xyz(0, 0, 0), 1, 1, 9, 10)))
          e.addShape(e.Truss.trussPyramid(10, 10, 1, 1, 0.98))
        },
        "token"
      )
  case Heart3D
      extends WorldExamplesOption(
        OldSyntaxGeometryExamples.world { e =>
          //e.addShape(e.Heart.path(x = 0, y = 0))
          //e.addShape(e.Heart.planeShape(x = 0, y = 0))
          //e.addShape(e.Heart.planeShape(x = 0, y = 0, size = 0.5))
          //e.addShape(e.Heart.planeShape(holes = Seq(e.Heart.path(x = 0, y = 1, size = 0.5)), x = 0, y = 0, size = 1))
          e.addShape(e.Heart.extrude(holes = Seq(e.Heart.path(x = 0, y = 1, size = 0.5)), x = 0, y = 0, size = 1))
        },
        "token"
      )

  case TestShape3D
      extends WorldExamplesOption(
        OldSyntaxGeometryExamples.world { e =>
          import e._
          addShape(TestShape())
        },
        "token"
      )
  case TextShape3D
      extends WorldExamplesOption(
        OldSyntaxGeometryExamples.world { e => e.addShape(TextShape("Hello World!", 3)) },
        "token"
      )
  case SurfaceGridShape3D
      extends WorldExamplesOption(
        OldSyntaxGeometryExamples.world { e =>
          import scala.math._
          import e._
          //val a = damped_sin_roof_pts(u0(), 20, 3, 10, 15, Pi, 0.03, Pi / 50, Pi / 10, 60, 100, 120, 800, 1)
          val a = damped_sin_roof_pts(e.u0(), 20, 3, 10, 15, Pi, 0.03, Pi / 50, Pi / 10, 60, 100, 24, 100, 1)
          addShape(
            SurfaceGridShape(a.map(_.toArray).toArray)
              .transformWith(Matrix.rotate(Pi / 2, Vec(1, 0, 0)).postTranslate(0, -100, 0))
          )
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

  def setWorldObserver(w: World) = Observer[org.scalajs.dom.MouseEvent](onNext = ev => AppGlobal.setWorld(w))
}
