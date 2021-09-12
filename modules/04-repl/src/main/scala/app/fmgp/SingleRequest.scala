package app.fmgp

import sttp.client3._
import io.circe._, io.circe.syntax._, io.circe.parser._

import app.fmgp.geo.EncoderDecoder.{given}
import app.fmgp.geo.prebuilt.GeoZioExample
import app.fmgp.geo.World

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
      .body(GeoZioExample.world.asJson.noSpaces) // World.w3D(Seq(GeoZioExample.shapes))

    val responseClean = requestClean.send(backend)
    val responseAdd = requestAdd.send(backend)

    println("SingleRequest Sleep")
    Thread.sleep(1000 * 60 * 5) // 5 mins
    println("SingleRequest End")
  }
}
