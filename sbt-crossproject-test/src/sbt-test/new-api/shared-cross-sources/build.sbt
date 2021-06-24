import sbtcrossproject.{crossProject, CrossType}

lazy val foo = crossProject(JVMPlatform).settings(
  crossScalaVersions := Seq("2.12.14", "2.13.6", "3.0.0")
)
