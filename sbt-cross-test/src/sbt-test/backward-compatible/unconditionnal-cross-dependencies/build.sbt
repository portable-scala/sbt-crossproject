import sbtcross.{crossProject, CrossType}

val g = "com.example.unconditionnal-cross-dependencies"
val a = "bar"
val v = "0.1.0-SNAPSHOT"

lazy val bar = crossProject
  .crossType(CrossType.Pure)
  .settings(
    scalaVersion := "2.11.8",
    organization := g,
    moduleName := a,
    version := v
  )

lazy val barJS  = bar.js
lazy val barJVM = bar.jvm

val noMacro = g %%%! a % v

lazy val foo = project.settings(
  scalaVersion := "2.11.8",
  libraryDependencies += noMacro
)
