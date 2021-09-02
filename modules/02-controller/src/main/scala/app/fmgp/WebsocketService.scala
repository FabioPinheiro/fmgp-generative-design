package app.fmgp

import scala.concurrent.{ExecutionContext, Future}

import akka.actor.ActorSystem
import zio._
import app.fmgp.MyAkkaServer
import app.fmgp.geo.{Shape, MyFile}

object websocket {
  type Websocket = Has[Websocket.Service]

  // Accessor Methods
  def clearWorld: ZIO[Websocket, Throwable, Unit] =
    ZIO.accessM(_.get.clearWorld)
  def send[T <: Shape](shape: => T): ZIO[Websocket, Throwable, T] =
    ZIO.accessM(_.get.send(shape))
  def sendFile(file: => MyFile): ZIO[Websocket, Throwable, MyFile] =
    ZIO.accessM(_.get.sendFile(file))
  def stopWebsocket: URIO[Websocket, Unit] =
    ZIO.accessM(_.get.stop)

  object Websocket {
    trait Service {
      // def interface: String
      // def port: Int
      def start: URIO[Any, Service]
      def isStoped: UIO[Boolean]
      def stop: URIO[Any, Unit]
      def stopAfterKeystroke: URIO[zio.console.Console, Unit]

      def send[T <: Shape](t: T): Task[T]
      def sendFile(file: MyFile): Task[MyFile]
      def clearWorld: ZIO[Websocket, Throwable, Unit]
    }

    private def makeService(interface: String, port: Int)(using ExecutionContext) = new Service {
      given actorSystem: ActorSystem = ActorSystem(
        "websocketServiceLive",
        config = None,
        classLoader = Some(this.getClass.getClassLoader),
        defaultExecutionContext = None
      )

      val server: MyAkkaServer = new app.fmgp.experiments.LocalAkkaServer() {} //MyAkkaServer(interface, port)

      override def send[T <: Shape](t: T): Task[T] =
        ZIO.effect(server.sendShape(t)) //TODO make this beter
      override def sendFile(file: MyFile): Task[MyFile] =
        ZIO.effect(server.sendFile(file))
      override def clearWorld: ZIO[Websocket, Throwable, Unit] =
        ZIO.effect(server.clearShapes)

      override def start: URIO[Any, Service] = ZIO.fromFuture(ex => server.start).map(_ => this).orDie

      private var isTerminated = false //TODO: this is a hack
      actorSystem.whenTerminated.map(_ => isTerminated = true)
      override def isStoped: UIO[Boolean] = ZIO.fromFunction(_ => isTerminated)

      override def stop: URIO[Any, Unit] =
        isStoped.flatMap {
          case true => ZIO.unit
          case false =>
            (for {
              _ <- ZIO.fromFuture { ex => server.stop }
              _ <- ZIO.fromFuture { ex => actorSystem.terminate }
            } yield ()).orDie
        }

      override def stopAfterKeystroke: URIO[zio.console.Console, Unit] =
        import zio.console._
        isStoped.flatMap {
          case true => ZIO.unit
          case false =>
            (for {
              _ <- putStrLn("Press any key to stop Websocket Service:")
              _ <- getStrLn
              _ <- stop
            } yield ()).orDie
        }
    }

    def websocketServiceLive(interface: String, port: Int): ZLayer[zio.console.Console, Throwable, Has[Service]] =
      ZLayer.fromAcquireRelease(
        makeService(interface, port)(using ExecutionContext.Implicits.global).start //FIXME ExecutionContext
      )(s => s.stopAfterKeystroke)
  }

}
