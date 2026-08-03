ThisBuild / scalaVersion := "2.12.20"

lazy val bar =
  crossProject(JSPlatform, JVMPlatform)
    .crossType(CrossType.Pure)
    .in(file("."))
    .jsSettings(
      scalaJSUseMainModuleInitializer := true
    )
