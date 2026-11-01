package com.master.bro.catdinogame;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class ProgressionPacingTest {
    private static int ticksOverTenSeconds(int hz) {
        ProgressionClock clock = new ProgressionClock();
        int ticks = 0;
        for (int frame = 0; frame < hz * 10; frame++) {
            ticks += clock.steps(1.0 / hz);
        }
        return ticks;
    }

    @Test
    void progressionHasSameRealTimeRateAcrossRefreshRates() {
        for (int hz : new int[] {30, 60, 120, 144}) {
            assertEquals(600, ticksOverTenSeconds(hz));
        }
    }

    @Test
    void longFrameIsCappedAndRestartClearsPartialTick() {
        ProgressionClock clock = new ProgressionClock();
        assertEquals(15, clock.steps(5.0));
        clock.steps(1.0 / 120.0);
        clock.reset();
        assertEquals(0, clock.steps(1.0 / 120.0));
    }

    @Test
    void openingObstaclesAreSlowerAndRampGradually() {
        assertEquals(4500.0, ObstaclePacing.intervalMillis(0));
        assertEquals(190.0, ObstaclePacing.speedPixelsPerSecond(0));
        assertFalse(ObstaclePacing.ready(4500, 0, 0));
        assertTrue(ObstaclePacing.ready(4501, 0, 0));
        assertTrue(ObstaclePacing.speedPixelsPerSecond(2000) > ObstaclePacing.speedPixelsPerSecond(0));
        assertTrue(ObstaclePacing.intervalMillis(2000) < ObstaclePacing.intervalMillis(0));
        assertEquals(280.0, ObstaclePacing.speedPixelsPerSecond(2000));
    }
}
