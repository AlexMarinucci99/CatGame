package com.master.bro.catdinogame;

/** Normal-level opening pace. Boss attacks retain their original timing. */
final class ObstaclePacing {
    private ObstaclePacing() {}

    static double intervalMillis(int score) {
        return Math.max(1000.0, 4500.0 - (score / 300.0) * 30.0);
    }

    static double speedPixelsPerSecond(int score) {
        return 190.0 + Math.max(0, score) * 0.045;
    }

    static boolean ready(long nowMillis, long lastSpawnMillis, int score) {
        return nowMillis - lastSpawnMillis > intervalMillis(score);
    }
}
