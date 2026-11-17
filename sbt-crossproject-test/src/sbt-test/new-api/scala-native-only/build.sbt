lazy val bar =
  crossProject(JVMPlatform, NativePlatform)
    .crossType(CrossType.Pure)
    .settings(scalaVersion := "2.12.21")

lazy val barJVM = bar.jvm
lazy val barNative = bar.native
