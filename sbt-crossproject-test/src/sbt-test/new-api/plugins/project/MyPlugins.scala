package myplugins

import sbt._
import sbt.Keys._

object PluginKeys extends AutoPlugin {
  object autoImport {
    lazy val checkedSettingSet =
      Def.settingKey[Set[String]]("checked setting set")
  }
}

import PluginKeys.autoImport._

object PluginForJVM extends AutoPlugin {
  override def projectSettings: Seq[Setting[_]] = Def.settings(
    checkedSettingSet += "jvm"
  )
}

object PluginForJS extends AutoPlugin {
  override def projectSettings: Seq[Setting[_]] = Def.settings(
    checkedSettingSet += "js"
  )
}

object PluginForNative extends AutoPlugin {
  override def projectSettings: Seq[Setting[_]] = Def.settings(
    checkedSettingSet += "native"
  )
}

object PluginForJVMAndJS extends AutoPlugin {
  override def projectSettings: Seq[Setting[_]] = Def.settings(
    checkedSettingSet += "jvm and js"
  )
}

object PluginForAll extends AutoPlugin {
  override def projectSettings: Seq[Setting[_]] = Def.settings(
    checkedSettingSet += "all"
  )
}
