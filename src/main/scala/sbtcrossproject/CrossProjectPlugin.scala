package sbtcrossproject
import sbt._

/**
  * Created by grinder on 23.07.16.
  */
object CrossProjectPlugin extends AutoPlugin {

  val autoImport = AutoImport
  object AutoImport extends CrossProjectExtra {}

}
