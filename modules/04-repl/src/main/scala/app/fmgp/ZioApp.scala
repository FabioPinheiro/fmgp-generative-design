package app.fmgp

import zio._

import app.fmgp.dsl._
import app.fmgp.meta.MacroUtils._
import app.fmgp.Logging._
import app.fmgp.Websocket._

import app.fmgp.geo.{Box, Shape}
import app.fmgp.geo.MyFile
import app.fmgp.geo.prebuilt.TreesExample

extension (m: MetaBase) //{ def sourceFile: String })
  def getFile: zio.Task[app.fmgp.geo.MyFile] =
    //import reflect.Selectable.reflectiveSelectable
    app.fmgp.geo.MyFile.readFile(m.sourceFile)

object ZioApp extends zio.ZIOAppDefault {
  //TODO Config // val (interface: String, port: Int) = ("127.0.0.1", 8888)

  // lazy val envLog = Console.live ++ Clock.live >>> LoggingLive.layer
  // lazy val envWS = Console.live >>> WebsocketLive.layer //WebsocketLive.websocketServiceLive(interface, port)
  // lazy val env = Console.live ++ Clock.live ++ envLog ++ DslLive.layer ++ envWS

  def run = program
    .inject(
      Console.live,
      Clock.live,
      LoggingLive.layer,
      WebsocketLive.layer,
      DslLive.layer,
      //TreesExample.Tree.layer
      ZLayer.Debug.mermaid,
    )
    .exitCode

  def program = for {
    _ <- Console.printLine("-")
    _ <- clearWorld
    b1 <- _box(1, 1, 1)
    _ <- log(b1.sourceFile)
    f <- b1.getFile
    _ <- sendFile(f)
    _ <- log(f.toString)
    b2 <- box(2, 2, 2)
    b3 <- box(3, 3, 3)
    _ <- log((b1: Shape).toString)
    s1 <- shapes(b1, b2, b3)
    _ <- send(b1)
    _ <- send(b2)
    _ <- send(b3)
    c <- cylinder(0.5, 20)
    _ <- send(c)
    _ <- Console.printLine(b1.prettyPrint)

    //_ <- stopWebsocket
  } yield ()

}
