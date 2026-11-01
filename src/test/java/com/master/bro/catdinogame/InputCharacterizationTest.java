package com.master.bro.catdinogame;

import static org.junit.jupiter.api.Assertions.*;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Test;

/** Rules observed in the original 1ffe89b keyboard handlers. */
class InputCharacterizationTest {
    private static MasterCat game() {
        MasterCat game = new MasterCat();
        game.WIDTH = 1200;
        game.GROUND_HEIGHT = 498;
        return game;
    }

    @Test
    void menuStartAndDebugBossShortcut() {
        MasterCat game = game();
        GameInput input = new GameInput(game);
        input.onKeyPressed(KeyCode.I);
        assertTrue(game.settingsMenuOpen);
        input.onKeyPressed(KeyCode.SPACE);
        assertEquals(MasterCat.GameState.PLAYING, game.currentState);
        game.resetToMenu();
        input.onKeyPressed(KeyCode.T);
        assertEquals(MasterCat.GameState.BOSS_FIGHT, game.currentState);
        assertEquals(10_000, game.score);
        assertEquals(50.0, game.energy);
    }

    @Test
    void spaceAllowsOneDoubleJumpUntilLanding() {
        MasterCat game = game();
        game.startGame();
        GameInput input = new GameInput(game);
        input.onKeyPressed(KeyCode.SPACE);
        assertEquals(-15.0, game.velocityY);
        assertFalse(game.onGround);
        assertTrue(game.canDoubleJump);
        input.onKeyPressed(KeyCode.SPACE);
        assertEquals(-12.0, game.velocityY);
        assertFalse(game.canDoubleJump);
        input.onKeyPressed(KeyCode.SPACE);
        assertEquals(-12.0, game.velocityY);
        game.catY = game.GROUND_HEIGHT;
        game.velocityY = 20;
        game.updater.update(0.016);
        assertTrue(game.onGround);
        assertFalse(game.canDoubleJump);
    }

    @Test
    void energyAttackAndBossShieldKeepOriginalCosts() {
        MasterCat game = game();
        game.startGame();
        GameInput input = new GameInput(game);
        game.energy = 49;
        input.onKeyPressed(KeyCode.C);
        assertEquals(0, game.dragonBallSpheres.size());
        game.energy = 50;
        input.onKeyPressed(KeyCode.C);
        assertEquals(1, game.dragonBallSpheres.size());
        assertEquals(0.0, game.energy);
        game.startBossFight();
        input.onKeyPressed(KeyCode.X);
        assertTrue(game.shieldActive);
        assertEquals(25.0, game.energy);
        input.onKeyPressed(KeyCode.X);
        assertEquals(25.0, game.energy);
    }

    @Test
    void bossMovementPressReleaseAndGlobalMusic() {
        MasterCat game = game();
        GameInput input = new GameInput(game);
        assertTrue(game.audio.isMusicEnabled());
        input.onKeyPressed(KeyCode.M);
        assertFalse(game.audio.isMusicEnabled());
        input.onKeyPressed(KeyCode.T);
        input.onKeyPressed(KeyCode.A);
        input.onKeyPressed(KeyCode.RIGHT);
        assertTrue(game.leftPressed);
        assertTrue(game.rightPressed);
        input.onKeyReleased(KeyCode.A);
        input.onKeyReleased(KeyCode.RIGHT);
        assertFalse(game.leftPressed);
        assertFalse(game.rightPressed);
        game.gameOver();
        input.onKeyPressed(KeyCode.R);
        assertEquals(MasterCat.GameState.MENU, game.currentState);
    }
}
