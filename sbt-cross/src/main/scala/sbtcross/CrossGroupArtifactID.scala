package sbtcross

import sbt._
import StringUtilities.nonEmpty

final class CrossGroupArtifactID(groupID: String,
                                 artifactID: String,
                                 crossVersion: CrossVersion) {
  def %(revision: String): ModuleID = {
    nonEmpty(revision, "Revision")
    ModuleID(groupID, artifactID, revision).cross(crossVersion)
  }
}
