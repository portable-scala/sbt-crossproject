package sbtcrossproject

import sbt._

import scala.language.implicitConversions

case object JVMPlatform extends Platform {
  def identifier: String                = "jvm"
  def sbtSuffix: String                 = "JVM"
  def enable(project: Project): Project = project
  val crossBinary: CrossVersion         = CrossVersion.binary
  val crossFull: CrossVersion           = CrossVersion.full
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

    def jvmConfigure(transformer: Project => Project): CrossProject =
      project.configurePlatform(JVMPlatform)(transformer)
  }
}
