import sbtcross.{crossProject, CrossType}

val g = "com.example.platform-specific"
val a = "bar"
val v = "0.1.0-SNAPSHOT"

lazy val bar =
  crossProject(NativePlatform)
    .crossType(CrossType.Pure)
    .settings(
      scalaVersion := "2.11.8",
      organization := g,
      moduleName := a,
      version := v
    )

lazy val barNative = bar.native

lazy val foo =
  crossProject(JVMPlatform, NativePlatform)
    .settings(
      scalaVersion := "2.11.8"
    )
    .nativeSettings(
      libraryDependencies += g %%% a % v
    )

lazy val fooNative = foo.native
