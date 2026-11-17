val g = "org.example.cross-dependencies-without-cross"
val a = "bar"
val v = "0.1.0"

val platformDependency = Def.setting {
  val dependency = g %% a % v
  if (sbtVersion.value.startsWith("1.")) dependency.cross(crossVersion.value)
  else dependency
}

val baseSettings = Seq(scalaVersion := "2.12.21")

val externalDependency = Seq(libraryDependencies += platformDependency.value)

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
