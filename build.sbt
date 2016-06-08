
lazy val sbtcrossproject =
  project.in(file(".")).
    settings(
      organization := "org.scala-native",
      name := "sbt-cross-project",
      version := "0.1-SNAPSHOT",
      sbtPlugin := true
    )
