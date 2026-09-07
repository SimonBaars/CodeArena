# PORT_STATUS — CodeArena (Fabric 26.2)

Updated: 2026-09-06 ~8:50 PM PT — schematic Blocks+Data via `LegacyBlockStates.fromLegacy` (same class of fix as IMS).

| Field | Value |
|-------|-------|
| Coverage | **~50–52%** of original Forge Java surface |
| Status | **Mostly OK** |
| Build | Green (`JAVA_HOME=/workspace/jdk-25 ./gradlew build`) |
| Playable core | Schematic `arena` + corner `watchtower`s with metadata-aware block states; `/codearena spawn\|scan\|end\|problems\|place`; 8 smell entity types; spider/cave-spider skins; package diamonds; sidebar score |

## Open gaps (N/A / deferred — not empty Done)

| Gap | Why not Done |
|-----|----------------|
| CloneRefactor Type-2/3 engine | **Deferred** — no CloneRefactor jar/checkout under `/workspace`; do **not** clone. Thin JavaParser method-level smells (demo-sources + gameDir `/codearena scan`) + DemoProblems fallback ship instead. |
| Swing CodeEditor | **N/A / deferred** — Forge desktop Swing UI is not portable to the Fabric client the same way; tips via chat + `/tips/*.html` blurbs only. |

## Port-able items already closed (do not reopen)

| Item | Evidence |
|------|----------|
| ModelCode* / Techne meshes | Legacy audit: only `ModelCodeSkeleton` (= vanilla thin biped); no Techne; custom textures were spider(+eyes) only — ported + derived cave spider. |
| Coliseum as default spawn | **Faithful opt-in** — legacy `CodeArena` loads `arena` schematic; huge `coliseum`/`colloseum` via `/codearena place` only (see MIGRATION.md major cuts). |
| Schematic Data meta | **Closed** — `LegacyBlockStates` adapted from IMS; place via `fromLegacy(id, meta)` instead of id-only `defaultBlockState()`. |

See `MIGRATION.md`, `JAVAPARSER_DETECTION.md`, `PLAYTEST.md`, `PLAYTEST_RESULTS.md`.
