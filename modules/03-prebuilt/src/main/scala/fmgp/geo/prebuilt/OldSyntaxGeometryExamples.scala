package fmgp.geo.prebuilt

import fmgp.syntax.OldSyntax
import fmgp.geo.prebuilt.KhepriExamples
import fmgp.geo.Shape
import fmgp.geo.WorldAddition

object OldSyntaxGeometryExamples extends OldSyntax with KhepriExamples {

  var shapes: Seq[Shape] = Seq.empty

  def world(f: OldSyntaxGeometryExamples.type => Unit) = {
    f(this)
    val tmp = shapes
    clear
    WorldAddition(tmp)
  }

  override def addShape[T <: Shape](t: T, wireframeMode: Boolean): T = {
    shapes = shapes :+ t
    t
  }

  // override def sendFile(file: MyFile): MyFile = {
  //   println(s"file: $file")
  //   file
  // }

  override def clear: Unit = {
    println("Clear all Shapes!")
    shapes = Seq.empty
  }
}
