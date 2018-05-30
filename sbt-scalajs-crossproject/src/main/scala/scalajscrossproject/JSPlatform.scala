package scalajscrossproject

import sbtcrossproject._
import sbt._
import org.scalajs.sbtplugin._

case object JSPlatform extends Platform {
  def identifier: String                = "js"
  def sbtSuffix: String                 = "JS"
  def enable(project: Project): Project = project.enablePlugins(ScalaJSPlugin)
}
