package fmgp.geo

trait WorldOperations {
  def sendFile(file: MyFile): MyFile
  def sendShape[T <: Shape](t: T): T
  def clearShapes: Unit
}
