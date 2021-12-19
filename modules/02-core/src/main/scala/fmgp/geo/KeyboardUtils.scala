package fmgp.geo

import scala.scalajs.js.annotation.{JSExportTopLevel, JSExport}
import org.scalajs.dom
import typings.std.global.KeyboardEvent

import zio._
import zio.stream._
import zio.Console._
import zio.Clock._
import typings.std.stdStrings.`object`
import cats.syntax.apply
import fmgp.Utils

import util.chaining._

@JSExportTopLevel("KeyboardUtils")
object KeyboardUtils {

  final case class KeyEvent(key: String, keyCode: Int, repeat: Boolean, ts: String, eventType: String)
  final case class KeyboardState(
      var keys: Map[Int, KeyEvent] = Map.empty,
      var matrix: Matrix = Matrix(),
      val camera1: typings.three.mod.Camera = Utils
        .newCamera(VisualizerJSLive.width, VisualizerJSLive.height, far = 100)
        .tap(_.position.set(0, 0, 5)),
      // .tap(_.lookAt(new typings.three.mod.Vector3(0, 0.3, -1)))
      val camera2: typings.three.mod.Camera = Utils
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
    .async[Clock, Throwable, KeyEvent] { cb =>
      registerCallback(
        event =>
          val ret: ZIO[Clock, Option[Throwable], Chunk[KeyEvent]] = localDateTime.map(ts =>
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
          case "keydown" => UIO { state.keys = state.keys + (i.keyCode -> i) }
          case "keyup"   => UIO { state.keys = state.keys.removed(i.keyCode) }
          case eventType => ZIO.fail(new RuntimeException(s"unknow event time $eventType"))
        }
      }
    }

  val keyboardApp: ZIO[ZEnv & KeyboardState, Throwable, Unit] =
    keyboardStream.run(keySink) // .injectCustom(ZState.makeLayer(KeyboardUtils.KeyboardState()))

  val keyboardStatePrinter: ZIO[Console & KeyboardState, Throwable, Unit] = {
    for {
      map <- ZIO.serviceWithZIO[KeyboardState](state => UIO(state.keys))
      _ <- Console.printLine(map.values.toSeq.toSeq)
    } yield ()
  }

  val keyboardStateCameraUpdate: ZIO[Console & KeyboardState, Throwable, Unit] = {

    for {

      map <- ZIO.serviceWithZIO[KeyboardState](state =>
        ZIO
          .foreachDiscard(state.keys)((id, key) =>
            key.key match {
              // UIO(state.matrix = state.matrix.postTranslate(0.1, 0, 0).postRotate(1, Vec(1, 1, 1))) *>
              // UIO(GeoImprovements.matrix2matrix(state.matrix, state.camera.matrix))
              case "i" => UIO(state.camera1.translateZ(-0.1)) // Move forward
              case "k" => UIO(state.camera1.translateZ(0.1)) // Move backwards
              case "j" => UIO(state.camera1.rotateY(0.025)) // Look LEFT
              case "l" => UIO(state.camera1.rotateY(-0.025)) // Look RIGHT

              case "y" => UIO(state.camera1.translateY(0.1)) // Move UP
              case "h" => UIO(state.camera1.translateY(-0.1)) // Move DOWN
              case "u" => UIO(state.camera1.translateX(0.1)) // Move LEFT
              case "o" => UIO(state.camera1.translateX(-0.1)) // Move RIGHT

              case "p" => UIO(state.camera1.rotateX(0.025))
              case ";" => UIO(state.camera1.rotateX(-0.025))
              case "m" => UIO(state.camera1.rotateZ(0.025))
              case "," => UIO(state.camera1.rotateZ(-0.025))

              // Camera!
              case "1" => UIO(state.cameraIndex = 1)
              case "2" => UIO(state.cameraIndex = 2)

              case any => ZIO.unit // Console.printLine(s"key with no effect: $any")
            }
          )
      )
    } yield ()
  }

  val aux = Runtime.default
  val layer = ZLayer.fromZIO(UIO(hack))
  def app = aux.unsafeRunAsync(keyboardApp.provideSomeLayer(layer))
  def appPrinter = aux.unsafeRunAsync(keyboardStatePrinter.provideSomeLayer(layer))
  def appCamera = aux.unsafeRunAsync(keyboardStateCameraUpdate.provideSomeLayer(layer))
}
