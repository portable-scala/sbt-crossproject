import Extra._

inThisBuild(
  Def.settings(
    scalaVersion := "2.12.21",
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
      addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.23"),
      publish / skip := {
        // TODO
        // https://github.com/scala-js/scala-js/issues/5238
        scalaBinaryVersion.value == "3"
      }
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
      libraryDependencies += {
        val scalaV = (update / scalaBinaryVersion).value
        val sbtV   = (pluginCrossBuild / sbtBinaryVersion).value
        val scalaNativeVersion = sbtV match {
          case "2" =>
            "0.5.11"
          case _ =>
            "0.3.7"
        }
        Defaults.sbtPluginExtra(
          "org.scala-native" % "sbt-scala-native" % scalaNativeVersion,
          sbtV,
          scalaV
        )
      }
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
      libraryDependencies ++= {
        val sbtV = (pluginCrossBuild / sbtBinaryVersion).value
        sbtV match {
          case "2" =>
            Nil
          case _ =>
            val scalaV = (update / scalaBinaryVersion).value
            Seq(
              Defaults.sbtPluginExtra(
                "org.portable-scala" % "sbt-platform-deps" % "1.0.2",
                sbtV,
                scalaV
              )
            )
        }
      }
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
