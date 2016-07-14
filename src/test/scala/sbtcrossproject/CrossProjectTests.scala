package sbtcrossproject

import org.scalatest.{FlatSpec, Matchers}
import sbt._
import sbt.plugins.CorePlugin

/**
  * Created by grinder on 13.09.16.
  */
class CrossProjectTests
    extends FlatSpec
    with Matchers
    with CrossProjectExtra
    with BuildExtra {

  "Cross Project" should "contains valid inner projects" in {
    val project = crossProject.platform(JVM).platform(TestPlatform)

    assert(project.id == "project")
    assert(project.projects(JVM).id == "jvm")
    assert(project.projects(JVM).base == file(".") / "jvm")
    assert(project.projects(TestPlatform).id == "test")
    assert(project.projects(TestPlatform).base == file(".") / "test")
  }

  it should "contains valid state after aggregation" in {
    val project1 = crossProject.platform(JVM)
    val project2 =
      crossProject.platform(JVM).platform(TestPlatform)
    val result = project1.aggregate(project2)

    assert(result.projects.get(JVM).isDefined)
    assert(result.projects.get(TestPlatform).isEmpty)
  }

  it should "change root dir after calling 'in' method" in {
    val project =
      crossProject.platform(JVM).platform(TestPlatform).in(file("testdir"))

    assert(project.projects(JVM).base == file("testdir") / "jvm")
    assert(project.projects(TestPlatform).base == file("testdir") / "test")
  }

  "enablePlugins method" should "enable plugins for each sub project" in {
    val project = crossProject
      .platform(JVM)
      .platform(TestPlatform)
      .enablePlugins(CrossProjectPlugin, CorePlugin)

    assert(
      project
        .projects(JVM)
        .plugins
        .toString == "sbtcrossproject.CrossProjectPlugin && sbt.plugins.CorePlugin")
    assert(
      project
        .projects(TestPlatform)
        .plugins
        .toString == "sbtcrossproject.CrossProjectPlugin && sbt.plugins.CorePlugin")
  }

  "disablePlugins method" should "disable plugins for each sub project" in {
    val project = crossProject
      .platform(JVM)
      .platform(TestPlatform)
      .disablePlugins(CrossProjectPlugin, CorePlugin)
    assert(
      project
        .projects(JVM)
        .plugins
        .toString == "!sbtcrossproject.CrossProjectPlugin && !sbt.plugins.CorePlugin")
  }

}

object TestPlatform extends Platform("test") {

  implicit class TestTypeExt[P <: TestPlatform.type](project: CrossProject[P]) {

    implicit def TestTypeExtFromBuilder[P <: TestPlatform.type](
        builder: CrossProject.Builder[P]): TestTypeExt[P] =
      new TestTypeExt[P](builder: CrossProject[P])

    def testSettings(settings: Seq[Def.Setting[_]]): CrossProject[P] =
      project.settings(JVM, settings)

    def test = project.projects(JVM)
  }

}
