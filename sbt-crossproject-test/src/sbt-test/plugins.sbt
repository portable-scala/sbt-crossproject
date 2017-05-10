// COPIED FROM sbt-crossproject-test/src/sbt-test/plugins.sbt
addSbtPlugin("org.scala-js"     % "sbt-scalajs"              % "0.6.16")
addSbtPlugin("org.scala-native" % "sbt-scalajs-crossproject" % "0.6.16")
addSbtPlugin("org.scala-native" % "sbt-scala-native"         % "0.2.1")
addSbtPlugin("org.scala-native" % "sbt-crossproject"         % "0.2.0")

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-feature",
  "-encoding",
  "utf8"
)
