val g = "org.example.cross-dependencies"
val a = "bar"
val v = "0.1.0"

val platformDependency = Def.setting {
  val dependency = g %% a % v
  if (sbtVersion.value.startsWith("1.")) dependency.cross(crossVersion.value)
  else dependency
}

lazy val bar = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .settings(
    scalaVersion := "2.12.21",
    organization := g,
    moduleName := a,
    version := v
  )

lazy val barJS     = bar.js
lazy val barJVM    = bar.jvm
lazy val barNative = bar.native

lazy val foo = crossProject(JSPlatform, JVMPlatform, NativePlatform).settings(
  scalaVersion := "2.12.21",
  libraryDependencies += platformDependency.value
)

lazy val fooJS = foo.js
lazy val fooJVM = foo.jvm
lazy val fooNative = foo.native
