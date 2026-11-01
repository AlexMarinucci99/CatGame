# Gameplay baseline and recovery audit

## References and method

Original: commit `1ffe89b`, `CatDinoGame/MasterCat/src/main/java/com/master/bro/catdinogame/MasterCat.java`. Refactored main: merge commit `1e06e21` and its parent `b43d71b`.

I compared the original and refactored method bodies for frame updates, physics, spawning, collisions, boss logic, state transitions, assets, audio, and all render methods after removing only delegation qualifiers (`game.`, `assets.`, `audio.`). The original and refactored keyboard press, release, and mouse handlers also matched after those qualifiers were removed. The only method-body differences found in that comparison were delegation, audio status access through `GameAudio`, and new cleanup at shutdown. No specific gameplay regression caused by the extraction was demonstrated by this static audit. That result does not prove runtime equivalence.

## Original rules characterized

| Area | Original `1ffe89b` rule | Test or evidence |
| --- | --- | --- |
| Score and acceleration | Per rendered frame: `score += (int)(gameSpeed * 3)` and `gameSpeed += 0.0002` | `GameBehaviorTest`, `ProgressionPacingTest` |
| Jump | Press Space on ground: velocity `-15`; once in air: `-12`; then unavailable until landing | `InputCharacterizationTest`, `JumpTimingTest` |
| Vertical physics | Per frame: `velocityY += 0.6`, then `catY += velocityY`; landing clamps to ground | `GameBehaviorTest`, `JumpTimingTest` |
| Normal obstacles | First nominal spawn after 3,000 ms; interval `max(1000, 3000 - score/300*30)`; speed `250 + score/300*150` | Source comparison; pacing tests document new values |
| Energy | Cap 50; normal recharge 8.5/s, boss recharge 15/s; C consumes full bar | `GameBehaviorTest`, `InputCharacterizationTest` |
| Boss | Begins at 10,000 points; health 2,000; time 120 s; barrier after 60 s; health win checked before timeout loss | `GameBehaviorTest` |
| Shield | X during boss costs 25 energy, lasts 5 s, cannot reactivate while active | `InputCharacterizationTest`; duration code retained |
| Input | M music, U/Esc fullscreen, I settings on menu, T boss debug, arrows/A/D boss movement | `InputCharacterizationTest` |
| Assets | Classpath images and audio; optional JPG backgrounds and projectile fallback drawings | `PackagedAssetsTest`, source comparison |

## User-reported behavior and changes in this milestone

1. **Opening obstacles feel too fast.** At 144 Hz the original per-frame score and acceleration advance roughly 2.4 times as fast as at 60 Hz, which rapidly increases obstacle speed. The original obstacle speed formula also reaches 1,250 px/s by 2,000 points. This is a baseline design problem, not a demonstrated extraction regression. The new progression clock preserves the original score/acceleration steps at 60 Hz, yields the same elapsed-time progression at 30/60/120/144 Hz, and limits a long stalled frame to 0.25 s of catch-up. Separately, an explicit pacing change starts obstacles after 4.5 s at 190 px/s and ramps speed by 0.045 px/s per point (280 px/s at 2,000 points). Boss attack timing is unchanged.
2. **White strip below the full-screen menu.** The original and refactored code both used `Screen.getPrimary().getVisualBounds()`, which excludes the desktop panel. On the local 1366×768 display the old startup logged a 1366×728 canvas. Using full screen bounds now logs 1366×768; the root background is also black. This is a verified display-layout defect inherited from the original, not an extraction regression.
3. **Normal and double jumps need to feel smoother.** The original vertical physics stepped once per rendered frame, so its real-time duration changed across monitor refresh rates. The new fractional-frame formula equals the original discrete formula at 60 Hz and produces the same trajectory over equal elapsed time at 30/60/120/144 Hz. Jump strength and the single double-jump rule remain unchanged.

## Coverage and uncertainty

The Linux runtime opened the JavaFX window at 1366×768 and loaded the original images and media. A short automated launch does not verify a full run, perceived difficulty, audio output, or absence of a visible strip on every window manager. The new timing and obstacle pacing are intentional gameplay changes requested after the baseline audit. Interactive play at different refresh rates and resolutions remains necessary. Random obstacle patterns and wall-clock spawn behavior remain nondeterministic; Milestone 2 should inject a clock and random source before changing them further.
