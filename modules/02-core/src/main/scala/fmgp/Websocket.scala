package fmgp

import org.scalajs.dom.{CloseEvent, Event, MessageEvent, WebSocket}
//import org.scalajs.logging.Logger

import fmgp.geo._

import scala.scalajs.js
import zio._

//case class World(data: String)

object Websocket {
  object State extends Enumeration {
    type State = Value

    /** Socket has been created. The connection is not yet open. */
    val CONNECTING = Value(0)

    /** The connection is open and ready to communicate. */
    val OPEN = Value(1)

    /** The connection is in the process of closing. */
    val CLOSING = Value(2)

    /** The connection is closed or couldn't be opened. */
    val CLOSED = Value(3)
  }

  case class AutoReconnect(
      wsUrl: String,
      log: Logger,
      defualtReconnectDelay: Int = 10000,
      var ws: js.UndefOr[WebSocket] = js.undefined,
  ) {
    implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
    connect(0)

    def getState: State.State = ws.map(e => State(e.readyState)).getOrElse(State.CLOSED)

    private def wsLayer =
      ZLayer.make[WebsocketJS](WebsocketJSLive.layer, VisualizerJSLive.live, MesherLive.live)

    /** @see https://japgolly.github.io/scalajs-react/#examples/websockets */
    private def connect(delay: Int): Unit = {
      log.info(s"WS try reconect to $wsUrl (in ${delay / 1000} s)")
      js.timers.setTimeout(delay) {
        WebsocketJSLive.onStateChange(getState)
        val tmpWS = new WebSocket(wsUrl) // TODO Add a timeout here
        ws = tmpWS

        tmpWS.onopen = { (ev: Event) =>
          zio.Runtime.global.unsafeRunToFuture(
            WebsocketJS.onOpen(ev.`type`).provide(wsLayer)
          )
        }
        tmpWS.onclose = { (ev: CloseEvent) =>
          zio.Runtime.global
            .unsafeRunToFuture(
              WebsocketJS.onClose(ev.reason).provide(wsLayer)
            )
            .map(_ => connect(defualtReconnectDelay))
        }
        tmpWS.onmessage = { (ev: MessageEvent) =>
          zio.Runtime.global.unsafeRunToFuture(
            WebsocketJS.onMessage(message = ev.data.toString).provide(wsLayer)
          )
        }
        tmpWS.onerror = { (ev: Event) => // TODO ErrorEvent
          val message = ev
            .asInstanceOf[js.Dynamic]
            .message
            .asInstanceOf[js.UndefOr[String]]
            .fold("")("Error: " + _)
          zio.Runtime.global.unsafeRunToFuture(
            WebsocketJS.onError(ev.`type`, message).provide(wsLayer)
          )
        }
      }
    }
  }
}
