# Testing status

- Baseline source comparison: original `1ffe89b` versus refactored `1e06e21`, documented in `GAMEPLAY_BASELINE.md`.
- Local Linux: Java 21 compilation with cached OpenJFX Linux jars; 15 JUnit test bodies executed in a headless test runner. The local environment has no Maven command.
- Local graphical smoke: JavaFX window opened at 1366×768, original images and media initialized, then the process was stopped by a nine-second timeout. This is startup evidence, not a complete playthrough or proof that audio was audible.
- GitHub Actions: `mvn clean verify` matrix for Ubuntu, Windows and macOS. Record the result of the milestone branch run before merge.
- Interactive acceptance still needed: verify opening obstacle feel, normal and double jumps, white-strip removal, boss progression, music and controls on target desktops.
