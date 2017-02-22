// COPIED FROM sbt-crossproject-test/src/sbt-test/plugins.sbt

resolvers += Resolver.sonatypeRepo("snapshots")

val pluginVersion = sys.props.get("plugin.version").getOrElse("0.1.0-SNAPSHOT")

addSbtPlugin("org.scala-js"     % "sbt-scalajs"              % "0.6.13")
addSbtPlugin("org.scala-native" % "sbt-scalajs-crossproject" % pluginVersion)
addSbtPlugin("org.scala-native" % "sbt-crossproject"         % pluginVersion)
addSbtPlugin("org.scala-native" % "sbt-scala-native"         % "0.1.0-SNAPSHOT")

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-feature",
  "-encoding",
  "utf8"
)