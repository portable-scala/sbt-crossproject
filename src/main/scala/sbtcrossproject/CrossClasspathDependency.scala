package sbtcrossproject

import sbt._

final class CrossClasspathDependency(val project: CrossProject[Platform],
                                     val configuration: Option[String]) {
  def dep(platform: Platform): ClasspathDependency =
    ClasspathDependency(project.projects(platform), configuration)
}

object CrossClasspathDependency {

  final class Constructor(crossProject: CrossProject[Platform]) {
    def %(conf: Configuration): CrossClasspathDependency = %(conf.name)

    def %(conf: String): CrossClasspathDependency =
      new CrossClasspathDependency(crossProject, Some(conf))
  }
}
