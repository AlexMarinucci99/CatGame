package com.master.bro.catdinogame;

/** Advances the original per-frame scoring rules at a 60 Hz reference rate. */
final class ProgressionClock {
    private static final double REFERENCE_HZ = 60.0;
    private double pendingTicks;

    int steps(double elapsedSeconds) {
        // A stalled window must not grant seconds of score in one frame.
        double elapsed = Math.max(0.0, Math.min(elapsedSeconds, 0.25));
        pendingTicks += elapsed * REFERENCE_HZ;
        int steps = (int) Math.floor(pendingTicks + 1e-9);
        pendingTicks -= steps;
        return steps;
    }

    void reset() {
        pendingTicks = 0;
    }
}
