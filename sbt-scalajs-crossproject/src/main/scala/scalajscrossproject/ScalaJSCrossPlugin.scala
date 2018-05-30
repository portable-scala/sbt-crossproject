package scalajscrossproject

import scala.language.implicitConversions

import sbt._
import sbtcrossproject._

import org.scalajs.sbtplugin.ScalaJSPlugin

object ScalaJSCrossPlugin extends AutoPlugin {
  override def trigger           = allRequirements
  override def requires: Plugins = ScalaJSPlugin

  object autoImport {

    val JSPlatform = scalajscrossproject.JSPlatform

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
}
