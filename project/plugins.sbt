addSbtPlugin("com.github.sbt" % "sbt-ci-release"  % "1.11.2")
addSbtPlugin("com.typesafe"   % "sbt-mima-plugin" % "1.1.6")
addSbtPlugin("com.github.sbt" % "sbt2-compat"     % "0.2.0")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-feature",
  "-encoding",
  "utf8"
)
