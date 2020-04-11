package app.fmgp

import akka.actor.{Actor, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage, WebSocketRequest}
import akka.http.scaladsl.server.Directives.{path, _}
import akka.stream.scaladsl.GraphDSL.Implicits._
import akka.stream.scaladsl.{Flow, GraphDSL, Keep, Sink, Source}
import akka.stream.{ActorMaterializer, FlowShape, OverflowStrategy}
import akka.{Done, NotUsed}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import scala.util.{Failure, Success}
import scala.io._

object Main extends Logger {

  implicit val actorSystem = ActorSystem("akka-system")
  implicit val flowMaterializer = ActorMaterializer()
  val interface = "127.0.0.1"
  val port = 8080
  val server = new app.fmgp.MyAkkaServer

  def start = {
    server.runServer(interface, port)
    logger.info(s"Server is now online at ws://$interface:$port\nPress RETURN to stop...")
  }

  def stop = {
    //import actorSystem.dispatcher
    logger.debug("Server is down...")
    val f = server.binding.flatMap(_.unbind()).onComplete(_ => actorSystem.terminate())
    //Await.result(f, 10.seconds)
    logger.info("Server down now")
  }

  def main(args: Array[String]): Unit = {
    start
    StdIn.readLine()
    stop
  }

}
