# Gameplay and lifecycle corrections

Baseline: main `c1e8e2f66352b2bac86dffbef0c5bf61e7ec7444`.

## Demonstrated defects and corrections

| Defect in baseline | Correction | Regression evidence |
| --- | --- | --- |
| `DragonBallSphere.isOffScreen()` returns true as soon as impact sets `exploding` | Keep explosion in the world until its 0.5-second lifetime expires; repeated destroy calls are idempotent | Explosion survives 0.49 seconds, is removed after 0.5 seconds, and cannot damage another barrier block |
| Every KEY_PRESSED event triggers an action, including OS repeat | Track physical held keys; action keys require release before another activation; movement aliases remain active until both keys release | Repeated Space cannot consume double jump, held menu Space cannot leak into play, repeated P/M cannot toggle again; focus-loss and alias tests |
| Boss gravity adds velocity once per rendered frame | Scale gravity by elapsed seconds, using the original 60 Hz rate; correct displacement for the same reference trajectory | Ballistic y after one second is 212.81 at 30/60/120/144 Hz from the tested initial condition |
| Timers, projectiles, score, physics, and animation accept different stall durations | Public simulation update rejects invalid deltas, accepts at most 50 ms, and splits that accepted time into steps no larger than 1/60 second | A ten-second stall produces the same score, energy, conveyor and sphere motion as 50 ms; boss and shield timers also advance only 50 ms |
| Obstacle spawning uses wall time while the rest of gameplay pauses | Accumulate accepted simulation time for obstacle intervals; discard catch-up bursts | Pause preserves remaining spawn delay and restart resets it |
| Mutable state can be assigned by input/updater/tests; a late callback can overwrite an end state | Keep current state private in MasterCat, expose a read accessor, guard terminal transitions, and stop processing after collision/state changes | Late gameOver cannot replace victory; terminal callbacks on the menu do nothing; same-frame boss victory remains authoritative |

The existing 60 Hz reference, controls, artwork, atlas, damage values, attack types, animation speed and collision geometry remain in place. The intended changes are repeat suppression, consistent elapsed-time behavior and safe lifecycle completion. No new gameplay features were added.

## Validation

`mvn -o verify`: 38 tests, zero failures, zero errors, zero skipped on Linux Java 21. The ordinary Maven JUnit provider was used. Tests that previously called key-press handlers repeatedly to represent taps now emit the missing key releases. No visual assets changed.

## Remaining risks

- Homing direction is recomputed each simulation step; exact homing paths can still differ slightly across refresh rates. Ballistic gravity is analytically covered; this does not prove every full encounter identical.
- Collision checks remain discrete. Bounded substeps reduce tunneling risk but are not swept collision detection.
- Excess frame-stall time is deliberately dropped, so a heavily overloaded machine experiences slower simulation rather than a catch-up burst.
- MasterCat still owns mutable entity lists and audio. Current-state writes are restricted, but this is not a wholesale model/controller migration.
- Random attack and obstacle choices remain nondeterministic. Interactive full runs, audible playback and focus/fullscreen behavior on target desktops remain manual acceptance checks.

## Proposed next animation and visual-polish milestone

1. Player: refine atlas foot anchors and the run-loop seam, then improve jump-to-land blending. Keep hitboxes fixed and retain reduced-effects behavior.
2. Combat: use the now-correct explosion lifetime for readable impact feedback, clear hit confirmation and restrained energy-ready cues. Pool effects only if profiling shows a need.
3. Boss: add a short anticipation pose tied to the existing warning window, distinct follow-through for each attack type, and readable phase transitions. Keep launch timing and damage fixed.
4. Acceptance: capture normal/double-jump, landing, shield, parcel hit and boss-hit clips at 30/60/144 Hz; compare reduced-effects mode and measure frame times before adding effects.
