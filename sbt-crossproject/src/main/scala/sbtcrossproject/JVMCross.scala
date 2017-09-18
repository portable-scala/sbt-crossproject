package sbtcrossproject

import sbt._

import scala.language.implicitConversions

trait JVMCross {
  val JVMPlatform = sbtcrossproject.JVMPlatform

  implicit def JVMCrossProjectBuilderOps(
      builder: CrossProject.Builder): JVMCrossProjectOps =
    new JVMCrossProjectOps(builder)

  implicit class JVMCrossProjectOps(project: CrossProject) {
    def jvm: Project = {
      (project.projects.get(JVMPlatform),
       project.projects.get(JVMPlatformNoSuffix)) match {

        case (Some(project), _) => project

        case (_, Some(project)) => project

        case (Some(_), Some(_)) =>
          sys.error("use JVMPlatform or JVMPlatformNoSuffix but not both")

        case (None, None) =>
          sys.error(s"$project does not contain JVMPlatform")
      }
    }

    def jvmSettings(ss: Def.SettingsDefinition*): CrossProject =
      jvmConfigure(_.settings(ss: _*))

    def jvmConfigure(transformer: Project => Project): CrossProject = {
      project.configurePlatform(JVMPlatform, JVMPlatformNoSuffix)(transformer)
    }
  }
}
