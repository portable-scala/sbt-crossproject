import sbtcrossproject.{crossProject, CrossType}

lazy val foo = crossProject(JVMPlatform).settings(
  scalaVersion := "2.12.20"
)

lazy val fooJVM = foo.jvm
