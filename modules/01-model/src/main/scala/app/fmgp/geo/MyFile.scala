package app.fmgp.geo

import scala.io.Source
import zio._

case class MyFile(filename: String, data: String)
object MyFile {
  def readFile(filename: String): Task[MyFile] =
    ZIO(MyFile(filename, data = Source.fromFile(filename).getLines.mkString))
}
