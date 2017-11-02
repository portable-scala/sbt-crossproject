package sbtcrossproject

import sbt._

@deprecated("Kept for binary compatibility; will be removed", "0.3.0")
final class CrossGroupArtifactID(groupID: String,
                                 artifactID: String,
                                 crossVersion: CrossVersion) {
  def %(revision: String): ModuleID = {
    nonEmpty(revision, "Revision")
    ModuleID(groupID, artifactID, revision).cross(crossVersion)
  }
}
