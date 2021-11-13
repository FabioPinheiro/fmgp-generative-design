package app.fmgp

import scala.concurrent.{ExecutionContext, Future}

import akka.actor.ActorSystem
import zio._
import app.fmgp.MyAkkaServer
import app.fmgp.geo.{Shape, MyFile}

trait Websocket {
  def start: URIO[Any, Websocket] // interface: String, port: Int
  def isStoped: UIO[Boolean]
  def stop: URIO[Any, Unit]
  def stopAfterKeystroke: URIO[Has[Console], Unit]

  def send[T <: Shape](t: T): Task[T]
  def sendFile(file: MyFile): Task[MyFile]
  def clearWorld: Task[Unit]
}

// Accessor Methods Inside the Companion Object
object Websocket {
  def clearWorld: RIO[Has[Websocket], Unit] =
    ZIO.serviceWith(e => e.clearWorld)
  def send[T <: Shape](shape: => T): RIO[Has[Websocket], T] =
    ZIO.serviceWith(_.send(shape))
  def sendFile(file: => MyFile): RIO[Has[Websocket], MyFile] =
    ZIO.serviceWith(_.sendFile(file))
  def stopWebsocket: URIO[Has[Websocket], Unit] =
    ZIO.serviceWith(_.stop)
}

case class WebsocketLive(console: Console) extends Websocket {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global //FIXME
  given actorSystem: ActorSystem = ActorSystem(
    "websocketServiceLive",
    config = None,
    classLoader = Some(this.getClass.getClassLoader),
    defaultExecutionContext = None
  )

  val server: MyAkkaServer = new app.fmgp.experiments.LocalAkkaServer() {} //MyAkkaServer(interface, port)

  private var isTerminated = false //TODO: this is a hack
  actorSystem.whenTerminated.map(_ => isTerminated = true)

  override def send[T <: Shape](t: T): Task[T] =
    ZIO.attempt(server.sendShape(t)) //TODO make this beter
  override def sendFile(file: MyFile): Task[MyFile] =
    ZIO.attempt(server.sendFile(file))
  override def clearWorld: Task[Unit] =
    ZIO.attempt(server.clearShapes)

  override def start: URIO[Any, Websocket] = ZIO.fromFuture(ex => server.start).map(_ => this).orDie

  override def isStoped: UIO[Boolean] =
    ZIO.access(_ => isTerminated)

  override def stop: URIO[Any, Unit] =
    isStoped.flatMap {
      case true => ZIO.unit
      case false =>
        (for {
          _ <- ZIO.fromFuture { ex => server.stop }
          _ <- ZIO.fromFuture { ex => actorSystem.terminate }
        } yield ()).orDie
    }

  override def stopAfterKeystroke: URIO[Has[Console], Unit] =
    isStoped.flatMap {
      case true => ZIO.unit
      case false =>
        (for {
          _ <- console.printLine("Press any key to stop Websocket Service:")
          _ <- console.readLine
          _ <- stop
        } yield ()).orDie
    }
}

object WebsocketLive {
  lazy val layer: URLayer[Has[Console], Has[Websocket]] = (WebsocketLive(_)).toLayer[Websocket]
}
