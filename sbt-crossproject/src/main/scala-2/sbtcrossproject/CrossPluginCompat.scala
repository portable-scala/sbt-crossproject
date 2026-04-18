package sbtcrossproject

import scala.language.experimental.macros

trait CrossPluginCompat { self: CrossPlugin.autoImport.type =>
  @deprecated("use crossProject(JSPlatform, JVMPlatform)", "0.1.0") def crossProject: CrossProject.Builder =
    macro CrossProjectMacros.oldCrossProject_impl

  def crossProject(platforms: Platform*): CrossProject.Builder =
    macro CrossProjectMacros.vargCrossProject_impl
}
