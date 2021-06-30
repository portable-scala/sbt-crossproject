import sbtcrossproject.{crossProject, CrossType}

lazy val foo = crossProject(JVMPlatform).settings(
  scalaVersion := "2.11.11"
)

lazy val fooJVM = foo.jvm
