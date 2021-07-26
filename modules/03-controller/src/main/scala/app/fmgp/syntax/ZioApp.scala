package app.fmgp.syntax

import zio._
import zio.console.Console
import zio.clock.Clock

import app.fmgp.dsl._
import app.fmgp.meta.MacroUtils._
import app.fmgp.syntax.logging._
import app.fmgp.syntax.websocket._

import app.fmgp.geo.{Box, Shape}
import app.fmgp.geo.MyFile

object ZioApp extends zio.App {
  lazy val envLog = Console.live ++ Clock.live >>> app.fmgp.syntax.logging.Logging.live
  lazy val envDsl = envLog >>> Dsl.live
  lazy val envWS = Console.live >>> Websocket.websocketServiceLive
  lazy val env = Console.live ++ Clock.live ++ envLog ++ envDsl ++ envWS

  def run(args: List[String]) = program.provideLayer(env).exitCode

  // def program: zio.ZIO[
  //   zio.Has[app.fmgp.syntax.logging.Logging.Service] &
  //     zio.Has[app.fmgp.syntax.zioDsl.ZioDsl.Service], // & //zio.Has[zio.console.Console] &
  //   java.io.IOException,
  //   Unit
  // ] =

  //extension (m: MetaBase) def getFile: zio.Task[app.fmgp.geo.MyFile] = app.fmgp.geo.MyFile.readFile(m.sourceFile)

  def program = for {
    _ <- console.putStrLn("-")
    _ <- clearWorld
    b1 <- _box(1, 1, 1)

    _ <- log(b1.sourceFile)
    f <- MyFile.readFile(b1.sourceFile)
    _ <- sendFile(f)
    _ <- log(f.toString)

    // b2 <- box(2, 2, 2)
    // b3 <- box(3, 3, 3)
    // _ <- log((b1: Shape).toString)
    // s1 <- shapes(b1, b2, b3)
    // _ <- send(b1)
    // _ <- send(b2)
    // _ <- send(b3)
    // c <- cylinder(0.5, 20)
    // _ <- send(c)
    // //_ <- log(s1.toString)
    // // _ <- worldConsole(b1)
    // // _ <- worldConsole(s1)
    // _ <- console.putStrLn(b1.prettyPrint)

    //_ <- stopWebsocket
  } yield ()

}
// @main def ZioApp = {
//   val runtime = Runtime(env, zio.internal.Platform.default)
//   runtime.unsafeRun(program)
// }
