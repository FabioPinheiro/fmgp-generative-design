package app.fmgp.geo.webapp

import scala.scalajs.js.annotation.JSExportTopLevel
import scala.scalajs.js.annotation.JSExport

import org.scalajs.dom
import com.raquo.laminar.api.L._
import com.raquo.domtypes.generic.codecs._

import MyRouter._
import app.fmgp.geo.prebuilt.{Atomium, GeometryExamples}
import app.fmgp.geo.World

enum WorldOptions {
  case Clean, Atomium, ShapesDemo2D, ShapesDemo3D
}

@JSExportTopLevel("AppUtils")
object AppUtils {

  def githubSVG = svg.svg(
    svg.style("width:24px;height:24px"),
    svg.viewBox("0 0 24 24"),
    svg.path(
      svg.fill("currentColor"),
      svg.d(
        "M12,2A10,10 0 0,0 2,12C2,16.42 4.87,20.17 8.84,21.5C9.34,21.58 9.5,21.27 9.5,21C9.5,20.77 9.5,20.14 9.5,19.31C6.73,19.91 6.14,17.97 6.14,17.97C5.68,16.81 5.03,16.5 5.03,16.5C4.12,15.88 5.1,15.9 5.1,15.9C6.1,15.97 6.63,16.93 6.63,16.93C7.5,18.45 8.97,18 9.54,17.76C9.63,17.11 9.89,16.67 10.17,16.42C7.95,16.17 5.62,15.31 5.62,11.5C5.62,10.39 6,9.5 6.65,8.79C6.55,8.54 6.2,7.5 6.75,6.15C6.75,6.15 7.59,5.88 9.5,7.17C10.29,6.95 11.15,6.84 12,6.84C12.85,6.84 13.71,6.95 14.5,7.17C16.41,5.88 17.25,6.15 17.25,6.15C17.8,7.5 17.45,8.54 17.35,8.79C18,9.5 18.38,10.39 18.38,11.5C18.38,15.32 16.04,16.16 13.81,16.41C14.17,16.72 14.5,17.33 14.5,18.26C14.5,19.6 14.5,20.68 14.5,21C14.5,21.27 14.66,21.59 15.17,21.5C19.14,20.16 22,16.42 22,12A10,10 0 0,0 12,2Z"
      )
    )
  )

  def atomiumSVG = svg.svg(
    svg.x("0px"),
    svg.y("0px"),
    svg.width("24px"),
    svg.height("24"),
    svg.viewBox("0 0 474.24 474.24"),
    svg.style("enable-background:new 0 0 474.24 474.24;"),
    svg.g(
      svg.path(
        svg.className("mdc-checkbox__checkmark-path"),
        svg.d(
          "M470.246,456.367l-73.517-108.807c12.6-7.82,21.035-21.74,21.035-37.627c0-18.502-11.411-34.371-27.558-40.99   l10.414-107.168c21.213-3.28,37.523-21.611,37.523-43.729c0-24.428-19.874-44.3-44.302-44.3c-12.992,0-24.659,5.656-32.771,14.595   l-80.569-35.907c0.492-2.641,0.781-5.353,0.781-8.134c0-24.428-19.875-44.3-44.302-44.3c-24.428,0-44.3,19.873-44.3,44.3   c0,3.095,0.325,6.115,0.932,9.032l-73.49,35.086c-8.006-11.536-21.327-19.117-36.4-19.117c-24.428,0-44.302,19.873-44.302,44.3   c0,22.748,17.241,41.528,39.338,44.007l8.422,113.771c-17.294,6.037-29.754,22.469-29.754,41.795   c0,14.934,7.447,28.137,18.801,36.168L4.145,456.041c-0.864,1.146-5.038,7.16-2.385,12.607c1.154,2.373,3.896,5.229,10.496,5.363   l47.603,0.121c2.945,0,6.528-1.77,7.894-5.729c0.682-1.98,20.841-81.434,28.416-111.312c1.826,0.229,3.673,0.387,5.561,0.387   c12.825,0,24.359-5.514,32.458-14.252l-0.572,1.016l59.482,33.479c-0.211,1.746-0.336,3.512-0.336,5.297   c0,9.178,2.888,18.137,8.157,25.584l-2.334,11.729h-1.856c-4.889,0-8.868,3.977-8.868,8.867v34.791   c0,4.891,3.978,8.869,8.868,8.869h79.366c4.889,0,8.867-3.979,8.867-8.869v-34.791c0-4.891-3.978-8.867-8.867-8.867h-1.986   l-1.926-10.281c5.804-7.67,9.012-17.057,9.012-26.717c0-1.553-0.106-3.086-0.262-4.605l63.078-35.797   c7.838,7.002,18.141,11.301,29.454,11.301c1.284,0,2.547-0.084,3.804-0.191c6.83,26.947,28.51,112.42,29.218,114.48   c1.366,3.951,4.95,5.721,7.908,5.721l47.684-0.121c6.506-0.135,9.247-2.988,10.399-5.361   C475.131,463.312,470.957,457.299,470.246,456.367z M373.464,265.633c-11.512,0-21.979,4.449-29.864,11.674l-67.948-41.075   c3.575-6.387,5.634-13.732,5.634-21.557c0-7.38-1.839-14.33-5.045-20.456l85.974-45.211c7.181,7.335,16.874,12.181,27.679,13.142   l-10.105,103.989C377.716,265.84,375.616,265.633,373.464,265.633z M231.9,349.609v13.848h-11.146l2.756-13.848H231.9z    M240.768,349.609h9.268l2.594,13.848h-11.861V349.609z M231.662,338.967h-6.034l6.034-30.318V338.967z M218.636,374.098H231.9   v14.037h-16.058L218.636,374.098z M240.768,374.098h13.854l2.629,14.037h-16.482V374.098z M242.304,338.967v-30.648l5.739,30.648   H242.304z M236.983,248.338c-18.56,0-33.659-15.1-33.659-33.663c0-18.56,15.099-33.659,33.659-33.659   c18.561,0,33.661,15.099,33.661,33.659C270.644,233.238,255.544,248.338,236.983,248.338z M393.843,84.387   c18.562,0,33.661,15.099,33.661,33.659c0,18.563-15.099,33.662-33.661,33.662c-18.562,0-33.661-15.099-33.661-33.662   C360.182,99.486,375.281,84.387,393.843,84.387z M354.78,97.188c-3.335,6.221-5.239,13.319-5.239,20.858   c0,8.169,2.262,15.803,6.134,22.38l-85.528,44.977c-6.996-7.916-16.751-13.317-27.763-14.665V88.235   c15.544-1.901,28.602-11.873,34.878-25.597L354.78,97.188z M236.983,10.641c18.561,0,33.661,15.099,33.661,33.659   s-15.099,33.659-33.661,33.659c-18.56,0-33.659-15.099-33.659-33.659S218.423,10.641,236.983,10.641z M197.102,63.458   c6.438,13.345,19.338,22.987,34.64,24.801v82.454c-11.224,1.331-21.156,6.872-28.204,14.998l-82.526-48.287   c4.417-6.888,7.011-15.051,7.011-23.824c0-5.555-1.072-10.853-2.948-15.757L197.102,63.458z M50.063,113.602   c0-18.56,15.099-33.659,33.661-33.659c18.56,0,33.659,15.099,33.659,33.659c0,18.563-15.099,33.662-33.659,33.662   C65.163,147.264,50.063,132.165,50.063,113.602z M89.423,157.496c9.512-1.23,18.074-5.477,24.711-11.765l83.433,48.818   c-3.101,6.047-4.885,12.876-4.885,20.126c0,8.528,2.464,16.474,6.657,23.241l-73.794,37.966   c-6.887-4.414-15.047-7.008-23.817-7.008c-1.366,0-2.711,0.084-4.045,0.205L89.423,157.496z M59.084,393.723l8.363-12.377h11.59   c-1.022,4.025-2.076,8.174-3.144,12.377H59.084z M73.189,404.363c-1.146,4.508-2.286,8.99-3.402,13.373h-26.93l9.037-13.373H73.189   z M58.083,463.488l-45.706-0.117c-0.102,0-0.199-0.004-0.291-0.008c0.206-0.385,0.456-0.785,0.719-1.15l22.862-33.836H67.08   C62.786,445.229,59.262,459.014,58.083,463.488z M81.737,370.703h-7.101l11.009-16.293c0.068,0.027,0.138,0.049,0.206,0.076   C84.665,359.164,83.27,364.67,81.737,370.703z M101.729,346.838c-18.561,0-33.661-15.1-33.661-33.662   c0-18.561,15.099-33.658,33.661-33.658s33.661,15.098,33.661,33.658C135.39,331.738,120.291,346.838,101.729,346.838z    M139.893,335.562c3.875-6.58,6.139-14.215,6.139-22.387c0-11.51-4.449-21.975-11.672-29.859l71.762-36.922   c6.584,6.408,15.146,10.76,24.674,12.102l-17.322,87.033c-8.152,5.141-14.291,12.725-17.691,21.488L139.893,335.562z    M203.402,383.018c0-7.275,2.4-14.191,6.564-19.873l-5.561,27.938C203.757,388.457,203.402,385.752,203.402,383.018z    M213.725,398.775H231.9v21.555h-22.466L213.725,398.775z M274.318,462.217H198.5v-31.244h75.818V462.217z M240.768,420.332   v-21.557h18.475l4.036,21.557H240.768z M263.264,362.426c4.633,5.838,7.289,13.137,7.289,20.908c0,3.377-0.54,6.705-1.521,9.893   L263.264,362.426z M260.115,345.613l-16.33-87.213c10.021-1.555,18.927-6.459,25.55-13.557l67.222,40.635   c-4.663,7.014-7.395,15.418-7.395,24.453c0,9.203,2.826,17.758,7.648,24.85l-58.375,33.127   C275.046,358.742,268.672,350.871,260.115,345.613z M339.803,309.934c0-18.561,15.099-33.66,33.661-33.66   c18.562,0,33.661,15.1,33.661,33.66s-15.1,33.658-33.661,33.658C354.901,343.592,339.803,328.492,339.803,309.934z    M415.157,393.832h-16.813c-1.068-4.203-2.122-8.352-3.144-12.377h11.594L415.157,393.832z M401.048,404.475h21.3l9.037,13.373   h-26.936C403.334,413.465,402.193,408.98,401.048,404.475z M399.604,370.814h-7.104c-1.598-6.293-3.051-12.027-4.269-16.83   L399.604,370.814z M461.954,463.482l-45.795,0.117c-1.221-4.631-4.735-18.369-9.001-35.111h31.417l23.009,34.051   c0.169,0.227,0.38,0.572,0.574,0.936C462.093,463.479,462.025,463.482,461.954,463.482z"
        )
      )
    )
  )

  val menuClickObserver = Observer[dom.MouseEvent](onNext = ev => {
    import typings.materialDrawer.mod.MDCDrawer
    val tmp = MDCDrawer.attachTo(dom.window.document.querySelector(".mdc-drawer"))
    tmp.open_=(!tmp.open)
  })

  val optionsClickObserver = Observer[dom.MouseEvent](onNext = ev => {
    import typings.materialMenu.mod.MDCMenu
    val tmp = MDCMenu.attachTo(dom.window.document.querySelector(".mdc-menu"))
    tmp.open_=(!tmp.open)
  })

  def setWorldObserver(w: WorldOptions) = Observer[dom.MouseEvent](onNext =
    ev =>
      AppGlobal.setWorld(
        w match {
          case WorldOptions.Clean        => World.w3DEmpty
          case WorldOptions.Atomium      => Atomium.atomiumWorld
          case WorldOptions.ShapesDemo2D => GeometryExamples.shapesDemo2D
          case WorldOptions.ShapesDemo3D => GeometryExamples.shapesDemo3D
        }
      )
  )

  def topBarHeader(title: Signal[String]) = { //(title: String) = {
    val menuButton = button(
      className("material-icons mdc-top-app-bar__navigation-icon mdc-icon-button"),
      aria.label("Options"),
      onClick --> menuClickObserver,
      "menu"
    )
    typings.materialRipple.mod.MDCRipple.attachTo(menuButton.ref)

    val options = {
      div(
        className("mdc-menu mdc-menu-surface"),
        minWidth("200px"),
        ul(
          className("mdc-list"),
          role("menu"),
          aria.hidden(true),
          aria.orientation("vertical"),
          tabIndex(-1),
          li(
            className("mdc-list-item"),
            role("menuitem"),
            span(className("mdc-list-item__ripple")),
            i(className("material-icons mdc-list-item__graphic"), "delete"),
            span(className("mdc-list-item__text"), WorldOptions.Clean.toString),
            onClick --> setWorldObserver(WorldOptions.Clean),
          ),
          li(className("mdc-list-divider"), role("separator")),
          li(
            ul(
              className("mdc-menu__selection-group"),
              li(
                className("mdc-list-item"),
                role("menuitem"),
                span(className("mdc-list-item__ripple")),
                i(className("material-icons mdc-list-item__graphic"), atomiumSVG),
                // span(
                //   className("mdc-list-item__graphic mdc-menu__selection-group-icon"),
                //   i(aria.label("Atomium"), atomiumSVG)
                // ),
                span(className("mdc-list-item__text"), WorldOptions.Atomium.toString),
                onClick --> setWorldObserver(WorldOptions.Atomium),
              ),
              li(
                className("mdc-list-item"),
                role("menuitem"),
                span(className("mdc-list-item__ripple")),
                i(className("material-icons mdc-list-item__graphic"), "token"),
                span(className("mdc-list-item__text"), WorldOptions.ShapesDemo2D.toString),
                onClick --> setWorldObserver(WorldOptions.ShapesDemo2D),
              ),
              li(
                className("mdc-list-item"),
                role("menuitem"),
                span(className("mdc-list-item__ripple")),
                i(className("material-icons mdc-list-item__graphic"), "token"),
                span(className("mdc-list-item__text"), WorldOptions.ShapesDemo3D.toString),
                onClick --> setWorldObserver(WorldOptions.ShapesDemo3D),
              )
              //TODO
            )
          ),
        )
      )
    }

    val optionsButton = button(
      className("material-icons mdc-top-app-bar__navigation-icon mdc-icon-button"),
      aria.label("Open navigation menu"),
      onClick --> optionsClickObserver,
      "more_vert"
    )
    typings.materialMenu.mod.MDCMenu.attachTo(options.ref)

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
          // button(
          //   className("material-icons mdc-top-app-bar__action-item mdc-icon-button"),
          //   aria.label("Search"),
          //   "search"
          // ),
          div(
            className("mdc-menu-surface--anchor"),
            optionsButton,
            options
          )
        ),
      ),
    )
  }

  val drawerScrim = div(className("mdc-drawer-scrim"))
  def drawer(linkPages: List[Page], currentPage: Signal[Page]) =
    aside(
      className("mdc-drawer mdc-drawer--modal"),
      div(
        className("mdc-drawer__header"),
        h3(className("mdc-drawer__title"), "FMGP GEO"),
        h6(className("mdc-drawer__subtitle"), "fabiomgpinheiro@gmail.com"),
      ),
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
