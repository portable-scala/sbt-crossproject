// COPIED FROM sbt-crossproject-test/src/sbt-test/plugins.sbt
val pluginVersion = sys.props.get("plugin.version").get
val snVersion     = sys.props.get("plugin.sn-version").get
val sjsVersion    = sys.props.get("plugin.sjs-version").get

addSbtPlugin("org.scala-js"     % "sbt-scalajs"              % sjsVersion)
addSbtPlugin("org.scala-native" % "sbt-scalajs-crossproject" % pluginVersion)
addSbtPlugin("org.scala-native" % "sbt-crossproject"         % pluginVersion)
addSbtPlugin("org.scala-native" % "sbt-scala-native"         % snVersion)

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-feature",
  "-encoding",
  "utf8"
)
