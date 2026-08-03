## 1. Cross-Build Structure

- [x] 1.1 Apply the shared Scala 2.12/SBT 1.9.0 and Scala 3/SBT 2.0.4 cross-build settings to the root and all plugin modules.
- [x] 1.2 Remove root compiled-product dependencies on child projects while retaining root aggregation for build and release tasks.
- [x] 1.3 Verify root and module compilation select valid Scala/SBT coordinates for both cross-build entries after the platform upgrade.
- [x] 1.4 Configure Scala.js 1.22.0 and Scala Native 0.5.12 for both targets while retaining SBT 1-only compatibility libraries.

## 2. Version-Specific Plugin Sources

- [x] 2.1 Keep shared plugin behavior in the common source directory and preserve the existing Scala 2.12 macro implementation in its version-specific directory.
- [x] 2.2 Complete the Scala 3 plugin and package-object entry points with the same public cross-project API and `%%%` dependency syntax as the Scala 2.12 build.
- [x] 2.3 Use SBT-compatible local project references for cross-project aggregation and classpath dependencies, then compile the behavior on both SBT targets.

## 3. Scala 3 Project-Name Macro

- [x] 3.1 Update Scala 3 project-name derivation to support ordinary and lazy vals, access modifiers, backticked names, explicit type annotations, multiline declarations, and comments or whitespace around the assignment.
- [x] 3.2 Preserve the deprecated no-argument macro behavior and its default platform selection on SBT 2.
- [x] 3.3 Add focused build-loading fixtures for every supported declaration form and for the compile-time error produced by an indirect macro call.

## 4. Scripted Test Matrix

- [x] 4.1 Configure scripted launch properties to use SBT 1.9.0 with Scala.js 1.22.0 and Scala Native 0.5.12 for the SBT 1 target, and retain the selected SBT 2 versions.
- [x] 4.2 Update SBT 1 scripted fixtures to Scala 2.12.20 and maintained platform-plugin APIs without changing the cross-project behavior they verify.
- [x] 4.3 Run the complete scripted suite for Scala 2.12/SBT 1 and resolve all regressions.
- [x] 4.4 Run the complete scripted suite for Scala 3/SBT 2 and resolve all regressions.

## 5. Compatibility, CI, and Release

- [x] 5.1 Configure MiMa to check all existing SBT 1 artifact families and skip the new SBT 2 artifact family until it has a published predecessor.
- [x] 5.2 Update CI and release jobs to use Java 17 or newer.
- [x] 5.3 Add explicit CI validation for compilation, MiMa where applicable, and scripted tests on both cross-build entries.
- [x] 5.4 Verify the root cross-publish selection produces the expected three SBT 1 and three SBT 2 artifact coordinates for one release version.
- [x] 5.5 Verify the existing `sbt-ci-release` workflow publishes both cross-build entries without custom per-module release duplication.

## 6. Final Validation and Documentation

- [x] 6.1 Run aggregate compilation for both cross-build entries after the platform upgrade and confirm the root does not resolve mixed `_sbt2_2.12` coordinates.
- [x] 6.2 Run formatting and all configured compatibility checks, including MiMa for every SBT 1 module.
- [x] 6.3 Update README requirements and installation guidance with SBT 1.9.0, SBT 2.0.4, and Java 17 minimums plus the maintained Scala.js and Scala Native versions.
- [x] 6.4 Confirm the final diff contains only the dual-SBT implementation, tests, automation, release, and documentation changes described by this proposal.
