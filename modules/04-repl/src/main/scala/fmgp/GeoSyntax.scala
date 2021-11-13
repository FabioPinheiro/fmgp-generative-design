package fmgp

import fmgp.geo.{Wireframe, Shape}
import fmgp.geo.prebuilt._
import fmgp.syntax.OldSyntax

class GeoSyntax(server: MyAkkaServer) extends OldSyntax with KhepriExamples with RhythmicGymnasticsPavilionUtils {

  override def addShape[T <: Shape](t: T, wireframeMode: Boolean): T = {
    val s: Shape = if (wireframeMode) Wireframe(t) else t
    server.sendShape(s)
    t
  }

  override def clear: Unit = {
    server.clearShapes
    super.clear
  }
}
