package scalanativecrossproject

import sbt._
import sbtcrossproject._
import scalanative.sbtplugin._

case object NativePlatform extends Platform {
  def identifier: String = "native"
  def sbtSuffix: String  = "Native"
  def enable(project: Project): Project =
    project.enablePlugins(ScalaNativePlugin)
  val crossBinary: CrossVersion = ScalaNativeCrossVersion.binary
  val crossFull: CrossVersion   = ScalaNativeCrossVersion.full
}
