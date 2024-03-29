package fmgp

import scala.concurrent.{Future, ExecutionContext}
import scala.collection.concurrent.TrieMap
import scala.collection.immutable
import scala.concurrent.duration._
import scala.util._
import java.util.concurrent.TimeUnit

import io.circe._, io.circe.syntax._, io.circe.generic.semiauto._, io.circe.parser._

import akka.actor._
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpEntity, ContentTypes, ContentType, MediaTypes, HttpCharsets}
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.Unmarshaller
import akka.stream.{Materializer, Graph, OverflowStrategy, SinkShape}
import akka.stream.scaladsl.{Broadcast, BroadcastHub, Flow, Keep, MergeHub, RunnableGraph, Sink, Source}
import akka.util.ByteString

import fmgp.geo.{World, WorldAddition, Shape, MyFile}
import fmgp.geo.EncoderDecoder.{WorldOrFile, given}

class MyAkkaServer(interface: String, port: Int)(using
    ex: ExecutionContext,
    system: ActorSystem,
    mat: Materializer
) extends com.typesafe.scalalogging.LazyLogging
    with fmgp.geo.WorldOperations {

  implicit val um: Unmarshaller[HttpEntity, Option[WorldOrFile]] = { // byteStringUnmarshaller
    Unmarshaller.stringUnmarshaller.mapWithCharset { (data, charset) =>
      decode[fmgp.geo.EncoderDecoder.WorldOrFile](data) match {
        case Left(error) =>
          logger.error("Error", error)
          None
        case Right(wf) => Some(wf)
      }
    }
  }

  lazy val binding: Future[Http.ServerBinding] = {
    val wsRoute = path("browser") {
      handleWebSocketMessages(browserFlow)
    }

    // curl -X POST 'http://localhost:8888/add' -d '{"WorldState":{"shapes":[],"dimensions":"D2"}}'
    val addRoute = path("add") {
      concat(
        post {
          decodeRequest {
            // unmarshal with in-scope unmarshaller
            entity(as[Option[WorldOrFile]]) { data =>
              data match {
                case Some(wf) =>
                  geoSink.runWith(Source.single(wf))
                  logger.info(s"WorldOrFile received")
                  complete { "WorldOrFile received" }
                case None => complete { "Fail" }
              }

            }
          }
        },
        get {
          complete((World.w3DEmpty: World).asJson.noSpaces)
        }
      )
    }

    val appRoutes = {
      import scala.io.Source
      val serveIndex =
        Try(Source.fromResource("index.html").iter.mkString) match { // getFromResourceDirectory("index.html")
          case Failure(ex)   => failWith(ex)
          case Success(data) => complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, data))
        }
      val serveMainJs = {
        Try(Source.fromResource("fmgp-geometry-webapp-fastopt-bundle.js").iter.mkString)
          .orElse(Try(Source.fromResource("fmgp-geometry-webapp-fullopt-bundle.js").iter.mkString))
      } match {
        case Failure(ex) => failWith(ex)
        case Success(data) =>
          complete(HttpEntity(ContentType.WithCharset(MediaTypes.`application/javascript`, HttpCharsets.`UTF-8`), data))
      }

      val serveMaterialCSS =
        Try(scala.io.Source.fromResource("material-components-web.min.css").iter.mkString) match {
          case Failure(ex) => failWith(ex)
          case Success(data) =>
            complete(HttpEntity(ContentType.WithCharset(MediaTypes.`text/css`, HttpCharsets.`UTF-8`), data))
        }

      concat(
        path("") { get(serveIndex) },
        path("main.js") { get(serveMainJs) },
        path("material-components-web.min.css") { get(serveMaterialCSS) },
      )
    }

    val myServer = Http().newServerAt(interface, port) // .bindAndHandle(route, interface, port)
    logger.info(s"Server is now online at [$interface:$port]")
    myServer.bind(concat(wsRoute, addRoute, appRoutes))
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
      .via(Flow.fromSinkAndSource(sinkDumy, geoSource)) // .filterNot(_.shapes.isEmpty)
      .map { world => TextMessage(world.asJson.noSpaces) }
  }

  val (geoSink, geoSource) = MergeHub.source[WorldOrFile].toMat(BroadcastHub.sink[WorldOrFile])(Keep.both).run()

  override def sendShape[T <: Shape](shape: T): T = {
    val w = WorldAddition(shapes = Seq(shape))
    geoSink.runWith(Source(Seq(w)))
    shape
  }
  override def sendFile(file: MyFile): MyFile = {
    geoSink.runWith(Source.single(file))
    file
  }
  override def clearShapes: Unit =
    geoSink.runWith(Source(Seq(World.w3DEmpty)))
}
