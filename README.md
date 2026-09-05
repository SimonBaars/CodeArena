# CodeArena (Fabric 26.2 subset)

Education / clone-detection arena mod, ported from Forge **1.12.2** to Fabric **Minecraft 26.2** as a **working subset** (~30% feature coverage).

- Build: `export JAVA_HOME=/workspace/jdk-25 && ./gradlew build`
- Artifact: `build/libs/codearena-1.0.0+26.2.jar`
- Docs: [MIGRATION.md](MIGRATION.md), [PLAYTEST.md](PLAYTEST.md)
- Legacy Forge sources: `_forge_legacy/`

In-game: `/codearena spawn`, `/codearena end`, `/codearena problems`, or creative items **Spawn / End Code Arena**.

Spawn loads legacy `structures/arena.structure` (remapped) and demo metric smells as named spider/zombie/skeleton/creeper mobs with sidebar scoring. Full CloneRefactor AST detection and the Swing code editor remain cut.
