import sbtcrossproject.{crossProject, CrossType}

val g = "org.example.unconditionnal-cross-dependencies"
val a = "bar"
val v = "0.1.0"

lazy val bar = crossProject
  .crossType(CrossType.Pure)
  .settings(
    scalaVersion := "2.12.20",
    organization := g,
    moduleName := a,
    version := v
  )

lazy val barJS  = bar.js
lazy val barJVM = bar.jvm

val noMacro = (g % a % v).cross(CrossVersion.binary)

lazy val foo = project.settings(
  scalaVersion := "2.12.20",
  libraryDependencies += noMacro
)
