package sbtcrossproject

import sbtcrossproject._

import scala.language.implicitConversions

import scala.collection.immutable.HashMap
import scala.collection.immutable.ListSet

import sbt._
import Keys._

final class CrossProject private[sbtcrossproject] (
    private val id: String,
    crossType: CrossType,
    val projects: Map[Platform, Project]
) extends CompositeProject {

  // CompositeProject API
  override def componentProjects: Seq[Project] = projects.valuesIterator.toSeq

  def aggregate(refs: CrossProject*): CrossProject = {
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
      f: Project => Project): CrossProject =
    configurePlatforms(platforms: _*)(f)

  def configurePlatforms(platforms: Platform*)(
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

  def platformsEnablePlugins(platforms: Platform*)(
      plugins: Plugins*): CrossProject =
    configurePlatforms(platforms: _*)(_.enablePlugins(plugins: _*))

  def in(dir: File): CrossProject =
    mapProjectsByPlatform(
      (platform, project) => project.in(crossType.platformDir(dir, platform)))

  def overrideConfigs(cs: Configuration*): CrossProject =
    transform(_.overrideConfigs(cs: _*))

  def settings(ss: Def.SettingsDefinition*): CrossProject =
    transform(_.settings(ss: _*))

  def platformsSettings(platforms: Platform*)(
      ss: Def.SettingsDefinition*): CrossProject =
    configurePlatforms(platforms: _*)(_.settings(ss: _*))

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
    for (ref <- refs) {
      val missings = platforms -- ref.platforms
      if (missings.nonEmpty) {
        throw new IllegalArgumentException(
          s"The cross-project ${this.id} cannot depend on ${ref.id} because " +
            "the latter lacks some platforms of the former: " +
            missings.mkString(", ")
        )
      }
    }
  }
}

object CrossProject {
  final class Builder private[CrossProject] (
      id: String,
      base: File,
      platforms: Seq[Platform],
      _crossType: CrossType,
      platformWithoutSuffix: Option[Platform]
  ) {
    private[CrossProject] def this(id: String,
                                   base: File,
                                   platforms: Seq[Platform],
                                   internal: Boolean) =
      this(id, base, platforms, CrossType.Full, None)

    @deprecated("Use CrossProject(id, base)(platforms) instead", "0.3.1")
    def this(id: String, base: File, platforms: Platform*) =
      this(id, base, platforms, internal = true)

    /** Specify a platform that should not receive a suffix in its ID.
     *
     *  For example,
     *  {{{
     *  val foo = crossProject(JSPlatform, JVMPlatform, NativePlatform)
     *    .withoutSuffixFor(JVMPlatform)
     *    .settings(...)
     *
     *  val fooJS = foo.js
     *  val fooJVM = foo.jvm
     *  val fooNative = foo.native
     *  }}}
     *  will give the ID `foo` to `foo.jvm`, instead of the default `fooJVM`.
     *  This then allows to run sbt tasks such as
     *  {{{
     *  > foo/test
     *  }}}
     *  instead of
     *  {{{
     *  > fooJVM/test
     *  }}}
     *  for the JVM.
     *
     *  This is useful if there is one "default" platform in your project,
     *  which is more commonly manipulated than the others.
     */
    def withoutSuffixFor(platform: Platform): Builder =
      copy(platformWithoutSuffix = Some(platform))

    def crossType(crossType: CrossType): Builder =
      copy(crossType = crossType)

    private def copy(
        crossType: CrossType = _crossType,
        platformWithoutSuffix: Option[Platform] = platformWithoutSuffix
    ): Builder = {
      new Builder(id, base, platforms, crossType, platformWithoutSuffix)
    }

    def build(): CrossProject = {
      val crossType       = _crossType
      val sharedSrc       = sharedSrcSettings(crossType)
      val sharedResources = sharedResourcesSettings(crossType)

      val projects =
        platforms.map { platform =>
          val projectID =
            if (platformWithoutSuffix.exists(_ == platform)) id
            else id + platform.sbtSuffix

          platform -> platform.enable(
            Project(
              projectID,
              crossType.platformDir(base, platform)
            ).settings(
              CrossPlugin.autoImport.crossProjectPlatform := platform,
              CrossPlugin.autoImport.crossProjectCrossType := crossType,
              name := id, // #80
              sharedSrc(platform),
              sharedResources(platform)
            )
          )
        }.toMap

      new CrossProject(id, crossType, projects)
    }

    private def sharedSrcSettings(
        crossType: CrossType): Map[Platform, Seq[Setting[_]]] = {
      def makeCrossSources(sharedSrcDir: Option[File],
                           scalaBinaryVersion: String,
                           cross: Boolean): Seq[File] = {
        sharedSrcDir match {
          case Some(dir) =>
            if (cross) {
              val scalaEpochVersion = scalaBinaryVersion.takeWhile(_ != '.')
              Seq(
                Some(dir.getParentFile / s"${dir.name}-$scalaBinaryVersion"),
                if (scalaEpochVersion != scalaBinaryVersion)
                  Some(dir.getParentFile / s"${dir.name}-$scalaEpochVersion")
                else
                  None,
                Some(dir)
              ).flatten
            } else {
              Seq(dir)
            }
          case None => Seq()
        }
      }

      def makeSharedSettings(
          key: SettingKey[Seq[File]],
          config: String): Seq[(Platform, Seq[Setting[_]])] = {
        val partiallyShared = makePartiallySharedSettings(platforms) {
          platformSubset =>
            Seq(
              key ++= makeCrossSources(
                crossType.partiallySharedSrcDir(baseDirectory.value,
                                                platformSubset,
                                                config),
                scalaBinaryVersion.value,
                crossPaths.value)
            )
        }

        val shared = Seq(
          key ++= makeCrossSources(
            crossType.sharedSrcDir(baseDirectory.value, config),
            scalaBinaryVersion.value,
            crossPaths.value)
        )

        partiallyShared ++ platforms.map(_ -> shared) // add shared to each platform
      }

      val compileSettings =
        makeSharedSettings(unmanagedSourceDirectories in Compile, "main")
      val testSettings =
        makeSharedSettings(unmanagedSourceDirectories in Test, "test")

      (compileSettings ++ testSettings) // group-reduce the settings per-platform
        .groupBy(_._1)
        .map(kv => kv._1 -> kv._2.flatMap(_._2))
    }

    private def sharedResourcesSettings(
        crossType: CrossType): Map[Platform, Seq[Setting[_]]] = {
      def makeSharedSettings(
          key: SettingKey[Seq[File]],
          config: String): Seq[(Platform, Seq[Setting[_]])] = {
        val partiallyShared = makePartiallySharedSettings(platforms) {
          platformSubset =>
            Seq(
              key ++= crossType
                .partiallySharedResourcesDir(baseDirectory.value,
                                             platformSubset,
                                             config)
                .toSeq
            )
        }

        val shared = Seq(
          key ++= crossType
            .sharedResourcesDir(baseDirectory.value, config)
            .toSeq
        )

        partiallyShared ++ platforms.map(_ -> shared) // add shared to each platform
      }

      val compileSettings =
        makeSharedSettings(unmanagedResourceDirectories in Compile, "main")
      val testSettings =
        makeSharedSettings(unmanagedResourceDirectories in Test, "test")

      (compileSettings ++ testSettings) // group-reduce the settings per-platform
        .groupBy(_._1)
        .map(kv => kv._1 -> kv._2.flatMap(_._2))
    }

    private def makePartiallySharedSettings(platforms: Seq[Platform])(
        mkSettings: Seq[Platform] => Seq[Setting[_]])
      : Seq[(Platform, Seq[Setting[_]])] = {
      platforms.toSet
        .subsets()
        .filter(_.size > 1) // skip the empty+singleton subsets
        .filterNot(_.size == platforms.size) // skip the all-platform subset
        .toSeq
        .map(_.toSeq.sortBy(_.identifier)) // use a consistent ordering
        .flatMap { platformSubset => // make the settings for this subset of platforms
          val settings = mkSettings(platformSubset)
          platformSubset.map(_ -> settings)
        }
    }

  }

  object Builder {
    final implicit def crossProjectFromBuilder(
        builder: CrossProject.Builder): CrossProject = {
      builder.build()
    }
  }

  def apply(id: String, base: File)(platforms: Platform*): Builder =
    new Builder(id, base, platforms, internal = true)

  @deprecated(
    "Use the other overload of apply() and methods of the returned Builder.",
    "0.3.1")
  def apply(id: String,
            base: File,
            crossType: CrossType,
            platforms: Platform*): CrossProject = {
    apply(id, base)(platforms: _*).crossType(crossType)
  }
}
