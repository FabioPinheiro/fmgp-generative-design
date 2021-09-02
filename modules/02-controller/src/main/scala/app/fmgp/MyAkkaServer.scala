package app.fmgp

import java.util.concurrent.TimeUnit

import akka.NotUsed
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.stream.{Materializer, Graph, OverflowStrategy, SinkShape}
import akka.stream.scaladsl.{Broadcast, BroadcastHub, Flow, Keep, MergeHub, RunnableGraph, Sink, Source}
import akka.util.ByteString
import io.circe._, io.circe.syntax._, io.circe.generic.semiauto._, io.circe.parser._

import scala.collection.concurrent.TrieMap
import scala.collection.immutable
import scala.concurrent.Future
import scala.concurrent.duration._
import akka.actor._
import scala.concurrent.ExecutionContext
import app.fmgp.geo.{World, WorldAddition, Shape, MyFile}
import app.fmgp.geo.EncoderDecoder.{WorldOrFile, given}

class MyAkkaServer(interface: String, port: Int)(using
    ex: ExecutionContext,
    system: ActorSystem,
    mat: Materializer
) extends com.typesafe.scalalogging.LazyLogging
    with app.fmgp.geo.WorldOperations {

  lazy val binding: Future[Http.ServerBinding] = {
    val aux = Http().newServerAt(interface, port) //.bindAndHandle(route, interface, port)
    logger.info(s"Server is now online at [$interface:$port]\nPress RETURN to stop...")
    val route = path("browser") {
      handleWebSocketMessages(browserFlow)
    }
    aux.bind(route)
  }

  def start = binding
  def stop = binding.flatMap { s =>
    logger.info(s"Server unbinding")
    s.unbind()
      .flatMap { _ =>
        logger.info(s"Server terminating")
        s.terminate(5.seconds)
      }
      .map { e =>
        logger.info(s"Server in now terminated")
        e
      }
  }

  val sinkDumy = Sink.onComplete {
    case scala.util.Success(done) => println(s"Completed: $done")
    case scala.util.Failure(ex)   => println(s"Failed: ${ex.getMessage}")
  }

  def browserFlow = {
    Flow[Message]
      .mapAsync(1) {
        case TextMessage.Strict(text) =>
          logger.info(s"BrowserFlow(Strict): $text")
          Future.successful(())
        case streamed: TextMessage.Streamed =>
          streamed
            .toStrict(FiniteDuration(5, TimeUnit.SECONDS))
            .map { case TextMessage.Strict(text) =>
              logger.info(s"BrowserFlow(Streamed): $text")
              Future.successful(())
            }
        case bm: BinaryMessage =>
          logger.warn(s"BrowserFlow(Binary): ${bm.toString}")
          Future.successful(())
      }
      .via(Flow.fromSinkAndSource(sinkDumy, geoSource)) //.filterNot(_.shapes.isEmpty)
      .map { world => TextMessage(world.asJson.noSpaces) }
  }

  val (geoSink, geoSource) = MergeHub.source[WorldOrFile].toMat(BroadcastHub.sink[WorldOrFile])(Keep.both).run()

  override def sendShape[T <: Shape](shape: T): T = {
    val w = WorldAddition(shapes = Seq(shape))
    geoSink.runWith(Source(Seq(w)))
    shape
  }
  override def sendFile(file: MyFile): MyFile =
    geoSink.runWith(Source.single(file))
    file
  override def clearShapes: Unit =
    geoSink.runWith(Source(Seq(World.w3DEmpty)))
}
