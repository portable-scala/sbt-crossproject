val defaultSettings = Seq(
  scalaVersion := "2.11.8"
)

lazy val lib = 
  crossProject2.in(file("lib"))
    .settings(defaultSettings)
    .jsSettings(name := "lib js")
    .jvmSettings(name := "lib jvm")
    .nativeSettings(name := "lib native")

lazy val app = 
  crossProject2.in(file("app"))
    .settings(defaultSettings)
    .dependsOn(lib)
    .jsSettings(name := "app js")
    .jvmSettings(name := "app jvm")
    .nativeSettings(name := "app native")

lazy val appJs = app.js
lazy val appJVM = app.jvm
lazy val appNative = app.native