package sbtcrossproject

import sbt._

private[sbtcrossproject] trait JVMPlatformBase {
  def enable(project: Project): Project = project
  val crossBinary: CrossVersion         = CrossVersion.binary
  val crossFull: CrossVersion           = CrossVersion.full
  def identifier: String = "jvm"
}

case object JVMPlatform extends Platform with JVMPlatformBase {
  def sbtSuffix: String  = "JVM"
}

case object JVMPlatformNoSuffix extends Platform with JVMPlatformBase {
  def sbtSuffix: String  = ""
}