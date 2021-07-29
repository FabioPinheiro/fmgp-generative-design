package app.fmgp

import zio._
import zio.clock.Clock
import zio.console.Console

object logging {
  type Logging = Has[Logging.Service]

  // Companion object exists to hold service definition and also the live implementation.
  object Logging {
    trait Service {
      def log(line: String): UIO[Unit]
    }

    val live: URLayer[Clock & Console, Logging] =
      ZLayer.fromServices[Clock.Service, Console.Service, Logging.Service] {
        (clock: Clock.Service, console: Console.Service) =>
          new Service {
            override def log(line: String): UIO[Unit] =
              for {
                current <- clock.currentDateTime.orDie
                _ <- console.putStrLn(current.toString + "--" + line).orDie
              } yield ()
          }
      }
  }

  // Accessor Methods
  def log(line: => String): URIO[Logging, Unit] =
    ZIO.accessM(_.get.log(line))
}
