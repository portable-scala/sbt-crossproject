val g = "org.example.platform-specific"
val a = "bar"
val v = "0.1.0"

val platformDependency = Def.setting {
  val dependency = g %% a % v
  if (sbtVersion.value.startsWith("1.")) dependency.cross(crossVersion.value)
  else dependency
}

lazy val bar =
  crossProject(NativePlatform)
    .crossType(CrossType.Pure)
    .settings(
      scalaVersion := "2.12.21",
      organization := g,
      moduleName := a,
      version := v
    )

lazy val barNative = bar.native

lazy val foo =
  crossProject(JVMPlatform, NativePlatform)
    .settings(
      scalaVersion := "2.12.21"
    )
    .nativeSettings(
      libraryDependencies += platformDependency.value
    )

lazy val fooNative = foo.native
