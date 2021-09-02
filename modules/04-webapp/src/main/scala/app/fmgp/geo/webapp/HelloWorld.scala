package app.fmgp.geo.webapp

import org.scalajs.dom
import com.raquo.laminar.api.L._
import com.raquo.domtypes.generic.codecs._

object HelloWorld {

  val nameVar = Var(initial = "world")

  val rootElement = div(
    // /https://material.io/components/navigation-drawer/web
    label("Your name: "),
    input(
      onMountFocus,
      placeholder := "Enter your name here",
      inContext { thisNode => onInput.map(_ => thisNode.ref.value) --> nameVar }
    ),
    span(
      "Hello, ",
      child.text <-- nameVar.signal.map(_.toUpperCase)
    ),
    div(
      className("mdc-checkbox"),
      input(
        typ := "checkbox",
        className("mdc-checkbox__native-control"),
        customProp("data-indeterminate", BooleanAsIsCodec) := true
      ),
      div(
        className("mdc-checkbox__background"),
        svg.svg(
          svg.className("mdc-checkbox__checkmark"),
          svg.viewBox("0 0 24 24"),
          svg.path(
            svg.className("mdc-checkbox__checkmark-path"),
            svg.fill("none"),
            svg.d("M1.73,12.91 8.1,19.28 22.79,4.59")
          )
        ),
        div(className("mdc-checkbox__mixedmark")),
      ),
      div(className("mdc-checkbox__ripple")),
    ),
    label(
      //forAttr("basic-indeterminate-checkbox"), //FIXME
      idAttr("basic-indeterminate-checkbox-label"),
      "This is my indeterminate checkboxx"
    )
  )

  def apply(): HtmlElement = rootElement
}
