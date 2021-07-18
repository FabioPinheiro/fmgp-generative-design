package app.fmgp.meta

import scala.quoted.*

object MacroUtils {

  /** MyPosition
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
  case class MyPosition[T](
      start: Int,
      end: Int,
      sourceFile: String,
      startLine: Int,
      endLine: Int,
      startColumn: Int,
      endColumn: Int,
      sourceCode: Option[String],
      showExpr: String,
      value: T
  ) {
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
          | showExpr:    $showExpr
          | value:       $value
          |---------------------""".stripMargin
  }

  inline def getMyPosition[T](inline expr: T) =
    ${ getMyPositionImpl('expr) }

  def getMyPositionImpl[T](expr: Expr[T])(using Quotes, Type[T]): Expr[MyPosition[T]] = {
    import quotes.reflect.*

    val tree: Term = expr.asTerm
    //report.info("tree:" + tree.toString)
    // tree match
    //   case Inlined(_, _, Literal(BooleanConstant(n))) =>
    //     tree.asExprOf[Boolean]
    //   case _ =>
    //     report.info("Parameter must be a known boolean constant")
    //     '{ false }

    val pos = tree.pos
    //val pos = Position.ofMacroExpansion

    '{
      MyPosition(
        start = ${ Expr(pos.start) },
        end = ${ Expr(pos.end) },
        sourceFile = ${ Expr(pos.sourceFile.jpath.toString) },
        startLine = ${ Expr(pos.startLine) },
        endLine = ${ Expr(pos.endLine) },
        startColumn = ${ Expr(pos.startColumn) },
        endColumn = ${ Expr(pos.endColumn) },
        sourceCode = ${ Expr(pos.sourceCode) },
        showExpr = ${ showExpr(expr) },
        value = $expr
      )
    }
  }

  def showExpr[T](expr: Expr[T])(using Quotes): Expr[String] =
    val code: String = expr.show
    Expr(code)

}
