# CodeArena Playtest Results

Date: 2026-09-05 ~7:20 PM PT (disk-scan wave re-shot)  
World: `arenaplay` (Creative, cheats enabled)  
Auto: `./gradlew runClient -Parenashot` on `DISPLAY=:5`

## Results

- Minecraft 26.2 Fabric client loaded; registered **8** smell entity types; client renderers bound.
- `/codearena scan` walked **2** `.java` files under `run/codearena-sample` and spawned **5** method-level AST smells (`OrderService` / `OrderHelpers`: duplication, complexity, volume, 2× interface size).
- Chat explicitly noted **Disk scan** path (not DemoProblems). DemoProblems fallback retained for empty scans; `/codearena spawn` still uses embedded demo-sources.
- Schematic arena **21936** + **4** watchtowers; scoreboard Remaining=5 for scan wave.
- Screenshots: `playtest-shots/06-scan-ast-wave.png` (overview after scan); `04-arena-overview.png` / `05-code-spider-texture.png` refreshed.

## Notes

Coverage after disk scan: **~50–52%**. Status **Mostly OK** — remaining gaps are N/A/deferred only: CloneRefactor Type-2/3 (no jar under `/workspace`; do not clone); Swing CodeEditor. Fabric does not sandbox FS; `/codearena scan` still refuses paths outside the game directory. No further ModelCode* or default-coliseum work (legacy default = `arena`; meshes audited).
