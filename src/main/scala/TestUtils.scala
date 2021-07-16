import app.fmgp.{extensionMethod}

object TestUtils {
  inline def anInlineMethod: Unit = 1.extensionMethod
}
