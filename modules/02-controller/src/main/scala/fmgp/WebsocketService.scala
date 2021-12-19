package fmgp

import scala.concurrent.{ExecutionContext, Future}

import akka.actor.ActorSystem
import zio._
import fmgp.MyAkkaServer
import fmgp.geo.{Shape, MyFile}

trait Websocket {
  def start: URIO[Any, Websocket] // interface: String, port: Int
  def isStoped: UIO[Boolean]
  def stop: URIO[Any, Unit]
  def stopAfterKeystroke: URIO[Console, Unit]

  def send[T <: Shape](t: T): Task[T]
  def sendFile(file: MyFile): Task[MyFile]
  def clearWorld: Task[Unit]
}

// Accessor Methods Inside the Companion Object
object Websocket {
  def clearWorld: RIO[Websocket, Unit] =
    ZIO.serviceWithZIO(e => e.clearWorld)
  def send[T <: Shape](shape: => T): RIO[Websocket, T] =
    ZIO.serviceWithZIO(_.send(shape))
  def sendFile(file: => MyFile): RIO[Websocket, MyFile] =
    ZIO.serviceWithZIO(_.sendFile(file))
  def stopWebsocket: URIO[Websocket, Unit] =
    ZIO.serviceWithZIO(_.stop)
}

case class WebsocketLive(console: Console) extends Websocket {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global // FIXME
  given actorSystem: ActorSystem = ActorSystem(
    "websocketServiceLive",
    config = None,
    classLoader = Some(this.getClass.getClassLoader),
    defaultExecutionContext = None
  )

  val server: MyAkkaServer = new fmgp.experiments.LocalAkkaServer() {} // MyAkkaServer(interface, port)

  private var isTerminated = false // TODO: this is a hack
  actorSystem.whenTerminated.map(_ => isTerminated = true)

  override def send[T <: Shape](t: T): Task[T] =
    ZIO.attempt(server.sendShape(t)) // TODO make this beter
  override def sendFile(file: MyFile): Task[MyFile] =
    ZIO.attempt(server.sendFile(file))
  override def clearWorld: Task[Unit] =
    ZIO.attempt(server.clearShapes)

  override def start: URIO[Any, Websocket] = ZIO.fromFuture(ex => server.start).map(_ => this).orDie

  override def isStoped: UIO[Boolean] =
    ZIO.environmentWith(_ => isTerminated)

  override def stop: URIO[Any, Unit] =
    isStoped.flatMap {
      case true => ZIO.unit
      case false =>
        (for {
          _ <- ZIO.fromFuture { ex => server.stop }
          _ <- ZIO.fromFuture { ex => actorSystem.terminate }
        } yield ()).orDie
    }

  override def stopAfterKeystroke: URIO[Console, Unit] =
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
  lazy val layer: URLayer[Console, Websocket] = (WebsocketLive(_)).toLayer[Websocket]
}
