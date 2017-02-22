// COPIED FROM sbt-crossproject-test/src/sbt-test/plugins.sbt

resolvers += Resolver.url(
  "bintray-scala-native-sbt-plugins",
  url("http://dl.bintray.com/scala-native/sbt-plugins"))(
  Resolver.ivyStylePatterns)

val pluginVersion = sys.props.get("plugin.version").getOrElse("0.1.0-SNAPSHOT")

addSbtPlugin("org.scala-js"     % "sbt-scalajs"              % "0.6.13")
addSbtPlugin("org.scala-native" % "sbt-scalajs-crossproject" % pluginVersion)
addSbtPlugin("org.scala-native" % "sbt-crossproject"         % pluginVersion)
addSbtPlugin("org.scala-native" % "sbt-scala-native"         % "0.0.0.1")

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-feature",
  "-encoding",
  "utf8"
)
