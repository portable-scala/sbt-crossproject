package sbtcrossproject

import sbt._

import scala.language.implicitConversions

case object JVMPlatform extends Platform {
  def identifier: String                = "jvm"
  def sbtSuffix: String                 = "JVM"
  def enable(project: Project): Project = project

  @deprecated("Will be removed", "0.3.0")
  val crossBinary: CrossVersion = CrossVersion.binary

  @deprecated("Will be removed", "0.3.0")
  val crossFull: CrossVersion = CrossVersion.full
}

trait JVMCross {
  val JVMPlatform = sbtcrossproject.JVMPlatform

  implicit def JVMCrossProjectBuilderOps(
      builder: CrossProject.Builder): JVMCrossProjectOps =
    new JVMCrossProjectOps(builder)

  implicit class JVMCrossProjectOps(project: CrossProject) {
    def jvm: Project = project.projects(JVMPlatform)

    def jvmSettings(ss: Def.SettingsDefinition*): CrossProject =
      jvmConfigure(_.settings(ss: _*))

    def jvmConfigure(transformer: Project => Project): CrossProject = {
      val jvmPlatforms = project.platforms.filter {
        case JVMPlatform                   => true
        case ScalaPlatform(JVMPlatform, _) => true
        case _                             => false
      }
      project.configurePlatforms(jvmPlatforms.toSeq: _*)(transformer)
    }
  }
}

case class ScalaPlatform(base: Platform, version: String) extends Platform {
  def identifier: String                = s"${base.identifier}-scala-$version"
  def sbtSuffix: String                 = s"${base.sbtSuffix}_${version.replace('.', '_')}"
  def enable(project: Project): Project = project

  @deprecated("Will be removed", "0.3.0")
  val crossBinary: CrossVersion = CrossVersion.binary

  @deprecated("Will be removed", "0.3.0")
  val crossFull: CrossVersion = CrossVersion.full
}

trait ScalaCross {

  implicit class JVMPlatformExt(platform: JVMPlatform.type) {
    def scala(version: String) = ScalaPlatform(platform, version)
  }

  implicit class ScalaCrossProjectOps(project: CrossProject) {
    def jvmScala(version: String): Project =
      project.projects(JVMPlatform.scala(version))

    def jvmScalaSettings(version: String)(
        ss: Def.SettingsDefinition*): CrossProject =
      jvmScalaConfigure(version)(_.settings(ss: _*))

    def jvmScalaConfigure(version: String)(
        transformer: Project => Project): CrossProject =
      project.configurePlatform(JVMPlatform.scala(version))(transformer)
  }

}
