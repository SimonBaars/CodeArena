# JavaParser AST Smell Detection (Fabric 26.2)

## Overview

Thin **JavaParser**-based smell detection wired into the Fabric CodeArena port so arena waves can come from real ASTs — not only hardcoded `DemoProblems`.

Sources (in preference order for each trigger):

1. **Disk folder** — `/codearena scan [path]` walks `.java` under a game-directory-scoped path (default: bundled `codearena-sample/`).
2. **Embedded demo-sources** — `/codearena spawn` parses classpath `demo-sources/*.java`.
3. **DemoProblems fallback** — if AST yields nothing.

Fabric Loader does **not** sandbox ordinary mod filesystem reads (no Fabric 26.2 permission gate for this). Scans are still **scoped to the Minecraft server/game directory** so `/codearena scan` cannot read outside the instance.

This is **method-level** smells (identical-body duplication, cyclomatic complexity, method size, parameter count). It is **not** full CloneRefactor Type-2/Type-3 clone detection.

## What is detected

| Smell | Heuristic | Mob (via `ProblemType`) |
|-------|-----------|-------------------------|
| Duplication | Identical normalized method bodies (length > 50 chars), ≥2 methods | `DUPLICATION` → code spider |
| Unit complexity | Cyclomatic complexity > 10 | `UNITCOMPLEXITY` → code zombie |
| Unit volume | Method line span > 50 | `UNITVOLUME` → code creeper |
| Unit interface size | Parameter count > 5 | `UNITINTERFACESIZE` → code skeleton |

## What is NOT detected

- Type-2 clones (renamed identifiers)
- Type-3 clones (gapped / modified statements)
- Statement-level cross-file clone graphs
- Nesting depth / god-class metrics (those remain DemoProblems-only extras)

## Integration (Fabric)

1. `SmellDetector` (`com.simonbaars.codearena.javaparser`) parses demo resources and/or an on-disk directory → `List<CodeProblem>`.
2. `SampleProject.ensureOnDisk(gameDir)` materializes jar `sample-project/**` into `gameDir/codearena-sample/` when missing.
3. `ArenaSession.spawn` → demo AST → DemoProblems fallback.
4. `ArenaSession.spawnFromDirectory` → disk scan → demo AST → DemoProblems fallback.
5. No Forge `ProblemDetectionThread` — arena session is the integration point.
6. Soft cap: **200** `.java` files per disk scan.

## Demo / sample sources

`src/main/resources/demo-sources/` (classpath, `/codearena spawn`):

- `DemoSmells.java` — complexity, many params, large method, duplicated sum helpers
- `DuplicateCode.java` — third duplicate of the sum body + complex `validate`

`src/main/resources/sample-project/` (materialized to `run/codearena-sample/`, `/codearena scan`):

- `com/example/OrderService.java` — route complexity, checkout params, longProcess volume, tallyOrders
- `com/example/OrderHelpers.java` — tallyLegacy duplicate + multi-param gate

## Dependency

```gradle
implementation include("com.github.javaparser:javaparser-core:3.28.2")
```

Jar-in-jar via Loom `include` so `runClient` / production jars carry the parser.

## Trigger in-game

```text
/codearena spawn
/codearena scan
/codearena scan codearena-sample
/codearena scan <relative-or-absolute-path-under-game-dir>
```

Chat announces whether the wave is disk AST, demo AST, or DemoProblems fallback. Then `/codearena problems` lists findings.

## Build

```bash
export JAVA_HOME=/workspace/jdk-25
./gradlew build
```
