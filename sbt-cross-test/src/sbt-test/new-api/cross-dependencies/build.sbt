import sbtcross.{crossProject, CrossType}

val g = "com.example.cross-dependencies"
val a = "bar"
val v = "0.1.0-SNAPSHOT"

lazy val bar = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .settings(
    scalaVersion := "2.11.8",
    organization := g,
    moduleName := a,
    version := v
  )
  .nativeSettings(resolvers += Resolver.sonatypeRepo("snapshots"))

lazy val barJS     = bar.js
lazy val barJVM    = bar.jvm
lazy val barNative = bar.native

lazy val foo = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .settings(
    scalaVersion := "2.11.8",
    libraryDependencies += g %%% a % v
  )
  .nativeSettings(resolvers += Resolver.sonatypeRepo("snapshots"))

lazy val fooJS = foo.js
lazy val fooJVM = foo.jvm
lazy val fooNative = foo.native
