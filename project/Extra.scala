import sbt._
import Keys._
import ScriptedPlugin._

object Extra {
  private val createRootDoc = taskKey[File]("Generate ScalaDoc from README")

  lazy val scaladocFromReadme = Seq(
    createRootDoc := {
      // rootdoc.txt is the ScalaDoc landing page
      // we tweak the markdown so it's valid Scaladoc

      val readmeFile = (baseDirectory in ThisBuild).value / "README.md"
      val readme     = IO.read(readmeFile)
      val scaladocReadme =
        readme
          .replaceAllLiterally("```scala", "{{{")
          .replaceAllLiterally("```", "}}}")

      val rootdoc = target.value / "rootdoc.txt"
      IO.delete(rootdoc)
      IO.write(rootdoc, scaladocReadme)
      rootdoc
    },
    scalacOptions in (Compile, doc) ++= Seq(
      "-doc-root-content",
      (target.value / "rootdoc.txt").getPath
    ),
    doc in Compile := (doc in Compile).dependsOn(createRootDoc).value
  )

  private lazy val duplicateProjectFoldersTask =
    taskKey[Unit]("Copy project folders in all scripted directories")

  val duplicateProjectFolders = Seq(
    duplicateProjectFoldersTask := {

      println("duplicating")

      val pluginsFileName         = "plugins.sbt"
      val buildPropertiesFileName = "build.properties"
      val project                 = "project"

      val pluginsSbt      = sbtTestDirectory.value / pluginsFileName
      val buildProperties = (baseDirectory in ThisBuild).value / project / buildPropertiesFileName

      val groups = sbtTestDirectory.value.listFiles.filter(_.isDirectory)
      val tests  = groups.flatMap(_.listFiles)

      tests.foreach { test =>
        val testProjectDir = test / project
        IO.createDirectory(testProjectDir)
        IO.copyFile(pluginsSbt, testProjectDir / pluginsFileName)
        IO.copyFile(buildProperties, testProjectDir / buildPropertiesFileName)
      }
    },
    scripted := scripted.dependsOn(duplicateProjectFoldersTask).evaluated
  )
}
