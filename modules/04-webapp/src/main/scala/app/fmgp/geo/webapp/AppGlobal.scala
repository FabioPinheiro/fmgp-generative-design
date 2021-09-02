package app.fmgp.geo.webapp

import com.raquo.laminar.api.L._
//import com.raquo.airstream.state.Var
import app.fmgp.geo.World

object AppGlobal {
  val worldVar = Var[World](initial = World.w3DEmpty)
}
