package sbtcrossproject

import scala.quoted.*

private[sbtcrossproject] object CrossProjectMacros {
  def crossProjectImpl(platforms: Expr[Seq[Platform]])(using
      Quotes): Expr[CrossProject.Builder] = {
    val name = enclosingValName
    '{
      CrossProject(${Expr(name)}, new java.io.File(${Expr(name)}))(
        $platforms: _*)
    }
  }

  def oldCrossProjectImpl(using Quotes): Expr[CrossProject.Builder] = {
    import quotes.reflect.*

    report.warning("use crossProject(JSPlatform, JVMPlatform)")

    val jsPlatform =
      Ref(Symbol.requiredModule("scalajscrossproject.JSPlatform"))
        .asExprOf[Platform]
    val jvmPlatform =
      Ref(Symbol.requiredModule("sbtcrossproject.JVMPlatform"))
        .asExprOf[Platform]

    crossProjectImpl('{Seq($jsPlatform, $jvmPlatform)})
  }

  private def enclosingValName(using Quotes): String = {
    import quotes.reflect.*

    val prefix = Position.ofMacroExpansion.sourceFile.content.mkString
      .take(Position.ofMacroExpansion.start)
    val trivia = """(?:\s|//[^\r\n]*(?:\r?\n|$)|/\*.*?\*/)*"""
    val assignment =
      ("""(?s).*\b(?:lazy\s+)?val\s+(?:`([^`]+)`|([^\s:`=]+))""" +
        trivia +
        """(?::[^=]*)?=""" +
        trivia +
        "$"
      ).r

    prefix match {
      case assignment(backtickedName, plainName) =>
        Option(backtickedName).getOrElse(plainName)
      case _ =>
        report.errorAndAbort(
          "crossProject must be directly assigned to a val, such as `val x = crossProject(...)`."
        )
    }
  }
}
