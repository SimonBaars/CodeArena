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

## Scope honesty — coverage ≈ **50–52%** of original Java surface

Original tree: **~129** `.java` files (arena + clone detection + Swing editor + metric monsters + Forge GUIs).  
This port ships a **deeper playable subset** — schematic arena + watchtowers, eight demo problem→mob types, registered smell entities (custom spider / cave-spider textures), package-filter diamonds, structure place command — still not a line-for-line reimplementation.

| Legacy area | Files (approx) | Port status | Notes |
|-------------|----------------|-------------|-------|
| Mod init / proxies / FG event bus | ~8 | **Done (replaced)** | `CodeArenaMod` + Fabric entrypoints; no sided proxies |
| Creative items (checkmark / crossmark) | ~2 | **Done** | Right-click spawn / end arena |
| Commands `/codeclones`, end | ~2 | **Partial** | `/codearena spawn\|scan\|end\|problems\|place`; `/codeclones` stub |
| Arena / Challenges + scoreboard | ~3 | **Mostly done** | Sidebar + emerald reward + package diamonds; no wave loop |
| Schematic structure loader | ~8 | **Done (remap)** | `arena` + corner `watchtower`; `/codearena place` for coliseum/etc. |
| Custom monsters (zombie/skeleton/creeper/spider) | ~20 | **Mostly done** | 8 entity types + attrs; `code_spider`/`code_cave_spider` (+eyes) skins; ModelCode* = vanilla only (see audit) |
| Clone detection engine (`clonerefactor.*`) | ~60+ | **Partial (thin AST)** | JavaParser `SmellDetector` on embedded `demo-sources` + **disk `/codearena scan`** (gameDir-scoped; method-level); `DemoProblems` fallback; **no** Type-2/3 CloneRefactor |
| Swing / RSyntaxTextArea code editor | ~5 | **N/A / deferred** | Forge **desktop** Swing UI — not portable to Fabric client the same way; tips via chat + `/tips/*.html` blurbs |
| Forge GUIs (setup / end challenge) | ~4 | **Dropped** | Chat + items + commands |
| Keybind `c` open menu | ~1 | **Dropped** | Use items / commands |
| Tips / ResourceCommons extract | ~4 | **Partial** | Tip HTML on classpath; stripped to chat blurbs |

Honest total: treat as **~50–52%** feature coverage of the education product; **~30–35%** of clone-detection depth (thin JavaParser method-level smells on demo-sources + optional disk folder scan + DemoProblems fallback; still no Type-2/3 CloneRefactor).

## AST / CloneRefactor wiring policy

**Shipped:** thin **JavaParser** `SmellDetector` (method-level duplication / complexity / volume / params) on embedded `demo-sources/` and optional **gameDir-scoped disk folder** via `/codearena scan` (bundled `codearena-sample/`), preferred by `ArenaSession` with **`DemoProblems` fallback**. Fabric does not sandbox FS reads; path resolve still refuses escapes outside the server game directory.

**Still not shipped:** full **CloneRefactor** Type-2/Type-3 clone engine. No standalone CloneRefactor jar/checkout under `/workspace`; `_forge_legacy` embeds `clonerefactor.*` Forge sources only — **do not clone**. Port status is **Mostly OK** with those two as the remaining **N/A/deferred** gaps (not Done).

## API mapping (high level)

| Forge 1.12.2 | Fabric 26.2 (Mojmap) |
|--------------|----------------------|
| `@Mod` + `FML*Event` | `ModInitializer` / `ClientModInitializer` |
| `ICommand` | Brigadier via `CommandRegistrationCallback` |
| `Item` + `onItemRightClick` | `Item.use` → `InteractionResult` |
| `SchematicStructure` + numeric IDs | `structureloader.SchematicStructure` + `LegacyBlockIds` |
| Custom `EntityCode*` | `ModEntities` registry ids + vanilla classes; `CodeSpiderRenderer` / `CodeCaveSpiderRenderer` + eyes layer |
| Swing `CodeEditor` | **N/A / deferred** (desktop Swing ≠ Fabric client; chat / HTML tips on resolve) |
| Scoreboard challenge UI | `Scoreboard` sidebar `codearena_score` |
| Package-filter diamonds | Named `Items.DIAMOND` + session tick invisibility |
| `mcmod.info` | `fabric.mod.json` |
| `en_us.lang` | `assets/codearena/lang/en_us.json` (lowercase) |

## Major cuts (do not expect)

1. **No full CloneRefactor Type-2/3 scan** — thin JavaParser method-level AST on demo-sources + optional disk folder (`/codearena scan`); CloneRefactor jar still absent under `/workspace`.
2. **Swing CodeEditor N/A / deferred** — Forge desktop UI, not portable to the Fabric client the same way; tips via chat / HTML when “fighting” a smell.
3. **No Techne / unique ModelCode* meshes** — legacy audit (`_forge_legacy`):
   - Only `ModelCodeSkeleton.java` existed; it is a **vanilla `ModelSkeleton` clone** (thin biped arms/legs), not a Techne export.
   - Zombie / creeper / spider used stock `ModelZombie` / `ModelCreeper` / `ModelSpider`.
   - Custom textures found: `mobs/spider.png` + `mobs/spider_eyes.png` only (now `code_spider*` + darkened `code_cave_spider.png`).
   - Other smell types keep vanilla skins on registered entity types.
4. **No Forge GUI screens** / keybind overlay.
5. **Metadata on schematics ignored** (stairs/doors facing not remapped; default states).
6. **Large schematics** (`coliseum`, `colloseum`) are opt-in via `/codearena place`, not auto-loaded on spawn.

## Local only

No `git push`, no PAT, no remote publish for this port.

## Verification

```bash
export JAVA_HOME=/workspace/jdk-25
./gradlew build
```

Expected: `build/libs/codearena-1.0.0+26.2.jar`

## ModelCode* / texture audit (2026-09-05 PT)

Searched `_forge_legacy` for `ModelCode*`, Techne (`.tcn`), and mob PNGs:

| Asset | Result |
|-------|--------|
| `ModelCodeSkeleton` | Present — **vanilla skeleton geometry** (thin arms/legs on `ModelBiped`), not Techne |
| `ModelCodeZombie` / `ModelCodeCreeper` / `ModelCodeSpider` | **Absent** — renderers used stock Model* classes |
| `mobs/spider.png` | **Custom** (~70% pixels differ from vanilla) → `textures/entity/code_spider.png` |
| `mobs/spider_eyes.png` | Present (pixel-identical to vanilla eyes) → `code_spider_eyes.png` + `CodeSpiderEyesLayer` |
| Other mob textures | **None** — zombie/skeleton/creeper/witch/blaze/enderman use vanilla skins |
| Cave spider skin | Not in legacy; Fabric adds darkened derivative `code_cave_spider.png` from spider PNG |

Conclusion: nothing further to port for custom meshes; texture work is complete for assets that existed.
