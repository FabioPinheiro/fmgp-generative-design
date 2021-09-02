package app.fmgp.geo.webapp

import scala.scalajs.js.annotation.JSExportTopLevel
import scala.scalajs.js.annotation.JSExport

import org.scalajs.dom
import com.raquo.laminar.api.L._
import com.raquo.domtypes.generic.codecs._

import MyRouter._

@JSExportTopLevel("AppUtils")
object AppUtils {

  val githubSVG = svg.svg(
    svg.style("width:24px;height:24px"),
    svg.viewBox("0 0 24 24"),
    svg.path(
      svg.fill("currentColor"),
      svg.d(
        "M12,2A10,10 0 0,0 2,12C2,16.42 4.87,20.17 8.84,21.5C9.34,21.58 9.5,21.27 9.5,21C9.5,20.77 9.5,20.14 9.5,19.31C6.73,19.91 6.14,17.97 6.14,17.97C5.68,16.81 5.03,16.5 5.03,16.5C4.12,15.88 5.1,15.9 5.1,15.9C6.1,15.97 6.63,16.93 6.63,16.93C7.5,18.45 8.97,18 9.54,17.76C9.63,17.11 9.89,16.67 10.17,16.42C7.95,16.17 5.62,15.31 5.62,11.5C5.62,10.39 6,9.5 6.65,8.79C6.55,8.54 6.2,7.5 6.75,6.15C6.75,6.15 7.59,5.88 9.5,7.17C10.29,6.95 11.15,6.84 12,6.84C12.85,6.84 13.71,6.95 14.5,7.17C16.41,5.88 17.25,6.15 17.25,6.15C17.8,7.5 17.45,8.54 17.35,8.79C18,9.5 18.38,10.39 18.38,11.5C18.38,15.32 16.04,16.16 13.81,16.41C14.17,16.72 14.5,17.33 14.5,18.26C14.5,19.6 14.5,20.68 14.5,21C14.5,21.27 14.66,21.59 15.17,21.5C19.14,20.16 22,16.42 22,12A10,10 0 0,0 12,2Z"
      )
    )
  )

  val clickObserver = Observer[dom.MouseEvent](onNext = ev => {
    import typings.materialDrawer.mod.MDCDrawer
    val tmp = MDCDrawer.attachTo(dom.window.document.querySelector(".mdc-drawer"))
    tmp.open_=(!tmp.open)
  })

  def topBarHeader(title: Signal[String]) = { //(title: String) = {
    val menuButton = button(
      className("material-icons mdc-top-app-bar__navigation-icon mdc-icon-button"),
      aria.label("Open navigation menu"),
      onClick --> clickObserver,
      "menu"
    )
    typings.materialRipple.mod.MDCRipple.attachTo(menuButton.ref)
    header(
      className("mdc-top-app-bar"),
      div(
        className("mdc-top-app-bar__row"),
        section(
          className("mdc-top-app-bar__section mdc-top-app-bar__section--align-start"),
          menuButton,
          span(className("mdc-top-app-bar__title"), child.text <-- title)
        ),
        section(
          className("mdc-top-app-bar__section mdc-top-app-bar__section--align-end"),
          role("toolbar"),
          a(
            className("material-icons mdc-top-app-bar__action-item mdc-icon-button"),
            href("https://github.com/FabioPinheiro/fmgp-generative-design"),
            i(aria.label("Github"), githubSVG),
          ),
          button(
            className("material-icons mdc-top-app-bar__action-item mdc-icon-button"),
            aria.label("Search"),
            "search"
          ),
          button(
            className("material-icons mdc-top-app-bar__action-item mdc-icon-button"),
            aria.label("Options"),
            "more_vert"
          ),
        ),
      ),
    )
  }

  // @JSExport
  // def xxxRipple() = {
  //   import typings.materialRipple.mod.MDCRipple
  //   MDCRipple.attachTo(dom.window.document.querySelector(".mdc-button"))
  // }

  // @JSExport
  // def xxxList() = {
  //   import typings.materialList.mod.MDCList
  //   MDCList.attachTo(dom.window.document.querySelector(".mdc-list"))
  // }

  // @JSExport
  // def xxxDrawer() = {
  //   import typings.materialDrawer.mod.MDCDrawer
  //   MDCDrawer.attachTo(dom.window.document.querySelector(".mdc-drawer")).open_=(true)
  // }

  val drawerScrim = div(className("mdc-drawer-scrim"))
  def drawer(linkPages: List[Page], currentPage: Signal[Page]) =
    aside(
      className("mdc-drawer mdc-drawer--modal"),
      div(
        className("mdc-drawer__content"),
        nav(
          className("mdc-list"),
          linkPages.map(page =>
            a(
              className <-- currentPage.map { p =>
                if (p == page) "mdc-list-item mdc-list-item--activated" else "mdc-list-item"
              },
              aria.customProp("current", StringAsIsCodec)("page"),
              tabIndex(0),
              span(className("mdc-list-item__ripple")),
              i(
                className("material-icons mdc-list-item__graphic"),
                aria.hidden(true),
                page match {
                  case _: HomePage.type        => "home"
                  case _: HelloPage.type       => "streetview"
                  case _: GeoPage.type         => "explore"
                  case _: ShowGeoJsonPage.type => "precision_manufacturing"
                  case _: ShowGeoHtmlPage.type => "dashboard"
                }
              ),
              navigateTo(page),
              span(className("mdc-list-item__text"), page.title),
            ),
          )
        )
      )
    )

}
