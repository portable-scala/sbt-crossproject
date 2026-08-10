import sbtcrossproject.{crossProject, CrossType}

lazy val foo = crossProject(JVMPlatform).settings(
  crossScalaVersions := Seq("2.12.17", "2.13.9", "3.0.0")
)
