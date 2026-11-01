# Testing status

- Baseline source comparison: original `1ffe89b` versus refactored `1e06e21`, documented in `GAMEPLAY_BASELINE.md`.
- Local Linux: Java 21 `mvn -o verify` passes 44 JUnit tests through Maven Surefire. The GitHub Actions matrix runs `mvn clean verify` on Ubuntu, Windows and macOS.
- Local JavaFX rendering: captured and inspected the settings panel, boss attack warning, and pause overlay at 1366×768 and 1200×600 using packaged images. The Linux launch script also opened the game at 1366×768 and initialized audio. This verifies startup and layout rendering, not a complete playthrough or audible output.
- Automated coverage includes pause/resume timing, focus-loss movement release, restart/menu controls, hitbox alignment, boss warning delay, settings interactions, obstacle progression, and sphere collisions.
- Animation checks cover atlas size/transparency, equal playback rate across refresh rates, pause/reset behavior, and landing feedback that leaves the hitbox unchanged. Rendered normal-level frames verify conveyor contact and compositing of the generated cat atlas.
- Interactive acceptance still needed: play the complete run, judge hitbox fairness and warning time, verify volume and full-screen interactions, and listen to audio on the target desktop.

- Energy-combat coverage checks mouse-only firing, charge/pause/reset gates, special trajectory and one-time 1,000 damage, barrier interception, explosion expiry and special-triggered victory. JavaFX snapshots verify the refreshed HUD, sprite compositing, parcel destruction and the boss at 1,000/2,000 HP after a special hit.
