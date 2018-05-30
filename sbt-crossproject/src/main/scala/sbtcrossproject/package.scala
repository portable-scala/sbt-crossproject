import scala.language.experimental.macros

package object sbtcrossproject {

  @deprecated("use crossProject(JSPlatform, JVMPlatform)", "0.1.0") def crossProject: CrossProject.Builder =
    macro CrossProjectMacros.oldCrossProject_impl

  @deprecated(
    "import sbtcrossproject.CrossPlugin.autoImport.crossProject instead",
    "0.5.0") def crossProject(platforms: Platform*): CrossProject.Builder =
    macro CrossProjectMacros.vargCrossProject_impl

}
