lazy val foo = crossProject(JVMPlatform).settings(
  scalaVersion := "2.12.21"
)

lazy val fooJVM = foo.jvm
