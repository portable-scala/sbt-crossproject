package sbtcrossproject

import sbt._
import sbt.KeyRanks.BSetting

import scala.language.implicitConversions

import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object CrossPlugin extends AutoPlugin {
  override def trigger = allRequirements

  @deprecated("Use autoImport instead.", "0.5.0")
  val AutoImport = autoImport

  object autoImport extends JVMCross with CrossProjectExtra {

    val CrossType = sbtcrossproject.CrossType

    final implicit def toCrossClasspathDependencyConstructor(
        cp: CrossProject): CrossClasspathDependency.Constructor =
      new CrossClasspathDependency.Constructor(cp)

    final implicit def toCrossClasspathDependency(
        cp: CrossProject): CrossClasspathDependency =
      new CrossClasspathDependency(cp, None)
  }
}
