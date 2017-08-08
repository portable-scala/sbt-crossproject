val snVersion = sys.props.get("plugin.sn-version").get

addSbtPlugin("org.scala-native" % "sbt-scala-native" % snVersion)

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-feature",
  "-encoding",
  "utf8"
)
