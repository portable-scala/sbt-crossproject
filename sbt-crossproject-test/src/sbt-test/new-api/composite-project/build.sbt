import sbtcrossproject.{crossProject, CrossType}

lazy val bar = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .settings(scalaVersion := "2.11.11")

lazy val foo = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .settings(scalaVersion := "2.11.11")
  .jsSettings(
    scalaJSUseMainModuleInitializer := true
  )
  .dependsOn(bar)

lazy val foobar = crossProject(JVMPlatform, NativePlatform)
  .settings(scalaVersion := "2.11.11")
  .dependsOn(bar % "test")
