# Prioritized improvement roadmap

## Milestone 1 — Baseline and reported behavior

Acceptance: original/refactored comparison recorded; opening obstacle curve is gradual; score and jump timing agree across 30/60/120/144 Hz at the 60 Hz reference; full-screen canvas covers physical display; automated tests and CI pass. Interactive feel and window-manager appearance remain an explicit follow-up.

## Milestone 2 — State and simulation boundaries

Introduce an owned game model, a validated state transition controller, and injectable randomness. Pause and focus-loss movement release are now implemented, and obstacle timing has an injectable millisecond clock while boss attacks use simulation time. Replace reflection in old tests with supported test seams. Acceptance: deterministic obstacle and boss traces, input press/release tests, and restart/reset tests.

## Milestone 3 — Animation foundation

Add visual-only animation states, easing and bounded transient effects without changing hitboxes. Add reduced-effects setting. Acceptance: normal/double jump, shield and impacts show clear feedback; effects never alter collision results; frame-time measurements stay within an agreed budget on reference hardware.

## Milestone 4 — Presentation and audio

Improve HUD hierarchy, attack telegraphs, menu transitions, music crossfades and volume controls while retaining artwork and music. Acceptance: interactive review of readability and accessibility; resource disposal checks; no duplicate audio playback.

## Milestone 5 — Release quality

Profile actual play on ordinary hardware, optimize measured hotspots, test full runs on Windows/Linux/macOS, and document reproducible release results. Compilation and headless CI alone do not satisfy this milestone.
