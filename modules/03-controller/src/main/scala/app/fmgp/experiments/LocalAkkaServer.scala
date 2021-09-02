package app.fmgp.experiments

import app.fmgp.geo._
import app.fmgp.MyAkkaServer

import akka.actor._
import scala.concurrent.ExecutionContext
import akka.stream.Materializer
import akka.stream.scaladsl.Source

class LocalAkkaServer(using
    ex: ExecutionContext,
    system: ActorSystem,
    mat: Materializer
) extends MyAkkaServer(interface = "127.0.0.1", port = 8888)(using ex, system, mat) {}
