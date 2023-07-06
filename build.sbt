import Extra._

inThisBuild(
  Def.settings(
    scalaVersion := "2.12.10",
    scalacOptions ++= Seq(
      "-deprecation",
      "-unchecked",
      "-feature",
      "-encoding",
      "utf8"
    ),
    organization := "org.portable-scala",
    versionScheme := Some("semver-spec"),
    homepage := Some(
      url("https://github.com/portable-scala/sbt-crossproject")),
    licenses := Seq(
      "BSD-like" -> url("http://www.scala-lang.org/downloads/license.html")
    ),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/portable-scala/sbt-crossproject"),
        "scm:git:git@github.com:portable-scala/sbt-crossproject.git",
        Some("scm:git:git@github.com:portable-scala/sbt-crossproject.git")
      )
    )
  ))

lazy val `sbt-crossproject-root` =
  project
    .in(file("."))
    .aggregate(`sbt-scalajs-crossproject`,
               `sbt-scala-native-crossproject`,
               `sbt-crossproject`,
               `sbt-crossproject-test`)
    .dependsOn(`sbt-scalajs-crossproject`,
               `sbt-scala-native-crossproject`,
               `sbt-crossproject`,
               `sbt-crossproject-test`)
    .settings(noPublishSettings)

lazy val `sbt-scalajs-crossproject` =
  project
    .in(file("sbt-scalajs-crossproject"))
    .enablePlugins(SbtPlugin)
    .settings(sbtPluginSettings)
    .settings(
      moduleName := "sbt-scalajs-crossproject",
      addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.23")
    )
    .settings(publishSettings)
    .dependsOn(`sbt-crossproject`)

lazy val `sbt-scala-native-crossproject` =
  project
    .in(file("sbt-scala-native-crossproject"))
    .enablePlugins(SbtPlugin)
    .settings(sbtPluginSettings)
    .settings(
      moduleName := "sbt-scala-native-crossproject",
      addSbtPlugin("org.scala-native" % "sbt-scala-native" % "0.3.7")
    )
    .settings(publishSettings)
    .dependsOn(`sbt-crossproject`)

lazy val `sbt-crossproject` =
  project
    .in(file("sbt-crossproject"))
    .enablePlugins(SbtPlugin)
    .settings(moduleName := "sbt-crossproject")
    .settings(sbtPluginSettings)
    .settings(scaladocFromReadme)
    .settings(publishSettings)
    .settings(
      addSbtPlugin("org.portable-scala" % "sbt-platform-deps" % "1.0.2")
    )

lazy val `sbt-crossproject-test` =
  project
    .in(file("sbt-crossproject-test"))
    .enablePlugins(SbtPlugin) // for scripted
    .settings(sbtPluginSettings)
    .settings(noPublishSettings)
    .settings(
      scriptedLaunchOpts ++= Seq(
        "-Dplugin.version=" + version.value,
        "-Dplugin.sn-version=0.3.7",
        "-Dplugin.sjs-version=0.6.23"
      ),
      scripted := scripted
        .dependsOn(
          `sbt-crossproject` / publishLocal,
          `sbt-scalajs-crossproject` / publishLocal,
          `sbt-scala-native-crossproject` / publishLocal
        )
        .evaluated
    )
    .settings(duplicateProjectFolders)
