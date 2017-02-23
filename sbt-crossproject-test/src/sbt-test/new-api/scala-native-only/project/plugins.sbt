resolvers += Resolver.url(
  "bintray-scala-native-sbt-plugins",
  url("http://dl.bintray.com/scala-native/sbt-plugins"))(
  Resolver.ivyStylePatterns)

addSbtPlugin("org.scala-native" % "sbt-scala-native" % "0.1.0")

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-feature",
  "-encoding",
  "utf8"
)
