lazy val app = 
  crossProject2.in(file("."))
  .settings(scalaVersion := "2.11.8")
  .jsSettings(name := "app js")
  .jvmSettings(name := "app jvm")
  .nativeSettings(name := "app native")

lazy val appJs = app.js
lazy val appJVM = app.jvm
lazy val appNative = app.native