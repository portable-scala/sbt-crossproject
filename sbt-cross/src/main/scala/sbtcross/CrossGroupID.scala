package sbtcross

import sbt._
import StringUtilities.nonEmpty

import scala.language.experimental.macros
import scala.reflect.macros.Context

final class CrossGroupID private[sbtcross] (private val groupID: String) {
  def %%%(artifactID: String): CrossGroupArtifactID =
    macro CrossGroupID.cross_binary_impl
}

object CrossGroupID {

  /** Internal. Used by the macro implementing [[CrossGroupID.%%%]]. Use:
   *  {{{
   *  ("a" % artifactID % revision).cross(cross)
   *  }}}
   *  instead.
   */
  def withCross(groupID: CrossGroupID,
                artifactID: String,
                cross: CrossVersion): CrossGroupArtifactID = {
    nonEmpty(artifactID, "Artifact ID")
    new CrossGroupArtifactID(groupID.groupID, artifactID, cross)
  }

  def cross_binary_impl(c: Context { type PrefixType = CrossGroupID })(
      artifactID: c.Expr[String]): c.Expr[CrossGroupArtifactID] = {

    import c.universe._

    // Hack to work around bug in sbt macros (wrong way of collecting local
    // definitions)
    val keysSym =
      rootMirror.staticModule("_root_.sbtcross.CrossPlugin.AutoImport")
    val keys = c.Expr[CrossPlugin.AutoImport.type](Ident(keysSym))
    reify {
      val cross = keys.splice.crossPlatform.value.crossBinary
      CrossGroupID.withCross(c.prefix.splice, artifactID.splice, cross)
    }
  }
}
