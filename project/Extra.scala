import sbt._
import Keys._
import ScriptedPlugin._

import scala.util.Try

import bintray.BintrayKeys.{bintrayRepository, bintrayOrganization}

object Extra {

  val sbtPluginSettings = ScriptedPlugin.scriptedSettings ++ Seq(
      organization := "org.scala-native",
      version := "0.2.3-SNAPSHOT",
      sbtPlugin := true,
      scriptedLaunchOpts ++= Seq(
        "-Dplugin.version=" + version.value,
        "-Dplugin.sn-version=0.3.2",
        "-Dplugin.sjs-version=0.6.19"
      ),
      scalacOptions ++= Seq(
        "-deprecation",
        "-unchecked",
        "-feature",
        "-encoding",
        "utf8"
      )
    )

  // to publish plugin (we only need to do this once, it's already done!)
  // follow: http://www.scala-sbt.org/0.13/docs/Bintray-For-Plugins.html
  // then add a new package ()
  // name: sbt-crossproject, license: BSD-like, version control: git@github.com:scala-native/sbt-crossproject.git
  // to be available without a resolver
  // follow: http://www.scala-sbt.org/0.13/docs/Bintray-For-Plugins.html#Linking+your+package+to+the+sbt+organization
  lazy val publishSettings = Seq(
    publishArtifact in Compile := true,
    publishArtifact in Test := false,
    licenses := Seq(
      "BSD-like" -> url("http://www.scala-lang.org/downloads/license.html")
    ),
    scmInfo := Some(
      ScmInfo(
        browseUrl = url("https://github.com/scala-native/sbt-crossproject"),
        connection = "scm:git:git@github.com:scala-native/sbt-crossproject.git"
      )
    ),
    bintrayRepository := "sbt-plugins",
    bintrayOrganization := Some("scala-native")
  )

  lazy val noPublishSettings = Seq(
    publishArtifact := false,
    packagedArtifacts := Map.empty,
    publish := {},
    publishLocal := {}
  )

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
      val tests =
        groups.flatMap(_.listFiles).filterNot(_.name == "scala-native-only")

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
