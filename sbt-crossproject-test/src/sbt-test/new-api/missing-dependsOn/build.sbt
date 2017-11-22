import sbtcrossproject.{crossProject, CrossType}

lazy val bar = crossProject(JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .settings(scalaVersion := "2.11.11")

lazy val barJVM    = bar.jvm
lazy val barNative = bar.native

lazy val foo = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .settings(scalaVersion := "2.11.11")
  .dependsOn(bar)

lazy val fooJS     = foo.js
lazy val fooJVM    = foo.jvm
lazy val fooNative = foo.native

lazy val foobar = crossProject(JVMPlatform, NativePlatform)
  .settings(scalaVersion := "2.11.11")
  .dependsOn(bar % "test")

lazy val foobarJVM = foobar.jvm
lazy val foobarNative = foobar.native
