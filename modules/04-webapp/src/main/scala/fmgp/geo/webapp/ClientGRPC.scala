package fmgp.geo.webapp

import scala.scalajs.js.annotation._

import io.grpc.stub.StreamObserver
import scalapb.grpc.Channels
import scalapb.grpcweb.Metadata
import fmgp.geo.proto.service.{VisualizerGrpcWeb, DataReq => Req, DataRes => Res}
import scala.concurrent.ExecutionContext.Implicits.global

class GrpcError(code: String, message: String) extends RuntimeException(s"Grpc-web error ${code}: ${message}")

@JSExportTopLevel("ClientGRPC")
object ClientGRPC {

  lazy val stub = {
    val channel = Channels.grpcwebChannel("http://localhost:" + fmgp.geo.BuildInfo.grpcWebPort)
    VisualizerGrpcWeb.stub(channel)
  }

  @JSExport
  def send(data: String) = stub.sayHello(Req(data)).map(_.value)

  @JSExport
  // @JSExportTopLevel(name = "start", moduleID = "clientGRPC")
  def run(): Unit = {
    println("ClientGRPC Run")

    val req = Req("Fabio")
    // val req = Req(payload="error", vals=Seq(-4000, -1, 17, 39, 4175))

    val metadata: Metadata = Metadata("custom-header-1" -> "unary-value")

    // Make an async call
    stub.sayHello(req).onComplete { f => println("async call" -> f) }

    // Make an async call with metadata
    stub.sayHello(req, metadata).onComplete { f => println("async call with metadata" -> f) }

    // Make an async server streaming call
    val metadata2: Metadata = Metadata("custom-header-2" -> "streaming-value")
    val stream = stub.sayHelloServerStreaming(
      req,
      metadata2,
      new StreamObserver[Res] {
        override def onNext(value: Res): Unit = {
          println("Next: " + value)
        }

        override def onError(throwable: Throwable): Unit = {
          println("Error! " + throwable)
        }

        override def onCompleted(): Unit = {
          println("Completed!")
        }
      }
    )

    // Cancel ongoing streamig call
    // stream.cancel()
  }
}
