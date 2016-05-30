package sbtcrossproject

import scala.language.implicitConversions
import scala.language.experimental.macros

import scala.reflect.macros.Context

import sbt._
import Keys._
import Project.projectToRef

import java.io.File

final class CrossProject private (
    crossType: CrossType,
    val projects: Map[CrossPlatform, Project]
) {
  import CrossProject._

  // Transformers for inner projects

  def configure(platform: CrossPlatform, transform: Project => Project) =
    copy(projects = projects + (platform -> transform(projects(platform))))

  def settings(platform: CrossPlatform, ss: Def.Setting[_]*): CrossProject =
    configure(platform, _.settings(ss: _*))

  // Scala.js-style cross project aliases

  /** Project for JVM platform. */
  def jvm: Project = projects(JVM)

  /** Transform the underlying JVM project */
  def jvmConfigure(transform: Project => Project): CrossProject =
    configure(JVM, transform)

  /** Add settings specific to the underlying JVM project */
  def jvmSettings(ss: Def.Setting[_]*): CrossProject =
    settings(JVM, ss: _*)

  // Concrete alteration members

  def aggregate(refs: CrossProject*): CrossProject =
    mapCrossPlatforms { p =>
      projects(p).aggregate(refs.map(_.projects(p): ProjectReference): _*)
    }

  def configs(cs: Configuration*): CrossProject =
    mapProjects(_.configs(cs: _*))

  def configureCross(transforms: (CrossProject => CrossProject)*): CrossProject =
    transforms.foldLeft(this)((p, t) => t(p))

  def configureAll(transforms: (Project => Project)*): CrossProject =
    mapProjects(_.configure(transforms: _*))

  def dependsOn(deps: CrossClasspathDependency*): CrossProject =
    mapCrossPlatforms(p => projects(p).dependsOn(deps.map(_.dep(p)): _*))

  def disablePlugins(ps: AutoPlugin*): CrossProject =
    mapProjects(_.enablePlugins(ps: _*))

  def enablePlugins(ns: Plugins*): CrossProject =
    mapProjects(_.enablePlugins(ns: _*))

  def in(dir: File): CrossProject =
    mapCrossPlatforms(p => projects(p).in(crossType.dir(p, dir)))

  def overrideConfigs(cs: Configuration*): CrossProject =
    mapProjects(_.overrideConfigs(cs: _*))

  /** Configures how settings from other sources, such as .sbt files, are
   *  appended to the explicitly specified settings for this project.
   *
   *  Note: If you disable AutoPlugins here, non-JVM platforms will not work
   */
  def settingSets(select: AddSettings*): CrossProject =
    mapProjects(_.settingSets(select: _*))

  def settings(ss: Def.Setting[_]*): CrossProject =
    mapProjects(_.settings(ss: _*))

  override def toString(): String = {
    val entries = this.projects.map {
      case (platform, project) => "$platform = $project"
    }
    s"CrossProject(${entries.mkString(", ")})"
  }

  // Helpers

  private def mapCrossPlatforms(transform: CrossPlatform => Project): CrossProject =
    copy(projects.map { case (p, _) => p -> transform(p) })

  private def mapProjects(transform: Project => Project): CrossProject =
    copy(projects.mapValues(transform))

  private def copy(projects: Map[CrossPlatform, Project] = projects): CrossProject =
    new CrossProject(crossType, projects)
}

object CrossProject extends CrossProjectExtra {
  def apply(id: String, platforms: Seq[CrossPlatform], base: File,
      crossType: CrossType): CrossProject =
    apply(platforms.map(p => (p, id + p.name)).toMap, base, crossType)

  def apply(ids: Map[CrossPlatform, String], base: File,
      crossType: CrossType): CrossProject = {

    val sss = sharedSrcSettings(crossType)

    val projects = ids.map { case (p, id) =>
      val project = Project(id, crossType.dir(p, base)).
        settings(sss: _*).
        enablePlugins(p.plugin.toSeq: _*)

      p -> project
    }

    new CrossProject(crossType, projects)
  }

  private def sharedSrcSettings(crossType: CrossType) = Seq(
      unmanagedSourceDirectories in Compile ++= {
        makeCrossSources(crossType.sharedSrcDir(baseDirectory.value, "main"),
            scalaBinaryVersion.value, crossPaths.value)
      },
      unmanagedSourceDirectories in Test ++= {
        makeCrossSources(crossType.sharedSrcDir(baseDirectory.value, "test"),
            scalaBinaryVersion.value, crossPaths.value)
      }
  )

  // Inspired by sbt's Defaults.makeCrossSources
  private def makeCrossSources(sharedSrcDir: Option[File],
      scalaBinaryVersion: String, cross: Boolean): Seq[File] = {
    sharedSrcDir.fold[Seq[File]] {
      Seq.empty
    } { srcDir =>
      if (cross)
        Seq(srcDir.getParentFile / s"${srcDir.name}-$scalaBinaryVersion", srcDir)
      else
        Seq(srcDir)
    }
  }

  final class Builder(id: String, platforms: Seq[CrossPlatform], base: File) {
    def crossType(crossType: CrossType): CrossProject =
      CrossProject(id, platforms, base, crossType)
  }

  def crossProject_impl(c: Context)(platforms: c.Expr[CrossPlatform]*): c.Expr[Builder] = {
    import c.universe._
    val enclosingValName = MacroUtils.definingValName(c, methodName =>
      s"""$methodName must be directly assigned to a val, such as `val x = $methodName`.""")
    val name = c.Expr[String](Literal(Constant(enclosingValName)))
    val SeqApplyPath = Seq("scala", "collection", "Seq", "apply")
    val SeqApply = SeqApplyPath.foldLeft[Tree](Ident("_root_")) {
      case (t, name) => Select(t, newTermName(name))
    }
    val platformSeq = c.Expr[Seq[CrossPlatform]](Apply(SeqApply, platforms.map(_.tree).toList))
    reify { new Builder(name.splice, platformSeq.splice, new File(name.splice)) }
  }

}

trait CrossProjectExtra {

  def crossProject(platforms: CrossPlatform*): CrossProject.Builder =
    macro CrossProject.crossProject_impl

  implicit def crossProjectFromBuilder(
      builder: CrossProject.Builder): CrossProject = {
    builder.crossType(CrossType.Full)
  }

  implicit def crossClasspathDependencyConstructor(
      cp: CrossProject): CrossClasspathDependency.Constructor =
    new CrossClasspathDependency.Constructor(cp)

  implicit def crossClasspathDependency(
      cp: CrossProject): CrossClasspathDependency =
    new CrossClasspathDependency(cp, None)
}
