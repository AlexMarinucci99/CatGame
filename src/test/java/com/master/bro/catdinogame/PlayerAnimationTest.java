package com.master.bro.catdinogame;

import static org.junit.jupiter.api.Assertions.*;
import javafx.scene.input.KeyCode;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;

class PlayerAnimationTest {
    @Test
    void atlasHasSixSquareCellsAndTransparentBackground() throws Exception {
        var atlas = ImageIO.read(getClass().getResourceAsStream("/images/player/cat-run-v1.png"));
        assertEquals(1536, atlas.getWidth());
        assertEquals(1024, atlas.getHeight());
        assertTrue(atlas.getColorModel().hasAlpha());
        assertEquals(0, atlas.getRGB(0, 0) >>> 24);
        for (int frame = 0; frame < PlayerAnimation.FRAME_COUNT; frame++) {
            int center = atlas.getRGB(frame % 3 * 512 + 256, frame / 3 * 512 + 256);
            assertTrue((center >>> 24) > 200);
        }
    }

    @Test
    void runningCycleHasTheSameRateAcrossRefreshRates() {
        for (int hz : new int[] {30, 60, 120, 144}) {
            double phase = 0;
            for (int i = 0; i < hz; i++) phase = PlayerAnimation.advance(phase, 1.0 / hz, 190);
            assertEquals(5.9, phase, 1e-9);
        }
        assertEquals(2, PlayerAnimation.frame(0, false, -10));
        assertEquals(3, PlayerAnimation.frame(0, false, 10));
    }

    @Test
    void conveyorAndRunningStopInPauseAndResetOnRestart() {
        MasterCat game = game();
        game.startGame();
        game.updater.update(1.0 / 60);
        double travel = game.conveyorTravel;
        double phase = game.runFramePhase;
        assertTrue(travel > 0);
        assertTrue(phase > 0);
        game.pauseGame();
        game.updater.update(10);
        assertEquals(travel, game.conveyorTravel);
        assertEquals(phase, game.runFramePhase);
        game.startGame();
        assertEquals(0, game.conveyorTravel);
        assertEquals(0, game.runFramePhase);
    }

    @Test
    void landingStartsAVisualResponseWithoutChangingHitbox() {
        MasterCat game = game();
        game.startGame();
        double width = PlayerHitbox.normal(game).width();
        new GameInput(game).onKeyPressed(KeyCode.SPACE);
        for (int i = 0; i < 100 && !game.onGround; i++) game.updater.update(1.0 / 60);
        assertTrue(game.onGround);
        assertTrue(game.landingTimer > 0);
        assertEquals(width, PlayerHitbox.normal(game).width());
    }

    private MasterCat game() {
        MasterCat game = new MasterCat();
        game.WIDTH = 1200;
        game.HEIGHT = 600;
        game.GROUND_HEIGHT = 498;
        game.scaleX = game.scaleY = game.uniformScale = 1;

        return game;
    }
}
