# CodeArena (Fabric 26.2 subset)

Education / clone-detection arena mod, ported from Forge **1.12.2** to Fabric **Minecraft 26.2** as a **working subset** (~48–50% feature coverage).

- Build: `export JAVA_HOME=/workspace/jdk-25 && ./gradlew build`
- Artifact: `build/libs/codearena-1.0.0+26.2.jar`
- Docs: [MIGRATION.md](MIGRATION.md), [PLAYTEST.md](PLAYTEST.md), [JAVAPARSER_DETECTION.md](JAVAPARSER_DETECTION.md)
- Legacy Forge sources: `_forge_legacy/`

In-game: `/codearena spawn`, `/codearena end`, `/codearena problems`, `/codearena place <structure>`, or creative items **Spawn / End Code Arena**.

Spawn loads legacy `structures/arena.structure` plus corner `watchtower`s, then prefers **JavaParser AST smells** from embedded `demo-sources/` (method-level duplication / complexity / volume / params) with **DemoProblems fallback** if parse fails. Eight registered smell entity types (custom spider / cave-spider textures; no Techne ModelCode*), package-filter diamonds, sidebar scoring. **Not** full CloneRefactor Type-2/3. Swing CodeEditor is **N/A/deferred** (Forge desktop UI, not portable to Fabric client).
