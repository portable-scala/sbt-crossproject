
lazy val sbtcrossproject =
  project.in(file(".")).
    settings(
      organization := "sh.den",
      name := "sbt-cross-project",
      version := "0.1-SNAPSHOT",
      sbtPlugin := true
    )
