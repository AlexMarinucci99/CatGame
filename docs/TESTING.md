# Testing status

- Baseline source comparison: original `1ffe89b` versus refactored `1e06e21`, documented in `GAMEPLAY_BASELINE.md`.
- Local Linux: Java 21 offline Maven compilation and package; 25 JUnit test bodies passed through a headless runner. Offline Maven Surefire itself needs an uncached provider, so the GitHub Actions matrix runs the actual `mvn clean verify` gate.
- Local JavaFX rendering: captured and inspected the settings panel, boss attack warning, and pause overlay at 1366×768 and 1200×600 using packaged images. The Linux launch script also opened the game at 1366×768 and initialized audio. This verifies startup and layout rendering, not a complete playthrough or audible output.
- Automated coverage includes pause/resume timing, focus-loss movement release, restart/menu controls, hitbox alignment, boss warning delay, settings interactions, obstacle progression, and sphere collisions.
- Interactive acceptance still needed: play the complete run, judge hitbox fairness and warning time, verify volume and full-screen interactions, and listen to audio on the target desktop.
