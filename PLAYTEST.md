# Playtest Checklist — CodeArena (Fabric 26.2 subset)

Target: Minecraft **26.2**, Fabric Loader **0.19.5**, Fabric API **0.159.0+26.2**, Java **25**.  
Built JAR: `build/libs/codearena-1.0.0+26.2.jar`  
Coverage: **~50–52%** of original Forge feature surface (schematic arena + watchtowers, JavaParser method-level AST on demo-sources + **disk `/codearena scan`** + DemoProblems fallback, registered smell entities + spider/cave-spider textures, package-filter diamonds, structure place command). Full Type-2/3 CloneRefactor still absent; Swing editor **N/A/deferred**; no Techne ModelCode* (vanilla only).

Use a Creative world with cheats enabled (`arenaplay` exists under `run/saves/`).

## 0. Load

- [ ] Fabric Loader 0.19.5 + Fabric API `0.159.0+26.2`
- [ ] Place `codearena-1.0.0+26.2.jar` in `mods/`
- [ ] Log shows `codearena` initializing, 8 smell entity types, schematic load
- [ ] No crash on registry bind

## 1. Creative tab & items

- [ ] Creative tab **CodeArena** appears
- [ ] **Spawn Code Arena** / **End Code Arena** listed with textures
- [ ] `/give @s codearena:checkmark` and `/give @s codearena:crossmark` work

## 2. Spawn arena (command)

- [ ] `/codearena spawn` places **legacy `arena.structure`** plus up to **4 `watchtower`s** at corners when assets load
- [ ] Player teleported slightly above center gold block; diamond sword + package-filter diamonds given
- [ ] Smell mobs spawn from **JavaParser AST** on demo-sources (or DemoProblems fallback). AST wave typically covers duplication/complexity/volume/params (not all 8 types)
- [ ] Code spider uses custom `textures/entity/code_spider.png` + eyes (legacy `mobs/spider.png`)
- [ ] Code cave spider uses darkened `textures/entity/code_cave_spider.png`
- [ ] Sidebar scoreboard **CodeArena** shows Score / metric lines / Remaining
- [ ] Mob griefing disabled for the session
- [ ] Second `/codearena spawn` while active fails with “already active”

## 2b. Disk scan wave

- [ ] `/codearena end` then `/codearena scan` materializes/uses `codearena-sample/` under the game dir
- [ ] Chat notes disk scan path; `/codearena problems` lists method-level smells from OrderService/OrderHelpers
- [ ] `/codearena scan ../` (or path outside game dir) is refused
- [ ] DemoProblems still used if scan+demo AST empty (fallback path)

## 3. Problem flow & package filter

- [ ] `/codearena problems` lists AST (or fallback) problems with [ ] / [x] status
- [ ] Killing a smell mob increments Score, prints tip (HTML tip blurbs when present), decreases Remaining
- [ ] Holding a named diamond filters visibility by package (`Show All Packages` shows all)
- [ ] Clearing all prompts emerald reward hint

## 4. Structures on demand

- [ ] `/codearena place watchtower` places a single watchtower at feet
- [ ] `/codearena place arenacheck|coliseum|colloseum` load when present (coliseum is huge — expect hitch)

## 5. Spawn / end via items

- [ ] Right-click **Spawn Code Arena** / **End Code Arena** items

## 6. Legacy command stub

- [ ] `/codeclones` prints stub (no local CloneRefactor jar under `/workspace`)

## Known gaps (do not fail build)

- No Type-2/3 CloneRefactor scan (**jar still required**; thin JavaParser method-level on demo-sources + gameDir disk scan; do not clone)
- Swing CodeEditor **N/A/deferred** (Forge desktop UI ≠ Fabric client; tips via chat / HTML blurbs only)
- No Techne ModelCode* geometry — only `ModelCodeSkeleton` (vanilla thin biped); spider(+eyes) skins + derived cave spider; others vanilla models/skins
- Schematic block **metadata** (facing) not remapped
- Coliseum not auto-placed on spawn (opt-in via `/codearena place`)
- No multi-wave detection thread from original

## Coverage summary

| Area | Status |
|------|--------|
| Mod init + Fabric metadata | Covered |
| Creative tab + 2 items | Covered |
| `/codearena spawn\|scan\|end\|problems\|place` | Covered |
| Legacy `arena.structure` + corner `watchtower`s | Covered |
| Opt-in coliseum/colloseum/arenacheck | Covered (command) |
| JavaParser method-level AST → typed smell entities | Covered (demo-sources + disk scan) |
| DemoProblems fallback wave | Covered |
| Custom spider + cave-spider texture renderers (+ eyes) | Covered |
| Package-filter diamonds | Covered |
| Sidebar score + kill tips + emerald reward | Covered |
| `/codeclones` full CloneRefactor | **Gap / stub** (no local jar) |
| Swing CodeEditor | **N/A / deferred** (desktop Swing, not Fabric-portable) |
| Thin JavaParser SmellDetector | Covered (demo-sources + `/codearena scan` disk) |
| CloneRefactor Type-2/3 engine | **Open gap** (jar/checkout absent) |
| Techne / unique ModelCode* meshes | **N/A** (legacy had none; ModelCodeSkeleton = vanilla) |

## Auto screenshot (dev)

```bash
export JAVA_HOME=/workspace/jdk-25 DISPLAY=:4
./gradlew runClient -Parenashot --no-daemon
```
