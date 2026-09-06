# Testing Guide — JavaParser smells (Fabric 26.2)

## Build

```bash
export JAVA_HOME=/workspace/jdk-25
cd /workspace/minecraft-mods/CodeArena
./gradlew build
```

Artifact: `build/libs/codearena-1.0.0+26.2.jar`

## In-game (recommended)

1. Client: `./gradlew runClient` (or install the jar + Fabric API `0.159.0+26.2`).
2. Open Creative world with cheats (`arenaplay` under `run/saves/` if using Loom run).
3. `/codearena spawn`
4. Expect chat mentioning **AST smells via JavaParser** (or fallback notice).
5. `/codearena problems` — should list method-level findings from `DemoSmells` / `DuplicateCode` (duplication group, complexity, volume, params).
6. Kill smell mobs; tips + scoreboard update; `/codearena end` for emeralds.

Optional auto-shot: `./gradlew runClient -Parenashot` (see `PLAYTEST.md`).

## Expected demo findings (approx.)

- **Duplication**: `calculateSum` / `computeTotal` / `addNumbers` (identical bodies)
- **Complexity**: `processInput`, `validate` (cyclomatic > 10)
- **Volume**: `largeMethod` (> 50 lines)
- **Interface size**: `complexMethod` (8 parameters)

Exact counts can vary slightly with JavaParser range math; wave must be non-empty when resources load.

## Fallback

If classpath resources missing or parse throws, session uses `DemoProblems.sampleWave()` (includes Type-2/3 / nesting / god-class demos the thin detector does not emit).

## Limitations

- Fabric only (this port); Forge PR branch kept for reference
- Method-level only — not CloneRefactor Type-2/3
- Swing CodeEditor N/A on Fabric
