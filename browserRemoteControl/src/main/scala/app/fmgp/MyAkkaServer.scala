package app.fmgp

import java.util.concurrent.TimeUnit

import akka.NotUsed
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.stream.{Materializer, Graph, OverflowStrategy, SinkShape}
import akka.stream.scaladsl.{Broadcast, BroadcastHub, Flow, Keep, MergeHub, RunnableGraph, Sink, Source}
import akka.util.ByteString
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import io.circe._

import scala.collection.concurrent.TrieMap
import scala.collection.immutable
import scala.concurrent.Future
import scala.concurrent.duration._
import akka.actor._
import scala.concurrent.ExecutionContext
import app.fmgp.geo.{World, WorldAddition}

case class MyAkkaServer(interface: String, port: Int)(
    implicit ex: ExecutionContext,
    system: ActorSystem,
    mat: Materializer
) extends Logger {

  lazy val binding: Future[Http.ServerBinding] = {
    val aux = Http().bindAndHandle(route, interface, port)
    logger.info(s"Server is now online at [$interface:$port]\nPress RETURN to stop...")
    aux
  }

  def start = binding
  def stop = binding.flatMap { s =>
    logger.info(s"Server unbinding")
    s.unbind
      .flatMap { _ =>
        logger.info(s"Server terminating")
        s.terminate(5.seconds)
      }
      .map { e =>
        logger.info(s"Server in now terminated")
        e
      }
  }

  val route =
    pathSingleSlash {
      get {
        handleWebSocketMessages(broadcastFlow)
      }
    } ~ path("room") {
      parameters(Symbol("room") ? "A") { room => handleWebSocketMessages(roomFlow(room)) }
    } ~ path("solo") {
      handleWebSocketMessages(soloFlow)
    } ~ path("broadcast") {
      handleWebSocketMessages(broadcastFlow)
    } ~ path("ada") {
      handleWebSocketMessages(adaFlow)
    } ~ path("browser") {
      handleWebSocketMessages(browserFlow)
    }

  case class AkkaChatMessage(msg: String, oUser: Option[String])
  //trait ChatServerEvent
  //case class Message(value: String) extends ChatServerEvent
  //case class AddClient(ref: akka.actor.ActorRef) extends ChatServerEvent
  //case class RemoveClient(ref: akka.actor.ActorRef) extends ChatServerEvent

  private val (chatSink, chatSource) = MergeHub.source[String].toMat(BroadcastHub.sink[String])(Keep.both).run

  // Attach a BroadcastHub Sink to the producer. This will materialize to a corresponding Source.
  // (We need to use toMat and Keep.right since by default the materialized value to the left is used)
  val runnableGraph: RunnableGraph[Source[String, NotUsed]] = chatSource.toMat(BroadcastHub.sink)(Keep.right)
  // By running/materializing the producer, we get back a Source, which
  // gives us access to the elements published by the producer.
  val broadcastProducer: Source[String, NotUsed] = runnableGraph.run()

  private def getChatRoom(room: String): (Sink[String, NotUsed], Source[String, NotUsed]) = {
    chatRooms.getOrElseUpdate(
      room, {
        // Easy enough to use merge hub / broadcast sink to create a dynamically joinable chat room
        val (sink, source) = MergeHub.source[String].toMat(BroadcastHub.sink[String])(Keep.both).run
        broadcastProducer.runWith(sink)
        (sink, source)
      }
    )
  }

  private val chatRooms = TrieMap.empty[String, (Sink[String, NotUsed], Source[String, NotUsed])]

  def flowParse[T](implicit decoder: Decoder[T]): Flow[Message, Either[Error, T], NotUsed] = Flow[Message].mapAsync(1) {
    case TextMessage.Strict(text) =>
      Future.successful(decode[T](text))
    case streamed: TextMessage.Streamed =>
      streamed.toStrict(FiniteDuration(5, TimeUnit.SECONDS)).map {
        case TextMessage.Strict(text) =>
          decode[T](text)
      }
    case bm: BinaryMessage =>
      /*ignore binary messages but drain content to avoid the stream being clogged*/
      bm.dataStream.runWith(Sink.ignore)
      val errorMsg = "FlowParse[T] to not support BinaryMessage"
      logger.info(s"error($errorMsg)")
      Future.successful(Left(DecodingFailure(errorMsg, List())))
  }

  private def echoFlow = flowParse[AkkaChatMessage].map {
    case Right(AkkaChatMessage(msg, Some(user))) => s"${user.name}: $msg"
    case Right(AkkaChatMessage(msg, None))       => s"JoneDoe?: $msg"
    case Left(value)                             => s"ERROR: ${value.getMessage}"
  }

  def roomFlow(room: String) = {
    val (sink, source) = getChatRoom(room)
    echoFlow.via(Flow.fromSinkAndSourceCoupled(sink, source)).map[Message](s => TextMessage(s"Room($room): $s"))
  }

  /**
    * @see https://doc.akka.io/docs/akka/2.5/stream/stream-dynamic.html
    */
  def broadcastFlow: Flow[Message, Message, NotUsed] = {
    //chatRooms.values.map(e => e._1 : Graph[SinkShape[String], _] ).toSeq.foldLeft(aaa){(a,b) => a.alsoTo(b)}
    echoFlow.via(Flow.fromSinkAndSource(chatSink, chatSource)).map[Message](s => TextMessage(s))
  }

  def soloFlow: Flow[Message, Message, Any] = echoFlow.map[Message](s => TextMessage(s"ECHO: $s"))

  // def bytesToHex(bytes: Array[Byte]): String =
  //   bytes.map(b => String.format("%02x", new Integer(b.toInt & 0xff))).mkString(" ")

  // private def echoProtoFlow[T]( //[T <: scalapb.GeneratedMessage with scalapb.Message[T]](
  //     implicit decoder: Decoder[T],
  //     //gmc: GeneratedMessageCompanion[T]
  // ) = {
  //   Flow[Message]
  //     .mapAsync(1) {
  //       case TextMessage.Strict(text) => //Should not be used
  //         Future.successful(decode[T](text))
  //       case streamed: TextMessage.Streamed => //Should not be used
  //         streamed.toStrict(FiniteDuration(5, TimeUnit.SECONDS)).map {
  //           case TextMessage.Strict(text) =>
  //             decode[T](text)
  //         }
  //       // case bm: BinaryMessage =>
  //       //   /*ignore binary messages but drain content to avoid the stream being clogged*/
  //       //   val data = bm.getStrictData.toArray
  //       //   //logger.debug(s"Data: ${bytesToHex(data)}")
  //       //   //logger.debug(s"After proto: ${gmc.parseFrom(data)}")
  //       //   Future.successful(Right(gmc.parseFrom(data)))
  //     }
  // }

  val (geoSink, geoSource) = MergeHub.source[World].toMat(BroadcastHub.sink[World])(Keep.both).run

  // val geoRunnableGraph: RunnableGraph[Source[World, NotUsed]] = geoSource.toMat(BroadcastHub.sink)(Keep.right)
  // val geoConsoleProducer: Source[World, NotUsed] = geoRunnableGraph.run()
  object GeoSyntax extends app.fmgp.geo.Syntax {
    def addShape[T <: app.fmgp.geo.Shape](t: T): T = {
      val w = WorldAddition(shapes = Seq(t))
      geoSink.runWith(Source(Seq(w)))
      t
    }

    def clear: Unit = {
      geoSink.runWith(Source(Seq(World.w3DEmpty)))
    }
  }

  val sourceDumy = Source.maybe[World]
  val sinkDumy = Sink.onComplete {
    case scala.util.Success(done) => println(s"Completed: $done")
    case scala.util.Failure(ex)   => println(s"Failed: ${ex.getMessage}")
  }

  def adaFlow = {
    flowParse[World]
      .map {
        case Right(w: World) =>
          logger.debug(s"Sending World: ${w.asJson.noSpaces}")
          w
        case Left(error) =>
          logger.error("Failed to decode World", error)
          World.w2D(Seq.empty)
      }
      .via(Flow.fromSinkAndSource(geoSink, sourceDumy))
      .map { world => TextMessage(world.asJson.noSpaces) }
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
            .map {
              case TextMessage.Strict(text) =>
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

}
