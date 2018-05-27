package sbtcrossproject

import sbt._

trait Platform {
  def identifier: String
  def sbtSuffix: String
  def enable(project: Project): Project
}
