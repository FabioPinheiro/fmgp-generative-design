package app.fmgp.meta

import MacroUtils._
@main def MainMeta() = {
  //inline def a = true
  val mp = getMyPosition {
    val b = "asdasd"
    val a = 0x999
    a
  }

  println(mp.prettyPrint)
}

/*
controller/runMain app.fmgp.meta.MainMeta
#####################
 sourceFile:  /home/fabio/workspace/fmgp-threejs/modules/03-controller/src/main/scala/app/fmgp/meta/MainMeta.scala
 start:       116
 end:         166
 startLine:   5
 endLine:     9
 startColumn: 25
 endColumn:   3
 sourceCode:  Some({
    val b = "asdasd"
    val a = 0x999
    a
  })
 showExpr:    {
  val b: java.lang.String = "asdasd"
  val a: scala.Int = 2457

  (a: scala.Int)
}
 value:       2457
---------------------
 */
