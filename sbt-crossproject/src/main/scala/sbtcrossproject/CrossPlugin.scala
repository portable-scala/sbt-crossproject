package sbtcrossproject

import sbt._
import sbt.KeyRanks.BSetting

import scala.language.implicitConversions

import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object CrossPlugin extends AutoPlugin {
  override def trigger = allRequirements

  val autoImport = AutoImport
  object AutoImport extends JVMCross with CrossProjectExtra {

    val CrossType = sbtcrossproject.CrossType

    // Non-deprecated version for internal use
    private[CrossPlugin] val crossPlatformInternal = SettingKey[Platform](
      "crossPlatform",
      "Tells the current project's platform." +
        "Do not set the value of this setting (only use it as read-only).",
      BSetting)

    @deprecated("Use platformDepsCrossVersion instead", "0.3.0")
    val crossPlatform = crossPlatformInternal

    @deprecated("Kept for binary compatibility; will be removed", "0.3.0")
    final def toCrossGroupID(groupID: String): CrossGroupID = {
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
      crossPlatformInternal := JVMPlatform
    )
  }

  /* Compatibility for Scala Native 0.3.x and Scala.js 1.0.0-M1, which set
   * crossPlatform instead of platformDepsCrossVersion.
   */
  override def projectSettings: Seq[Setting[_]] = {
    super.projectSettings ++ Seq(
      platformDepsCrossVersion := {
        val prev     = platformDepsCrossVersion.value
        val platform = crossPlatformInternal.value
        if (platform != JVMPlatform)
          platform.crossBinary
        else
          prev
      }
    )
  }
}
