<h1>sbt-crossproject</h1>

[![Join the chat at https://gitter.im/scala-native/sbt-crossproject](https://badges.gitter.im/scala-native/sbt-crossproject.svg)](https://gitter.im/scala-native/sbt-crossproject)

[![Build Status](https://travis-ci.org/scala-native/sbt-crossproject.svg?branch=master)](https://travis-ci.org/scala-native/sbt-crossproject)

Cross-platform compilation support for sbt.

<h2>Installation</h2>

<h3>Cross-Compiling Scala.js, JVM and Native</h3>

In `project/plugins.sbt`:

```scala
addSbtPlugin("org.scala-js"     % "sbt-scalajs"              % "0.6.19")
addSbtPlugin("org.scala-native" % "sbt-crossproject"         % "0.2.2")  // (1)
addSbtPlugin("org.scala-native" % "sbt-scalajs-crossproject" % "0.2.2")  // (2)
addSbtPlugin("org.scala-native" % "sbt-scala-native"         % "0.3.3")  // (3)
```

In `build.sbt`:

```scala
// (4) shadow sbt-scalajs' crossProject and CrossType until Scala.js 1.0.0 is released
import sbtcrossproject.{crossProject, CrossType}

val sharedSettings = Seq(scalaVersion := "2.11.11")

lazy val bar =
  // (5) select supported platforms
  crossProject(JSPlatform, JVMPlatform, NativePlatform)
    .crossType(CrossType.Pure) // [Pure, Full, Dummy], default: CrossType.Full
    .settings(sharedSettings)
    .jsSettings(/* ... */) // defined in sbt-scalajs-crossproject
    .jvmSettings(/* ... */)
    // (6) configure Scala-Native settings
    .nativeSettings(/* ... */) // defined in sbt-scala-native

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
addSbtPlugin("org.scala-native" % "sbt-crossproject" % "0.2.2") // (1)
addSbtPlugin("org.scala-native" % "sbt-scala-native" % "0.3.3") // (2)
```

In `build.sbt`:

```scala
val sharedSettings = Seq(scalaVersion := "2.11.11")

lazy val bar =
  // (3) select supported platforms
  crossProject(JVMPlatform, NativePlatform)
    .settings(sharedSettings)
    // (4) configure JVM settings
    .jvmSettings(/* ... */)
    // (5) configure Scala-Native settings
    .nativeSettings(/* ... */) // defined in sbt-scala-native

lazy val barJVM    = bar.jvm
lazy val barNative = bar.native
```

<h3>Migration from Scala.js' default crossProject</h3>

We carefully implemented sbt-crossproject to be mostly source compatible with Scala.js crossProject

In `project/plugins.sbt`:

```scala
addSbtPlugin("org.scala-js"     % "sbt-scalajs"              % "0.6.19")
addSbtPlugin("org.scala-native" % "sbt-crossproject"         % "0.2.2")  // (1)
addSbtPlugin("org.scala-native" % "sbt-scalajs-crossproject" % "0.2.2")  // (2)
```

In `build.sbt`:

```scala
// (3) shadow sbt-scalajs' crossProject and CrossType until Scala.js 1.0.0 is released
import sbtcrossproject.{crossProject, CrossType}

lazy val bar =
  // (4) select supported platforms
  crossProject(JSPlatform, JVMPlatform)
    .crossType(CrossType.Pure) // [Pure, Full, Dummy], default: CrossType.Full
    .settings(/* ... */)
    .jsSettings(/* ... */) // defined in sbt-scalajs-crossproject
    .jvmSettings(/* ... */)

lazy val barJS = bar.js
lazy val barJVM = bar.jvm
```

<h3>No prefix for jvm project</h3>

sbt-crossproject appends the platform suffix to the sbt project. For the sake of brevity,
it's possible to not append the suffix for the jvm platform.

```scala
In `build.sbt`:

lazy val bar =
  // (4) select supported platforms
  crossProject(JSPlatform, JVMPlatformNoSuffix)
    .crossType(CrossType.Pure)
    .settings(/* ... */)
    .jsSettings(/* ... */)
    .jvmSettings(/* ... */)

lazy val barJS = bar.js
lazy val barJVM = bar.jvm
```

the `barJVM` project is now available as simply `bar` in your sbt prompt.

<h3>When using Build.scala</h3>

```
import scala.scalanative.sbtplugin.ScalaNativePlugin.autoImport._
import sbtcrossproject.CrossPlugin.autoImport._
import scalajscrossproject.ScalaJSCrossPlugin.autoImport.{toScalaJSGroupID => _, _}
import scalajscrossproject.JSPlatform
import sbtcrossproject.{crossProject, CrossType}
```

<h2>Configuration</h2>

<h3>CrossTypes</h3>

Setting a specific CrossType allows the definition of a custom source tree
layout for managing native-, js- and jvm-specific sources in directories of their
own.

sbt-cross provides by default 3 implementations of the CrossType class that one can
pass as `.crossType` parameter:

- `.crossType(CrossType.Pure)`:

```
.
├── .js
├── .jvm
├── .native
└── src
```
This layout is preferred for codebases which do not contain any platform-specific code.

Since this is the case of most existing sbt projects it is often desired during conversion to sbt-cross to place the cross-project at the root of the project source tree.

This can be done with the following set of options:

`lazy val foo = crossProject.crossType(CrossType.Pure).in(file("."))`

- `.crossType(CrossType.Full)`

```
.
├── js
├── jvm
├── native
└── shared
```

This layout gives full control by providing a `shared` directory for common code.

- `.crossType(CrossType.Dummy)`

```
.
├── js
├── jvm
└── native
```

- `.crossType({/*custom*/})`

One can easily extend CrossType and provide a custom tree structure.
