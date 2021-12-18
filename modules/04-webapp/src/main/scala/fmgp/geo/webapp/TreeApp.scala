package fmgp.geo.webapp

import org.scalajs.dom
import com.raquo.laminar.api.L._
import typings.std.stdStrings.text

import io.circe._, io.circe.syntax._
import fmgp.geo.*
import fmgp.geo.EncoderDecoder.given_Encoder_Shape
import fmgp.geo.EncoderDecoder.given_Encoder_World
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom.HTMLElement

import typings.mermaid
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("MermaidApp")
object MermaidApp {

  @JSExport
  def hack = mermaid.mod.default

  def apply(): HtmlElement = //rootElement
    div(child <-- AppGlobal.worldVar.signal.map(e => getHtml(e.shapes)))

  AppGlobal.worldVar.signal.map(_ => update)

  @JSExport
  def update = {
    println("MermaidApp Update!!")
    //val config = mermaid.mermaidAPIMod.mermaidAPI.Config().setStartOnLoad(false)
    //mermaid.mod.default.initialize(config)
    mermaid.mod.default.init("div.mermaid")
  }

  def getHtml(shape: Shape, indent: Int = 0): ReactiveHtmlElement[HTMLElement] =
    div(className("mermaid"), shapeToMermaid(shape), onMountCallback(ctx => { update }))

  def shapeToMermaid(shape: Shape): String = {
    var countId = 0
    def id = { countId = countId + 1; countId }
    def fAux(shape: Shape, parent: String): String = {
      shape match {
        case s: TransformationShape =>
          val name = s"TransformationShape-$id"
          s"$parent --> $name" + "\n" + fAux(s.shape, name) + "\n"
        case s: Wireframe =>
          val name = s"Wireframe-$id"
          s"$parent --> name" + "\n" + fAux(s.shape, name) + "\n"
        case s: ShapeSeq =>
          val name = s"ShapeSeq-$id "
          s"$parent --> $name" + "\n" + s.shapes.map(e => fAux(e, name)).mkString("\n")
        case s: Points           => s"$parent --> Points-$id"
        case s: Box              => s"$parent --> Box-$id"
        case s: Sphere           => s"$parent --> Sphere-$id"
        case s: Cylinder         => s"$parent --> Cylinder-$id"
        case s: Torus            => s"$parent --> Torus-$id"
        case s: Extrude          => s"$parent --> Extrude-$id"
        case s: PlaneShape       => s"$parent --> PlaneShape-$id"
        case s: SurfaceGridShape => s"$parent --> SurfaceGridShape-$id"
        case s: LinePath         => s"$parent --> LinePath-$id"
        case s: MultiPath        => s"$parent --> MultiPath-$id"
        case s: CubicBezierPath  => s"$parent --> CubicBezierPath-$id"
        case s: Circle           => s"$parent --> Circle-$id"
        case s: TriangleShape    => s"$parent --> TriangleShape-$id"
        case s: Arrow            => s"$parent --> Arrow-$id"
        case s: Axes             => s"$parent --> Axes-$id"
        case s: TestShape        => s"$parent --> TestShape-$id"
        case s: TextShape        => s"$parent --> TextShape-$id"
      }
    }
    "graph TD" + "\n" + fAux(shape, "ROOT")
  }
}
