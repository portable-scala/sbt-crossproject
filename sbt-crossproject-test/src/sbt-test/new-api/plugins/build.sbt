import sbtcrossproject.{crossProject, CrossType}

lazy val check = taskKey[Unit]("check settings are applied")

Global / checkedSettingSet := Set("global")

lazy val foo =
  crossProject(JSPlatform, JVMPlatform, NativePlatform)
    .crossType(CrossType.Pure)
    .settings(
      scalaVersion := "2.11.11"
    )
    .enablePlugins(PluginForAll)
    .jvmEnablePlugins(PluginForJVM)
    .jsEnablePlugins(PluginForJS)
    .nativeEnablePlugins(PluginForNative)
    .platformsEnablePlugins(JVMPlatform, JSPlatform)(PluginForJVMAndJS)

check := {
  def assertEquals(expected: Set[String], actual: Set[String]): Unit =
    assert(actual == expected, s"expected: $expected; but was: $actual")

  assertEquals(Set("global", "all", "jvm", "jvm and js"),
               (foo.jvm / checkedSettingSet).value)
  assertEquals(Set("global", "all", "js", "jvm and js"),
               (foo.js / checkedSettingSet).value)
  assertEquals(Set("global", "all", "native"),
               (foo.native / checkedSettingSet).value)
}
