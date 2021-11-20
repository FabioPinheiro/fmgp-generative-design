package fmgp

import zio._
import zio.{Clock, Console}

trait Logging {
  def log(line: String): UIO[Unit]
}

// Accessor Methods Inside the Companion Object
object Logging {
  def log(line: => String): URIO[Logging, Unit] = ZIO.serviceWithZIO(_.log(line))
}

case class LoggingLive(console: Console, clock: Clock) extends Logging {
  override def log(line: String): UIO[Unit] =
    for {
      current <- clock.currentDateTime
      _ <- console.printLine(current.toString + "--" + line).orDie
    } yield ()
}

object LoggingLive {
  val layer: URLayer[Console with Clock, Logging] =
    (LoggingLive(_, _)).toLayer[Logging]
}
