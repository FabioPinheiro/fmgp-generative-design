package fmgp.experiments

object DslPure extends Dsl {
  def sideEffect(w: Dsl.WorldBox) = ()
}
object DslConsole extends Dsl {
  def sideEffect(w: Dsl.WorldBox) = println(w)
}
object DslJson extends Dsl { self =>
  import io.circe._, io.circe.syntax._
  import fmgp.geo.EncoderDecoder.{given}

  def sideEffect(w: Dsl.WorldBox) =
    val aux = fmgp.geo.ShapeSeq(w.v)
    println(aux.asJson)
}
