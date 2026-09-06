# JavaParser AST Smell Detection

## Overview

This enhancement adds real AST-based smell detection to CodeArena using JavaParser, without requiring the CloneRefactor library for demo/embedded sources.

## What's New

### Real AST Detection vs Demo Problems

**Real AST Detection (NEW):**
- Uses JavaParser to analyze actual Java source code syntax trees
- Detects real code smells:
  - **Duplication**: Methods with identical or nearly identical code
  - **Unit Complexity**: Methods with high cyclomatic complexity (>10)
  - **Unit Volume**: Large methods (>50 lines)
  - **Unit Interface Size**: Methods with too many parameters (>5)

**Demo Problems (EXISTING):**
- Placeholder problems for testing without real code analysis
- Still available as fallback when no real sources are present

## How to Use

### Trigger Detection In-Game

1. Start a Minecraft world with CodeArena mod installed
2. Press `c` to open the CodeArena dialog
3. In the project field, enter one of the following:
   - **"demo"** - Scans embedded demo Java sources with intentional smells
   - **"embedded"** - Same as demo
   - **[Any project path]** - Attempts to use CloneParser (requires CloneRefactor dependency)

### Example Demo Sources

The mod includes two demo Java files in `src/main/resources/demo-sources/`:
- `DemoSmells.java` - Contains examples of all smell types
- `DuplicateCode.java` - Additional duplication examples

## Architecture

### New Components

1. **SmellDetector** (`com.simonbaars.codearena.javaparser.SmellDetector`)
   - Standalone JavaParser-based detector
   - No CloneRefactor dependency required
   - Scans demo sources from classpath resources
   - Notifies existing SequenceObservable system

2. **Demo Sources** (`src/main/resources/demo-sources/`)
   - Embedded Java files with intentional code smells
   - Used for demonstration and testing
   - Packaged with the mod jar

### Integration

- Modified `ProblemDetectionThread` to route "demo"/"embedded" projects to SmellDetector
- Full projects still use CloneParser (if available)
- Uses existing `MetricProblem` and monster spawning system

## Coverage Limitations

### What IS Detected (Real AST Analysis)
✅ Code duplication via method body comparison  
✅ Cyclomatic complexity calculation  
✅ Method size (line count)  
✅ Parameter count (interface size)  

### What IS NOT Detected (Would Require Full CloneRefactor)
❌ Cross-file statement-level clone detection  
❌ Type-2 clones (with renamed variables)  
❌ Type-3 clones (with modifications)  
❌ Clone refactorability analysis  
❌ Clone relationship metrics  

## Technical Details

### Dependencies
- **JavaParser 3.14.1**: Already present in build.gradle
- **No additional dependencies required**

### Java Version
- Compatible with Minecraft Forge 1.12.2 / Java 8
- Note: User requested Fabric 26.2 / Java 25, but no Fabric port branch exists

### Swing UI
- N/A - This implementation has no Swing dependency
- Code editor functionality is unchanged

## Building

```bash
./gradlew build
```

The demo sources will be automatically included in the jar under `demo-sources/`.

## Future Enhancements

To get feature parity with CloneRefactor:
1. Implement statement-level clone detection
2. Add Type-2/Type-3 clone support
3. Add more sophisticated duplication algorithms
4. Support scanning larger codebases efficiently
5. Port to Fabric 26.2 (requires complete mod rewrite)

## Notes

- This is a Forge 1.12.2 mod, not Fabric 26.2 as requested
- Fabric port would require complete restructuring of the mod
- SmellDetector provides a minimal viable subset of CloneRefactor functionality
- Suitable for demonstration and educational purposes
- For production use with real projects, CloneRefactor library is still recommended
