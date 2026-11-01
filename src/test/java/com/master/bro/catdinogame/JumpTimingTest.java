package com.master.bro.catdinogame;

import static org.junit.jupiter.api.Assertions.*;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Test;

class JumpTimingTest {
    private static void advance(MasterCat game, int hz, double seconds) {
        double elapsed = 0;
        while (elapsed < seconds - 1e-12) {
            double dt = Math.min(1.0 / hz, seconds - elapsed);
            game.updater.update(dt);
            elapsed += dt;
        }
    }

    private static MasterCat newGame() {
        MasterCat game = new MasterCat();
        game.WIDTH = 1200;
        game.GROUND_HEIGHT = 498;
        game.startGame();
        return game;
    }

    @Test
    void normalJumpMatchesOriginalSixtyHertzTrajectory() {
        for (int hz : new int[] {30, 60, 120, 144}) {
            MasterCat game = newGame();
            new GameInput(game).onKeyPressed(KeyCode.SPACE);
            advance(game, hz, 0.5);
            assertEquals(327.0, game.catY, 1e-7);
            assertEquals(3.0, game.velocityY, 1e-7);
        }
    }

    @Test
    void doubleJumpHasTheSameSmoothTrajectoryAtEveryRefreshRate() {
        double referenceY = Double.NaN;
        for (int hz : new int[] {30, 60, 120, 144}) {
            MasterCat game = newGame();
            GameInput input = new GameInput(game);
            input.onKeyPressed(KeyCode.SPACE);
            advance(game, hz, 0.2);
            input.onKeyPressed(KeyCode.SPACE);
            assertEquals(-12.0, game.velocityY, 1e-9);
            advance(game, hz, 0.3);
            if (Double.isNaN(referenceY)) referenceY = game.catY;
            assertEquals(referenceY, game.catY, 1e-7);
            assertFalse(game.canDoubleJump);
        }
    }
}
