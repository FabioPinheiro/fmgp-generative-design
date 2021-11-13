package fmgp.meta

import MacroUtils._

class MetaTest extends munit.FunSuite {
  test("extract metadata") {
    val data = getMeta { val b = "asdasd"; val a = 0x999; a }
    println(data.sourceCode)
    assertNoDiff(
      data.sourceCode.getOrElse("NoString"),
      """{ val b = "asdasd"; val a = 0x999; a }"""
    )
  }
}
