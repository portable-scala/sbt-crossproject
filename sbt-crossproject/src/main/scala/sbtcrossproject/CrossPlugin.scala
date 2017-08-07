package sbtcrossproject

import sbt._
import sbt.KeyRanks.BSetting

import scala.language.implicitConversions

object CrossPlugin extends AutoPlugin {
  override def trigger = allRequirements

  val autoImport = AutoImport
  object AutoImport extends JVMCross with CrossProjectExtra {

    val CrossType = sbtcrossproject.CrossType

    val crossPlatform = SettingKey[Platform](
      "crossPlatform",
      "Tells the current project's platform." +
        "Do not set the value of this setting (only use it as read-only).",
      BSetting)

    final implicit def toCrossGroupID(groupID: String): CrossGroupID = {
      nonEmpty(groupID, "Group ID")
      new CrossGroupID(groupID)
    }

    final implicit def toCrossClasspathDependencyConstructor(
        cp: CrossProject): CrossClasspathDependency.Constructor =
      new CrossClasspathDependency.Constructor(cp)

    final implicit def toCrossClasspathDependency(
        cp: CrossProject): CrossClasspathDependency =
      new CrossClasspathDependency(cp, None)
  }
  import AutoImport._

  override def globalSettings: Seq[Setting[_]] = {
    super.globalSettings ++ Seq(
      crossPlatform := JVMPlatform
    )
  }
}
