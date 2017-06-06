import sbtcrossproject.{crossProject, CrossType}

val g = "org.example.platform-specific"
val a = "bar"
val v = "0.1.0"

lazy val bar =
  crossProject(NativePlatform)
    .crossType(CrossType.Pure)
    .settings(
      scalaVersion := "2.11.11",
      organization := g,
      moduleName := a,
      version := v
    )

lazy val barNative = bar.native

lazy val foo =
  crossProject(JVMPlatform, NativePlatform)
    .settings(
      scalaVersion := "2.11.11"
    )
    .nativeSettings(
      libraryDependencies += g %%% a % v,
      resolvers += Resolver.sonatypeRepo("snapshots")
    )

lazy val fooNative = foo.native
