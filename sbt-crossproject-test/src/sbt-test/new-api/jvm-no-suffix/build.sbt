import sbtcrossproject.crossProject

lazy val bar =
  crossProject(JVMPlatformNoSuffix, JsPlatform)
    .crossType(CrossType.Pure)
    .settings(scalaVersion := "2.11.8")

lazy val barJVM = bar.jvm
lazy val barJS = bar.js
