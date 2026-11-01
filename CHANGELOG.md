# Changelog

## Unreleased — Milestone 1

- Characterized original input, scoring, jumping, boss and collision rules against commit `1ffe89b`.
- Made scoring and acceleration time-consistent at a 60 Hz reference rate.
- Tuned opening obstacle interval and speed to a gradual curve, as requested.
- Made normal and double jumps smooth across display refresh rates while preserving the original 60 Hz trajectory.
- Used physical full-screen bounds and a black root background to address the menu strip.
- Added deterministic tests, baseline assessment and a prioritized roadmap.
- Increased obstacle arrival frequency continuously from a 4.5-second opening interval to 1.5 seconds at 10,000 points.
- Let energy spheres destroy every boss projectile variant; parcels and barrier blocks remain destructible.
- Redesigned the settings panel and boss HUD with consistent hit bounds, grouped controls, and clearer status bars.
