package app.fmgp.experiments

import io.grpc.ServerBuilder
import io.grpc.stub.StreamObserver
import io.grpc.protobuf.services.ProtoReflectionService
import fmgp.geo.proto.service.VisualizerGrpc
import fmgp.geo.proto.service.VisualizerGrpc.Visualizer
import fmgp.geo.proto.service.{DataReq => Req, DataRes => Res}

import scala.concurrent.{ExecutionContext, Future}

class VisualizerImpl extends Visualizer {
  override def sayHello(request: Req): Future[Res] = Future.successful {
    Res(s"Hello, ${request.value}")
  }

  override def sayHelloServerStreaming(
      request: Req,
      responseObserver: StreamObserver[Res]
  ): Unit = {
    responseObserver.onNext(Res(s"Ola, ${request.value}"))
    responseObserver.onNext(Res(s"Alo, ${request.value}"))
    responseObserver.onNext(Res(s"Hey, ${request.value}"))
    if (request.value == "error") {
      responseObserver.onError(new RuntimeException("Problem Problem"))
    } else {
      responseObserver.onNext(Res("No 'error'"))
      responseObserver.onCompleted()
    }
  }

  override def sayHelloBidiStreaming(
      responseObserver: StreamObserver[Res]
  ): StreamObserver[Req] = new StreamObserver[Req]() {
    override def onNext(req: Req): Unit = {
      val res = responseObserver.onNext(Res("Oi " + req.value))
    }

    override def onError(ex: Throwable): Unit = throw ex
    override def onCompleted(): Unit = responseObserver.onCompleted
  }

  override def sayHelloClientStreaming(
      responseObserver: StreamObserver[Res]
  ): StreamObserver[Req] = new StreamObserver[Req] {
    var reqs = Seq.empty[Req]
    override def onCompleted(): Unit = {
      responseObserver.onNext(Res("Hey all: " + reqs.map(_.value).mkString(", ")))
      responseObserver.onCompleted
    }
    override def onError(ex: Throwable): Unit = throw ex
    override def onNext(req: Req): Unit = reqs = reqs :+ req
  }
}
