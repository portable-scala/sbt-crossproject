package scalanativecrossproject

import sbt._, Keys._
import scalanative.sbtplugin._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

import scala.language.implicitConversions

object ScalaNativeCrossPlugin extends AutoPlugin {
  override def trigger           = allRequirements
  override def requires: Plugins = ScalaNativePlugin

  object autoImport extends ScalaNativeCross
}
