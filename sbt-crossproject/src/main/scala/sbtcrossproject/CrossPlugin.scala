package sbtcrossproject

import scala.language.experimental.macros

import sbt._
import sbt.KeyRanks.BSetting

import scala.language.implicitConversions

import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object CrossPlugin extends AutoPlugin {
  override def trigger = allRequirements

  @deprecated("Use autoImport instead.", "0.5.0")
  val AutoImport = autoImport

  object autoImport {

    type CrossType = sbtcrossproject.CrossType
    val CrossType = sbtcrossproject.CrossType

    // The crossProject macro

    @deprecated("use crossProject(JSPlatform, JVMPlatform)", "0.1.0") def crossProject: CrossProject.Builder =
      macro CrossProjectMacros.oldCrossProject_impl

    def crossProject(platforms: Platform*): CrossProject.Builder =
      macro CrossProjectMacros.vargCrossProject_impl

    // Cross-classpath dependency builders

    final implicit def toCrossClasspathDependencyConstructor(
        cp: CrossProject): CrossClasspathDependency.Constructor =
      new CrossClasspathDependency.Constructor(cp)

    final implicit def toCrossClasspathDependency(
        cp: CrossProject): CrossClasspathDependency =
      new CrossClasspathDependency(cp, None)

    // The JVM platform

    val JVMPlatform = sbtcrossproject.JVMPlatform

    implicit def JVMCrossProjectBuilderOps(
        builder: CrossProject.Builder): JVMCrossProjectOps =
      new JVMCrossProjectOps(builder)

    implicit class JVMCrossProjectOps(project: CrossProject) {
      def jvm: Project = project.projects(JVMPlatform)

      def jvmSettings(ss: Def.SettingsDefinition*): CrossProject =
        jvmConfigure(_.settings(ss: _*))

      def jvmEnablePlugins(plugins: Plugins*): CrossProject =
        jvmConfigure(_.enablePlugins(plugins: _*))

      def jvmConfigure(transformer: Project => Project): CrossProject =
        project.configurePlatform(JVMPlatform)(transformer)
    }

    lazy val crossProjectPlatform =
      settingKey[Platform]("platform of the current project")

  }
}
