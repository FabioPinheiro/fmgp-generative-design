package app.fmgp

import org.scalajs.dom.raw.{CloseEvent, Event, MessageEvent, WebSocket}
import org.scalajs.logging.Logger

import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import io.circe._
import app.fmgp.geo.World

//case class World(data: String)

object Websocket {
  val decoder = implicitly[Decoder[World]]

  /** @see https://japgolly.github.io/scalajs-react/#examples/websockets */
  def newWebSocket(wsUrl: String, log: Logger, dynamicWorld: DynamicWorldWarp) = {
    val ws = new WebSocket(wsUrl) //TODO Add a timeout here
    ws.onopen = { ev: Event => log.info(s"WS Connected '${ev.`type`}'") }
    ws.onclose = { ev: CloseEvent => log.warn(s"WS Closed because '${ev.reason}'") }
    ws.onmessage = { ev: MessageEvent =>
      log.info(ev.data.toString)
      decode[World](ev.data.toString) match {
        case Left(ex)     => log.error(s"Error parsing the obj World: $ex")
        case Right(value) => dynamicWorld.update(value)
      }
    }
    ws.onerror = { ev: Event =>
      import scala.scalajs.js
      log.error(
        ev.asInstanceOf[js.Dynamic].message.asInstanceOf[js.UndefOr[String]].fold(s"WS Error occurred!")("Error: " + _)
      )
    }
    ws
  }
}
