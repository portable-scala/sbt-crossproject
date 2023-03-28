import sbtcrossproject.{crossProject, CrossType, Platform}

lazy val check = taskKey[Unit]("check")

def doCheckPlatform(platform: Platform, id: String) =
  assert(platform.identifier == id)

def doCheckType(got: CrossType, expected: CrossType) =
  assert(got == expected)

lazy val root =
  crossProject(JVMPlatform, NativePlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .jvmSettings(
      check := {
        doCheckPlatform(crossProjectPlatform.value, "jvm")
        doCheckType(crossProjectCrossType.value, CrossType.Pure)
      }
    )
    .jsSettings(
      check := {
        doCheckPlatform(crossProjectPlatform.value, "js")
        doCheckType(crossProjectCrossType.value, CrossType.Pure)
      }
    )
    .nativeSettings(
      check := {
        doCheckPlatform(crossProjectPlatform.value, "native")
        doCheckType(crossProjectCrossType.value, CrossType.Pure)
      }
    )

lazy val rootJVM    = root.jvm
lazy val rootJS     = root.js
lazy val rootNative = root.native

lazy val fullCross =
  crossProject(JVMPlatform, NativePlatform, JSPlatform)
    .crossType(CrossType.Full)
    .jvmSettings(
      check := {
        doCheckPlatform(crossProjectPlatform.value, "jvm")
        doCheckType(crossProjectCrossType.value, CrossType.Full)
      }
    )
    .jsSettings(
      check := {
        doCheckPlatform(crossProjectPlatform.value, "js")
        doCheckType(crossProjectCrossType.value, CrossType.Full)
      }
    )
    .nativeSettings(
      check := {
        doCheckPlatform(crossProjectPlatform.value, "native")
        doCheckType(crossProjectCrossType.value, CrossType.Full)
      }
    )
