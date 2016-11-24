libraryDependencies += "org.scala-sbt" % "scripted-plugin" % sbtVersion.value

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-feature",
  "-encoding",
  "utf8"
)
