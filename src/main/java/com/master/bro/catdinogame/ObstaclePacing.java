package com.master.bro.catdinogame;

/** Gradually increases the normal level's obstacle arrival rate. */
final class ObstaclePacing {
    private ObstaclePacing() {}

    static double intervalMillis(int score) {
        // The first obstacle has a generous lead-in; by the boss threshold,
        // arrivals are three times as frequent without a sudden difficulty step.
        return Math.max(1500.0, 4500.0 - Math.max(0, score) * 0.3);
    }

    static double speedPixelsPerSecond(int score) {
        return 190.0 + Math.max(0, score) * 0.045;
    }

    static boolean ready(long nowMillis, long lastSpawnMillis, int score) {
        return nowMillis - lastSpawnMillis > intervalMillis(score);
    }
}
