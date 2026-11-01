# CatGame (MasterCat)

CatGame is a full-screen JavaFX arcade game. Guide the cat through incoming parcels, survive the boss encounter, and defeat the courier before time runs out.

## Requirements

- Java Development Kit 21
- Apache Maven 3.9 or newer on `PATH`
- A graphical desktop session and working audio output
- Internet access on the first build so Maven can download JavaFX and plugins

JavaFX 21.0.2 is resolved through Maven for the host platform. The repository does not include a JavaFX SDK or a portable executable JAR.

## Run

Run the script for your operating system from a terminal. The scripts work from any current directory and accept repository paths with spaces.

| System | Command |
| --- | --- |
| Windows | `run-windows.bat` |
| Linux | `./run-linux.sh` |
| macOS | `./run-macos.sh` |

The scripts build the project with Maven and launch `GameLauncher` through the JavaFX Maven plugin. Install the JDK and Maven first; check `java -version` and `mvn -version` if the script reports a missing command. On Linux, JavaFX Media may also require system multimedia libraries provided by the distribution.

## Controls

| Key or input | Action |
| --- | --- |
| Space | Start from menu; jump and double jump during play and boss fight |
| C | Fire an energy sphere when energy is full (50) |
| X | Activate a shield during the boss fight when at least 25 energy is available |
| Left / Right or A / D | Move horizontally during the boss fight |
| M | Toggle music in every state |
| U or Esc | Toggle full-screen mode in every state |
| I | Toggle the settings panel on the menu |
| T | Debug shortcut from the menu directly to the boss fight |
| Mouse | Open or close the menu settings panel |
| Any other key | Return to the menu after game over or victory |

Global `M`, `U`, and `Esc` controls take precedence over the “any other key” action on end screens.

## Progression

The normal level awards points each frame and increases speed gradually. At 10,000 points, the game enters the boss fight. The cat begins that fight with 50 energy. The boss has 2,000 health and a 120-second limit; at 60 seconds a barrier appears. Defeat the boss before the timer expires to win. Collision with a parcel can end a run. Energy spheres can destroy parcels, boss projectiles, and barrier blocks. The boss fight also provides a five-second shield for 25 energy.

The game has menu, normal play, boss fight, game over, and victory screens. Music changes with those states.

## Build and development

```sh
mvn clean verify
mvn javafx:run
```

The Maven project is at the repository root. Production sources and resources are under `src/main`; focused tests are under `src/test`. `MasterCat` owns the game state and animation loop. `GameInput` installs controls; `GameUpdate` handles frame updates, physics, spawning, and collisions; `GameRenderer` draws the screens. `GameAssets` loads images, `GameAudio` owns music players, and `Obstacle` and `BarrierBlock` are independent entities. `GameLauncher` is the entry point. The project intentionally retains the original frame-based physics and scoring formulas.

The GitHub Actions workflow compiles and tests on Ubuntu, Windows, and macOS with Java 21. Its first matrix run passed on all three systems. It does not exercise a live graphical session or audio device.

## Troubleshooting

- **Java or Maven not found:** Install a Java 21 JDK and Maven, then ensure both commands are on `PATH`.
- **Dependency download failure:** Check Maven Central access and retry the build.
- **No graphical display:** Start the game from a desktop session; a headless shell cannot validate the window.
- **No audio:** Check OS audio support and JavaFX Media dependencies. The game can continue if audio initialization fails.
- **Missing image:** Maven packages files from `src/main/resources`. The renderer uses its original fallback drawings for optional images. The optional boss projectile textures are absent from this repository and intentionally remain absent.

## Compatibility and assets

Compilation and headless logic checks were performed on Linux with Java 21 using the original JavaFX SDK jars. Maven was unavailable locally, but [the three-platform Maven CI run](https://github.com/AlexMarinucci99/CatGame/actions/runs/41294615779) completed successfully. Window rendering, audio playback, and the launch scripts still need interactive validation. See [the refactor audit](docs/REFACTOR_AUDIT.md) for details.

The original image and audio files were left byte-for-byte unchanged. Their provenance and reuse terms are not documented in the original repository; verify rights before redistributing them. JavaFX is obtained from the OpenJFX Maven artifacts under its own license. `legacy-assets/cat.png` is an unused repository image retained pending provenance review.
