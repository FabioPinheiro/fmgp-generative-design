package fmgp

object proto {
  object Dimensions {
    trait D
    object D2 extends D
    object D3 extends D
    case class Unrecognized(i: Int) extends D
  }
}
