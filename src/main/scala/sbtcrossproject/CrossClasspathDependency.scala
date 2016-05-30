package sbtcrossproject

import sbt._

final class CrossClasspathDependency[P <: CrossPlatform](
    val project: CrossProject[P],
    val configuration: Option[String]
) {
  def dep(p: P): ClasspathDependency =
    ClasspathDependency(project.projects(p), configuration)
}

object CrossClasspathDependency {
  final class Constructor[P <: CrossPlatform](crossProject: CrossProject[P]) {
    def %(conf: Configuration): CrossClasspathDependency[P] = %(conf.name)

    def %(conf: String): CrossClasspathDependency[P] =
      new CrossClasspathDependency(crossProject, Some(conf))
  }
}
