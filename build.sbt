import Extra._

inThisBuild(
  Def.settings(
    crossSbtVersions := Seq((sbtVersion in Global).value, "0.13.17"),
    crossScalaVersions := Seq("2.12.6", "2.10.7")
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
    .settings(sbtPluginSettings)
    .settings(
      moduleName := "sbt-scalajs-crossproject",
      addSbtPlugin("org.portable-scala" % "sbt-platform-deps" % "1.0.0"),
      addSbtPlugin("org.scala-js"       % "sbt-scalajs"       % "0.6.23")
    )
    .settings(publishSettings)
    .dependsOn(`sbt-crossproject`)

lazy val `sbt-scala-native-crossproject` =
  project
    .in(file("sbt-scala-native-crossproject"))
    .settings(sbtPluginSettings)
    .settings(
      moduleName := "sbt-scala-native-crossproject",
      addSbtPlugin("org.portable-scala" % "sbt-platform-deps" % "1.0.0"),
      addSbtPlugin("org.scala-native"   % "sbt-scala-native"  % "0.3.7")
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
    .settings(
      addSbtPlugin("org.portable-scala" % "sbt-platform-deps" % "1.0.0")
    )

lazy val `sbt-crossproject-test` =
  project
    .in(file("sbt-crossproject-test"))
    .enablePlugins(ScriptedPlugin)
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
          publishLocal in `sbt-crossproject`,
          publishLocal in `sbt-scalajs-crossproject`,
          publishLocal in `sbt-scala-native-crossproject`
        )
        .evaluated
    )
    .settings(duplicateProjectFolders)
