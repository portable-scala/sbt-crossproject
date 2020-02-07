scalaVersion in ThisBuild := "2.12.10"

lazy val bar =
  crossProject(JSPlatform, JVMPlatform)
    .crossType(CrossType.Pure)
    .in(file("."))
    .jsSettings(
      scalaJSUseMainModuleInitializer := true
    )
