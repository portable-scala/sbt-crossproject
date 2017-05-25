package scalajscrossproject

import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.impl.ScalaJSGroupID

import sbtcrossproject.CrossPlugin.autoImport._

import sbt._

import scala.language.implicitConversions

object ScalaJSCrossPlugin extends AutoPlugin {
  override def trigger           = allRequirements
  override def requires: Plugins = ScalaJSPlugin

  object autoImport extends ScalaJSCross {
    final implicit def toScalaJSGroupID(groupID: String): ScalaJSGroupID =
      throw new Exception("???")

    final implicit def toScalaJSGroupIDCompat(
        groupID: String): ScalaJSGroupIDCompat =
      new ScalaJSGroupIDCompat(groupID)
  }

  import autoImport._

  override def projectSettings: Seq[Setting[_]] = Seq(
    crossPlatform := JSPlatform
  )
}
