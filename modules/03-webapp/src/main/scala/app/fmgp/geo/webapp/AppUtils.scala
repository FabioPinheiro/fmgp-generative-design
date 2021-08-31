package app.fmgp.geo.webapp

import scala.scalajs.js.annotation.JSExportTopLevel
import scala.scalajs.js.annotation.JSExport

import org.scalajs.dom
import com.raquo.laminar.api.L._
import com.raquo.domtypes.generic.codecs._

import MyRouter._

@JSExportTopLevel("AppUtils")
object AppUtils {

  val clickObserver = Observer[dom.MouseEvent](onNext = ev => {
    import typings.materialDrawer.mod.MDCDrawer
    val tmp = MDCDrawer.attachTo(dom.window.document.querySelector(".mdc-drawer"))
    tmp.open_=(!tmp.open)
  })

  def topBarHeader(title: String) = {
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
          span(className("mdc-top-app-bar__title"), title)
        ),
        section(
          className("mdc-top-app-bar__section mdc-top-app-bar__section--align-end"),
          role("toolbar"),
          button(
            className("material-icons mdc-top-app-bar__action-item mdc-icon-button"),
            aria.label("Favorite"),
            "favorite"
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
              i(className("material-icons mdc-list-item__graphic"), aria.hidden(true), "inbox"),
              navigateTo(page),
              span(className("mdc-list-item__text"), page.title),
            ),
          )
        )
      )
    )

}
