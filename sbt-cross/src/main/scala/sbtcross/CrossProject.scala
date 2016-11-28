package sbtcross

import sbtcross._

import scala.language.implicitConversions

import sbt._
import Keys._

final class CrossProject private[sbtcross] (
    private val id: String,
    crossType: CrossType,
    val projects: Map[Platform, Project]
) {

  def aggregate(refs: CrossProject*): CrossProject = {
    requireDependencies(refs.toList)

    val aggregatesByPlatform =
      refs.toSeq.flatMap(_.projects).groupBy(_._1).mapValues(_.map(_._2))

    mapProjectsByPlatform((platform, project) =>
      project.aggregate(aggregatesByPlatform(platform).map(p =>
        p: ProjectReference): _*))
  }

  def dependsOn(deps: CrossClasspathDependency*): CrossProject = {
    requireDependencies(deps.toList.map(_.project))

    val dependenciesByPlatform =
      deps.toSeq
        .flatMap(dep =>
          dep.project.projects.map {
            case (platform, project) =>
              platform -> ClasspathDependency(project, dep.configuration)
        })
        .groupBy(_._1)
        .mapValues(_.map(_._2))

    mapProjectsByPlatform((platform, project) =>
      project.dependsOn(dependenciesByPlatform(platform): _*))
  }

  def configs(cs: Configuration*): CrossProject =
    transform(_.configs(cs: _*))

  def configureCross(
      transforms: (CrossProject => CrossProject)*): CrossProject =
    transforms.foldLeft(this)((p, t) => t(p))

  def configure(transforms: (Project => Project)*): CrossProject =
    transform(_.configure(transforms: _*))

  @deprecated("use configure", "0.1.0")
  def configureAll(transforms: (Project => Project)*): CrossProject =
    configure(transforms: _*)

  def configurePlatform(platforms: Platform*)(
      f: Project => Project): CrossProject = {

    val updatedProjects =
      platforms.foldLeft(projects)((acc, platform) =>
        acc.updated(platform, f(acc(platform))))

    new CrossProject(id, crossType, updatedProjects)
  }

  def disablePlugins(ps: AutoPlugin*): CrossProject =
    transform(_.disablePlugins(ps: _*))

  def enablePlugins(ns: Plugins*): CrossProject =
    transform(_.enablePlugins(ns: _*))

  def in(dir: File): CrossProject =
    mapProjectsByPlatform(
      (platform, project) => project.in(crossType.platformDir(dir, platform)))

  def overrideConfigs(cs: Configuration*): CrossProject =
    transform(_.overrideConfigs(cs: _*))

  def settingSets(select: AddSettings*): CrossProject =
    transform(_.settingSets(select: _*))

  def settings(ss: Def.SettingsDefinition*): CrossProject =
    transform(_.settings(ss: _*))

  override def toString(): String =
    projects.map {
      case (platform, project) =>
        s"${platform.identifier} = $project"
    }.mkString("CrossProject(", ",", ")")

  private def platforms = projects.keySet

  private def mapProjectsByPlatform(
      f: (Platform, Project) => Project): CrossProject = {
    val updatedProjects = projects.map {
      case (platform, project) => platform -> f(platform, project)
    }
    new CrossProject(id, crossType, updatedProjects)
  }

  private def transform(f: Project => Project): CrossProject =
    mapProjectsByPlatform((platform, project) => f(project))

  private def requireDependencies(refs: List[CrossProject]): Unit = {
    val infos =
      refs.flatMap { ref =>
        val missings = platforms -- ref.platforms
        val discards = ref.platforms -- platforms

        if (missings.isEmpty && discards.isEmpty) Nil
        else List((ref, missings, discards))
      }

    val hasMissing = infos.exists(!_._2.isEmpty)
    val hasDiscard = infos.exists(!_._3.isEmpty)

    def msg = {
      val nl = System.lineSeparator

      val projectPlatforms = platforms.map(_.identifier).mkString(", ")

      val depedenciesInfo =
        infos.map {
          case (ref, missings, discards) =>
            val missingMessage =
              if (missings.isEmpty) ""
              else "missings: " + missings.map(_.identifier).mkString(", ")

            val discardedMessage =
              if (discards.isEmpty) ""
              else "discards: " + discards.map(_.identifier).mkString(", ")

            s"""|project ${ref.id}
                |  $missingMessage
                |  $discardedMessage""".stripMargin

        }.mkString(nl)

      s"""|Project defines platforms: $projectPlatforms
          |$depedenciesInfo""".stripMargin
    }

    if (hasMissing || hasDiscard) println(msg)
  }
}

object CrossProject {
  final implicit def crossClasspathDependencyConstructor(
      cp: CrossProject): CrossClasspathDependency.Constructor =
    new CrossClasspathDependency.Constructor(cp)

  final implicit def crossClasspathDependency(
      cp: CrossProject): CrossClasspathDependency =
    new CrossClasspathDependency(cp, None)

  final class Builder(id: String, base: File, platforms: Platform*) {
    def crossType(crossType: CrossType): CrossProject =
      CrossProject(id, base, crossType, platforms: _*)
  }
  object Builder {
    final implicit def crossProjectFromBuilder(
        builder: CrossProject.Builder): CrossProject = {
      builder.crossType(CrossType.Full)
    }
  }

  def apply(id: String,
            base: File,
            crossType: CrossType,
            platforms: Platform*): CrossProject = {
    def sharedSrcSettings(crossType: CrossType) = {
      def makeCrossSources(sharedSrcDir: Option[File],
                           scalaBinaryVersion: String,
                           cross: Boolean): Seq[File] = {
        sharedSrcDir match {
          case Some(dir) =>
            if (cross)
              Seq(dir.getParentFile / s"${dir.name}-$scalaBinaryVersion", dir)
            else
              Seq(dir)
          case None => Seq()
        }
      }

      Seq(
        unmanagedSourceDirectories in Compile ++= {
          makeCrossSources(crossType.sharedSrcDir(baseDirectory.value, "main"),
                           scalaBinaryVersion.value,
                           crossPaths.value)
        },
        unmanagedSourceDirectories in Test ++= {
          makeCrossSources(crossType.sharedSrcDir(baseDirectory.value, "test"),
                           scalaBinaryVersion.value,
                           crossPaths.value)
        }
      )
    }

    val shared = sharedSrcSettings(crossType)

    val projects =
      platforms.map { platform =>
        platform -> platform.enable(
          Project(
            id + platform.sbtSuffix,
            crossType.platformDir(base, platform)
          ).settings(shared)
        )
      }.toMap

    new CrossProject(id, crossType, projects)
  }
}
