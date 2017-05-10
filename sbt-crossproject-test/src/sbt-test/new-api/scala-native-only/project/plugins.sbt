addSbtPlugin("org.scala-native" % "sbt-scala-native" % "0.2.1")

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-feature",
  "-encoding",
  "utf8"
)
