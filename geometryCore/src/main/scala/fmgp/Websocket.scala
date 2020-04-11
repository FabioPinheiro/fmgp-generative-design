package app.fmgp

import org.scalajs.dom.raw.{CloseEvent, Event, MessageEvent, WebSocket}
import org.scalajs.logging.Logger

//case class World(data: String)

object Websocket {

  /** @see https://japgolly.github.io/scalajs-react/#examples/websockets */
  def newWebSocket(wsUrl: String, log: Logger, textarea: org.scalajs.dom.raw.Element) = {
    val ws = new WebSocket(wsUrl) //TODO Add a timeout here
    ws.onopen = { ev: Event => log.info(s"WS Connected '${ev.`type`}'") }
    ws.onclose = { ev: CloseEvent => log.warn(s"WS Closed because '${ev.reason}'") }
    ws.onmessage = { ev: MessageEvent =>
      log.debug(ev.data.toString)
      text = text + "\n" + ev.data.toString
      textarea.innerHTML = text
    }
    ws.onerror = { ev: Event =>
      import scala.scalajs.js
      log.error(
        ev.asInstanceOf[js.Dynamic].message.asInstanceOf[js.UndefOr[String]].fold(s"WS Error occurred!")("Error: " + _)
      )
    }
    ws
  }

  var text = "Init"

}
