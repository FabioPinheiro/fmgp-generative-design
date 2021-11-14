package fmgp

import org.scalajs.dom.raw.{CloseEvent, Event, MessageEvent, WebSocket}
//import org.scalajs.logging.Logger

import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import io.circe._
import fmgp.geo._
import fmgp.geo.EncoderDecoder.{WorldOrFile, given_Decoder_WorldOrFile}

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

    /** @see https://japgolly.github.io/scalajs-react/#examples/websockets */
    private def connect(delay: Int): Unit = {
      log.info(s"WS try reconect to $wsUrl (in ${delay / 1000} s)")
      js.timers.setTimeout(delay) {
        WebsocketJSLive.onStateChange(getState)
        val tmpWS = new WebSocket(wsUrl) //TODO Add a timeout here
        ws = tmpWS

        tmpWS.onopen = { (ev: Event) =>
          zio.Runtime.global.unsafeRunToFuture(
            WebsocketJS
              .onOpen(ev.`type`)
              .inject(WebsocketJSLive.layer, VisualizerJSLive.live, MesherLive.live)
          )
        }
        tmpWS.onclose = { (ev: CloseEvent) =>
          zio.Runtime.global
            .unsafeRunToFuture(
              WebsocketJS
                .onClose(ev.reason)
                .inject(WebsocketJSLive.layer, VisualizerJSLive.live, MesherLive.live)
            )
            .map(_ => connect(defualtReconnectDelay))
        }
        tmpWS.onmessage = { (ev: MessageEvent) =>
          zio.Runtime.global.unsafeRunToFuture(
            WebsocketJS
              .onMessage(message = ev.data.toString)
              .inject(WebsocketJSLive.layer, VisualizerJSLive.live, MesherLive.live)
          )
        }
        tmpWS.onerror = { (ev: Event) => //TODO ErrorEvent
          val message = ev
            .asInstanceOf[js.Dynamic]
            .message
            .asInstanceOf[js.UndefOr[String]]
            .fold("")("Error: " + _)
          zio.Runtime.global.unsafeRunToFuture(
            WebsocketJS
              .onError(ev.`type`, message)
              .inject(WebsocketJSLive.layer, VisualizerJSLive.live, MesherLive.live)
          )
        }
      }
    }
  }
}

trait WebsocketJS {
  def onOpen(evType: String): UIO[Unit]
  def onClose(reason: String): UIO[Unit]
  def onMessage(message: String): UIO[Unit]
  def onError(evType: String, message: String): UIO[Unit]
}

object WebsocketJS {
  // Accessor Methods Inside the Companion Object
  def onOpen(evType: String): URIO[Has[WebsocketJS], Unit] = ZIO.serviceWith(_.onOpen(evType))
  def onClose(reason: String): URIO[Has[WebsocketJS], Unit] = ZIO.serviceWith(_.onClose(reason))
  def onMessage(message: String): URIO[Has[WebsocketJS], Unit] = ZIO.serviceWith(_.onMessage(message))
  def onError(evType: String, message: String): URIO[Has[WebsocketJS], Unit] =
    ZIO.serviceWith(_.onError(evType: String, message: String))
}
case class WebsocketJSLive(vissualizer: Visualizer) extends WebsocketJS {
  override def onOpen(evType: String): UIO[Unit] =
    UIO(WebsocketJSLive.onStateChange(Websocket.State.OPEN)) <&>
      UIO { Log.info(s"WS Connected '$evType'") }
  override def onClose(reason: String): UIO[Unit] =
    UIO(WebsocketJSLive.onStateChange(Websocket.State.CLOSED)) <&>
      UIO { Log.info(s"WS Closed because '${reason}'") }
  override def onMessage(message: String): UIO[Unit] =
    decode[WorldOrFile](message) match { //TODO user ZIO.fromEither
      case Left(ex) =>
        UIO(Log.debug(message)).map(_ => Log.error(s"Error parsing the obj World: $ex"))
      case Right(value: World)  => vissualizer.update(value)
      case Right(value: MyFile) => UIO(Log.warn(s"MyFile: $value"))
    }
  override def onError(evType: String, messageError: String): UIO[Unit] =
    UIO(Log.error(s"WS Error (type:$evType) occurred! " + messageError))
}

object WebsocketJSLive {
  val layer: URLayer[Has[Visualizer], Has[WebsocketJS]] =
    (WebsocketJSLive(_)).toServiceBuilder[WebsocketJS]

  var onStateChange: Websocket.State.State => Unit = (_: Websocket.State.State) => ()
  val wsUrl = "ws://127.0.0.1:8888/browser"

  lazy val autoReconnect = Websocket.AutoReconnect(wsUrl, Log)
}
