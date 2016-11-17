package scalajscross

import org.scalajs.sbtplugin._
import impl._

final class ScalaJSGroupIDCompat private[scalajscross] (
    private val groupID: String) {
  def %%%!(artifactID: String): CrossGroupArtifactID =
    new CrossGroupArtifactID(groupID, artifactID, ScalaJSCrossVersion.binary)
}
