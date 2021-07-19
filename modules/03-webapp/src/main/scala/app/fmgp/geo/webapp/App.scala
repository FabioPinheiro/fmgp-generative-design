package app.fmgp.geo.webapp

import org.scalajs.dom
import com.raquo.laminar.api.L._
import com.raquo.waypoint._

import MyRouter._

object App {

  def main(args: Array[String]): Unit = {

    // This div, its id and contents are defined in index-fastopt.html and index-fullopt.html files
    lazy val container = dom.document.getElementById("app-container")

    lazy val appElement = {
      div(
        child.maybe <-- MyRouter.router.$currentPage.map {
          case HomePage => None
          case _        => Some(h3(a(navigateTo(HomePage), "Back to home")))
        },
        child <-- $selectedApp.$view
      )
    }

    // Wait until the DOM is loaded, otherwise app-container element might not exist
    renderOnDomContentLoaded(container, appElement)
  }

  private val $selectedApp = SplitRender(MyRouter.router.$currentPage)
    .collectStatic(HomePage)(renderHomePage())
    .collectStatic(HelloPage)(HelloWorld())
    .collectStatic(GeoPage)(GeoApp())

  private def renderHomePage(): HtmlElement = {
    div(
      h1("FMGP GEOMETRY"),
      ul(
        fontSize := "120%",
        lineHeight := "2em",
        listStyleType.none,
        paddingLeft := "0px",
        linkPages.map { page =>
          li(a(navigateTo(page), page.title))
        }
      )
    )
  }

  val linkPages: List[Page] = List(
    HelloPage,
    GeoPage
  )

}
