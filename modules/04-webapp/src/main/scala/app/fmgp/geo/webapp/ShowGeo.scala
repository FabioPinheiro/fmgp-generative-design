package app.fmgp.geo.webapp

import org.scalajs.dom
import com.raquo.laminar.api.L._
import typings.std.stdStrings.text

import io.circe._, io.circe.syntax._
import app.fmgp.geo.*
import app.fmgp.geo.EncoderDecoder.given_Encoder_Shape
import app.fmgp.geo.EncoderDecoder.given_Encoder_World
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom.raw.HTMLElement

object ShowGeo {

  def apply(b: Boolean): HtmlElement = div(
    child <-- AppGlobal.worldVar.signal.map(e => if (b) e.json else e.html)
  )

  extension (w: World)
    def json = textArea(
      width("100%"),
      height("80vh"),
      //child.text <--
      w.asJson.spaces2
    )
    def html: ReactiveHtmlElement[HTMLElement] = div(w match {
      case WorldAddition(shapes: ShapeSeq) =>
        label("WorldAddition")
      case WorldState(shapes: ShapeSeq, dimensions: Dimensions) =>
        label(s"WorldState($dimensions)")
        getHtml(shapes)
    })

  def getHtml(shape: Shape, indent: Int = 0): ReactiveHtmlElement[HTMLElement] =
    shape match {
      //case b: Box => label(b.toString)
      //case todo   => label(s"TODO-$todo")
      case s: TransformationShape =>
        div(
          label("TransformationShape:"),
          div(
            paddingLeft("10px"),
            label(s.transformation.toString),
            div(getHtml(s.shape))
          )
        )
      case s: Wireframe => label(s"TODO-Wireframe $s")
      case s: ShapeSeq =>
        div(
          label("ShapeSeq:"),
          div(paddingLeft("10px"), s.shapes.map(e => Seq(br(), getHtml(e))).flatten)
        )
      case s: Points           => label(s.asJson(using given_Encoder_Shape).noSpaces)
      case s: Box              => label(s.asJson(using given_Encoder_Shape).noSpaces)
      case s: Sphere           => label(s.asJson(using given_Encoder_Shape).noSpaces)
      case s: Cylinder         => label(s.asJson(using given_Encoder_Shape).noSpaces)
      case s: Torus            => label(s.asJson(using given_Encoder_Shape).noSpaces)
      case s: Extrude          => label(s.asJson(using given_Encoder_Shape).noSpaces)
      case s: PlaneShape       => label(s.asJson(using given_Encoder_Shape).noSpaces)
      case s: SurfaceGridShape => label(s.asJson(using given_Encoder_Shape).noSpaces)
      case s: LinePath         => label(s.asJson(using given_Encoder_Shape).noSpaces)
      case s: MultiPath        => label(s.asJson(using given_Encoder_Shape).noSpaces)
      case s: CubicBezierPath  => label(s.asJson(using given_Encoder_Shape).noSpaces)
      case s: Circle           => label(s.asJson(using given_Encoder_Shape).noSpaces)
      case s: TriangleShape    => label(s.asJson(using given_Encoder_Shape).noSpaces)
      case s: Arrow            => label(s.asJson(using given_Encoder_Shape).noSpaces)
      case s: Axes             => label(s.asJson(using given_Encoder_Shape).noSpaces)
      case s: TestShape        => label(s.asJson(using given_Encoder_Shape).noSpaces)
      case s: TextShape        => label(s.asJson(using given_Encoder_Shape).noSpaces)
    }
}
