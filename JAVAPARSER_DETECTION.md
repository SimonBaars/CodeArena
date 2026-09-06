# JavaParser AST Smell Detection (Fabric 26.2)

## Overview

Thin **JavaParser**-based smell detection wired into the Fabric CodeArena port so arena waves can come from real ASTs on embedded demo sources — not only hardcoded `DemoProblems`.

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
- Live scan of an arbitrary project folder (no CloneRefactor jar)

## Integration (Fabric)

1. `SmellDetector` (`com.simonbaars.codearena.javaparser`) parses classpath `demo-sources/*.java` and returns `List<CodeProblem>`.
2. `ArenaSession.spawn` calls `SmellDetector.tryDetectDemoWave()`; if empty/failed → `DemoProblems.sampleWave()`.
3. No Forge `ProblemDetectionThread` — Fabric has no that class; arena session is the integration point.

## Demo sources

`src/main/resources/demo-sources/`:

- `DemoSmells.java` — complexity, many params, large method, duplicated sum helpers
- `DuplicateCode.java` — third duplicate of the sum body + complex `validate`

## Dependency

```gradle
implementation include("com.github.javaparser:javaparser-core:3.28.2")
```

Jar-in-jar via Loom `include` so `runClient` / production jars carry the parser.

## Trigger in-game

```text
/codearena spawn
```

Chat announces whether the wave is AST-backed or DemoProblems fallback. Then `/codearena problems` lists findings.

## Build

```bash
export JAVA_HOME=/workspace/jdk-25
./gradlew build
```
