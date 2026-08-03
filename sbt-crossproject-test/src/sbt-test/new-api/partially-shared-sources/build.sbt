import sbtcrossproject.{crossProject, CrossType}

lazy val foo = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .settings(scalaVersion := "2.12.20")
  .jsSettings(scalaJSUseMainModuleInitializer := true)
