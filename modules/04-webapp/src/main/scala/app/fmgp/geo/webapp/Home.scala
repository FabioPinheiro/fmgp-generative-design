package app.fmgp.geo.webapp

import com.raquo.laminar.api.L._
import com.raquo.domtypes.generic.codecs._
import typings.std.stdStrings.style

object Home {

  val rootElement = div(
    ul(
      className("mdc-image-list mdc-image-list--masonry"),
      li(
        className("mdc-image-list__item"),
        img(
          className("mdc-image-list__image"),
          src("https://drive.google.com/uc?id=1Mx9SWaeODxP9B845gS3R0PX8xXu39AxL")
        ),
        div(
          className("mdc-image-list__supporting"),
          span(
            className("mdc-image-list__label"),
            "Example of some geometry created with this library on a browser"
          )
        ),
        div(
          p("A Scala/ScalaJS library for Generative Design. The visualizer run on any Browser with JS."),
          p("Scala's RELP can be used to interact with the visualizer via WS to develop your designs.")
        )
      )
    )
  )

  def apply(): HtmlElement = rootElement
}
