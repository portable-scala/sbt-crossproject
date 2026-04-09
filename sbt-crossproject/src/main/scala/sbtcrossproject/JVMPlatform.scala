package sbtcrossproject

import sbt._

case object JVMPlatform extends Platform {
  def identifier: String                = "jvm"
  def sbtSuffix: String                 = "JVM"
  def enable(project: Project): Project = project
}
