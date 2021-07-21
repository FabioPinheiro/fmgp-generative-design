package app.fmgp.syntax

import zio._
import app.fmgp.syntax.ZioDsl._
import app.fmgp.meta.MacroUtils._
// import zio.console._
// @main def MainSyntax = {
//   val runtime = Runtime.default
//   runtime.unsafeRun(program)
// }

import app.fmgp.geo.{Box, Shape}
object ZioApp extends zio.App {

  def run(args: List[String]) = program.exitCode

  val program =
    for {
      _ <- console.putStrLn("-")
      b1 <- mBox(1, 1, 1)
      b2 <- box(2, 2, 2)
      _ = println(b1: Shape)
      s1 <- shapes(b1, b2)
      _ <- worldConsole(b1)
      _ <- worldConsole(s1)
      _ <- console.putStrLn(b1.prettyPrint)
    } yield ()

}
