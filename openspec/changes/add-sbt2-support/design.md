## Context

sbt plugins for SBT 1 are compiled with Scala 2.12, while SBT 2 plugins are compiled with Scala 3 and published under a distinct SBT/Scala artifact suffix. The repository contains three published plugins and one scripted-test module. Most implementation code can remain shared, but the macro that derives a cross-project name from its enclosing `val` requires separate Scala 2 and Scala 3 implementations.

The dual-target build requires one aligned cross-build matrix, Java 17 automation, target-aware MiMa handling, equivalent Scala 3 macro behavior, and maintained platform-plugin versions for both SBT families.

## Goals / Non-Goals

**Goals:**
- Publish each plugin for both Scala 2.12/SBT 1.9.0+ and Scala 3/SBT 2.0.4+.
- Keep SBT 1 behavior and binary compatibility intact.
- Provide equivalent cross-project behavior and practical build-source compatibility on SBT 2.
- Make aggregate compilation, validation, and release publication select only supported target combinations.
- Set Java 17 as the minimum runtime and validate both target families on it.

**Non-Goals:**
- Drop support for SBT 1.9.0 and newer.
- Change the public cross-project model, source layouts, or platform semantics.
- Add support for platforms other than JVM, Scala.js, and Scala Native.
- Redesign deprecated APIs unrelated to SBT 2 compatibility.
- Guarantee arbitrary macro usage that is not directly assigned to a `val`.

## Decisions

### Cross-build one release from one build

All published modules and the root aggregate will use the same two-entry cross-build matrix:

```text
Scala 2.12 ──► SBT 1.9.0 ──► existing SBT 1 artifact family
Scala 3    ──► SBT 2.0.4 ──► new *_sbt2_3 artifact family
```

The root will aggregate the child projects without depending on their compiled products. This keeps root cross-commands aligned with child projects and avoids invalid mixed coordinates such as an SBT 2 plugin compiled as Scala 2.12.

Alternative considered: retain the current root settings and publish each child variant with explicit release commands. This was rejected because it duplicates release orchestration and leaves aggregate compilation inconsistent.

### Share implementation except where Scala versions require separation

SBT-neutral and source-compatible plugin code will remain under the shared Scala source directory. Scala 2 macro code and Scala 3 quoted macro code will remain in version-specific source directories. The Scala 2 source branch will preserve the existing implementation unless an SBT 1 validation failure requires a scoped correction.

Alternative considered: duplicate all plugin sources by Scala version. This was rejected because it would create unnecessary maintenance divergence.

### Preserve practical directly assigned val forms

The Scala 3 macro will derive the same project name for ordinary `val` and `lazy val` declarations, including backticked names, explicit type annotations, multiline declarations, access modifiers, and comments or whitespace around the assignment. Both the argument-taking and deprecated no-argument macro entry points will follow this rule. Calls that are not directly assigned to a `val` will continue to fail with a clear compile-time error.

The implementation will use Scala 3 quote structure and source positions where they provide stable declaration information. Any source-text parsing retained for SBT-generated build sources will be limited to the enclosing declaration and covered by focused scripted fixtures.

Alternative considered: support only `lazy val name = crossProject(...)`. This was rejected because it creates avoidable source changes when users move the same build from SBT 1 to SBT 2.

### Preserve platform-aware dependency syntax

SBT 1 will continue to obtain `%%%` from `sbt-platform-deps`. SBT 2 platform plugins use SBT 2's built-in `platform` setting, where `%%` applies the platform suffix. The Scala 3 plugin entry point will provide a `%%%` compatibility alias to that platform-aware `%%` behavior so the same cross-project dependency declarations compile on both targets.

### Use a Java 17-compatible platform ecosystem
### Use a Java 17-compatible platform ecosystem

Both target families will compile and run scripted fixtures with Scala.js 1.22.0 and Scala Native 0.5.12. The SBT 1 target and scripted launcher will use SBT 1.9.0, which runs on Java 17 and resolves the maintained platform plugins through standard `addSbtPlugin` coordinates. SBT 1 will retain `sbt-platform-deps` where required, while SBT 2 will not add that SBT 1-only compatibility dependency. SBT 1 fixtures will use Scala 2.12.20 instead of legacy Scala 2.11 releases that cannot run reliably on Java 17.

### Validate and publish both target families explicitly

CI will run formatting once and then validate both cross-build entries. SBT 1 artifacts will continue to run MiMa against the previous release. SBT 2 artifacts will skip previous-artifact comparison until an SBT 2 predecessor exists. Scripted tests will run for both target families.

The release workflow will run on Java 17 or newer and use the aligned root cross-build so the existing `sbt-ci-release` cross-publish command emits all six published artifacts: three modules for each SBT family.

## Risks / Trade-offs

- [Scala 3 macro inspection differs from Scala 2 compiler internals] → Cover each supported declaration form with build-loading tests and retain a clear error for unsupported contexts.
- [Platform dependency syntax differs between SBT families] → Map `%%%` to SBT 2's platform-aware `%%` and run the same dependency fixtures on each target.
- [A cross-command can silently select the wrong root Scala version] → Give root and children the same cross-build matrix and verify the selected project IDs before release.
- [Legacy Scala and platform fixtures cannot run reliably on Java 17] → Use Scala 2.12.20 and maintained platform plugins throughout the scripted suite.
- [A new SBT 2 artifact has no MiMa baseline] → Skip only that artifact family; retain MiMa for all existing SBT 1 artifacts.

## Migration Plan

1. Align root and child cross-build settings on SBT 1.9.0 and SBT 2.0.4, then remove root compiled dependencies.
2. Complete the shared and version-specific source layout and macro behavior.
3. Upgrade platform dependencies and scripted fixtures to the Java 17-compatible versions selected for both targets.
4. Add dual-target CI, Java 17, and target-aware MiMa validation.
5. Verify aggregate compilation, both scripted suites, expected artifact coordinates, formatting, and documentation.
6. Release both artifact families together through the existing release workflow.

Before publication, rollback consists of reverting the SBT 2 cross-build changes and retaining the existing SBT 1 release path. After publication, published coordinates remain immutable; defects require a follow-up release.

## Open Questions

None. The support policy, root publication model, and macro compatibility scope are decided.
