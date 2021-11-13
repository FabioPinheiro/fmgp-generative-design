package fmgp

import sttp.client3._
import io.circe._, io.circe.syntax._, io.circe.parser._

import fmgp.geo.EncoderDecoder.{given}
import fmgp.geo.prebuilt.GeoZioExample
import fmgp.geo.World
import fmgp.geo.prebuilt.{runtime, TreesExample}

object SingleRequest {
  def main(args: Array[String]): Unit = {
    println("SingleRequest Start")

    val backend = HttpURLConnectionBackend()

    val request = basicRequest.get(uri"http://localhost:8888/add")

    val requestClean = basicRequest
      .post(uri"http://localhost:8888/add")
      .body(World.w3DEmpty.asJson.noSpaces)

    val requestAdd = basicRequest
      .post(uri"http://localhost:8888/add")
      .body {

        val shapes = runtime.run(GeoZioExample.program)

        World.addition(shapes).asWorld.asJson.noSpaces
      } // World.w3D(Seq(GeoZioExample.shapes))

    val responseClean = requestClean.send(backend)
    val responseAdd = requestAdd.send(backend)

    println("SingleRequest Sleep")
    Thread.sleep(1000 * 60 * 5) // 5 mins
    println("SingleRequest End")
  }
}
