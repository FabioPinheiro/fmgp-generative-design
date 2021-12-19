package fmgp.geo.webapp

import org.scalajs.dom
import com.raquo.laminar.api.L._
import com.raquo.domtypes.generic.codecs._
import scala.util.chaining._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.annotation.{JSExportTopLevel, JSExport}

@JSExportTopLevel("ConfigApp")
object ConfigApp {

  sealed trait Command
  case class SendText(itemText: String) extends Command

  private val (websocket, grpc) = ("websocket", "grpc")

  private val nameVar = Var(initial = "Bob")
  private val itemsVar = Var(Seq.empty[String])
  private val protocolVar = Var(initial = websocket)
  private val protocolWebsocketVar = Var(initial = "http://localhost:8889")
  private val protocolGrpcVar = Var(initial = "http://localhost:8890")

  private val commandObserver = Observer[Command] {
    case SendText(itemText) => {
      nameVar.set("")
      ClientGRPC.send(itemText).map(res => s"$itemText --> $res").map(str => itemsVar.update(_ :+ str))
    }
  }

  val sendClickObserver = Observer[dom.MouseEvent](onNext =
    ev =>
      nameVar.now().tap { text =>
        if (!text.isEmpty) commandObserver.onNext(SendText(itemText = text))
      }
  )

  private def renderTodoItem(text: String): HtmlElement =
    li(label(s"Text: $text"))

  // @JSExport
  // def formField = {
  //   import typings.materialFormField.mod.MDCFormField
  //   val formField = MDCFormField.attachTo(
  //     dom.window.document.querySelector(".mdc-form-field").asInstanceOf[org.scalajs.dom.raw.HTMLElement]
  //   )
  //   formField
  // }

  // @JSExport
  // def radio = {
  //   import typings.materialRadio.mod.MDCRadio
  //   val radio = MDCRadio.attachTo(dom.window.document.querySelector(".mdc-radio"));
  //   radio
  // }

  // List
  private def createListElement(group: String, id: String, text: String, vVar: Var[String], selected: Boolean = false) =
    li(
      className("mdc-list-item"),
      role("radio"),
      aria.checked(checked.toString),
      span(className("mdc-list-item__ripple")),
      span(
        className("mdc-list-item__graphic"),
        div(
          className("mdc-radio"),
          input(
            className("mdc-radio__native-control"),
            `type`("radio"),
            idAttr(s"$group-$id"),
            name(group),
            value(id),
            defaultChecked(selected),
            onClick.mapToValue --> protocolVar
          ),
          div(
            className("mdc-radio__background"),
            div(className("mdc-radio__outer-circle")),
            div(className("mdc-radio__inner-circle")),
          ),
          div(className("mdc-radio__ripple"))
        ),
        label(
          className("mdc-list-item__text"),
          customHtmlAttr("for", StringAsIsCodec)(s"$group-$id"),
          child.text <-- vVar.signal.map(url => s"$text - $url")
        ),
      )
    )

  val rootElement = {
    val $todoItems = itemsVar.signal
    div(
      // /https://material.io/components/navigation-drawer/web
      div(
        label("Your name: "),
        input(
          onMountFocus,
          placeholder := "Enter your name here",
          value <-- nameVar,
          inContext { thisNode => onInput.map(_ => thisNode.ref.value) --> nameVar }
        ),
        button(
          className("mdc-button mdc-button--raised"),
          span(className("mdc-button__label"), "Send Button!"),
          onClick --> sendClickObserver,
          disabled(nameVar.now().isEmpty)
        ),
      ),
      div(
        ul(
          cls("todo-list"),
          children <-- $todoItems.map(_.map(renderTodoItem))
        )
      ), {
        val group = "protocol"
        div(
          h2("Choose communication Protocol"),
          ul(
            className("mdc-list"),
            role("radiogroup"),
            createListElement(group, websocket, "Websocket", protocolWebsocketVar, selected = true),
            createListElement(group, grpc, "GRPC", protocolGrpcVar),
            li(
              className("mdc-list-item"),
              role("radio"),
              aria.checked(checked.toString),
              span(className("mdc-list-item__ripple")),
              span(
                className("mdc-list-item__graphic"),
                // div(
                //   className("mdc-checkbox"),
                //   input(
                //     typ := "checkbox",
                //     className("mdc-checkbox__native-control"),
                //     idAttr(s"custom-url"),
                //     customProp("data-indeterminate", BooleanAsIsCodec) := true
                //   ),
                //   div(
                //     className("mdc-checkbox__background"),
                //     svg.svg(
                //       svg.className("mdc-checkbox__checkmark"),
                //       svg.viewBox("0 0 24 24"),
                //       svg.path(
                //         svg.className("mdc-checkbox__checkmark-path"),
                //         svg.fill("none"),
                //         svg.d("M1.73,12.91 8.1,19.28 22.79,4.59")
                //       )
                //     ),
                //     div(className("mdc-checkbox__mixedmark")),
                //   ),
                //   div(className("mdc-checkbox__ripple")),
                // ),
                label(
                  className("mdc-list-item__text"),
                  customHtmlAttr("for", StringAsIsCodec)(s"custom-url"),
                  "Use custom URL:"
                ),
                label(
                  // className("mdc-list-item__text"),
                  className("mdc-text-field mdc-text-field--filled"),
                  customHtmlAttr("for", StringAsIsCodec)(s"custom-url"),
                  span(className("mdc-text-field__ripple")),
                  // span(className("mdc-floating-label"), idAttr("my-label-id"), "Hint text"),
                  child <-- protocolVar.signal.map {
                    case `websocket` =>
                      input(
                        className("mdc-text-field__input"),
                        `type`("text"),
                        aria.labelledBy("my-label-id"),
                        onMountFocus,
                        placeholder := "Enter your custom Websocket URL here",
                        value <-- protocolWebsocketVar,
                        inContext { thisNode => onInput.map(_ => thisNode.ref.value) --> protocolWebsocketVar }
                      )
                    case `grpc` =>
                      input(
                        className("mdc-text-field__input"),
                        `type`("text"),
                        aria.labelledBy("my-label-id"),
                        onMountFocus,
                        placeholder := "Enter your custom GRPC URL here",
                        value <-- protocolGrpcVar,
                        inContext { thisNode => onInput.map(_ => thisNode.ref.value) --> protocolGrpcVar }
                      )
                  },
                  span(className("mdc-line-ripple")),
                ),
              )
            )
          )
        )
      },
      div(
        span(
          child.text <-- nameVar.signal.combineWith(protocolVar, protocolWebsocketVar, protocolGrpcVar).map {
            (name, protocol, websocketUrl, grpcUrl) =>
              s"Hello, $name! You are using using" + {
                protocol match {
                  case `websocket` => s" the Websocket protocol with the url $websocketUrl"
                  case `grpc`      => s" the GRPC protocol with the url $grpcUrl"
                  case p           => s" a unknowed protocol '$p'"
                }
              }
          }
        )
      )
    )
  }

  def apply(): HtmlElement = rootElement
}
