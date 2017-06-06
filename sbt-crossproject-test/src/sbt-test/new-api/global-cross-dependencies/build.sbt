import sbtcrossproject.{crossProject, CrossType}

val g = "org.example.cross-dependencies-global"
val a = "bar"
val v = "0.1.0"

lazy val bar = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .settings(
    scalaVersion := "2.11.11",
    organization := g,
    moduleName := a,
    version := v
  )

lazy val barJS     = bar.js
lazy val barJVM    = bar.jvm
lazy val barNative = bar.native

lazy val foo = project.settings(
  scalaVersion := "2.11.11",
  libraryDependencies += g %%% a % v
)
