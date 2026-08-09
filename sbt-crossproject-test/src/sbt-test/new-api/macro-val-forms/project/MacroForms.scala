import sbtcrossproject.JVMPlatform
import sbtcrossproject.CrossPlugin.autoImport.crossProject

object MacroForms {
  private lazy val privateProject = crossProject(JVMPlatform)

  def privateProjectName: String =
    privateProject.build().projects(JVMPlatform).id
}
