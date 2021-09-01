package app.fmgp.geo

import typings.statsJs.mod.{^ => Stats}

object StatsComponent {
  lazy val stats: Stats = {
    val tmp = new Stats()
    tmp.showPanel(0); // 0: fps, 1: ms, 2: mb, 3+: custom
    tmp.dom.style.right = "0px"
    tmp.dom.style.left = null
    org.scalajs.dom.document.body.appendChild(tmp.dom) //FIXME Not the best place ...
    tmp
  }
}
