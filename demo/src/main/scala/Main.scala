import sttp.client3._
import io.circe._, io.circe.syntax._ //, io.circe.parser._

import zio._
import scala.math._
import fmgp.geo._
import fmgp.geo.EncoderDecoder.{given}
import fmgp.dsl._

import IsenbergSchoolOfManagementHubExample._

@main
def Main(args: String*): Unit =
  println("SingleRequest Start")

  val requestClean = basicRequest
    .post(uri"http://localhost:8888/add")
    .body(World.w3DEmpty.asJson.noSpaces)

  val requestAdd = basicRequest
    .post(uri"http://localhost:8888/add")
    .body {
      val shapes = zio.Runtime.global.unsafeRun(program.inject(DslLive.layer))
      World.addition(shapes).asWorld.asJson.noSpaces
    }

  val backend = HttpURLConnectionBackend()
  val responseClean = requestClean.send(backend)
  val responseAdd = requestAdd.send(backend)
  println("SingleRequest End")
end Main

def program: ZIO[Dsl, Nothing, fmgp.geo.Shape] =
  for {
    _ <- ZIO.unit
    isenberg = Isenberg(
      XYZ.origin,
      ri = 5,
      re = 10,
      alfa_init = 0,
      alfa_proj = Pi,
      alfa_end = 3d / 2 * Pi,
      n = 50,
      slabThickness = 0.02,
    )
    height = 1.5
    floors = ShapeSeq((1 to 5).map(f => isenberg.slabRoof(height * f)))
    beams = isenberg.beams(height)
    shapes = Shape(
      isenberg.slabFloor,
      floors,
      beams
    ).transformWith(Matrix.rotate(-Pi / 2, Vec(x = 1)))
  } yield (shapes)
