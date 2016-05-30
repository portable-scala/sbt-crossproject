
lazy val sbtcrossproject =
  project.in(file(".")).
    settings(
      sbtPlugin := true
    )
