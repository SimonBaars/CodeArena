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
3. `/codearena spawn` — embedded `demo-sources` AST (or DemoProblems fallback).
4. `/codearena end` then `/codearena scan` — disk walk of `codearena-sample/` (materialized from jar `sample-project/`).
5. Expect chat mentioning **Disk scan** path (or fallback notice).
6. `/codearena problems` — method-level findings from OrderService / OrderHelpers (or demo sources for spawn).
7. Optional: `/codearena scan path/under/gameDir` (paths outside the game directory are refused).
8. Kill smell mobs; tips + scoreboard update; `/codearena end` for emeralds.

Optional auto-shot: `./gradlew runClient -Parenashot` (issues `/codearena scan`; see `PLAYTEST.md`).

## Expected demo findings (approx.) — `/codearena spawn`

- **Duplication**: `calculateSum` / `computeTotal` / `addNumbers` (identical bodies)
- **Complexity**: `processInput`, `validate` (cyclomatic > 10)
- **Volume**: `largeMethod` (> 50 lines)
- **Interface size**: `complexMethod` (8 parameters)

## Expected sample-project findings (approx.) — `/codearena scan`

- **Duplication**: `OrderService.tallyOrders` / `OrderHelpers.tallyLegacy`
- **Complexity**: `OrderService.route` (and possibly `OrderHelpers.gate`)
- **Volume**: `OrderService.longProcess`
- **Interface size**: `OrderService.checkout`, `OrderHelpers.gate`

Exact counts can vary slightly with JavaParser range math; wave must be non-empty when sources load.

## Fallback

If disk scan and classpath demo resources are empty or parse throws, session uses `DemoProblems.sampleWave()` (includes Type-2/3 / nesting / god-class demos the thin detector does not emit).

## Limitations

- Fabric only (this port); Forge PR branch kept for reference
- Method-level only — not CloneRefactor Type-2/3
- Disk scan scoped to Minecraft game/server directory (Fabric has no FS sandbox; path escape refused in command)
- Swing CodeEditor N/A on Fabric
