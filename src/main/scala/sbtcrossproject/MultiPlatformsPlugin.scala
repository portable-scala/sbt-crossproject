package sbtcrossproject

import sbt._

object CrossProjectPlugin extends AutoPlugin {

  val autoImport = AutoImport
  object AutoImport extends DependencyBuilders 
                    with CrossProjectExtra {}
}
