addSbtPlugin("org.scala-native" % "sbt-scala-native" % "0.3.0")

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-feature",
  "-encoding",
  "utf8"
)
