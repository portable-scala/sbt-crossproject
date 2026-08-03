## ADDED Requirements

### Requirement: Dual SBT plugin artifacts
The build SHALL produce supported plugin artifacts for both Scala 2.12/SBT 1.9.0+ and Scala 3/SBT 2.0.4+ for `sbt-crossproject`, `sbt-scalajs-crossproject`, and `sbt-scala-native-crossproject`.

#### Scenario: Build SBT 1 artifacts
- **WHEN** the Scala 2.12 cross-build entry is selected
- **THEN** all three published modules compile against SBT 1.9.0 and use the existing SBT 1 artifact family

#### Scenario: Build SBT 2 artifacts
- **WHEN** the Scala 3 cross-build entry is selected
- **THEN** all three published modules compile against SBT 2 and use the SBT 2/Scala 3 artifact family

### Requirement: Consistent aggregate cross-build
The root aggregate and its child modules SHALL use the same supported cross-build entries, and the root SHALL aggregate validation and publication without adding compiled-product dependencies on the child modules.

#### Scenario: Compile the aggregate
- **WHEN** aggregate compilation is invoked for either supported cross-build entry
- **THEN** the build resolves only coordinates valid for that Scala and SBT combination

#### Scenario: Select all publication variants
- **WHEN** cross-publication is invoked from the root aggregate
- **THEN** each published module is selected once for SBT 1 and once for SBT 2

### Requirement: Equivalent cross-project behavior
The SBT 2 plugin SHALL provide the same public cross-project construction, platform selection, settings, aggregation, and classpath-dependency behavior as the SBT 1 plugin for supported APIs.

#### Scenario: Configure platform projects
- **WHEN** an SBT 2 build creates a cross-project for supported platforms and applies shared and platform-specific settings
- **THEN** each generated project receives the same platform identity, source layout, and applicable settings as the equivalent SBT 1 build

#### Scenario: Aggregate and depend on cross-projects
- **WHEN** an SBT 2 build aggregates or adds a classpath dependency between compatible cross-projects
- **THEN** the generated platform projects reference matching local projects and preserve the requested dependency configuration

#### Scenario: Use platform-aware library dependencies
- **WHEN** a shared build uses `%%%` in cross-project settings on either supported SBT target
- **THEN** the dependency receives the JVM, Scala.js, or Scala Native suffix selected for each generated project

### Requirement: Compatible project-name derivation
The Scala 3 macro SHALL derive the cross-project identifier from practical directly assigned `val` declarations accepted by the SBT 1 API. Supported forms SHALL include `val` and `lazy val`, access modifiers, backticked identifiers, explicit type annotations, multiline declarations, and comments or whitespace around the assignment.

#### Scenario: Derive a simple project name
- **WHEN** `crossProject` is directly assigned using `lazy val foo = crossProject(...)`
- **THEN** the generated cross-project identifier and base directory name are `foo`

#### Scenario: Derive a typed or formatted project name
- **WHEN** `crossProject` is directly assigned to a supported declaration with a type annotation, backticked identifier, multiline formatting, access modifier, or intervening comment
- **THEN** the generated identifier matches the declared `val` name

#### Scenario: Reject an indirect macro call
- **WHEN** `crossProject` is used outside a direct `val` assignment
- **THEN** build compilation fails with an error explaining that `crossProject` must be directly assigned to a `val`

#### Scenario: Use the deprecated no-argument entry point
- **WHEN** the deprecated no-argument `crossProject` form is directly assigned to a supported `val` declaration
- **THEN** it derives the declared name, selects its legacy default platforms, and reports the existing deprecation warning

### Requirement: Java 17-compatible platform integration
Each SBT target SHALL use maintained Scala.js and Scala Native plugin versions that run on Java 17 while preserving JVM, JavaScript, and native cross-project behavior. The SBT 1 build SHALL retain its required SBT 1-only platform compatibility dependency.

#### Scenario: Run platform fixtures on SBT 1
- **WHEN** the SBT 1 scripted suite runs on SBT 1.9.0 with Scala 2.12.20 and the selected Java 17-compatible platform-plugin versions
- **THEN** JVM, Scala.js, and Scala Native fixtures load and complete their expected checks

#### Scenario: Run platform fixtures on SBT 2
- **WHEN** the SBT 2 scripted suite runs with the selected Java 17-compatible platform-plugin versions
- **THEN** JVM, Scala.js, and Scala Native fixtures load and complete their expected checks

### Requirement: Dual-target validation and release
Automation SHALL require Java 17 or newer, validate both SBT target families, apply binary-compatibility checks only where a previous artifact exists, and publish both target families through the release workflow.

#### Scenario: Validate an SBT 1 change
- **WHEN** CI validates the Scala 2.12/SBT 1 entry
- **THEN** compilation, the SBT 1 scripted suite, and MiMa checks against the previous SBT 1 release complete successfully

#### Scenario: Validate an SBT 2 change
- **WHEN** CI validates the Scala 3/SBT 2 entry
- **THEN** compilation and the SBT 2 scripted suite complete successfully without resolving a nonexistent previous SBT 2 artifact

#### Scenario: Publish a release
- **WHEN** the release workflow publishes from the aligned root cross-build
- **THEN** it publishes the three SBT 1 artifacts and the three SBT 2 artifacts for the same release version

### Requirement: Published support documentation
Project documentation SHALL state installation coordinates, SBT 1.9.0 and SBT 2.0.4 minimums, the Java 17 runtime minimum, and supported Scala.js and Scala Native versions.

#### Scenario: Select installation instructions
- **WHEN** a user reads the installation documentation
- **THEN** the user can identify the correct plugin coordinates and runtime requirements for either SBT 1 or SBT 2
