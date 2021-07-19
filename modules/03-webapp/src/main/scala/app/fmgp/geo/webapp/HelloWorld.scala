package app.fmgp.geo.webapp

import org.scalajs.dom
import com.raquo.laminar.api.L._

object HelloWorld {

  val nameVar = Var(initial = "world")

  val rootElement = div(
    label("Your name: "),
    input(
      onMountFocus,
      placeholder := "Enter your name here",
      inContext { thisNode => onInput.map(_ => thisNode.ref.value) --> nameVar }
    ),
    span(
      "Hello, ",
      child.text <-- nameVar.signal.map(_.toUpperCase)
    )
  )

// In most other examples, containerNode will be set to this behind the scenes
  //val containerNode = dom.document.querySelector("#mdoc-html-run0")
  //render(containerNode, rootElement)

  def apply(): HtmlElement = rootElement
}
