val g = "org.example.platform-specific"
val a = "bar"
val v = "0.1.0"

lazy val bar =
  crossProject(NativePlatform)
    .crossType(CrossType.Pure)
    .settings(
      scalaVersion := "2.12.17",
      organization := g,
      moduleName   := a,
      version      := v
    )

lazy val barNative = bar.native

lazy val foo =
  crossProject(JVMPlatform, NativePlatform)
    .settings(
      scalaVersion := "2.12.17"
    )
    .nativeSettings(
      libraryDependencies += g %%% a % v
    )

lazy val fooNative = foo.native
