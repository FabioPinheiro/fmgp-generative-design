package app.fmgp

import org.scalajs.dom.raw.{CloseEvent, Event, MessageEvent, WebSocket}
//import org.scalajs.logging.Logger

import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import io.circe._
import app.fmgp.geo._
import app.fmgp.geo.EncoderDecoder.{WorldOrFile, given_Decoder_WorldOrFile}

import scala.scalajs.js

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
      dynamicWorld: DynamicWorldWarp,
      defualtReconnectDelay: Int = 10000,
      var onStateChange: State.State => Unit = (_: State.State) => (),
      var ws: js.UndefOr[WebSocket] = js.undefined,
  ) {
    connect(0)

    def getState: State.State = ws.map(e => State(e.readyState)).getOrElse(State.CLOSED)

    /** @see https://japgolly.github.io/scalajs-react/#examples/websockets */
    private def connect(delay: Int): Unit = {
      log.info(s"WS try reconect to $wsUrl (in ${delay / 1000} s)")
      js.timers.setTimeout(delay) {
        onStateChange(getState)
        val tmpWS = new WebSocket(wsUrl) //TODO Add a timeout here
        ws = tmpWS
        tmpWS.onopen = { (ev: Event) =>
          log.info(s"WS Connected '${ev.`type`}'")
          onStateChange(getState)
        }
        tmpWS.onclose = { (ev: CloseEvent) =>
          log.warn(s"WS Closed because '${ev.reason}'")
          connect(defualtReconnectDelay)
          onStateChange(getState)
        }
        tmpWS.onmessage = { (ev: MessageEvent) =>
          log.info(ev.data.toString)
          decode[WorldOrFile](ev.data.toString) match {
            case Left(ex)             => log.error(s"Error parsing the obj World: $ex")
            case Right(value: World)  => dynamicWorld.update(value)
            case Right(value: MyFile) => log.warn(s"MyFile: $value")
          }
        }
        tmpWS.onerror = { (ev: Event) =>
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
