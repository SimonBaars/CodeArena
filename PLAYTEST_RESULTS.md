# CodeArena Playtest Results

Date: 2026-09-05 evening (PT) / 2026-09-06 ~00:51 UTC  
World: `arenaplay` (Creative, cheats enabled)  
Auto: `./gradlew runClient -Parenashot` on `DISPLAY=:4`

## Results

- Minecraft 26.2 Fabric client loaded; registered **8** smell entity types; client renderers bound (custom spider texture).
- `/codearena spawn` loaded legacy `arena.structure` (**21936** blocks) + **4 watchtowers** (**1280** each; **27056** total).
- Chat: 9 demo smells / 8 types; package-filter diamonds; CloneRefactor AST jar not available (expected).
- Scoreboard sidebar shows Remaining=9 and all 8 metric labels.
- Screenshot: `playtest-shots/03-arena-watchtowers.webp` (+ `.png`) — mesa overview with arena/watchtower stone and smell nameplates.
- Earlier smoke shots retained: `01-creative-tab.webp`, `02-arena-spawned.webp`.

## Notes

Coverage after deepen: **~42%**. Remaining cuts: AST engine (no local jar), Swing CodeEditor, ModelCode* meshes (only spider PNG existed), schematic metadata facing, coliseum not auto-spawned (opt-in `/codearena place`).
