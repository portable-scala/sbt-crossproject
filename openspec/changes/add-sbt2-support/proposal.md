## Why

sbt-crossproject currently publishes only for SBT 1, which prevents SBT 2 builds from using the plugin. The repository needs a dual-target build so supported SBT 1 users remain supported while SBT 2 users receive a native Scala 3 plugin artifact.

## What Changes

- Cross-build all three published plugins for Scala 2.12/SBT 1.9.0+ and Scala 3/SBT 2.0.4+.
- Keep the public cross-project API, platform-aware `%%%` dependency syntax, and practical directly assigned `val` forms consistent across both SBT variants.
- Align the root and child cross-build configuration so aggregate compilation and cross-publication select valid target combinations.
- Compile and test both SBT integrations with maintained Scala.js and Scala Native plugins that run on Java 17.
- Require Java 17 or newer and validate both SBT variants in CI.
- Apply MiMa only where a previous artifact exists, while retaining binary-compatibility checks for the SBT 1 artifact family.
- Publish distinct artifacts for both SBT plugin families through the existing release workflow.
- Update project documentation with SBT 2 installation and runtime requirements.

## Capabilities

### New Capabilities
- `dual-sbt-plugin-support`: Build, validate, publish, and use sbt-crossproject plugins on both SBT 1 and SBT 2 without losing practical source compatibility.

### Modified Capabilities

None.

## Impact

- Build definition: `build.sbt`, `project/Extra.scala`, `project/plugins.sbt`, and root aggregation.
- Plugin implementation: version-specific Scala 2.12 and Scala 3 source directories, including macro implementations.
- Integration tests: scripted fixtures and execution for both SBT targets.
- Automation: CI and release workflows require Java 17 and dual-target checks.
- Published modules: `sbt-crossproject`, `sbt-scalajs-crossproject`, and `sbt-scala-native-crossproject` gain SBT 2/Scala 3 artifact variants while retaining SBT 1 variants.
- Documentation: SBT 1.9.0 and SBT 2.0.4 minimums, supported Scala.js and Scala Native versions, and Java 17 as the minimum runtime.
