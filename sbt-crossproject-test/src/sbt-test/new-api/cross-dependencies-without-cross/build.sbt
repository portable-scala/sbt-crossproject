val g = "org.example.cross-dependencies-without-cross"
val a = "bar"
val v = "0.1.0"

val baseSettings = Seq(scalaVersion := "2.11.8")

val externalDependency = Seq(libraryDependencies += g %%% a % v)

lazy val bar = project
  .settings(baseSettings)
  .settings(
    organization := g,
    moduleName := a,
    version := v
  )

lazy val foo = project.settings(baseSettings).settings(externalDependency)

lazy val foo2 = project.settings(baseSettings).dependsOn(bar)

lazy val barJS = project
  .settings(baseSettings)
  .settings(externalDependency)
  .enablePlugins(ScalaJSPlugin)
