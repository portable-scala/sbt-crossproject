
lazy val sbtcrossproject =
  project.in(file(".")).
    settings(
      organization := "sh.den",
      version := "0.1-SNAPSHOT",
      sbtPlugin := true
    )
