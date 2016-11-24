<h1>sbt-cross</h1>

[![Build Status](https://travis-ci.org/scala-native/sbt-cross.svg?branch=master)](https://travis-ci.org/scala-native/sbt-cross)

Cross-platform compilation support for sbt.

<h2>Installation</h2>

<h3>Cross-Compiling Scala.js, JVM and Native</h3>

In `project/plugins.sbt`:

```scala
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.13")

resolvers += Resolver.sonatypeRepo("snapshots")                         // (1)
addSbtPlugin("org.scala-native" % "sbt-cross"         % "0.1-SNAPSHOT") // (2)
addSbtPlugin("org.scala-native" % "sbt-scalajs-cross" % "0.1-SNAPSHOT") // (3)
addSbtPlugin("org.scala-native" % "sbt-native"        % "0.1-SNAPSHOT") // (4)
```

In `build.sbt`:

```scala
// (5) shadow sbt-scalajs' crossProject and CrossType until Scala.js 1.0.0 is released
import sbtcross.{crossProject, CrossType}

val sharedSettings = Seq(scalaVersion := "2.11.8") // Scala Native only supports 2.11

lazy val bar =
  // (6) select supported platforms
  crossProject(JSPlatform, JVMPlatform, NativePlatform)
    .crossType(CrossType.Pure) // [Pure, Full, Dummy], default: CrossType.Full
    .settings(sharedSettings)
    .jsSettings(/* ... */) // defined in sbt-scalajs-cross
    .jvmSettings(/* ... */)
    // (7) configure Scala-Native settings
    .nativeSettings(/* ... */) // defined in sbt-native

lazy val barJS     = bar.js
lazy val barJVM    = bar.jvm
lazy val barNative = bar.native

lazy val foo =
  crossProject(JSPlatform, JVMPlatform, NativePlatform)
    .settings(
      // %%% now include Scala Native. It applies to all selected platforms
      libraryDependencies += "org.example" %%% "foo" % "1.2.3"
    )

lazy val fooJS = foo.js
lazy val fooJVM = foo.jvm
lazy val fooNative = foo.native
```

<h3>Cross-Compiling JVM and Native</h3>

In `project/plugins.sbt`:

```scala
resolvers += Resolver.sonatypeRepo("snapshots")
addSbtPlugin("org.scala-native" % "sbt-cross" % "0.1-SNAPSHOT") // (1)
addSbtPlugin("org.scala-native" % "sbt-native" % "0.1-SNAPSHOT") // (2)
```

In `build.sbt`:

```scala
val sharedSettings = Seq(scalaVersion := "2.11.8") // Scala Native only supports 2.11

lazy val bar =
  // (3) select supported platforms
  crossProject(JVMPlatform, NativePlatform)
    .settings(sharedSettings)
    // (4) configure JVM settings
    .jvmSettings(/* ... */)
    // (5) configure Scala-Native settings
    .nativeSettings(/* ... */) // defined in sbt-native

lazy val barJVM    = bar.jvm
lazy val barNative = bar.native
```

<h3>Migration from Scala.js' default crossProject</h3>

We carefully implemented sbt-cross to be mostly source compatible with Scala.js crossProject

In `project/plugins.sbt`:

```scala
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.13")

resolvers += Resolver.sonatypeRepo("snapshots")                         // (1)
addSbtPlugin("org.scala-native" % "sbt-cross"         % "0.1-SNAPSHOT") // (2)
addSbtPlugin("org.scala-native" % "sbt-scalajs-cross" % "0.1-SNAPSHOT") // (3)
```

In `build.sbt`:

```scala
// (5) shadow sbt-scalajs' crossProject and CrossType until Scala.js 1.0.0 is released
import sbtcross.{crossProject, CrossType}

lazy val bar =
  // (4) select supported platforms
  crossProject(JSPlatform, JVMPlatform)
    .crossType(CrossType.Pure) // [Pure, Full, Dummy], default: CrossType.Full
    .settings(/* ... */)
    .jsSettings(/* ... */) // defined in sbt-scalajs-cross
    .jvmSettings(/* ... */)

lazy val barJS = bar.js
lazy val barJVM = bar.jvm
```
