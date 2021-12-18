package fmgp.experiments

import zio._
import zio.stream._
import zio.Clock._
import zio.Console._

@main def MainStreams() = {
  //val stream: ZStream[Clock, Nothing, Unit] = ZStream.tick(1.seconds)

  def countdown(n: Int) = ZStream.unfold(n) {
    case 0 => None
    case s => Some((s, s - 1))
  }
  val p = countdown(10).foreach(printLine(_))

  // val a = ZIO.fail("Boom!")
  // /Runtime.global.unsafeRun(aaa.foreach(printLine(_)))
  val testLayer = ZLayer.make[Console](Console.live)

  Runtime.unsafeFromLayer(testLayer).unsafeRun(p)

//   final case class MyState(counter: Int)

//   val b = UIO(MyState(10))

//   val a = ZState.make(b)

//   val app1 = for {
//     s <- a
//     _ <- s.update(state => state.copy(counter = state.counter + 1))
//     count <- s.get.map(_.counter)
//     _ <- Console.printLine(count)
//   } yield count

//   def run1 = app1

//   val app2 = for {
//     s <- a
//     _ <- s.update(state => state.copy(counter = state.counter + 1))
//     count <- s.get.map(_.counter)
//     _ <- Console.printLine(count)
//   } yield count

//   def run2 = app2

//   Runtime.default.unsafeRun(run1)
//   Runtime.default.unsafeRun(run2)

}

// // object TracingExample extends ZIOAppDefault {

// //   def doSomething(input: Int): ZIO[Console with Clock, String, Unit] =
// //     for {
// //       _ <- Console.printLine(s"Do something $input").orDie
// //       // /_ <- ZIO.fail("Boom!") // line number 8
// //       _ <- ZIO.logSpan("myspan") {
// //         ZIO.sleep(1.second) *> ZIO.log("The job is finished!")
// //       }
// //       _ <- Console.printLine("Finished my job").orDie
// //     } yield ()

// //   def myApp: ZIO[Console with Clock, String, Unit] =
// //     for {
// //       _ <- Console.printLine("Hello!").orDie
// //       _ <- doSomething(5)
// //       _ <- Console.printLine("Bye Bye!").orDie
// //     } yield ()

// //   def run = myApp.inject(ZLayer.Debug.mermaid, ZLayer.Debug.tree, Console.live, Clock.live)
// // }
