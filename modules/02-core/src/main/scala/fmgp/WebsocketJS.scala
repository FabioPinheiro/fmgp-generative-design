package fmgp

import zio._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import io.circe._

import fmgp.geo.{Visualizer, World, MyFile}
import fmgp.geo.EncoderDecoder.{WorldOrFile, given_Decoder_WorldOrFile}

trait WebsocketJS {
  def onOpen(evType: String): UIO[Unit]
  def onClose(reason: String): UIO[Unit]
  def onMessage(message: String): UIO[Unit]
  def onError(evType: String, message: String): UIO[Unit]
}

object WebsocketJS {
  // Accessor Methods Inside the Companion Object
  def onOpen(evType: String): URIO[WebsocketJS, Unit] = ZIO.serviceWithZIO(_.onOpen(evType))
  def onClose(reason: String): URIO[WebsocketJS, Unit] = ZIO.serviceWithZIO(_.onClose(reason))
  def onMessage(message: String): URIO[WebsocketJS, Unit] = ZIO.serviceWithZIO(_.onMessage(message))
  def onError(evType: String, message: String): URIO[WebsocketJS, Unit] =
    ZIO.serviceWithZIO(_.onError(evType: String, message: String))
}
case class WebsocketJSLive(vissualizer: Visualizer) extends WebsocketJS {
  override def onOpen(evType: String): UIO[Unit] =
    ZIO.succeed(WebsocketJSLive.onStateChange(Websocket.State.OPEN)) <&>
      ZIO.succeed { Log.info(s"WS Connected '$evType'") }
  override def onClose(reason: String): UIO[Unit] =
    ZIO.succeed(WebsocketJSLive.onStateChange(Websocket.State.CLOSED)) <&>
      ZIO.succeed { Log.info(s"WS Closed because '${reason}'") }
  override def onMessage(message: String): UIO[Unit] =
    decode[WorldOrFile](message) match { // TODO user ZIO.fromEither
      case Left(ex) =>
        ZIO.succeed(Log.debug(message)).map(_ => Log.error(s"Error parsing the obj World: $ex"))
      case Right(value: World)  => vissualizer.update(value)
      case Right(value: MyFile) => ZIO.succeed(Log.warn(s"MyFile: $value"))
    }
  override def onError(evType: String, messageError: String): UIO[Unit] =
    ZIO.succeed(Log.error(s"WS Error (type:$evType) occurred! " + messageError))
}

object WebsocketJSLive {
  val layer: URLayer[Visualizer, WebsocketJS] =
    ZLayer.fromFunction((v: Visualizer) => WebsocketJSLive(v): WebsocketJS)

  var onStateChange: Websocket.State.State => Unit = (_: Websocket.State.State) => ()
  val wsUrl = "ws://127.0.0.1:8888/browser"

  lazy val autoReconnect = Websocket.AutoReconnect(wsUrl, Log)
}
