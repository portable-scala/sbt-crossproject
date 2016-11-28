import sbtcross.{crossProject, CrossType}

lazy val bar = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .settings(scalaVersion := "2.11.8")

lazy val barJS  = bar.js
lazy val barJVM = bar.jvm

lazy val buzz = crossProject(JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .settings(scalaVersion := "2.11.8")
  .nativeSettings(resolvers += Resolver.sonatypeRepo("snapshots"))

lazy val buzzJVM    = buzz.jvm
lazy val buzzNative = buzz.native

lazy val foo = crossProject(JSPlatform, NativePlatform)
  .settings(scalaVersion := "2.11.8")
  .aggregate(bar, buzz)
  .nativeSettings(resolvers += Resolver.sonatypeRepo("snapshots"))

lazy val fooJS = foo.js
lazy val fooNative = foo.native
