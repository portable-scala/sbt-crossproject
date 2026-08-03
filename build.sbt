import Extra._

inThisBuild(
  Def.settings(
    scalaVersion := "2.12.20",
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
    .settings(sbt2CrossPluginSettings)
    .settings(noPublishSettings)

lazy val `sbt-scalajs-crossproject` =
  project
    .in(file("sbt-scalajs-crossproject"))
    .enablePlugins(SbtPlugin)
    .settings(sbt2CrossPluginSettings)
    .settings(
      moduleName := "sbt-scalajs-crossproject",
      libraryDependencies += Defaults.sbtPluginExtra(
        "org.scala-js" % "sbt-scalajs" % "1.22.0",
        (pluginCrossBuild / sbtBinaryVersion).value,
        scalaBinaryVersion.value
      )
    )
    .settings(publishSettings)
    .dependsOn(`sbt-crossproject`)

lazy val `sbt-scala-native-crossproject` =
  project
    .in(file("sbt-scala-native-crossproject"))
    .enablePlugins(SbtPlugin)
    .settings(sbt2CrossPluginSettings)
    .settings(
      moduleName := "sbt-scala-native-crossproject",
      libraryDependencies += Defaults.sbtPluginExtra(
        "org.scala-native" % "sbt-scala-native" % "0.5.12",
        (pluginCrossBuild / sbtBinaryVersion).value,
        scalaBinaryVersion.value
      )
    )
    .settings(publishSettings)
    .dependsOn(`sbt-crossproject`)

lazy val `sbt-crossproject` =
  project
    .in(file("sbt-crossproject"))
    .enablePlugins(SbtPlugin)
    .settings(moduleName := "sbt-crossproject")
    .settings(sbt2CrossPluginSettings)
    .settings(scaladocFromReadme)
    .settings(publishSettings)
    .settings(
      libraryDependencies ++= {
        if (scalaBinaryVersion.value == "2.12")
          Seq(
            Defaults.sbtPluginExtra(
              "org.portable-scala" % "sbt-platform-deps" % "1.0.2",
              (pluginCrossBuild / sbtBinaryVersion).value,
              scalaBinaryVersion.value
            )
          )
        else Seq.empty
      }
    )

lazy val `sbt-crossproject-test` =
  project
    .in(file("sbt-crossproject-test"))
    .enablePlugins(SbtPlugin) // for scripted
    .settings(sbt2CrossPluginSettings)
    .settings(noPublishSettings)
    .settings(
      scriptedSbt := (pluginCrossBuild / sbtVersion).value,
      scriptedLaunchOpts ++= Seq(
        "-Dplugin.version=" + version.value,
        "-Dplugin.sn-version=0.5.12",
        "-Dplugin.sjs-version=1.22.0"
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
