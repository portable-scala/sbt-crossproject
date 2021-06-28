addSbtPlugin("com.jsuereth" % "sbt-pgp"         % "1.1.0")
addSbtPlugin("com.typesafe" % "sbt-mima-plugin" % "0.8.1")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-feature",
  "-encoding",
  "utf8"
)
