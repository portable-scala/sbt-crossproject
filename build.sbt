import Extra._

val ScalaJSVersionBaseline     = "1.22.0"  // first version supporting sbt 2.x
val ScalaNativeVersionBaseline = "0.5.11"  // first version supporting sbt 2.x
val SBT1xVersionBaseline       = "1.11.5"  // required by sbt-scala-native 0.5.11
val SBT1xScalaVersion          = "2.12.20" // version used to build sbt 1.11.5

inThisBuild(
  Def.settings(
    scalaVersion := SBT1xScalaVersion,
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
    .settings(
      sbtVersion := SBT1xVersionBaseline,
      moduleName := "sbt-scalajs-crossproject",
      addSbtPlugin("org.scala-js" % "sbt-scalajs" % ScalaJSVersionBaseline)
    )
    .settings(publishSettings)
    .dependsOn(`sbt-crossproject`)

lazy val `sbt-scala-native-crossproject` =
  project
    .in(file("sbt-scala-native-crossproject"))
    .enablePlugins(SbtPlugin)
    .settings(
      sbtVersion := SBT1xVersionBaseline,
      moduleName := "sbt-scala-native-crossproject",
      addSbtPlugin("org.scala-native" % "sbt-scala-native" % ScalaNativeVersionBaseline)
    )
    .settings(publishSettings)
    .dependsOn(`sbt-crossproject`)

lazy val `sbt-crossproject` =
  project
    .in(file("sbt-crossproject"))
    .enablePlugins(SbtPlugin)
    .settings(moduleName := "sbt-crossproject")
    .settings(scaladocFromReadme)
    .settings(publishSettings)
    .settings(
      sbtVersion := SBT1xVersionBaseline,
      addSbtPlugin("org.portable-scala" % "sbt-platform-deps" % "1.0.2")
    )

lazy val `sbt-crossproject-test` =
  project
    .in(file("sbt-crossproject-test"))
    .enablePlugins(SbtPlugin) // for scripted
    .settings(noPublishSettings)
    .settings(
      sbtVersion := SBT1xVersionBaseline,
      scriptedLaunchOpts ++= Seq(
        "-Dplugin.version=" + version.value,
        s"-Dplugin.sn-version=$ScalaNativeVersionBaseline",
        s"-Dplugin.sjs-version=$ScalaJSVersionBaseline",
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
