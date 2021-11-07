package app.fmgp.geo.webapp

import scala.scalajs.js.annotation._

import org.scalajs.dom
import com.raquo.laminar.api.L._
import com.raquo.waypoint._

import MyRouter._
object App {

  // @JSExportTopLevel(name = "start", moduleID = "app_print")
  // def print() = println("Ola from webapp App")

  def main( /*args: Array[String]*/ ): Unit = {

    // This div, its id and contents are defined in index-fastopt.html and index-fullopt.html files
    lazy val container = dom.document.getElementById("app-container")

    lazy val appElement = {
      div(
        AppUtils.drawer(linkPages, MyRouter.router.$currentPage),
        AppUtils.drawerScrim,
        AppUtils.topBarHeader(MyRouter.router.$currentPage.map {
          case p: HomePage.type => "FMGP GEOMETRY"
          case p                => p.title
        }),
        com.raquo.laminar.api.L.main(
          className("mdc-top-app-bar--fixed-adjust"),
          child <-- $selectedApp.$view
        )
      )
    }

    // Wait until the DOM is loaded, otherwise app-container element might not exist
    renderOnDomContentLoaded(container, appElement)
  }

  private val $selectedApp = SplitRender(MyRouter.router.$currentPage)
    .collectStatic(HomePage)(Home())
    .collectStatic(ConfigPage)(ConfigApp())
    .collectStatic(GeoPage)(GeoApp())
    .collectStatic(ShowGeoJsonPage)(ShowGeo(true))
    .collectStatic(ShowGeoHtmlPage)(ShowGeo(false))

  private val linkPages: List[Page] = List(
    HomePage,
    ConfigPage,
    GeoPage,
    ShowGeoJsonPage,
    ShowGeoHtmlPage,
  )

}
