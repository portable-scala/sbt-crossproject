package scalajscrossproject

import org.scalajs.sbtplugin._
import impl._

final class ScalaJSGroupIDCompat private[scalajscrossproject] (
    private val groupID: String) {
  @deprecated(
    """Use %%% if possible, or '"com.example" % "foo" % "1.0" cross """ +
      """ScalaJSCrossVersion.binary"'""",
    "0.3.0")
  def %%%!(artifactID: String): CrossGroupArtifactID =
    new CrossGroupArtifactID(groupID, artifactID, ScalaJSCrossVersion.binary)
}
