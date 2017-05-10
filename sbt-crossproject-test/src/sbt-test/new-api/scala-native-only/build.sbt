lazy val bar =
  crossProject(JVMPlatform, NativePlatform)
    .crossType(CrossType.Pure)
    .settings(scalaVersion := "2.11.11")

lazy val barJVM = bar.jvm
lazy val barNative = bar.native
