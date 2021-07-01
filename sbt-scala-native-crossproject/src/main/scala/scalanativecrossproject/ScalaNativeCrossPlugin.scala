package scalanativecrossproject

import scala.language.implicitConversions

import sbt._
import sbtcrossproject._

object ScalaNativeCrossPlugin extends sbt.AutoPlugin {
  object autoImport {

    val NativePlatform = scalanativecrossproject.NativePlatform

    implicit def NativeCrossProjectBuilderOps(
        builder: CrossProject.Builder): NativeCrossProjectOps =
      new NativeCrossProjectOps(builder.crossType(CrossType.Full))

    implicit class NativeCrossProjectOps(project: CrossProject) {
      def native: Project = project.projects(NativePlatform)

      def nativeSettings(ss: Def.SettingsDefinition*): CrossProject =
        nativeConfigure(_.settings(ss: _*))

      def nativeEnablePlugins(plugins: Plugins*): CrossProject =
        nativeConfigure(_.enablePlugins(plugins: _*))

      def nativeConfigure(transformer: Project => Project): CrossProject =
        project.configurePlatform(NativePlatform)(transformer)
    }

  }
}
