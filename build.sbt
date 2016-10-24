val toolScalaVersion = "2.10.6"

val libScalaVersion = "2.11.8"

lazy val sbtcrossproject =
  project
    .in(file("."))
    .settings(
      organization := "org.scala-native",
      name := "sbt-cross-project",
      version := "0.1-SNAPSHOT",
      sbtPlugin := true,
      scalafmtConfig := Some(file(".scalafmt")),
      scalaVersion := toolScalaVersion,
      libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.0" % "test",
      libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.0" % "test"
    )
