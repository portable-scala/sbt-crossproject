import Extra._

val sbtPluginSettings = ScriptedPlugin.scriptedSettings ++ Seq(
    organization := "org.scala-native",
    version := "0.1.0-SNAPSHOT",
    sbtPlugin := true,
    scalaVersion := "2.10.6",
    scriptedLaunchOpts += "-Dplugin.version=" + version.value,
    scalacOptions ++= Seq(
      "-deprecation",
      "-unchecked",
      "-feature",
      "-encoding",
      "utf8"
    )
  )

lazy val `sbt-crossproject-root` =
  project
    .in(file("."))
    .aggregate(`sbt-scalajs-crossproject`, `sbt-crossproject`, `sbt-crossproject-test`)
    .dependsOn(`sbt-scalajs-crossproject`, `sbt-crossproject`, `sbt-crossproject-test`)
    .settings(noPublishSettings)

lazy val `sbt-scalajs-crossproject` =
  project
    .in(file("sbt-scalajs-crossproject"))
    .settings(sbtPluginSettings)
    .settings(
      moduleName := "sbt-scalajs-crossproject",
      addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.13")
    )
    .settings(publishSettings)
    .dependsOn(`sbt-crossproject`)

lazy val `sbt-crossproject` =
  project
    .in(file("sbt-crossproject"))
    .settings(moduleName := "sbt-crossproject")
    .settings(sbtPluginSettings)
    .settings(scaladocFromReadme)
    .settings(publishSettings)

lazy val `sbt-crossproject-test` =
  project
    .in(file("sbt-crossproject-test"))
    .settings(sbtPluginSettings)
    .settings(noPublishSettings)
    .settings(
      scripted := scripted
        .dependsOn(
          publishLocal in `sbt-crossproject`,
          publishLocal in `sbt-scalajs-crossproject`
        )
        .evaluated
    )
    .settings(duplicateProjectFolders)
