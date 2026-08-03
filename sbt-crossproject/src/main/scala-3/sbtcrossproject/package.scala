import scala.quoted.*

package object sbtcrossproject {

  @deprecated("use crossProject(JSPlatform, JVMPlatform)", "0.1.0")
  inline def crossProject: CrossProject.Builder =
    ${CrossProjectMacros.oldCrossProjectImpl}

  @deprecated(
    "import sbtcrossproject.CrossPlugin.autoImport.crossProject instead",
    "0.5.0")
  inline def crossProject(inline platforms: Platform*): CrossProject.Builder =
    ${CrossProjectMacros.crossProjectImpl('platforms)}
}
