import sbt._
import Keys._
import ScriptedPlugin.autoImport._

import com.typesafe.tools.mima.plugin.MimaPlugin.autoImport._

import scala.util.Try

object Extra {

  val previousVersion: Option[String]                  = Some("1.2.0")
  val newScalaBinaryVersionsInThisRelease: Set[String] = Set()

  val sbtPluginSettings = Def.settings(
    organization := "org.portable-scala",
    version := "1.3.0-SNAPSHOT",
    versionScheme := Some("semver-spec"),
    scalacOptions ++= Seq(
      "-deprecation",
      "-unchecked",
      "-feature",
      "-encoding",
      "utf8"
    ),
    sbtVersion := "1.2.1"
  )

  lazy val publishSettings = Seq(
    Compile / publishArtifact := true,
    Test / publishArtifact := false,
    homepage := Some(
      url("https://github.com/portable-scala/sbt-crossproject")),
    licenses := Seq(
      "BSD-like" -> url("http://www.scala-lang.org/downloads/license.html")
    ),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/portable-scala/sbt-crossproject"),
        "scm:git:git@github.com:portable-scala/sbt-crossproject.git",
        Some("scm:git:git@github.com:portable-scala/sbt-crossproject.git")
      )
    ),
    // Publishing
    publishMavenStyle := true,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (version.value.endsWith("-SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    pomExtra := (
      <developers>
        <developer>
          <id>sjrd</id>
          <name>Sébastien Doeraene</name>
          <url>https://github.com/sjrd/</url>
        </developer>
        <developer>
          <id>gzm0</id>
          <name>Tobias Schlatter</name>
          <url>https://github.com/gzm0/</url>
        </developer>
      </developers>
    ),
    pomIncludeRepository := { _ =>
      false
    },
    // MiMa auto-configuration
    mimaPreviousArtifacts ++= {
      val scalaV        = scalaVersion.value
      val scalaBinaryV  = scalaBinaryVersion.value
      val thisProjectID = projectID.value
      previousVersion match {
        case None =>
          Set.empty
        case _ if newScalaBinaryVersionsInThisRelease.contains(scalaBinaryV) =>
          // New in this release, no binary compatibility to comply to
          Set.empty
        case Some(prevVersion) =>
          /* Filter out e:info.apiURL as it expects 1.1.1-SNAPSHOT, whereas the
           * artifact we're looking for has 1.1.0 (for example).
           */
          val prevExtraAttributes =
            thisProjectID.extraAttributes.filterKeys(_ != "e:info.apiURL")
          val prevProjectID =
            (thisProjectID.organization % thisProjectID.name % prevVersion)
              .cross(thisProjectID.crossVersion)
              .extra(prevExtraAttributes.toSeq: _*)
          Set(prevProjectID)
      }
    }
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

      val readmeFile = (ThisBuild / baseDirectory).value / "README.md"
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
    Compile / doc / scalacOptions ++= Seq(
      "-doc-root-content",
      (target.value / "rootdoc.txt").getPath
    ),
    Compile / doc := (Compile / doc).dependsOn(createRootDoc).value
  )

  private lazy val duplicateProjectFoldersTask =
    taskKey[Unit]("Copy project folders in all scripted directories")

  val duplicateProjectFolders = Seq(
    duplicateProjectFoldersTask := {
      println("duplicating")

      val pluginsFileName = "plugins.sbt"
      val project         = "project"

      val pluginsSbt = sbtTestDirectory.value / pluginsFileName

      val groups = sbtTestDirectory.value.listFiles.filter(_.isDirectory)
      val tests =
        groups.flatMap(_.listFiles).filterNot(_.name == "scala-native-only")

      tests.foreach { test =>
        val testProjectDir = test / project
        IO.createDirectory(testProjectDir)
        IO.copyFile(pluginsSbt, testProjectDir / pluginsFileName)
      }
    },
    scripted := scripted.dependsOn(duplicateProjectFoldersTask).evaluated
  )
}
