package fmgp.geo.webapp

import com.raquo.laminar.api.L.{_, given}
import com.raquo.waypoint._
import org.scalajs.dom
import upickle.default._

object MyRouter {
  sealed abstract class Page(val title: String)

  case object HomePage extends Page("Home")
  case object GeoPage extends Page("GeoApp")
  case object MermaidPage extends Page("MermaidApp")
  case object ConfigPage extends Page("Config")
  case object ShowGeoJsonPage extends Page("ShowGeoJson")
  case object ShowGeoHtmlPage extends Page("ShowGeoHTML")

  given HomePageRW: ReadWriter[HomePage.type] = macroRW
  given ConfigPageRW: ReadWriter[ConfigPage.type] = macroRW
  // given GeoAppPageRW: ReadWriter[GeoAppPage.type] = macroRW
  given ShowGeoJsonPageRW: ReadWriter[ShowGeoJsonPage.type] = macroRW

  given rw: ReadWriter[Page] = macroRW

  private val routes = List(
    Route.static(HomePage, root / endOfSegments, Router.localFragmentBasePath),
    Route.static(GeoPage, root / "geo" / endOfSegments, Router.localFragmentBasePath),
    Route.static(MermaidPage, root / "mermaid" / endOfSegments, Router.localFragmentBasePath),
    Route.static(ConfigPage, root / "config" / endOfSegments, Router.localFragmentBasePath),
    Route.static(ShowGeoJsonPage, root / "json" / endOfSegments, Router.localFragmentBasePath),
    Route.static(ShowGeoHtmlPage, root / "html" / endOfSegments, Router.localFragmentBasePath),
  )

  val router = new Router[Page](
    routes = routes,
    getPageTitle = _.title, // displayed in the browser tab next to favicon
    serializePage = page => write(page)(rw), // serialize page data for storage in History API log
    deserializePage = pageStr => read(pageStr)(rw) // deserialize the above
  )(
    $popStateEvent = windowEvents.onPopState, // this is how Waypoint avoids an explicit dependency on Laminar
    owner = unsafeWindowOwner // this router will live as long as the window
  )

  // Note: for fragment ('#') URLs this isn't actually needed.
  // See https://github.com/raquo/Waypoint docs for why this modifier is useful in general.
  def navigateTo(page: Page): Binder[HtmlElement] = Binder { el =>

    val isLinkElement = el.ref.isInstanceOf[dom.html.Anchor]

    if (isLinkElement) {
      el.amend(href(router.absoluteUrlForPage(page)))
    }

    // If element is a link and user is holding a modifier while clicking:
    //  - Do nothing, browser will open the URL in new tab / window / etc. depending on the modifier key
    // Otherwise:
    //  - Perform regular pushState transition
    (onClick
      .filter(ev => !(isLinkElement && (ev.ctrlKey || ev.metaKey || ev.shiftKey || ev.altKey)))
      .preventDefault
      --> (_ => router.pushState(page))).bind(el)
  }
}
