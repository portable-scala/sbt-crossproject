lazy val foo = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .settings(scalaVersion := "2.12.21")
  .jsSettings(scalaJSUseMainModuleInitializer := true)
