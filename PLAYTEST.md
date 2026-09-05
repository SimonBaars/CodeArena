# Playtest Checklist — CodeArena (Fabric 26.2 subset)

Target: Minecraft **26.2**, Fabric Loader **0.19.5**, Fabric API **0.159.0+26.2**, Java **25**.  
Built JAR: `build/libs/codearena-1.0.0+26.2.jar`  
Coverage: **~30%** of original Forge feature surface (schematic arena + demo problem flow + named smell mobs; AST engine / Swing editor / custom entity classes cut).

Use a Creative world with cheats enabled.

## 0. Load

- [ ] Fabric Loader 0.19.5 + Fabric API `0.159.0+26.2`
- [ ] Place `codearena-1.0.0+26.2.jar` in `mods/`
- [ ] Log shows `codearena` initializing and schematic load (`Loaded schematic /structures/arena.structure` or procedural fallback)
- [ ] No crash on registry bind

## 1. Creative tab & items

- [ ] Creative tab **CodeArena** appears
- [ ] **Spawn Code Arena** (`codearena:checkmark`) and **End Code Arena** (`codearena:crossmark`) listed with textures
- [ ] `/give @s codearena:checkmark` and `/give @s codearena:crossmark` work

## 2. Spawn arena (command)

- [ ] `/codearena spawn` places **legacy `arena.structure`** (stone/sand/stairs/fences/torches) when load succeeds — not only a flat sandstone box
- [ ] Player teleported slightly above center gold block
- [ ] Diamond sword added to inventory
- [ ] Four distinctly typed smell mobs spawn:
  - Spider — **Duplication: …**
  - Zombie — **Unit Complexity: …**
  - Skeleton — **Unit Interface Size: …**
  - Creeper — **Unit Volume: …**
- [ ] Sidebar scoreboard **CodeArena** shows Score / metric lines / Remaining
- [ ] Mob griefing disabled for the session (creeper should not carve the arena)
- [ ] Second `/codearena spawn` while active fails with “already active”

## 3. Problem flow

- [ ] `/codearena problems` lists demo problems with [ ] / [x] status
- [ ] Killing a smell mob increments Score, prints tip in chat, decreases Remaining
- [ ] Clearing all four prompts emerald reward hint

## 4. Spawn arena (item)

- [ ] Right-click **Spawn Code Arena** item → same as `/codearena spawn`

## 5. End session

- [ ] `/codearena end` or right-click **End Code Arena** removes remaining session mobs
- [ ] Chat confirms end + emerald count from score
- [ ] Scoreboard sidebar cleared; mob griefing restored
- [ ] Ending with no session shows failure message

## 6. Legacy command stub

- [ ] `/codeclones` prints stub pointing at `/codearena spawn` / `problems`

## Known gaps (do not fail build)

- No project-folder CloneRefactor AST scan
- No Swing / in-game code editor (tips via chat only)
- No custom Code* entity classes / scaled model renders (vanilla + name + attributes)
- Schematic block **metadata** (facing) not remapped
- `coliseum` / `colloseum` / `watchtower` resources present but not auto-placed
- No package-filter diamonds / multi-wave detection thread from original

## Coverage summary

| Area | Status |
|------|--------|
| Mod init + Fabric metadata | Covered |
| Creative tab + 2 items | Covered |
| `/codearena spawn\|end\|problems` | Covered |
| Legacy `arena.structure` load | Covered (id remap; procedural fallback) |
| Demo problem → typed smell mobs | Covered |
| Sidebar score + kill tips + emerald reward | Covered |
| `/codeclones` real AST detection | **Gap / stub** |
| Swing CodeEditor | **Gap / dropped** |
| clonerefactor AST engine | **Gap / demo only** |
| Custom entity classes + Forge GUIs | **Gap / dropped** |
