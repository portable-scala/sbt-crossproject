package sbtcross

import sbt._
import sbt.KeyRanks.BSetting
import StringUtilities.nonEmpty

import scala.language.implicitConversions

object CrossPlugin extends AutoPlugin {
  override def trigger = allRequirements

  val autoImport = AutoImport
  object AutoImport extends JVMCross with CrossProjectExtra {

    val CrossType = sbtcross.CrossType

    val crossPlatform = SettingKey[Platform](
      "crossPlatform",
      "Tells the current project's platform." +
        "Do not set the value of this setting (only use it as read-only).",
      BSetting)

    final implicit def toCrossGroupID(groupID: String): CrossGroupID = {
      nonEmpty(groupID, "Group ID")
      new CrossGroupID(groupID)
    }
  }
  import AutoImport._

  override def globalSettings: Seq[Setting[_]] = {
    super.globalSettings ++ Seq(
      crossPlatform := JVMPlatform
    )
  }
}
