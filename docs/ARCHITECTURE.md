# Architecture assessment

`MasterCat` still owns a large mutable state surface and several nonstatic projectile/effect classes. `GameInput`, `GameUpdate`, and `GameRenderer` access that state directly. This made the previous extraction mechanically safe but left tight coupling and weak unit-test seams. `GameAudio` and `GameAssets` own their resources; `Obstacle` and `BarrierBlock` are independent.

Milestone 1 keeps the ownership model stable while introducing two small pure timing/pacing classes and direct input action methods. This limits regression risk and permits deterministic checks without a JavaFX window. The 60 Hz reference is explicit. Vertical physics uses a closed-form extension of the original 60 Hz recurrence, avoiding per-frame allocations.

A later architecture milestone should move game state into a purpose-built model, expose read-only render data, inject clock/random interfaces, and enforce transitions in a state controller. That migration should proceed subsystem by subsystem with graphical regression tests. A full fixed-step loop, interpolation, ECS, or broad renaming now would obscure the reported gameplay fixes.
