package scalajscross

import sbtcross._

import sbt._

import org.scalajs.sbtplugin.{ScalaJSCrossVersion, ScalaJSPlugin}

case object JSPlatform extends Platform {
  def identifier: String                = "js"
  def sbtSuffix: String                 = "JS"
  def enable(project: Project): Project = project.enablePlugins(ScalaJSPlugin)
  val crossBinary: CrossVersion         = ScalaJSCrossVersion.binary
  val crossFull: CrossVersion           = ScalaJSCrossVersion.full
}
