/* It is totally evil to introduce a class in the package of sbt-scalajs, but that is
 * the only way we can be forward source compatible with Scala.js 1.x.
 */
package org.scalajs.sbtplugin

import sbtcrossproject._

import sbt._

case object JSPlatform extends Platform {
  def identifier: String                = "js"
  def sbtSuffix: String                 = "JS"
  def enable(project: Project): Project = project.enablePlugins(ScalaJSPlugin)
  val crossBinary: CrossVersion         = ScalaJSCrossVersion.binary
  val crossFull: CrossVersion           = ScalaJSCrossVersion.full
}
