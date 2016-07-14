package sbtcrossproject

import sbt._

/**
  * Created by grinder on 17.07.16.
  */
abstract class Platform(val name: String)

object JVM extends Platform("jvm") {

  implicit class JvmTypeExt[P <: JVM.type](project: CrossProject[P]) {

    implicit def JvmTypeExtFromBuilder[P <: JVM.type](
        builder: CrossProject.Builder[P]): JvmTypeExt[P] =
      new JvmTypeExt[P](builder: CrossProject[P])

    def jvmSettings(settings: Seq[Def.Setting[_]]): CrossProject[P] =
      project.settings(JVM, settings)

    def jvm = project.projects(JVM)
  }

}
