lazy val bar = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .settings(scalaVersion := "2.12.17")

lazy val foo = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .settings(scalaVersion := "2.12.17")
  .jsSettings(
    scalaJSUseMainModuleInitializer := true
  )
  .dependsOn(bar)

lazy val foobar = crossProject(JVMPlatform, NativePlatform)
  .settings(scalaVersion := "2.12.17")
  .dependsOn(bar % "test")
