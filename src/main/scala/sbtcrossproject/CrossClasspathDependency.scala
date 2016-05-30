package sbtcrossproject

import sbt._

final class CrossClasspathDependency(
    val project: CrossProject,
    val configuration: Option[String]
) {
  def jvm: ClasspathDependency = ClasspathDependency(project.jvm, configuration)
}

object CrossClasspathDependency {
  final class Constructor(crossProject: CrossProject) {
    def %(conf: Configuration): CrossClasspathDependency = %(conf.name)

    def %(conf: String): CrossClasspathDependency =
      new CrossClasspathDependency(crossProject, Some(conf))
  }
}
