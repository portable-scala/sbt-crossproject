import Extra._

def addSbtPluginWorkaround(moduleID: ModuleID): Setting[_] = {
  /* Work around https://github.com/sbt/sbt/issues/3393.
   * This is the fixed definition of addSbtPlugin to be
   * released with sbt 0.13.17.
   */
  libraryDependencies += {
    val sbtV   = (sbtBinaryVersion in pluginCrossBuild).value
    val scalaV = (scalaBinaryVersion in update).value
    Defaults.sbtPluginExtra(moduleID, sbtV, scalaV)
  }
}

lazy val `sbt-crossproject-root` =
  project
    .in(file("."))
    .aggregate(`sbt-scalajs-crossproject`,
               `sbt-crossproject`,
               `sbt-crossproject-test`)
    .dependsOn(`sbt-scalajs-crossproject`,
               `sbt-crossproject`,
               `sbt-crossproject-test`)
    .settings(noPublishSettings)

lazy val `sbt-scalajs-crossproject` =
  project
    .in(file("sbt-scalajs-crossproject"))
    .settings(sbtPluginSettings)
    .settings(
      moduleName := "sbt-scalajs-crossproject",
      addSbtPluginWorkaround(
        "org.portable-scala"                % "sbt-platform-deps" % "1.0.0-M2"),
      addSbtPluginWorkaround("org.scala-js" % "sbt-scalajs"       % "0.6.22")
    )
    .settings(publishSettings)
    .dependsOn(`sbt-crossproject`)

lazy val `sbt-scala-native-crossproject` =
  project
    .in(file("sbt-scala-native-crossproject"))
    .settings(sbtPluginSettings)
    .settings(
      moduleName := "sbt-scala-native-crossproject",
      addSbtPluginWorkaround(
        "org.portable-scala"                    % "sbt-platform-deps" % "1.0.0-M2"),
      addSbtPluginWorkaround("org.scala-native" % "sbt-scala-native"  % "0.3.7")
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
      addSbtPluginWorkaround(
        "org.portable-scala" % "sbt-platform-deps" % "1.0.0-M2")
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
          publishLocal in `sbt-scalajs-crossproject`
        )
        .evaluated
    )
    .settings(duplicateProjectFolders)
