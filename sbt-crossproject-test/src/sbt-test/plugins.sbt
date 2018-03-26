// COPIED FROM sbt-crossproject-test/src/sbt-test/plugins.sbt
val pluginVersion = sys.props.get("plugin.version").get
val sjsVersion    = sys.props.get("plugin.sjs-version").get
val snVersion     = sys.props.get("plugin.sn-version").get

addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % pluginVersion)
addSbtPlugin(
  "org.portable-scala"          % "sbt-scala-native-crossproject" % pluginVersion)
addSbtPlugin("org.scala-js"     % "sbt-scalajs"                   % sjsVersion)
addSbtPlugin("org.scala-native" % "sbt-scala-native"              % snVersion)

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-feature",
  "-encoding",
  "utf8"
)
