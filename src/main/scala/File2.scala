import io.circe.generic.auto._

val test = summon[io.circe.Decoder[AAA]]
