package fmgp

abstract sealed class Level extends Ordered[Level] { x =>
  protected val order: Int
  def compare(y: Level): Int = x.order - y.order
}

object Level {
  case object Error extends Level { protected val order = 4 }
  case object Warn extends Level { protected val order = 3 }
  case object Info extends Level { protected val order = 2 }
  case object Debug extends Level { protected val order = 1 }
}

trait Logger {
  def log(level: Level, message: => String): Unit
  def trace(t: => Throwable): Unit

  final def error(message: => String): Unit = log(Level.Error, message)
  final def warn(message: => String): Unit = log(Level.Warn, message)
  final def info(message: => String): Unit = log(Level.Info, message)
  final def debug(message: => String): Unit = log(Level.Debug, message)
}
object Log extends Logger {
  def log(level: /*org.scalajs.logging.*/ Level, message: => String): Unit = println(s"[$level] $message")
  def trace(t: => Throwable): Unit = log( /*org.scalajs.logging.*/ Level.Debug, t.toString)
}
