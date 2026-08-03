package sbtcrossproject

import sbt._

import scala.quoted.*

object CrossPlugin extends AutoPlugin {
  override def trigger = allRequirements

  @deprecated("Use autoImport instead.", "0.5.0")
  val AutoImport = autoImport

  object autoImport {

    type CrossType = sbtcrossproject.CrossType
    val CrossType = sbtcrossproject.CrossType

    implicit final class PlatformDepsGroupIDOps(private val groupID: String)
        extends AnyVal {
      def %%%(artifactID: String) = groupID %% artifactID
    }

    inline def crossProject: CrossProject.Builder =
      ${CrossProjectMacros.oldCrossProjectImpl}

    inline def crossProject(inline platforms: Platform*): CrossProject.Builder =
      ${CrossProjectMacros.crossProjectImpl('platforms)}

    final implicit def toCrossClasspathDependencyConstructor(
        cp: CrossProject): CrossClasspathDependency.Constructor =
      new CrossClasspathDependency.Constructor(cp)

    final implicit def toCrossClasspathDependency(
        cp: CrossProject): CrossClasspathDependency =
      new CrossClasspathDependency(cp, None)

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
