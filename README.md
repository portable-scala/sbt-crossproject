<h1>sbt-crossproject</h1>

Cross-platform compilation support for sbt.

Requirements since sbt-crossproject 1.4.0:

* sbt 1.11.5+ or sbt 2.0.6+
* For `JSPlatform`: Scala.js 1.22.0+
* For `NativePlatform`: Scala Native 0.5.11+
* JDK 17+

For older versions of dependencies, use sbt-crossproject 1.3.2, which requires:

* sbt 1.2.1+
* For `JSPlatform`: Scala.js 0.6.23+ or 1.0.0+
* For `NativePlatform`: Scala Native 0.3.7+
* JDK 8+

<h2>Installation</h2>

<h3>Cross-Compiling Scala.js, JVM and Native</h3>

In `project/plugins.sbt`:

```scala
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject"      % "1.4.0")
addSbtPlugin("org.portable-scala" % "sbt-scala-native-crossproject" % "1.4.0")
addSbtPlugin("org.scala-js"       % "sbt-scalajs"                   % "1.22.0")
addSbtPlugin("org.scala-native"   % "sbt-scala-native"              % "0.5.11")
```

In `build.sbt`:

```scala
val sharedSettings = Seq(scalaVersion := "2.13.18")

lazy val bar =
  // select supported platforms
  crossProject(JSPlatform, JVMPlatform, NativePlatform)
    .crossType(CrossType.Pure) // [Pure, Full, Dummy], default: CrossType.Full
    .settings(sharedSettings)
    .jsSettings(/* ... */) // defined in sbt-scalajs-crossproject
    .jvmSettings(/* ... */)
    // configure Scala-Native settings
    .nativeSettings(/* ... */) // defined in sbt-scala-native

lazy val foo =
  crossProject(JSPlatform, JVMPlatform, NativePlatform)
    .settings(sharedSettings)
    .settings(
      // sbt 1.x: use %%%
      libraryDependencies += "org.example" %%% "foo" % "1.2.3",
      // sbt 2.x: use %%
      libraryDependencies += "org.example" %% "foo" % "1.2.3",
    )
```

<h3>Removing the platform suffix for one "default" platform</h3>

If you mainly use one "default" platform in your everyday development, you can tell sbt-crossproject not to add the platform suffix to its project ID.
For example, assuming you mainly compile and test for the JVM, you can write:

```scala
lazy val bar =
  crossProject(JSPlatform, JVMPlatform, NativePlatform)
    .withoutSuffixFor(JVMPlatform)
    .crossType(...)
    .settings(...)
```

The call to `withoutSuffixFor` must come first after the call to `crossProject()`, otherwise it will not compile.

Now, in the sbt prompt, you can do

```
> bar/test
```

to test the JVM platform (instead of `barJVM/test`).
This of course applies to all tasks.

Note that *inside the build*, you still need to use `barJVM` to the JVM `Project`.
`withoutSuffixFor` only changes the `id` of the project, which is used in the sbt prompt.

<h3>Detecting the current platform in a project's settings</h3>

Within the settings of a `crossProject`, you can detect the platform for which those settings are being applied to with `crossProjectPlatform`.
Here is a contrived example:

```scala
lazy val bar =
  crossProject(JSPlatform, JVMPlatform, NativePlatform)
    .crossType(...)
    .settings(
      name := "bar for " + crossProjectPlatform.value.identifier
    )

...
```

<h3>Cross-Compiling JVM and Native</h3>

In `project/plugins.sbt`:

```scala
addSbtPlugin("org.portable-scala" % "sbt-scala-native-crossproject" % "1.4.0")
addSbtPlugin("org.scala-native"   % "sbt-scala-native"              % "0.5.11")
```

In `build.sbt`:

```scala
ThisBuild / scalaVersion := "2.13.18"

lazy val bar =
  // select supported platforms
  crossProject(JVMPlatform, NativePlatform)
    .settings(sharedSettings)
    // configure JVM settings
    .jvmSettings(/* ... */)
    // configure Scala-Native settings
    .nativeSettings(/* ... */) // defined in sbt-scala-native
```

<h3>When using Build.scala</h3>

```
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType, _}
import scalajscrossproject.ScalaJSCrossPlugin.autoImport._
import scalanativecrossproject.ScalaNativeCrossPlugin.autoImport._
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

<h3>Customizing Directory Structure for Complex Projects</h3>

For complex projects it can be desirable to adopt a non-flat directory structure.

SBT provides an `in file("path")` mechanism for multi-project builds ( https://www.scala-sbt.org/1.x/docs/Multi-Project.html )

CrossProject also has support, for example:

```
.
├── apps
│   ├── a
│   └── b
└── lib
    ├── a
    ├── b
    └── c
```

can be configured as a normal `CrossProject` with

```
lazy val appA = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("app/a"))
  .dependsOn(libA, libB)

lazy val appB = crossProject(JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("app/b"))
  .dependsOn(libB, libC)

lazy val libA = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("lib/a"))

lazy val libB = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("lib/b"))

lazy val libC = crossProject(JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("lib/c"))
```
