addSbtPlugin("com.github.sbt" % "sbt-ci-release"  % "1.5.11")
addSbtPlugin("com.typesafe"   % "sbt-mima-plugin" % "0.8.1")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-feature",
  "-encoding",
  "utf8"
)
