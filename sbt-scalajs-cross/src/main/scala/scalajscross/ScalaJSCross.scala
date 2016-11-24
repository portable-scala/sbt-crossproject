package scalajscross

import sbtcross._

import sbt._

import org.scalajs.sbtplugin.ScalaJSPlugin

import scala.language.implicitConversions

trait ScalaJSCross {
  val JSPlatform = scalajscross.JSPlatform

  implicit def JSCrossProjectBuilderOps(
      builder: CrossProject.Builder): JSCrossProjectOps =
    new JSCrossProjectOps(builder.crossType(CrossType.Full))

  implicit class JSCrossProjectOps(project: CrossProject) {
    def js: Project = project.projects(JSPlatform)

    def jsSettings(ss: Def.SettingsDefinition*): CrossProject =
      jsConfigure(_.settings(ss: _*))

    def jsConfigure(transformer: Project => Project): CrossProject =
      project.configurePlatform(JSPlatform)(transformer)
  }
}
