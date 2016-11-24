package sbtcross

import sbt._

trait Platform {
  def identifier: String
  def sbtSuffix: String
  def enable(project: Project): Project
  val crossBinary: CrossVersion
  val crossFull: CrossVersion
}
