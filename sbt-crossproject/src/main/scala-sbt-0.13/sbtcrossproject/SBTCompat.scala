package sbtcrossproject

private[sbtcrossproject] object SBTCompat {
  trait CompositeProject { this: CrossProject =>
    def componentProjects: Seq[sbt.Project]
  }
}
