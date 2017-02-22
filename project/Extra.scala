import sbt._
import Keys._
import ScriptedPlugin._

import scala.util.Try

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

  private lazy val publishSnapshot =
    taskKey[Unit]("Publish snapshot to sonatype on every commit to master.")

  lazy val publishSettings = Seq(
    publishArtifact in Compile := true,
    publishArtifact in Test := false,
    publishMavenStyle := true,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (version.value.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    publishSnapshot := Def.taskDyn {
      val travis = Try(sys.env("TRAVIS")).getOrElse("false") == "true"
      val pr = Try(sys.env("TRAVIS_PULL_REQUEST"))
          .getOrElse("false") != "false"
      val branch   = Try(sys.env("TRAVIS_BRANCH")).getOrElse("")
      val snapshot = version.value.trim.endsWith("SNAPSHOT")

      (travis, pr, branch, snapshot) match {
        case (true, false, "master", true) =>
          println("on master, going to publish a snapshot")
          publish

        case _ =>
          println(
            "not going to publish a snapshot due to: " +
              s"travis = $travis, pr = $pr, " +
              s"branch = $branch, snapshot = $snapshot")
          Def.task()
      }
    }.value,
    credentials ++= {
      for {
        realm    <- sys.env.get("MAVEN_REALM")
        domain   <- sys.env.get("MAVEN_DOMAIN")
        user     <- sys.env.get("MAVEN_USER")
        password <- sys.env.get("MAVEN_PASSWORD")
      } yield {
        Credentials(realm, domain, user, password)
      }
    }.toSeq,
    pomIncludeRepository := { _ =>
      false
    },
    licenses := Seq(
      "BSD-like" -> url("http://www.scala-lang.org/downloads/license.html")),
    scmInfo := Some(
      ScmInfo(
        browseUrl = url("https://github.com/scala-native/sbt-crossproject-project"),
        connection =
          "scm:git:git@github.com:scala-native/sbt-crossproject-project.git"
      ))
  )

  lazy val noPublishSettings = Seq(
    publishArtifact := false,
    packagedArtifacts := Map.empty,
    publish := {},
    publishLocal := {},
    publishSnapshot := {
      println("no publish")
    }
  )

}
