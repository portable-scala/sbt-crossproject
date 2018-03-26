package scalanativecrossproject

import sbt._
import scalanative.sbtplugin._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

import scala.language.implicitConversions

object ScalaNativeCrossPlugin extends AutoPlugin {
  override def trigger           = allRequirements
  override def requires: Plugins = ScalaNativePlugin

  object autoImport extends ScalaNativeCross

  import autoImport._

  override def projectSettings: Seq[Setting[_]] = Seq(
    platformDepsCrossVersion := ScalaNativeCrossVersion.binary
  )
}
