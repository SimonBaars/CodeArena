# CodeArena Playtest Results

Date: 2026-09-05 evening (PT) / 2026-09-06 ~01:43 UTC  
World: `arenaplay` (Creative, cheats enabled)  
Auto: `./gradlew runClient -Parenashot` on `DISPLAY=:4`

## Results

- Minecraft 26.2 Fabric client loaded; registered **8** smell entity types; client renderers bound (`CodeSpiderRenderer` + `CodeCaveSpiderRenderer` + eyes layer; others vanilla).
- `/codearena spawn` loaded legacy `arena.structure` (**21936** blocks) + **4 watchtowers** (**1280** each; **27056** total).
- Chat: 9 demo smells / 8 types; package-filter diamonds; CloneRefactor AST jar not available (expected).
- Scoreboard sidebar shows Remaining=9 and all 8 metric labels.
- Open-air `summon codearena:code_spider` succeeded for texture close-up framing.
- Screenshots: `playtest-shots/04-arena-overview.webp` (+ `.png`); `playtest-shots/05-code-spider-texture.webp` (+ `.png`). Earlier: `01`–`03`.

## ModelCode* / texture audit

| Legacy asset | Finding |
|--------------|---------|
| `ModelCodeSkeleton` | Vanilla thin-biped clone only — **not Techne**; modern `SkeletonRenderer` already covers it |
| Other `ModelCode*` | **Absent** (zombie/creeper/spider used stock Model*) |
| `mobs/spider.png` | Custom → `code_spider.png` (+ darkened `code_cave_spider.png`) |
| `mobs/spider_eyes.png` | Present (≈vanilla pixels) → `code_spider_eyes.png` + `CodeSpiderEyesLayer` |

## Notes

Coverage after texture audit: **~43%**. Remaining **Open** gap: real AST (CloneRefactor jar still required; demo-only). Swing CodeEditor **N/A/deferred** (Forge desktop UI). Also: schematic metadata facing; coliseum not auto-spawned (opt-in `/codearena place`). No further custom meshes to port.
