# CodeArena (Fabric 26.2 subset)

Education / clone-detection arena mod, ported from Forge **1.12.2** to Fabric **Minecraft 26.2** as a **working subset** (~43% feature coverage).

- Build: `export JAVA_HOME=/workspace/jdk-25 && ./gradlew build`
- Artifact: `build/libs/codearena-1.0.0+26.2.jar`
- Docs: [MIGRATION.md](MIGRATION.md), [PLAYTEST.md](PLAYTEST.md)
- Legacy Forge sources: `_forge_legacy/`

In-game: `/codearena spawn`, `/codearena end`, `/codearena problems`, `/codearena place <structure>`, or creative items **Spawn / End Code Arena**.

Spawn loads legacy `structures/arena.structure` plus corner `watchtower`s, demo metric smells as eight registered entity types (custom spider / cave-spider textures; no Techne ModelCode*), package-filter diamonds, and sidebar scoring. Full CloneRefactor AST detection (no local jar) and the Swing code editor remain cut.
