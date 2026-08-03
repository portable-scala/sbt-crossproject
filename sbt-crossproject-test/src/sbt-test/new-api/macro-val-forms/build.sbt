import sbtcrossproject.{crossProject, CrossProject}

ThisBuild / scalaVersion := "2.12.20"

val ordinary = crossProject(JVMPlatform).settings()

lazy val simple = crossProject(JVMPlatform).settings()

lazy val typed: CrossProject = crossProject(JVMPlatform)

lazy val multiline =
  crossProject(JVMPlatform).settings()

lazy val aroundAssignment /* before equals */ =
  /* after equals */ crossProject(JVMPlatform).settings()

lazy val `back-ticked` = crossProject(JVMPlatform).settings()

lazy val legacy = crossProject.settings()

lazy val checkPrivateProjectName = taskKey[Unit]("Check private project name")
checkPrivateProjectName := {
  assert(MacroForms.privateProjectName == "privateProjectJVM")
}
