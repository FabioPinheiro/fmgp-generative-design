package fmgp.geo

import scala.io.Source
import zio._
import scala.util.Try

case class MyFile(filename: String, data: String)
object MyFile {
  def readFile(filename: String): Task[MyFile] =
    ZIO.fromTry(Try(MyFile(filename, data = Source.fromFile(filename).getLines.mkString)))
}
