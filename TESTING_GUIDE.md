# Testing Guide for JavaParser AST Detection

## Quick Test (Recommended)

### Using Demo Sources

1. **Build the mod**:
   ```bash
   ./gradlew build
   ```

2. **Install in Minecraft**:
   - Copy `build/libs/clonedetection-1.0.jar` to your Minecraft Forge 1.12.2 mods folder
   - Start Minecraft

3. **Trigger detection in-game**:
   - Start a single-player world
   - Press `c` to open the CodeArena dialog
   - In the project field, enter: **`demo`** or **`embedded`**
   - Click to start detection

4. **Expected results**:
   - Message: "Searching for code problems, please wait..."
   - Monsters spawn based on detected smells:
     - **Zombies**: For duplicated methods (3 instances detected)
     - **Creepers**: For complex methods (2 instances detected)
     - **Skeletons**: For large methods (1 instance detected)
     - **Spiders**: For methods with too many parameters (1 instance detected)
   - Message: "All metrics have been successfully parsed!"

## Demo Source Files

The mod includes two Java files with intentional code smells:

### DemoSmells.java
- `processInput()` - High cyclomatic complexity (11+ branches)
- `complexMethod()` - Too many parameters (8 parameters)
- `largeMethod()` - Large method (60+ lines)
- `calculateSum()` - Duplicated code (duplicate 1)
- `computeTotal()` - Duplicated code (duplicate 2)

### DuplicateCode.java
- `addNumbers()` - Duplicated code (duplicate 3)
- `validate()` - High cyclomatic complexity (10+ branches)

## Testing with Your Own Code

To test with a real Java project:

1. Enter a project path instead of "demo"
2. The system will attempt to use CloneParser (requires CloneRefactor dependency)
3. If CloneRefactor is not available, detection will fail

**Note**: Full project analysis requires the CloneRefactor library, which is not included as a standalone dependency in this branch.

## Manual Testing (Without Minecraft)

If you want to test the SmellDetector directly:

```java
SmellDetector detector = new SmellDetector();
detector.scanDemoSources();  // Scans embedded demo sources
```

This will notify the SequenceObservable with detected problems, which can be observed for testing.

## Troubleshooting

### "Failed to load demo sources"
- Check that `src/main/resources/demo-sources/` exists
- Verify demo sources are included in the jar: `jar tf build/libs/clonedetection-1.0.jar | grep demo-sources`

### "All metrics have been successfully parsed!" but no monsters spawn
- Check console for detection output
- Verify SequenceObservable is properly initialized
- Ensure CodeArena is properly initialized before detection

### Detection takes too long
- Demo sources are very small and should complete in <1 second
- If using a real project path, large projects may take minutes

## Verifying Detection Works

1. Check console output for:
   ```
   Starting project prepare
   ```

2. Look for monster spawning messages or entities in the world

3. Check inventory for diamond items with package names (one per detected problem)

## Known Limitations

- **Forge 1.12.2 only** - No Fabric 26.2 port exists
- **Demo mode only** for standalone detection without CloneRefactor
- **Basic detection** - Advanced clone detection requires CloneRefactor library
- **Method-level only** - No statement-level clone detection

## Success Criteria

✅ PR created: https://github.com/SimonBaars/CodeArena/pull/38  
✅ Demo sources included and scannable  
✅ SmellDetector integrates with existing system  
✅ Clear documentation of real vs demo detection  
✅ No CloneRefactor jar required for demo mode  
