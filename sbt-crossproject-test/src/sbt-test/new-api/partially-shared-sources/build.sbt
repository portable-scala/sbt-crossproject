import sbtcrossproject.{crossProject, CrossType}

lazy val foo = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .settings(scalaVersion := "2.11.11")
  .jsSettings(scalaJSUseMainModuleInitializer := true)
