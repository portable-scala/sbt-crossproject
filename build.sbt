import Extra._

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
      addSbtPlugin("org.portable-scala" % "sbt-platform-deps" % "1.0.0-M2"),
      addSbtPlugin("org.scala-js"       % "sbt-scalajs"       % "0.6.22")
    )
    .settings(publishSettings)
    .dependsOn(`sbt-crossproject`)

lazy val `sbt-scala-native-crossproject` =
  project
    .in(file("sbt-scala-native-crossproject"))
    .settings(sbtPluginSettings)
    .settings(
      moduleName := "sbt-scala-native-crossproject",
      addSbtPlugin("org.portable-scala" % "sbt-platform-deps" % "1.0.0-M2"),
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
      addSbtPlugin("org.portable-scala" % "sbt-platform-deps" % "1.0.0-M2")
    )

lazy val `sbt-crossproject-test` =
  project
    .in(file("sbt-crossproject-test"))
    .settings(sbtPluginSettings)
    .settings(noPublishSettings)
    .settings(
      scripted := scripted
        .dependsOn(
          publishLocal in `sbt-crossproject`,
          publishLocal in `sbt-scalajs-crossproject`,
          publishLocal in `sbt-scala-native-crossproject`
        )
        .evaluated
    )
    .settings(duplicateProjectFolders)
