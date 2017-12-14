import java.io.ByteArrayOutputStream
import sbtcrossproject.{crossProject, CrossType}

lazy val check    = taskKey[Unit]("check settings are applied")
lazy val expected = settingKey[String]("expected output")

def equals[T](actual: T, expected: T): Unit = {
  if (actual != expected) {
    assert(false, s"actual: $actual, expected: $expected")
  }
}

lazy val bar =
  crossProject(JVMPlatform.scala("2.11"),
               JVMPlatform.scala("2.12"),
               JVMPlatform.scala("2.13"))
    .crossType(CrossType.Pure)
    .settings(
      description := "common settings"
    )
    .jvmSettings(
      description := "jvm settings"
    )
    .jvmScalaSettings("2.11")(
      scalaVersion := "2.11.12",
      description := "2.11 settings"
    )
    .jvmScalaSettings("2.12")(
      scalaVersion := "2.12.4",
      description := "2.12 settings"
    )

lazy val bar211 = bar.jvmScala("2.11")
lazy val bar212 = bar.jvmScala("2.12")
lazy val bar213 = bar.jvmScala("2.13")

lazy val foo =
  crossProject(JVMPlatform.scala("2.11"), JVMPlatform.scala("2.12"))
    .crossType(CrossType.Pure)
    .dependsOn(bar)
    .jvmSettings(
      fork in run := true,
      outputStrategy := Some(CustomOutput(new ByteArrayOutputStream)),
      check := {
        (run in Compile).toTask("").value
        outputStrategy.value match {
          case Some(CustomOutput(stream: ByteArrayOutputStream)) =>
            equals(stream.toString.trim, expected.value)
          case _ => assert(false)
        }
      }
    )
    .jvmScalaSettings("2.11")(
      scalaVersion := "2.11.12",
      expected := "2.11"
    )
    .jvmScalaSettings("2.12")(
      scalaVersion := "2.12.4",
      expected := "2.12"
    )

lazy val foo211 = foo.jvmScala("2.11")
lazy val foo212 = foo.jvmScala("2.12")

check := {
  equals((description in bar211).value, "2.11 settings")
  equals((description in bar212).value, "2.12 settings")
  equals((description in bar213).value, "jvm settings")
}
