sbtPlugin := true

scalaVersion := "2.10.6"

organization := "org.scala-native"

name := "sbt-cross-project"

version := "0.1.0-SNAPSHOT"

resolvers += Resolver.sonatypeRepo("snapshots")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.13")
addSbtPlugin("org.scala-native" % "sbtplugin"  % "0.1-SNAPSHOT")

ScriptedPlugin.scriptedSettings
scriptedLaunchOpts := { scriptedLaunchOpts.value ++
  Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
}
scriptedBufferLog := false