package app.fmgp.meta

import scala.quoted.*

object MacroUtils {

  given Conversion[MetaValue[app.fmgp.geo.Shape], app.fmgp.geo.Shape] = _.value

  /** Meta
    *
    * @param start
    *   The start offset in the source file
    * @param end
    *   The end offset in the source file
    * @param sourceFile
    *   Source file in which this position is located
    * @param startLine
    *   The start line in the source file
    * @param endLine
    *   The end line in the source file
    * @param startColumn
    *   The start column in the source file
    * @param endColumn
    *   The end column in the source file
    * @param sourceCode
    *   Source code within the position
    */
  sealed trait MetaBase(
      start: Int,
      end: Int,
      val sourceFile: String,
      startLine: Int,
      endLine: Int,
      startColumn: Int,
      endColumn: Int,
      sourceCode: Option[String],
  )

  case class MetaValue[+T](
      start: Int,
      end: Int,
      override val sourceFile: String,
      startLine: Int,
      endLine: Int,
      startColumn: Int,
      endColumn: Int,
      sourceCode: Option[String],
      //showExpr: String,
      value: T
  ) extends MetaBase(start, end, sourceFile, startLine, endLine, startColumn, endColumn, sourceCode) {
    def prettyPrint =
      s"""#####################
          | sourceFile:  $sourceFile
          | start:       $start
          | end:         $end
          | startLine:   $startLine
          | endLine:     $endLine
          | startColumn: $startColumn
          | endColumn:   $endColumn
          | sourceCode:  $sourceCode
          | value:       $value
          |---------------------""".stripMargin
  }

  case class Meta(
      start: Int,
      end: Int,
      override val sourceFile: String,
      startLine: Int,
      endLine: Int,
      startColumn: Int,
      endColumn: Int,
      sourceCode: Option[String],
  ) extends MetaBase(start, end, sourceFile, startLine, endLine, startColumn, endColumn, sourceCode) {
    def withValue[T](value: T): MetaValue[T] =
      MetaValue(start, end, sourceFile, startLine, endLine, startColumn, endColumn, sourceCode, value)

    def prettyPrint =
      s"""#####################
          | sourceFile:  $sourceFile
          | start:       $start
          | end:         $end
          | startLine:   $startLine
          | endLine:     $endLine
          | startColumn: $startColumn
          | endColumn:   $endColumn
          | sourceCode:  $sourceCode
          |---------------------""".stripMargin
  }

  inline def getMeta[T](inline expr: T) =
    ${ getMetaImpl('expr) }

  def getMetaImpl[T](expr: Expr[T])(using Quotes, Type[T]): Expr[MetaValue[T]] = {
    import quotes.reflect.*

    val tree: Term = expr.asTerm
    //report.info("tree:" + tree.toString)
    // tree match
    //   case Inlined(_, _, Literal(BooleanConstant(n))) =>
    //     tree.asExprOf[Boolean]
    //   case _ =>
    //     report.info("Parameter must be a known boolean constant")
    //     '{ false }

    val pos: Position = tree.pos //Position.ofMacroExpansion

    '{
      MetaValue(
        start = ${ Expr(pos.start) },
        end = ${ Expr(pos.end) },
        sourceFile = ${ Expr(pos.sourceFile.getJPath.toString) },
        startLine = ${ Expr(pos.startLine) },
        endLine = ${ Expr(pos.endLine) },
        startColumn = ${ Expr(pos.startColumn) },
        endColumn = ${ Expr(pos.endColumn) },
        sourceCode = ${ Expr(pos.sourceCode) },
        value = $expr
      )
    }
  }

  def showExpr[T](expr: Expr[T])(using Quotes): Expr[String] =
    val code: String = expr.show
    Expr(code)

  def getMetaImpl()(using Quotes): Expr[Meta] = {
    val pos = quotes.reflect.Position.ofMacroExpansion
    '{
      Meta(
        start = ${ Expr(pos.start) },
        end = ${ Expr(pos.end) },
        sourceFile = ${ Expr(pos.sourceFile.getJPath.toString) },
        startLine = ${ Expr(pos.startLine) },
        endLine = ${ Expr(pos.endLine) },
        startColumn = ${ Expr(pos.startColumn) },
        endColumn = ${ Expr(pos.endColumn) },
        sourceCode = ${ Expr(pos.sourceCode) },
      )
    }
  }

}
