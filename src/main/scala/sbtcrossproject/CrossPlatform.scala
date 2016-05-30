package sbtcrossproject

import sbt._

trait CrossPlatform {
  def name: String
  def plugin: Option[AutoPlugin]
  override def toString = name
}

final object JVM extends CrossPlatform {
  val name: String = "jvm"
  val plugin: Option[AutoPlugin] = None
}
