val pluginVersion = sys.props.get("plugin.version").get
val snVersion     = sys.props.get("plugin.sn-version").get

addSbtPlugin(
  "org.portable-scala"          % "sbt-scala-native-crossproject" % pluginVersion)
addSbtPlugin("org.scala-native" % "sbt-scala-native"              % snVersion)

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-feature",
  "-encoding",
  "utf8"
)
