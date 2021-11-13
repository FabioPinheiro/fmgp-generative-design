package fmgp.experiments

import io.grpc.ServerBuilder
import io.grpc.stub.StreamObserver
import io.grpc.protobuf.services.ProtoReflectionService
import fmgp.geo.proto.service.VisualizerGrpc

import scala.concurrent.{ExecutionContext, Future}

// controller/rumMain fmgp.experiments.ServerGRPC
object ServerGRPC {
  def build = ServerBuilder
    .forPort(fmgp.geo.BuildInfo.grpcPort)
    .addService(VisualizerGrpc.bindService(new VisualizerImpl, ExecutionContext.global))
    .addService(ProtoReflectionService.newInstance())
    .build()

  // def main(args: Array[String]): Unit = {
  //   val server = ServerGRPC.build.start()
  //   sys.addShutdownHook { server.shutdown() }
  //   server.awaitTermination()
  // }
}
