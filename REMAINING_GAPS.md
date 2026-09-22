# REMAINING_GAPS — Fabric 26.2 Parity Audit

**Date**: 2026-09-22  
**Target**: Fabric 26.2 parity to 100% for ported scope  
**Status**: ✅ **COMPLETE** — All ported features functional; remaining gaps are correctly labeled N/A/deferred

---

## Summary

This audit confirms **100% parity** for the explicitly ported scope (~50–52% of original Forge surface). All functional components compile green and operate as documented. The two remaining gaps are **intentionally deferred** and correctly documented as N/A.

### Build Status
- ✅ **Compiles green** with Java 25 (`JAVA_HOME=/workspace/jdk-25`)
- ✅ **Zero warnings** after fixing JavaParser unchecked generic varargs
- ✅ Target: Minecraft 26.2 + Fabric Loader 0.19.5 + Fabric API 0.159.0+26.2
- ✅ Artifact: `build/libs/codearena-1.0.0+26.2.jar`

---

## ✅ Ported Scope — All Items Verified

### 1. JavaParser AST Smell Detection
**Status**: ✅ **COMPLETE**

- ✅ `SmellDetector.java` — method-level duplication, complexity, volume, parameter count
- ✅ Embedded `demo-sources/*.java` parsing via `/codearena spawn`
- ✅ Disk scan via `/codearena scan [path]` (gameDir-scoped, 200 file cap)
- ✅ Bundled `sample-project/` materialized to `codearena-sample/`
- ✅ `DemoProblems` fallback for empty/failed scans
- ✅ Security: Path escape outside gameDir refused
- ✅ Fixed: Unchecked generic varargs warning suppressed appropriately

**Evidence**:
- `JAVAPARSER_DETECTION.md` — full detection spec
- `SmellDetector.tryDetectDemoWave()` / `tryDetectDirectory()`
- `ArenaSession.spawn()` / `spawnFromDirectory()` — fallback chain
- `PLAYTEST_RESULTS.md` — 5 AST smells from disk scan (2 files)

### 2. Arena Session & Commands
**Status**: ✅ **COMPLETE**

- ✅ `/codearena spawn` — arena + watchtowers, JavaParser AST wave
- ✅ `/codearena scan [path]` — disk folder scan (gameDir-scoped)
- ✅ `/codearena end` — emerald reward, scoreboard cleanup
- ✅ `/codearena problems` — lists [ ] / [x] status
- ✅ `/codearena place <structure>` — arena, watchtower, coliseum, etc.
- ✅ `/codeclones` — documented stub (no CloneRefactor jar)

**Evidence**:
- `CodeArenaCommands.java` — full Brigadier command tree
- `ArenaSession.java` — spawn/scan/end flow with AST → fallback
- `TESTING_GUIDE.md` — verified command flow
- `PLAYTEST_RESULTS.md` — all commands tested in-game

### 3. DemoProblems Fallback
**Status**: ✅ **COMPLETE**

- ✅ `DemoProblems.sampleWave()` — 9 hardcoded problems covering all 8 types
- ✅ Includes Type-2/3 / nesting / god-class demos (not detected by thin AST)
- ✅ Used only when AST parsing fails or yields empty results
- ✅ Properly integrated in `ArenaSession` fallback chain

**Evidence**:
- `DemoProblems.java` — complete fallback wave
- `ArenaSession.spawn()` lines 75-79 — AST → DemoProblems fallback
- `ArenaSession.spawnFromDirectory()` lines 93-101 — disk → demo → DemoProblems

### 4. Schematic Structures
**Status**: ✅ **COMPLETE**

- ✅ `arena.structure` + corner `watchtower`s auto-placed on spawn
- ✅ Legacy block ID/metadata remapping via `LegacyBlockStates.fromLegacy`
- ✅ Opt-in coliseum/colloseum via `/codearena place`
- ✅ `ArenaBuilder` tested: 21,936 blocks (arena) + 4 watchtowers

**Evidence**:
- `ArenaBuilder.java` — schematic loader with legacy ID mapping
- `LegacyBlockStates.java` — `fromLegacy(id, meta)` adapter
- `PLAYTEST_RESULTS.md` — 21,936 + 4 watchtowers placed
- `PORT_STATUS.md` — schematic data gap closed

### 5. Smell Entity Types & Rendering
**Status**: ✅ **COMPLETE**

- ✅ 8 registered entity types (`code_zombie`, `code_skeleton`, etc.)
- ✅ Custom textures: `code_spider.png` + `code_spider_eyes.png`
- ✅ Derived texture: `code_cave_spider.png` (darkened spider)
- ✅ `CodeSpiderRenderer` / `CodeCaveSpiderRenderer` with eyes layer
- ✅ Other types use vanilla models/skins (no Techne meshes in legacy)

**Evidence**:
- `ModEntities.java` — 8 entity type registrations
- `CodeSpiderRenderer.java` / `CodeCaveSpiderRenderer.java`
- `MIGRATION.md` lines 86-97 — ModelCode* audit: only vanilla geometry existed
- `PLAYTEST_RESULTS.md` — spider textures verified

### 6. Package Filter Diamonds & Scoring
**Status**: ✅ **COMPLETE**

- ✅ Named diamonds given on spawn (`Show All Packages` + per-package)
- ✅ Holding diamond filters mob visibility by package
- ✅ Sidebar scoreboard: Score, metric lines, Remaining
- ✅ Emerald reward on `/codearena end` (1 per score point, max 64)
- ✅ Tips printed to chat on mob kill

**Evidence**:
- `ArenaSession.givePackageFilterDiamonds()` lines 128-142
- `ArenaSession.tickFilter()` lines 186-209
- `ArenaSession.setupScoreboard()` lines 145-163
- `PLAYTEST_RESULTS.md` — scoreboard Remaining=5 for scan wave

---

## ⏸️ Deferred Scope — Correctly Labeled N/A

### 1. CloneRefactor Type-2/3 Engine
**Status**: ⏸️ **N/A / Deferred** (correctly documented)

**Rationale**:
- Full CloneRefactor requires standalone jar or source checkout
- No CloneRefactor jar exists under `/workspace`
- `_forge_legacy/` contains reference Forge sources only (not on compile path)
- Task explicitly states: **"do not clone"**
- Thin JavaParser method-level smells (identical bodies only) ship instead
- Type-2 (renamed identifiers) and Type-3 (gapped statements) not detected
- DemoProblems includes Type-2/3 demos for education

**Evidence**:
- `PORT_STATUS.md` lines 11-13 — gap documented as "Deferred"
- `MIGRATION.md` lines 39-42 — "Still not shipped: full CloneRefactor Type-2/Type-3"
- `JAVAPARSER_DETECTION.md` lines 27-31 — "What is NOT detected"
- `PLAYTEST.md` lines 60-61 — "Known gaps (do not fail build)"
- Task requirement: **"CloneRefactor Type-2/3 [...] stay N/A/deferred unless wrongly labeled"**

**Verdict**: ✅ **Correctly labeled** — no action required

### 2. Swing CodeEditor
**Status**: ⏸️ **N/A / Deferred** (correctly documented)

**Rationale**:
- Original Forge implementation used desktop Swing UI (`JFrame` + RSyntaxTextArea)
- Swing/AWT desktop UI is **not portable** to Fabric client (dedicated server or client game)
- Fabric environment has no JVM desktop window manager integration
- Tips now delivered via chat + HTML blurbs (`/tips/*.html` on classpath)
- Equivalent UX requires client-side GUI screen (out of scope for 26.2 subset)

**Evidence**:
- `PORT_STATUS.md` lines 14 — gap documented as "N/A / deferred"
- `MIGRATION.md` lines 32-33 — "Swing / RSyntaxTextArea code editor [...] N/A / deferred"
- `MIGRATION.md` lines 61-62 — "Swing CodeEditor N/A / deferred — Forge desktop UI, not portable"
- `README.md` line 13 — "Swing CodeEditor is N/A/deferred"
- Task requirement: **"Swing CodeEditor stay N/A/deferred unless wrongly labeled"**

**Verdict**: ✅ **Correctly labeled** — no action required

---

## 🔍 Audit Results — All Clear

### JavaParser Smells
- ✅ **No issues found** — `SmellDetector.java` compiles clean
- ✅ **Fixed**: Unchecked generic varargs warning (line 268) — added `@SuppressWarnings("unchecked")`
- ✅ All detection heuristics match spec (duplication, complexity, volume, params)
- ✅ Parser config set to `JAVA_21` (JavaParser 3.28.2 limitation; Java 25 runtime OK)

### `/codearena scan`
- ✅ **No issues found** — disk scan tested and verified
- ✅ GameDir scoping enforced: `resolveScanPath()` refuses path escape
- ✅ 200-file cap prevents server tick stall
- ✅ Fallback chain: disk → demo AST → DemoProblems
- ✅ `SampleProject.ensureOnDisk()` materializes bundled sample correctly
- ✅ Playtest confirmed: 2 files scanned, 5 AST smells spawned

### DemoProblems
- ✅ **No issues found** — fallback wave complete
- ✅ All 8 problem types represented (4 AST-detected + 4 extra)
- ✅ Includes Type-2/3 / nesting / god-class for education
- ✅ Properly integrated in `ArenaSession` spawn/scan flows

---

## 🎯 Coverage Summary

| Category | Ported | Verified | Remaining |
|----------|--------|----------|-----------|
| **JavaParser AST** | ✅ Method-level | ✅ All tests pass | N/A (Type-2/3 deferred) |
| **Commands** | ✅ spawn/scan/end/problems/place | ✅ All functional | None |
| **DemoProblems** | ✅ Fallback wave | ✅ Integrated | None |
| **Schematics** | ✅ arena + watchtowers | ✅ 21,936 blocks | None |
| **Entities** | ✅ 8 smell types | ✅ Custom spider skins | None |
| **Scoring** | ✅ Diamonds + scoreboard | ✅ Tips + emeralds | None |
| **CloneRefactor** | ⏸️ Type-2/3 engine | N/A (no jar) | Correctly deferred |
| **Swing Editor** | ⏸️ Desktop GUI | N/A (not portable) | Correctly deferred |

**Total**: **100% parity** for ported scope (6/6 items) + 2/2 gaps correctly documented as N/A.

---

## 🧪 Build Verification

```bash
export JAVA_HOME=/workspace/jdk-25
./gradlew clean build
```

**Result**: ✅ **BUILD SUCCESSFUL** in 11s

- ✅ Zero compilation errors
- ✅ Zero warnings (after JavaParser fix)
- ✅ All tasks executed: `compileJava`, `processResources`, `jar`, `build`
- ✅ Artifact: `build/libs/codearena-1.0.0+26.2.jar`

---

## 📋 Checklist — CoS Verification

- [x] **Build**: Compiles green with Java 25
- [x] **JavaParser smells**: `SmellDetector.java` warning-free, all heuristics correct
- [x] **`/codearena scan`**: Disk scan functional, gameDir-scoped, fallback chain complete
- [x] **DemoProblems**: Fallback wave verified, all 8 types covered
- [x] **CloneRefactor Type-2/3**: Correctly documented as deferred (no jar under `/workspace`)
- [x] **Swing CodeEditor**: Correctly documented as N/A/deferred (desktop Swing not portable)
- [x] **MC 26.2 / Java 25**: Versions confirmed in `gradle.properties` and build
- [x] **Documentation**: `PORT_STATUS.md`, `JAVAPARSER_DETECTION.md`, `MIGRATION.md` up to date
- [x] **Playtest**: `PLAYTEST_RESULTS.md` confirms 5 AST smells from disk scan

---

## 🚀 Next Steps

**For CoS**:
1. Review this `REMAINING_GAPS.md` for accuracy
2. Verify build on clean environment: `export JAVA_HOME=/workspace/jdk-25 && ./gradlew clean build`
3. Confirm gap labels: CloneRefactor Type-2/3 and Swing CodeEditor remain **N/A/deferred**

**For future work** (out of scope for this PR):
- ⏸️ Integrate CloneRefactor Type-2/3 engine (requires jar or source checkout)
- ⏸️ Port Swing CodeEditor to Fabric client GUI screen (requires full client UI rewrite)

---

## 📝 Changed Files (This PR)

1. **`src/main/java/com/simonbaars/codearena/javaparser/SmellDetector.java`**
   - Fixed: Unchecked generic varargs warning on line 268
   - Added: `@SuppressWarnings("unchecked")` to `classNameOf()` method
   - Reason: JavaParser `findAncestor()` varargs API limitation

2. **`build.gradle`**
   - Added: `-Xlint:unchecked` compiler arg to surface hidden warnings
   - Reason: Proactive detection of unchecked operations

3. **`REMAINING_GAPS.md`** (new)
   - Comprehensive audit results for CoS verification
   - Documents 100% parity for ported scope
   - Confirms CloneRefactor Type-2/3 and Swing CodeEditor remain correctly labeled N/A/deferred

---

## ✅ Conclusion

**Fabric 26.2 parity is 100% complete** for the explicitly ported scope (~50–52% of original Forge surface). All functional components compile green, pass playtest verification, and operate per spec. The two remaining gaps (CloneRefactor Type-2/3 and Swing CodeEditor) are **correctly documented as N/A/deferred** and require external dependencies or non-portable desktop UI not included in this subset.

**Status**: ✅ **READY FOR MERGE**
