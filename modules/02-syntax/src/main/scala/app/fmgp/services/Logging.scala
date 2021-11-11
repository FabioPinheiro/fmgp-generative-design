package app.fmgp

import zio._
import zio.{Clock, Console, Has}

object logging {
  trait Logging {
    def log(line: String): UIO[Unit]
  }

  case class LoggingLive(console: Console, clock: Clock) extends Logging {
    override def log(line: String): UIO[Unit] =
      for {
        current <- clock.currentDateTime
        _ <- console.printLine(current.toString + "--" + line).orDie
      } yield ()
  }

  object LoggingLive {
    val layer: URLayer[Has[Console] with Has[Clock], Has[Logging]] =
      (LoggingLive(_, _)).toLayer[Logging]
  }

  // Accessor Methods
  def log(line: => String): URIO[Has[Logging], Unit] = ZIO.serviceWith(_.log(line))
}
