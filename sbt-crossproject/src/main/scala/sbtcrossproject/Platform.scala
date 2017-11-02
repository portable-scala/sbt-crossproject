package sbtcrossproject

import sbt._

trait Platform {
  def identifier: String
  def sbtSuffix: String
  def enable(project: Project): Project

  @deprecated("Will be removed", "0.3.0")
  val crossBinary: CrossVersion

  @deprecated("Will be removed", "0.3.0")
  val crossFull: CrossVersion
}
