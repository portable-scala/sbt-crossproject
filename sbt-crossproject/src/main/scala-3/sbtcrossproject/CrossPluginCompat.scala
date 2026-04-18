package sbtcrossproject

import java.io.File
import scala.quoted.Expr
import scala.quoted.Quotes
import scala.annotation.tailrec

trait CrossPluginCompat { self: CrossPlugin.autoImport.type =>
  inline def crossProject(platforms: Platform*): CrossProject.Builder =
    ${ CrossPluginCompat.crossProjectImpl('platforms) }
}

object CrossPluginCompat {
  def crossProjectImpl(platforms: Expr[Seq[Platform]])(using Quotes): Expr[CrossProject.Builder] = {
    val name = definingValName(
      CrossPlugin.enclosingValError("crossProject")
    )
    '{ CrossProject($name, new File($name))(${platforms}*) }
  }

  // https://github.com/sbt/sbt/blob/7218b2a1ac69230/main-settings/src/main/scala/sbt/std/KeyMacro.scala#L68-L86
  private def definingValName(errorMsg: String)(using q: Quotes): Expr[String] =
    val term = enclosingTerm
    if term.isValDef then Expr(term.name)
    else q.reflect.report.errorAndAbort(errorMsg)

  private def enclosingTerm(using qctx: Quotes) =
    import qctx.reflect.*
    @tailrec
    def enclosingTerm0(sym: Symbol): Symbol =
      sym match
        case sym if sym.flags.is(Flags.Macro)     => enclosingTerm0(sym.owner)
        case sym if sym.flags.is(Flags.Synthetic) => enclosingTerm0(sym.owner)
        case sym if !sym.isTerm                   => enclosingTerm0(sym.owner)
        case _                                    => sym
    enclosingTerm0(Symbol.spliceOwner)

}
