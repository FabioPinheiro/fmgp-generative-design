package app.fmgp.experiments

object DslPure extends Dsl {
  def sideEffect(w: Dsl.WorldBox) = ()
}
object DslConsole extends Dsl {
  def sideEffect(w: Dsl.WorldBox) = println(w)
}
object DslJson extends Dsl { self =>
  import io.circe._, io.circe.syntax._
  import app.fmgp.geo.EncoderDecoder.{given}

  def sideEffect(w: Dsl.WorldBox) =
    val aux = app.fmgp.geo.ShapeSeq(w.v)
    println(aux.asJson)
}
