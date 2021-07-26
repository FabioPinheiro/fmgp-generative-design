package app.fmgp.syntax

import scala.concurrent.ExecutionContext
import akka.actor.ActorSystem
import app.fmgp.MyAkkaServer
import zio._
import scala.concurrent.Future
import app.fmgp.geo.MyFile

object websocket {
  type Websocket = Has[Websocket.Service]

  // Accessor Methods
  def clearWorld: ZIO[Websocket, Throwable, Unit] =
    ZIO.accessM(_.get.clearWorld)
  def send[T <: app.fmgp.geo.Shape](shape: => T): ZIO[Websocket, Throwable, T] =
    ZIO.accessM(_.get.send(shape))
  def sendFile(file: => MyFile): ZIO[Websocket, Throwable, MyFile] =
    ZIO.accessM(_.get.sendFile(file))
  def stopWebsocket: URIO[Websocket, Unit] =
    ZIO.accessM(_.get.stop)

  object Websocket {
    trait Service(val interface: String, val port: Int) {
      def start: URIO[Any, Service]
      def isStoped: UIO[Boolean]
      def stop: URIO[Any, Unit]
      def stopAfterKeystroke: URIO[zio.console.Console, Unit]

      def send[T <: app.fmgp.geo.Shape](t: T): Task[T]
      def sendFile(file: MyFile): Task[MyFile]
      def clearWorld: ZIO[Websocket, Throwable, Unit]
    }

    private def makeService(using ExecutionContext) = new Service("127.0.0.1", 8888) {
      given actorSystem: ActorSystem = ActorSystem(
        "websocketServiceLive",
        config = None,
        classLoader = Some(this.getClass.getClassLoader),
        defaultExecutionContext = None
      )

      val server: MyAkkaServer = MyAkkaServer(interface, port)

      override def send[T <: app.fmgp.geo.Shape](t: T): Task[T] =
        ZIO.effect(server.GeoSyntax.addShape(t)) //TODO make this beter
      override def sendFile(file: MyFile): Task[MyFile] =
        ZIO.effect(server.GeoSyntax.sendFile(file))
      override def clearWorld: ZIO[Websocket, Throwable, Unit] =
        ZIO.effect(server.GeoSyntax.clear)

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

    lazy val websocketServiceLive: ZLayer[zio.console.Console, Throwable, Has[Service]] =
      ZLayer.fromAcquireRelease(
        makeService(using ExecutionContext.Implicits.global).start //FIXME ExecutionContext
      )(s => s.stopAfterKeystroke)
  }

}
