# Refactor audit

## Baseline

- Original repository branch: `main`, commit `1ffe89b`.
- Maven project: `CatDinoGame/MasterCat/pom.xml`, Java 21, JavaFX 21.0.2.
- `MasterCat.java`: 2,725 lines; `GameLauncher.java`: separate entry point.
- Original `javac` compilation with the vendored JavaFX jars succeeded on Linux. The baseline Maven build could not run because Maven was not installed. No graphical or audio session was available.
- Original Maven configuration declared controls, FXML, and media, plus an assembly JAR. No FXML files or FXML API calls were found. Old scripts mixed SDK-specific compilation, nested paths, and executable JAR claims.

## Behavior map

- States: menu, normal play, boss fight, game over, victory.
- Menu: Space starts, I/settings click toggles panel, T jumps to boss for debugging.
- Normal play: score increments by `(int)(gameSpeed * 3)` per frame; speed increments by `0.0002` per frame. At 10,000 points it enters the boss fight. Energy refills at 8.5 units per second, capped at 50. Space jumps and allows one double jump; C fires a sphere at full energy. Obstacles spawn using wall-clock time and move using `dt`.
- Boss: 120-second timer, 2,000 health, 60-second barrier, energy refills at 15 per second. X activates a five-second shield costing 25. Arrow/A/D movement uses `dt`. Projectile attacks spawn from wall-clock time. Health depletion is evaluated before timer expiry.
- Global inputs: M music, U/Esc full-screen. Any nonglobal key on the end screens returns to menu.
- Resources: classpath `/images/...` and `/audio/...`. PNG backgrounds and boss art have optional JPG fallbacks; missing optional boss projectile images use block textures. No optional fallback asset was deleted.

## Layout

Before:

```text
CatDinoGame/
  MasterCat/
    pom.xml
    src/main/java/com/master/bro/catdinogame/{MasterCat,GameLauncher}.java
    src/main/resources/{images,audio}/
    lib/javafx-sdk-21.0.2/ + javafx-sdk.zip
    target/
    legacy launch/build scripts and multiple READMEs
  legacy scripts, READMEs, workspace files, cat.png
README.md
```

After:

```text
pom.xml
run-windows.bat
run-linux.sh
run-macos.sh
src/main/java/com/master/bro/catdinogame/
  MasterCat.java  GameLauncher.java  GameInput.java  GameUpdate.java
  GameRenderer.java  GameAssets.java  GameAudio.java
  Obstacle.java  BarrierBlock.java
src/main/resources/{images,audio}/
src/test/java/com/master/bro/catdinogame/
.github/workflows/ci.yml
legacy-assets/cat.png
docs/REFACTOR_AUDIT.md
README.md
```

## Cleanup decisions

The exact path-by-path list of 161 deleted tracked files is in [REMOVED_FILES.txt](REMOVED_FILES.txt). Moved assets and sources are excluded from that deletion list.

- Removed all 101 tracked files in the manually bundled Windows JavaFX SDK and ZIP. Maven resolves the OS-specific dependencies. The removed SDK licensing files applied to that vendored copy; OpenJFX artifacts now provide their own notices.
- Removed all 27 tracked `target/` build files; these were generated copies/classes.
- Removed legacy batch and shell launch/build scripts after tracing their references. They pointed to the old nested layout, vendored SDK, or assembly JAR. Exactly three root launch scripts replace them.
- Consolidated the old READMEs, `LEGGI_PRIMA.txt`, `ESEGUIBILE_CREATO.md`, and image-specific instructions into one root README. Removed empty `AUDIO_GUIDE.md`.
- Removed obsolete VS Code workspace settings and workspace files tied to the old directory layout.
- Retained the distinct, unused `CatDinoGame/cat.png` as `legacy-assets/cat.png` because its origin and possible external use were uncertain.
- Retained all original game images and audio, including optional fallback behavior in the loader. The optional projectile image paths refer to files never present in the baseline.

## Verification and limits

| Platform | Compile | Automated behavior checks | Graphical/audio runtime |
| --- | --- | --- | --- |
| Linux | Original and refactored sources compiled with Java 21 and vendored JavaFX jars | All six JUnit test bodies passed in a local headless runner; the Maven/JUnit runner was unavailable locally | Not tested interactively |
| Windows | CI matrix configured; result pending | CI result pending | Not tested interactively |
| macOS | CI matrix configured; result pending | CI result pending | Not tested interactively |

The original update formulas and constants were copied into `GameUpdate` without modification. Input handlers and render calls were extracted without changing their order. Image/audio loading and two independent entities also compiled. This supports source-level preservation and the headless checks, but does not prove pixel-perfect, audio-identical, or full gameplay identity; interactive regression testing remains necessary.

`MasterCat` still owns game state and the three nonstatic projectile/effect entity classes, which depend on that state. Moving them further would require a larger state API and deserves graphical regression coverage first.
