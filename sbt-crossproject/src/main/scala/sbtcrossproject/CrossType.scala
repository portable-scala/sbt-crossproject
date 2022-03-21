package sbtcrossproject

import sbt._

import java.io.File

abstract class CrossType {

  /** The base directory for a (true sbt) Project
   *  @param crossBase The base directory of the CrossProject
   *  @param projectType "jvm" or "js". Other values may be supported
   */
  @deprecated("use platformDir", "0.1.0")
  def projectDir(crossBase: File, projectType: String): File

  def projectDir(crossBase: File, platform: Platform): File

  /** The base directory for the JVM project */
  @deprecated("use platformDir(crossBase, JVMPlatform)", "0.1.0")
  final def jvmDir(crossBase: File): File = platformDir(crossBase, JVMPlatform)

  /** The base directory for the JS project */
  @deprecated("use platformDir(crossBase, JSPlatform)", "0.1.0")
  final def jsDir(crossBase: File): File = projectDir(crossBase, "js")

  /** The base directory for a (true sbt) Project
   *  @param crossBase The base directory of the CrossProject
   *  @param platform JSPlatform, JVMPlatform, NativePlatform, ...
   */
  final def platformDir(crossBase: File, platform: Platform): File =
    projectDir(crossBase, platform)

  /** The location of a shared source directory (if it exists)
   *  @param projectBase the base directory of a (true sbt) Project
   *  @param conf name of sub-directory for the configuration (typically "main"
   *      or "test")
   */
  def sharedSrcDir(projectBase: File, conf: String): Option[File]

  /** The location of a partially shared source directory (if it exists)
   *  @param projectBase the base directory of a (true sbt) Project
   *  @param platforms non-empty seq of JSPlatform, JVMPlatform, NativePlatform, ...
   *  @param conf name of sub-directory for the configuration (typically "main"
   *      or "test")
   */
  def partiallySharedSrcDir(projectBase: File,
                            platforms: Seq[Platform],
                            conf: String): Option[File] = None

  /** The location of a shared resources directory (if it exists)
   *  @param projectBase the base directory of a (true sbt) Project
   *  @param conf name of sub-directory for the configuration (typically "main"
   *      or "test")
   */
  def sharedResourcesDir(projectBase: File, conf: String): Option[File] = None

  /** The location of a partially shared resources directory (if it exists)
   *  @param projectBase the base directory of a (true sbt) Project
   *  @param conf name of sub-directory for the configuration (typically "main"
   *      or "test")
   */
  def partiallySharedResourcesDir(projectBase: File,
                                  platforms: Seq[Platform],
                                  conf: String): Option[File] = None

}

object CrossType {

  /** * <pre>
   * .
   * ├── js
   * ├── jvm
   * ├── native
   * └── shared
   * </pre>
   */
  object Full extends CrossType {

    @deprecated("use projectDir(crossBase: File, platform: Platform): File",
                "0.1.0")
    def projectDir(crossBase: File, projectType: String): File =
      crossBase / projectType

    def projectDir(crossBase: File, platform: Platform): File =
      crossBase / platform.identifier

    def sharedSrcDir(projectBase: File, conf: String): Option[File] =
      Some(projectBase.getParentFile / "shared" / "src" / conf / "scala")

    override def partiallySharedSrcDir(projectBase: File,
                                       platforms: Seq[Platform],
                                       conf: String): Option[File] = {
      val dir = platforms.map(_.identifier).mkString("-")
      Some(projectBase.getParentFile / dir / "src" / conf / "scala")
    }

    override def sharedResourcesDir(projectBase: File,
                                    conf: String): Option[File] =
      Some(projectBase.getParentFile / "shared" / "src" / conf / "resources")

    override def partiallySharedResourcesDir(projectBase: File,
                                             platforms: Seq[Platform],
                                             conf: String): Option[File] = {
      val dir = platforms.map(_.identifier).mkString("-")
      Some(projectBase.getParentFile / dir / "src" / conf / "resources")
    }
  }

  /**
   * <pre>
   * .
   * ├── .js
   * ├── .jvm
   * ├── .native
   * └── src
   * </pre>
   */
  object Pure extends CrossType {
    @deprecated("use projectDir(crossBase: File, platform: Platform): File",
                "0.1.0")
    def projectDir(crossBase: File, projectType: String): File =
      crossBase / ("." + projectType)

    def projectDir(crossBase: File, platform: Platform): File =
      crossBase / ("." + platform.identifier)

    def sharedSrcDir(projectBase: File, conf: String): Option[File] =
      Some(projectBase.getParentFile / "src" / conf / "scala")

    override def sharedResourcesDir(projectBase: File,
                                    conf: String): Option[File] =
      Some(projectBase.getParentFile / "src" / conf / "resources")
  }

  /**
   * <pre>
   * .
   * ├── js
   * ├── jvm
   * └── native
   * </pre>
   */
  object Dummy extends CrossType {
    @deprecated("use projectDir(crossBase: File, platform: Platform): File",
                "0.1.0")
    def projectDir(crossBase: File, projectType: String): File =
      crossBase / projectType

    def projectDir(crossBase: File, platform: Platform): File =
      crossBase / platform.identifier

    def sharedSrcDir(projectBase: File, conf: String): Option[File] = None

    override def sharedResourcesDir(projectBase: File,
                                    conf: String): Option[File] =
      None
  }
}
