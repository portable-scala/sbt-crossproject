package sbtcrossproject

import java.io.File
import scala.reflect.macros.Context
import scala.language.experimental.macros

object CrossProjectExtra {
  private[sbtcrossproject] def crossProject_impl(c: Context)(
      platformsArgs: List[c.Expr[Platform]]): c.Expr[CrossProject.Builder] = {
    import c.universe._

    val enclosingValName = MacroUtils.definingValName(
      c,
      methodName =>
        s"""$methodName must be directly assigned to a val, such as `val x = $methodName`.""")

    val name = Literal(Constant(enclosingValName))

    def javaIoFile =
      reify { new _root_.java.io.File(c.Expr[String](name).splice) }.tree

    val platforms =
      if (!platformsArgs.isEmpty) platformsArgs.map(_.tree).toList
      else {
        // compatibility
        val jsPlatform =
          Select(
            Select(
              Ident(newTermName("_root_")),
              newTermName("scalajscrossproject")
            ),
            newTermName("JSPlatform")
          )
        val jvmPlatform =
          Select(
            Select(
              Ident(newTermName("_root_")),
              newTermName("sbtcrossproject")
            ),
            newTermName("JVMPlatform")
          )

        List(jsPlatform, jvmPlatform)
      }

    val crossProjectCompanionTerm =
      Select(
        Select(
          Ident(newTermName("_root_")),
          newTermName("sbtcrossproject")
        ),
        newTermName("CrossProject")
      )

    val applyFun =
      Select(
        crossProjectCompanionTerm,
        newTermName("apply")
      )

    c.Expr[CrossProject.Builder](
      Apply(
        Apply(
          applyFun,
          List(name, javaIoFile)
        ),
        platforms
      ))
  }

  def oldCrossProject_impl(c: Context): c.Expr[CrossProject.Builder] = {
    c.warning(c.enclosingPosition, "use crossProject(JSPlatform, JVMPlatform)")
    crossProject_impl(c)(Nil)
  }

  def vargCrossProject_impl(c: Context)(
      platforms: c.Expr[Platform]*): c.Expr[CrossProject.Builder] = {
    import c.universe._
    crossProject_impl(c)(platforms.toList)
  }
}

trait CrossProjectExtra {
  @deprecated("use crossProject(JSPlatform, JVMPlatform)", "0.1.0") def crossProject: CrossProject.Builder =
    macro CrossProjectExtra.oldCrossProject_impl

  def crossProject(platforms: Platform*): CrossProject.Builder =
    macro CrossProjectExtra.vargCrossProject_impl

  private[sbtcrossproject] def nonEmpty(s: String, label: String): Unit =
    require(s.trim.length > 0, label + " cannot be empty.")
}
