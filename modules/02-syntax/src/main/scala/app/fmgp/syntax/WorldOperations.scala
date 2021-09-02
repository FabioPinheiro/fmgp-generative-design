package app.fmgp.syntax

import app.fmgp.geo.{MyFile, Shape}

trait WorldOperations {
  def sendFile(file: MyFile): MyFile
  def sendShape[T <: Shape](t: T): T
  def clearShapes: Unit
}
