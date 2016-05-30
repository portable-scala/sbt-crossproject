package sbtcrossproject

import scala.language.implicitConversions
import scala.language.experimental.macros

import scala.reflect.macros.Context

import sbt._
import Keys._
import Project.projectToRef

import java.io.File

final class CrossProject[P <: CrossPlatform] private (
    crossType: CrossType,
    val projects: Map[P, Project]
) {
  import CrossProject._

  // Transformers for inner projects

  def configure(platform: P, transform: Project => Project) =
    copy(projects = projects + (platform -> transform(projects(platform))))

  def settings(platform: P, ss: Def.Setting[_]*): CrossProject[P] =
    configure(platform, _.settings(ss: _*))

  // Scala.js-style cross project aliases

  /** Project for JVM platform. */
  def jvm: Project = projects(JVM.asInstanceOf[P])

  /** Transform the underlying JVM project */
  def jvmConfigure(transform: Project => Project): CrossProject[P] =
    configure(JVM.asInstanceOf[P], transform)

  /** Add settings specific to the underlying JVM project */
  def jvmSettings(ss: Def.Setting[_]*): CrossProject[P] =
    settings(JVM.asInstanceOf[P], ss: _*)

  // Concrete alteration members

  def aggregate(refs: CrossProject[P]*): CrossProject[P] =
    mapPlatforms { p =>
      projects(p).aggregate(refs.map(_.projects(p): ProjectReference): _*)
    }

  def configs(cs: Configuration*): CrossProject[P] =
    mapProjects(_.configs(cs: _*))

  def configureCross(transforms: (CrossProject[P] => CrossProject[P])*): CrossProject[P] =
    transforms.foldLeft(this)((p, t) => t(p))

  def configureAll(transforms: (Project => Project)*): CrossProject[P] =
    mapProjects(_.configure(transforms: _*))

  def dependsOn(deps: CrossClasspathDependency[P]*): CrossProject[P] =
    mapPlatforms(p => projects(p).dependsOn(deps.map(_.dep(p)): _*))

  def disablePlugins(ps: AutoPlugin*): CrossProject[P] =
    mapProjects(_.enablePlugins(ps: _*))

  def enablePlugins(ns: Plugins*): CrossProject[P] =
    mapProjects(_.enablePlugins(ns: _*))

  def in(dir: File): CrossProject[P] =
    mapPlatforms(p => projects(p).in(crossType.dir(p, dir)))

  def overrideConfigs(cs: Configuration*): CrossProject[P] =
    mapProjects(_.overrideConfigs(cs: _*))

  /** Configures how settings from other sources, such as .sbt files, are
   *  appended to the explicitly specified settings for this project.
   *
   *  Note: If you disable AutoPlugins here, non-JVM platforms will not work
   */
  def settingSets(select: AddSettings*): CrossProject[P] =
    mapProjects(_.settingSets(select: _*))

  def settings(ss: Def.Setting[_]*): CrossProject[P] =
    mapProjects(_.settings(ss: _*))

  override def toString(): String = {
    val entries = this.projects.map {
      case (platform, project) => "$platform = $project"
    }
    s"CrossProject(${entries.mkString(", ")})"
  }

  // Helpers

  private def mapPlatforms(transform: P => Project): CrossProject[P] =
    copy(projects.map { case (p, _) => p -> transform(p) })

  private def mapProjects(transform: Project => Project): CrossProject[P] =
    copy(projects.mapValues(transform))

  private def copy(projects: Map[P, Project] = projects): CrossProject[P] =
    new CrossProject(crossType, projects)
}

object CrossProject extends CrossProjectExtra {
  def apply[P <: CrossPlatform](id: String, platforms: Seq[P], base: File,
      crossType: CrossType): CrossProject[P] =
    apply(platforms.map(p => (p, id + p.name)).toMap, base, crossType)

  def apply[P <: CrossPlatform](ids: Map[P, String], base: File,
      crossType: CrossType): CrossProject[P] = {

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

  final class Builder[P <: CrossPlatform](id: String, platforms: Seq[P], base: File) {
    def crossType(crossType: CrossType): CrossProject[P] =
      CrossProject(id, platforms, base, crossType)
  }

  def crossProject_impl[P <: CrossPlatform](c: Context)(platforms: c.Expr[P]*): c.Expr[Builder[P]] = {
    import c.universe._
    val enclosingValName = MacroUtils.definingValName(c, methodName =>
      s"""$methodName must be directly assigned to a val, such as `val x = $methodName`.""")
    val name = c.Expr[String](Literal(Constant(enclosingValName)))
    val SeqApplyPath = Seq("scala", "collection", "Seq", "apply")
    val SeqApply = SeqApplyPath.foldLeft[Tree](Ident("_root_")) {
      case (t, name) => Select(t, newTermName(name))
    }
    val platformSeq = c.Expr[Seq[P]](Apply(SeqApply, platforms.map(_.tree).toList))
    reify { new Builder(name.splice, platformSeq.splice, new File(name.splice)) }
  }

}

trait CrossProjectExtra {

  def crossProject[P <: CrossPlatform](platforms: P*): CrossProject.Builder[P] =
    macro CrossProject.crossProject_impl[P]

  implicit def crossProjectFromBuilder[P <: CrossPlatform](
      builder: CrossProject.Builder[P]): CrossProject[P] = {
    builder.crossType(CrossType.Full)
  }

  implicit def crossClasspathDependencyConstructor[P <: CrossPlatform](
      cp: CrossProject[P]): CrossClasspathDependency.Constructor[P] =
    new CrossClasspathDependency.Constructor(cp)

  implicit def crossClasspathDependency[P <: CrossPlatform](
      cp: CrossProject[P]): CrossClasspathDependency[P] =
    new CrossClasspathDependency[P](cp, None)
}
