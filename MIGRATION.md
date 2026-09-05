# Migration: Forge 1.12.2 CodeArena → Fabric Minecraft 26.2

## Version matrix

| Component | Before | After |
|-----------|--------|-------|
| Minecraft | 1.12.2 | **26.2** |
| Loader | Forge 14.23.5 / ForgeGradle 2.3 | Fabric Loader **0.19.5**, Fabric API **0.159.0+26.2** |
| Mappings | MCP `snapshot_20171003` | **Mojmap** (no Yarn for 26.2) |
| Build | ForgeGradle 2.3, Gradle 2.14 | Fabric Loom **1.17.20**, Gradle **9.5.1** |
| Java | 1.8 | **25** (`JAVA_HOME=/workspace/jdk-25`) |
| Mod id / artifact | `clonedetection` | `codearena` / `codearena-1.0.0+26.2.jar` |
| Package | `com.simonbaars.codearena` (+ `clonerefactor`) | `com.simonbaars.codearena` (subset) |

Legacy Forge tree kept for reference (not on compile path): `_forge_legacy/`.

## Scope honesty — coverage ≈ **28–32%** of original Java surface

Original tree: **~129** `.java` files (arena + clone detection + Swing editor + metric monsters + Forge GUIs).  
This port ships a **deeper playable subset** — schematic arena, demo problem→mob scoring, distinct smell mobs — still not a line-for-line reimplementation.

| Legacy area | Files (approx) | Port status | Notes |
|-------------|----------------|-------------|-------|
| Mod init / proxies / FG event bus | ~8 | **Done (replaced)** | `CodeArenaMod` + Fabric entrypoints; no sided proxies |
| Creative items (checkmark / crossmark) | ~2 | **Done** | Right-click spawn / end arena |
| Commands `/codeclones`, end | ~2 | **Partial** | `/codearena spawn\|end\|problems`; `/codeclones` stub points at demo flow |
| Arena / Challenges + scoreboard | ~3 | **Mostly done** | Sidebar score + emerald reward; no wave loop / package diamond filter |
| Schematic structure loader | ~8 | **Done (remap)** | Loads `structures/arena.structure` with 1.12 id remap; procedural fallback |
| Custom monsters (zombie/skeleton/creeper/spider) | ~20 | **Partial** | Vanilla spider/zombie/skeleton/creeper with problem names + scaled attrs (no custom entity classes / renders) |
| Clone detection engine (`clonerefactor.*`) | ~60+ | **Stubbed as demo** | `DemoProblems` sample wave; no JavaParser AST on classpath |
| Swing / RSyntaxTextArea code editor | ~5 | **Dropped** | Incompatible with modern MC client; tips printed in chat on kill |
| Forge GUIs (setup / end challenge) | ~4 | **Dropped** | Chat + items + `/codearena problems` replace dialogs |
| Keybind `c` open menu | ~1 | **Dropped** | Use items / commands |
| Tips / ResourceCommons extract | ~4 | **Partial** | Tips embedded on demo problems (chat), HTML tips not extracted |

Honest total: treat as **~30% feature coverage** of the education product; **~10%** of clone-detection depth (demo problems only, no AST).

## API mapping (high level)

| Forge 1.12.2 | Fabric 26.2 (Mojmap) |
|--------------|----------------------|
| `@Mod` + `FML*Event` | `ModInitializer` / `ClientModInitializer` |
| `ICommand` | Brigadier via `CommandRegistrationCallback` |
| `Item` + `onItemRightClick` | `Item.use` → `InteractionResult` |
| `SchematicStructure` + numeric IDs | `structureloader.SchematicStructure` + `LegacyBlockIds` |
| Custom `EntityCode*` | Vanilla mobs via `SmellMobFactory` (named + scaled) |
| Swing `CodeEditor` | **Not opened** (chat tips on resolve) |
| Scoreboard challenge UI | `Scoreboard` sidebar `codearena_score` |
| `mcmod.info` | `fabric.mod.json` |
| `en_us.lang` | `assets/codearena/lang/en_us.json` (lowercase) |

## Major cuts (do not expect)

1. **No live clone detection** against a Java project folder (demo problems only).
2. **No desktop / Swing code editor** when “fighting” a smell.
3. **No custom entity classes / renderers** — vanilla mobs with custom names & attributes.
4. **No Forge GUI screens** / keybind overlay.
5. **Metadata on schematics ignored** (stairs/doors facing not remapped; default states).
6. **Large schematics** (`coliseum`, `colloseum`) not auto-loaded (arena only; others remain as resources).

## Local only

No `git push`, no PAT, no remote publish for this port.

## Verification

```bash
export JAVA_HOME=/workspace/jdk-25
./gradlew build
```

Expected: `build/libs/codearena-1.0.0+26.2.jar`
