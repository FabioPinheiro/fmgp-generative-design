package fmgp.geo

import scala.scalajs.js.annotation.{JSExportTopLevel, JSExport}
import org.scalajs.dom
import fmgp.typings.std.global.KeyboardEvent

import zio._
import zio.stream._
import zio.Console._
import zio.Clock._
import fmgp.typings.std.stdStrings.`object`
import cats.syntax.apply
import fmgp.Utils

import util.chaining._

@JSExportTopLevel("KeyboardUtils")
object KeyboardUtils {

  final case class KeyEvent(key: String, keyCode: Int, repeat: Boolean, ts: String, eventType: String)
  final case class KeyboardState(
      var keys: Map[Int, KeyEvent] = Map.empty,
      var matrix: Matrix = Matrix(),
      val camera1: fmgp.typings.three.mod.Camera = Utils
        .newCamera(VisualizerJSLive.width, VisualizerJSLive.height, far = 100)
        .tap(_.position.set(0, 0, 5)),
      // .tap(_.lookAt(new fmgp.typings.three.mod.Vector3(0, 0.3, -1)))
      val camera2: fmgp.typings.three.mod.Camera = Utils
        .newCamera(VisualizerJSLive.width, VisualizerJSLive.height)
        .tap(_.position.set(0, 0, 10)),
      // .tap(_.lookAt(new Vector3(0, 0, 0)))
      var cameraIndex: Int = 1
  ) {
    def camera = cameraIndex match {
      case 1 => camera1
      case 2 => camera2
      case _ => camera1
    }
  }
  @JSExport
  val hack = KeyboardUtils.KeyboardState()

  // @JSExport
  // var event: js.Any = _

  // https://zio.dev/next/datatypes/stream/zstream#from-asynchronous-callback
  // https://developer.mozilla.org/en-US/docs/Web/API/KeyboardEvent
  def registerCallback(
      // /element: WebGLRenderer,
      onEvent: KeyboardEvent => Unit,
      onError: Throwable => Unit
  ): Unit = {
    println(s"RegisterCallback EventListener on 'keydown' & 'keyup'") // element.domElement.addEventListener
    dom.window.addEventListener("keydown", (ev: KeyboardEvent) => onEvent(ev))
    dom.window.addEventListener("keyup", (ev: KeyboardEvent) => onEvent(ev))
  }

  // Lifting an Asynchronous API to ZStream
  val keyboardStream = ZStream
    .async[Any, Throwable, KeyEvent] { cb =>
      registerCallback(
        event =>
          val ret: ZIO[Any, Option[Throwable], Chunk[KeyEvent]] = localDateTime.map(ts =>
            val keyEvent = KeyEvent(event.key, event.keyCode.toInt, event.repeat, ts.toString, event.`type`)
            // Chunk(s"$ts: keyCode:${event.keyCode}; key:${event.key}; type:${event.`type`}; repeat:${event.repeat}")
            Chunk(keyEvent)
          )
          // val aux = s"keyCode:${event.keyCode}; key:${event.key}; type:${event.`type`}; repeat:${event.repeat}"
          // val ret = ZIO.succeed(Chunk(aux))
          cb(ret)
        ,
        error => cb(ZIO.fail(error).mapError(Some(_)))
      )
    }
    .filter(!_.repeat)

  val keySink: ZSink[KeyboardState, RuntimeException, KeyEvent, Nothing, Unit] =
    ZSink.foreach { (i: KeyEvent) =>
      ZIO.serviceWithZIO[KeyboardState] { state =>
        i.eventType match {
          case "keydown" => ZIO.succeed { state.keys = state.keys + (i.keyCode -> i) }
          case "keyup"   => ZIO.succeed { state.keys = state.keys.removed(i.keyCode) }
          case eventType => ZIO.fail(new RuntimeException(s"unknow event time $eventType"))
        }
      }
    }

  val keyboardApp: ZIO[KeyboardState, Throwable, Unit] =
    keyboardStream.run(keySink) // .injectCustom(ZState.makeLayer(KeyboardUtils.KeyboardState()))

  val keyboardStatePrinter: ZIO[Console & KeyboardState, Throwable, Unit] = {
    for {
      map <- ZIO.serviceWithZIO[KeyboardState](state => ZIO.succeed(state.keys))
      _ <- Console.printLine(map.values.toSeq.toSeq)
    } yield ()
  }

  val keyboardStateCameraUpdate: ZIO[Console & KeyboardState, Throwable, Unit] = {

    for {

      map <- ZIO.serviceWithZIO[KeyboardState](state =>
        ZIO
          .foreachDiscard(state.keys)((id, key) =>
            key.key match {
              // ZIO.succeed(state.matrix = state.matrix.postTranslate(0.1, 0, 0).postRotate(1, Vec(1, 1, 1))) *>
              // ZIO.succeed(GeoImprovements.matrix2matrix(state.matrix, state.camera.matrix))
              case "i" => ZIO.succeed(state.camera1.translateZ(-0.1)) // Move forward
              case "k" => ZIO.succeed(state.camera1.translateZ(0.1)) // Move backwards
              case "j" => ZIO.succeed(state.camera1.rotateY(0.025)) // Look LEFT
              case "l" => ZIO.succeed(state.camera1.rotateY(-0.025)) // Look RIGHT

              case "y" => ZIO.succeed(state.camera1.translateY(0.1)) // Move UP
              case "h" => ZIO.succeed(state.camera1.translateY(-0.1)) // Move DOWN
              case "u" => ZIO.succeed(state.camera1.translateX(0.1)) // Move LEFT
              case "o" => ZIO.succeed(state.camera1.translateX(-0.1)) // Move RIGHT

              case "p" => ZIO.succeed(state.camera1.rotateX(0.025))
              case ";" => ZIO.succeed(state.camera1.rotateX(-0.025))
              case "m" => ZIO.succeed(state.camera1.rotateZ(0.025))
              case "," => ZIO.succeed(state.camera1.rotateZ(-0.025))

              // Camera!
              case "1" => ZIO.succeed(state.cameraIndex = 1)
              case "2" => ZIO.succeed(state.cameraIndex = 2)

              case any => ZIO.unit // Console.printLine(s"key with no effect: $any")
            }
          )
      )
    } yield ()
  }

  def runProgram[E](program: ZIO[Any, E, Unit]) = Unsafe.unsafe { implicit unsafe => // Run side effect
    Runtime.default.unsafe.fork(
      program
        .catchAllCause(cause => ZIO.logErrorCause("runProgram Fail", cause))
        .catchAllDefect(error => ZIO.logError("runProgram with Defect: " + error.getMessage()))
    )
  }

  val layer = ZLayer.fromZIO(ZIO.succeed(hack))
  def app = runProgram(keyboardApp.provideSomeLayer(layer))
  def appPrinter = runProgram(keyboardStatePrinter.provideSomeLayer(layer ++ ZLayer.succeed(Console.ConsoleLive)))
  def appCamera = runProgram(keyboardStateCameraUpdate.provideSomeLayer(layer ++ ZLayer.succeed(Console.ConsoleLive)))
}
