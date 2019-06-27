import sbtcrossproject.{crossProject, CrossType, Platform}

lazy val check = taskKey[Unit]("check")

def doCheck(platform: Platform, id: String) =
  assert(platform.identifier == id)

lazy val root =
  crossProject(JVMPlatform, NativePlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .jvmSettings(
      check := doCheck(crossProjectPlatform.value, "jvm")
    )
    .jsSettings(
      check := doCheck(crossProjectPlatform.value, "js")
    )
    .nativeSettings(
      check := doCheck(crossProjectPlatform.value, "native")
    )

lazy val rootJVM = root.jvm
lazy val rootJS = root.js
lazy val rootNative = root.native
