package sbtcrossproject

import sbt._

import java.io.File

abstract class CrossType {

  /** The base directory for a (true sbt) Project
   *  @param crossBase The base directory of the CrossProject
   *  @param projectType "jvm" or "js". Other values may be supported
   */
  def projectDir(crossBase: File, projectType: String): File

  /** The base directory for given platform's project */
  def dir(platform: CrossPlatform, crossBase: File) =
    projectDir(crossBase, platform.name)

  /** The base directory for the JVM project */
  final def jvmDir(crossBase: File): File = dir(JVM, crossBase)

  /** The location of a shared source directory (if it exists)
   *  @param projectBase the base directory of a (true sbt) Project
   *  @param conf name of sub-directory for the configuration (typically "main"
   *      or "test")
   */
  def sharedSrcDir(projectBase: File, conf: String): Option[File]

}

object CrossType {

  object Full extends CrossType {
    def projectDir(crossBase: File, projectType: String): File =
      crossBase / projectType

    def sharedSrcDir(projectBase: File, conf: String): Option[File] =
      Some(projectBase.getParentFile / "shared" / "src" / conf / "scala")
  }

  object Pure extends CrossType {
    def projectDir(crossBase: File, projectType: String): File =
      crossBase / ("." + projectType)

    def sharedSrcDir(projectBase: File, conf: String): Option[File] =
      Some(projectBase.getParentFile / "src" / conf / "scala")
  }

  object Dummy extends CrossType {
    def projectDir(crossBase: File, projectType: String): File =
      crossBase / projectType

    def sharedSrcDir(projectBase: File, conf: String): Option[File] = None
  }

}
