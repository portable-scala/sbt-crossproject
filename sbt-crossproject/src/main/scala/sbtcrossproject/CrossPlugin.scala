package sbtcrossproject

import sbt._

import scala.language.implicitConversions

object CrossPlugin extends AutoPlugin {
  override def trigger = allRequirements

  private[sbtcrossproject] def enclosingValError(methodName: String): String =
    s"""$methodName must be directly assigned to a val, such as `val x = $methodName`."""

  @deprecated("Use autoImport instead.", "0.5.0")
  val AutoImport = autoImport

  object autoImport extends CrossPluginCompat {

    type CrossType = sbtcrossproject.CrossType
    val CrossType = sbtcrossproject.CrossType

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

    lazy val crossProjectCrossType =
      settingKey[CrossType]("cross type of the current cross project")

    lazy val crossProjectBaseDirectory =
      settingKey[File]("base directory of the current cross project")
  }
}
