package scalajscrossproject

import sbtcrossproject._
import sbt._
import org.scalajs.sbtplugin._

case object JSPlatform extends Platform {
  def identifier: String                = "js"
  def sbtSuffix: String                 = "JS"
  def enable(project: Project): Project = project.enablePlugins(ScalaJSPlugin)

  @deprecated("Will be removed", "0.3.0")
  val crossBinary: CrossVersion = ScalaJSCrossVersion.binary

  @deprecated("Will be removed", "0.3.0")
  val crossFull: CrossVersion = ScalaJSCrossVersion.full
}
