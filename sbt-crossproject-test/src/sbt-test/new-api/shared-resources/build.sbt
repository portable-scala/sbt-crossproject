lazy val foo = crossProject(JVMPlatform).settings(
  scalaVersion := "2.12.17"
)

lazy val fooJVM = foo.jvm
