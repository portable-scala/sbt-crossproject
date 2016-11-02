package sbtcrossproject

import sbt._

import org.scalajs.sbtplugin.{ScalaJSCrossVersion, ScalaJSPlugin}
import org.scalajs.sbtplugin.impl.ScalaJSGroupID

import scala.scalanative.sbtplugin.{ScalaNativeCrossVersion, ScalaNativePlugin}
  
import scala.language.experimental.macros

import StringUtilities.nonEmpty

trait DependencyBuilders {
  final implicit def toCrossGroupID(groupID: String): CrossGroupID = {
    nonEmpty(groupID, "Group ID")
    new CrossGroupID(groupID)
  }
}

final class CrossGroupID private[sbtcrossproject] (private val groupID: String) {
  def %%%%(artifactID: String): CrossGroupArtifactID =
    macro CrossGroupID.auto_impl
}

object CrossGroupID {
  import scala.reflect.macros.Context

  /** Internal. Used by the macro implementing [[CrossGroupID.%%%%]]. Use:
   *  {{{
   *  ("a" % artifactID % revision).cross(cross)
   *  }}}
   *  instead.
   */
  def withCross(groupID: CrossGroupID, artifactID: String, 
    cross: CrossVersion): CrossGroupArtifactID = {

    nonEmpty(artifactID, "Artifact ID")
    new CrossGroupArtifactID(groupID.groupID, artifactID, cross)
  }

  def auto_impl(c: Context { type PrefixType = CrossGroupID })(
      artifactID: c.Expr[String]): c.Expr[CrossGroupArtifactID] = {
    import c.universe._

    // Hack to work around bug in sbt macros (wrong way of collecting local
    // definitions)
    val jsKeysSym = rootMirror.staticModule("_root_.org.scalajs.sbtplugin.ScalaJSPlugin.AutoImport")
    val jsKeys = c.Expr[ScalaJSPlugin.AutoImport.type](Ident(jsKeysSym))

    val nativeKeysSym = rootMirror.staticModule("_root_.scala.scalanative.sbtplugin.ScalaNativePlugin.AutoImport")
    val nativeKeys = c.Expr[ScalaNativePlugin.AutoImport.type](Ident(nativeKeysSym))

    reify {
      val cross = {
        if (jsKeys.splice.isScalaJSProject.value)
          ScalaJSCrossVersion.binary
        else if (nativeKeys.splice.isScalaNativeProject.value)
          ScalaNativeCrossVersion.binary
        else
          CrossVersion.binary
      }
      CrossGroupID.withCross(c.prefix.splice, artifactID.splice, cross)
    }
  }
}

final class CrossGroupArtifactID(groupID: String, artifactID: String, crossVersion: CrossVersion) {
  def %(revision: String): ModuleID = {
    nonEmpty(revision, "Revision")
    ModuleID(groupID, artifactID, revision).cross(crossVersion)
  }
}
