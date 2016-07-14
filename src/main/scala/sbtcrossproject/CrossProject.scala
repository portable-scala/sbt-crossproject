package sbtcrossproject

import sbt.Keys._
import sbt._

import scala.language.experimental.macros
import scala.language.implicitConversions
import scala.reflect.macros.Context

/**
  * Created by grinder on 17.07.16.
  */
class CrossProject[P <: Platform] private[sbtcrossproject] (
    val crossType: CrossType,
    val projects: Map[Platform, Project]) {

  def in(dir: File): CrossProject[P] = {
    copy(projects.map(x => x._1 -> x._2.in(crossType.dir(x._1, dir))))
  }

  def aggregate[U <: Platform](refs: CrossProject[U]*): CrossProject[P] = {
    copy(
      refs
        .flatMap(_.projects)
        .groupBy(_._1)
        .map {
          case (k, v) =>
            k -> projects
              .get(k)
              .map(x => x.aggregate(v.map(v => v._2: ProjectReference): _*))
              .orNull
        }
        .filter(p => p._2 != null))
  }

  def configs(cs: Configuration*): CrossProject[P] = {
    copy(projects.map((x) => x._1 -> x._2.configs(cs: _*)))
  }

  def disablePlugins(ps: AutoPlugin*): CrossProject[P] =
    copy(projects.map((x) => x._1 -> x._2.disablePlugins(ps: _*)))

  def enablePlugins(ns: Plugins*): CrossProject[P] =
    copy(projects.map((x) => x._1 -> x._2.enablePlugins(ns: _*)))

  def overrideConfigs(cs: Configuration*): CrossProject[P] =
    copy(projects.map((x) => x._1 -> x._2.overrideConfigs(cs: _*)))

  def dependsOn(deps: CrossClasspathDependency*): CrossProject[P] =
    copy(
      projects.map((x) => x._1 -> x._2.dependsOn(deps.map(_.dep(x._1)): _*)))

  def settings(ss: Def.SettingsDefinition*): CrossProject[P] =
    copy(projects.map((x) => x._1 -> x._2.settings(ss: _*)))

  override def toString: String = {
    val entries = this.projects.map {
      case (t, p) => s"${t.name} -> $p"
    }
    s"CrossProject(${entries.mkString("," + System.lineSeparator())})"
  }

  private[sbtcrossproject] def settings(
      p: Platform,
      settings: Seq[_root_.sbt.Def.Setting[_]]): CrossProject[P] =
    copy(projects + (p -> projects(p).settings(settings)))

  private def copy[Pl <: Platform](
      projects: Map[Platform, Project] = projects): CrossProject[Pl] =
    new CrossProject[Pl](crossType, projects)

}

object CrossProject extends CrossProjectExtra {

  def crossProject_impl(c: Context): c.Expr[Builder[Platform]] = {
    import c.universe._
    val enclosingValName = MacroUtils.definingValName(
      c,
      methodName =>
        s"""$methodName must be directly assigned to a val, such as `val x = $methodName`.""")
    val name = c.Expr[String](Literal(Constant(enclosingValName)))
    reify {
      new Builder(name.splice, file("."), Map.empty, CrossType.Full)
    }
  }
  // Inspired by sbt's Defaults.makeCrossSources
  private def makeCrossSources(sharedSrcDir: Option[File],
                               scalaBinaryVersion: String,
                               cross: Boolean): Seq[File] = {
    sharedSrcDir.fold[Seq[File]] {
      Seq.empty
    } { srcDir =>
      if (cross)
        Seq(srcDir.getParentFile / s"${srcDir.name}-$scalaBinaryVersion",
            srcDir)
      else
        Seq(srcDir)
    }
  }

  private def sharedSrcSettings(crossType: CrossType) = Seq(
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

  implicit def toCrossProject[P <: Platform](
      builder: Builder[P]): CrossProject[P] = {

    new CrossProject[P](builder.crossType, builder.projects.map(entry => {
      val settings = sharedSrcSettings(builder.crossType)
      entry._1 -> entry._2
        .settings(settings: _*)
        .enablePlugins(entry._2.plugins)
    }))
  }

  final class Builder[P <: Platform](val id: String,
                                     val root: File,
                                     val projects: Map[Platform, Project],
                                     val crossType: CrossType) {

    def crossType(crossType: CrossType): Builder[P] = {
      new Builder[P](id, root, projects, crossType)
    }

    def platform[P2 <: Platform](t: P2): Builder[P with P2] = {
      new Builder[P with P2](id,
                             root,
                             projects + (t -> Project(t.name, root / t.name)),
                             crossType)
    }

    def platforms[P2 <: Platform](t: P2*): Builder[P with P2] = {
      new Builder[P with P2](id, root, projects, crossType)
    }
  }

}

trait CrossProjectExtra {
  implicit def crossClasspathDependencyConstructor(
      cp: CrossProject[Platform]): CrossClasspathDependency.Constructor =
    new CrossClasspathDependency.Constructor(cp)

  implicit def crossClasspathDependency(
      cp: CrossProject[Platform]): CrossClasspathDependency =
    new CrossClasspathDependency(cp, None)

  def crossProject: CrossProject.Builder[Platform] =
    macro CrossProject.crossProject_impl
}
