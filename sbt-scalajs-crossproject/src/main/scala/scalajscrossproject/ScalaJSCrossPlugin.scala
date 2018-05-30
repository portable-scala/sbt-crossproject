package scalajscrossproject

import org.scalajs.sbtplugin.{ScalaJSCrossVersion, ScalaJSPlugin}
import org.scalajs.sbtplugin.impl.ScalaJSGroupID

import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

import sbt._

import scala.language.implicitConversions

object ScalaJSCrossPlugin extends AutoPlugin {
  override def trigger           = allRequirements
  override def requires: Plugins = ScalaJSPlugin

  object autoImport extends ScalaJSCross

  import autoImport._

  override def projectSettings: Seq[Setting[_]] = Seq(
    platformDepsCrossVersion := ScalaJSCrossVersion.binary
  )
}
