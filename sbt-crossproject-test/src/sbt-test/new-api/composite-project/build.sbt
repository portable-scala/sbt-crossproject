lazy val bar = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .settings(scalaVersion := "2.12.21")

lazy val foo = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .settings(scalaVersion := "2.12.21")
  .jsSettings(
    scalaJSUseMainModuleInitializer := true
  )
  .dependsOn(bar)

lazy val foobar = crossProject(JVMPlatform, NativePlatform)
  .settings(scalaVersion := "2.12.21")
  .dependsOn(bar % "test")
