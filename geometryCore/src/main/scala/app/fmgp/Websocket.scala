package app.fmgp

import org.scalajs.dom.raw.{CloseEvent, Event, MessageEvent, WebSocket}
import org.scalajs.logging.Logger

import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import io.circe._
import app.fmgp.geo.World

import scala.scalajs.js

//case class World(data: String)

object Websocket {
  val decoder = implicitly[Decoder[World]]

  object AutoReconnect {
    def apply(wsUrl: String, log: Logger, dynamicWorld: DynamicWorldWarp, defualtReconnectDelay: Int = 10000) =
      new AutoReconnect(wsUrl, log, dynamicWorld, defualtReconnectDelay)
  }

  class AutoReconnect(
      wsUrl: String,
      log: Logger,
      dynamicWorld: DynamicWorldWarp,
      defualtReconnectDelay: Int = 10000,
      var ws: js.UndefOr[WebSocket] = js.undefined,
  ) {
    connect(0)

    /** @see https://japgolly.github.io/scalajs-react/#examples/websockets */
    private def connect(delay: Int): Unit = {
      log.info(s"WS try reconect to $wsUrl (in ${delay / 1000} s)")
      js.timers.setTimeout(delay) {
        val tmpWS = new WebSocket(wsUrl) //TODO Add a timeout here
        ws = tmpWS
        tmpWS.onopen = { ev: Event => log.info(s"WS Connected '${ev.`type`}'") }
        tmpWS.onclose = { ev: CloseEvent =>
          log.warn(s"WS Closed because '${ev.reason}'")
          connect(defualtReconnectDelay)
        }
        tmpWS.onmessage = { ev: MessageEvent =>
          log.info(ev.data.toString)
          decode[World](ev.data.toString) match {
            case Left(ex)     => log.error(s"Error parsing the obj World: $ex")
            case Right(value) => dynamicWorld.update(value)
          }
        }
        tmpWS.onerror = { ev: Event =>
          log.error(
            ev.asInstanceOf[js.Dynamic] //TODO ErrorEvent
              .message
              .asInstanceOf[js.UndefOr[String]]
              .fold(s"WS Error (type:${ev.`type`}) occurred!")("Error: " + _)
          )
        }

      }
    }

  }

}
