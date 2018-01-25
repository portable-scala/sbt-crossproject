import sbtcrossproject.{crossProject, CrossType}

lazy val check = taskKey[Unit]("check settings are applied")

lazy val bar =
  crossProject(JSPlatform, JVMPlatform, NativePlatform)
    .withoutSuffixFor(JVMPlatform)
    .crossType(CrossType.Pure)
    .settings(
      scalaVersion := "2.11.11",
      description := "common settings"
    )

lazy val barJS     = bar.js
lazy val barJVM    = bar.jvm
lazy val barNative = bar.native

check := {
  def equals(actual: String, expected: String): Unit = {
    if (actual != expected) {
      assert(false, s"actual: $actual, expected: $expected")
    }
  }

  equals(barJS.id, "barJS")
  equals(barJVM.id, "bar")
  equals(barNative.id, "barNative")
}
