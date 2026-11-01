package com.master.bro.catdinogame;

import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.atomic.AtomicLong;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Test;

class PauseOptionsAndCombatTest {
    private MasterCat game() {
        MasterCat game = new MasterCat();
        game.WIDTH = 1200;
        game.HEIGHT = 600;
        game.GROUND_HEIGHT = 498;
        game.scaleX = 1;
        game.scaleY = 1;
        game.uniformScale = 1;
        return game;
    }

    @Test
    void pauseFreezesProgressAndPreservesObstacleDelay() {
        MasterCat game = game();
        AtomicLong time = new AtomicLong(10_000);
        game.clock = time::get;
        game.startGame();
        GameInput input = new GameInput(game);
        game.updater.update(1.0 / 60);
        int scoreBeforePause = game.score;
        input.onKeyPressed(KeyCode.P);
        assertEquals(MasterCat.GameState.PAUSED, game.currentState);
        time.addAndGet(20_000);
        game.updater.update(20);
        assertEquals(scoreBeforePause, game.score);
        input.onKeyPressed(KeyCode.P);
        assertEquals(MasterCat.GameState.PLAYING, game.currentState);
        assertEquals(30_000, game.lastObstacleTime);
        game.updater.update(0);
        assertTrue(game.obstacles.isEmpty());
        time.addAndGet(5_000);
        game.updater.update(0);
        assertEquals(1, game.obstacles.size());
    }

    @Test
    void focusLossClearsMovementAndPausesBossTimer() {
        MasterCat game = game();
        game.startBossFight();
        GameInput input = new GameInput(game);
        input.onKeyPressed(KeyCode.A);
        assertTrue(game.leftPressed);
        game.updater.update(1.0);
        double timeBeforePause = game.bossTimer;
        input.onFocusLost();
        assertEquals(MasterCat.GameState.PAUSED, game.currentState);
        assertFalse(game.leftPressed);
        assertFalse(game.rightPressed);
        game.updater.update(30);
        assertEquals(timeBeforePause, game.bossTimer);
        input.onKeyPressed(KeyCode.P);
        game.updater.update(0.1);
        assertEquals(timeBeforePause + 0.1, game.bossTimer, 1e-9);
    }

    @Test
    void pauseMenuCanRestartOrReturnHome() {
        MasterCat game = game();
        GameInput input = new GameInput(game);
        game.startGame();
        game.score = 250;
        input.onKeyPressed(KeyCode.P);
        input.onKeyPressed(KeyCode.R);
        assertEquals(MasterCat.GameState.PLAYING, game.currentState);
        assertEquals(0, game.score);
        input.onKeyPressed(KeyCode.P);
        input.onKeyPressed(KeyCode.Q);
        assertEquals(MasterCat.GameState.MENU, game.currentState);
    }

    @Test
    void bossAttackShowsWarningBeforeSpawning() {
        MasterCat game = game();
        game.startBossFight();
        game.bossTimer = 2.2;
        game.updater.generateBossAttacks();
        assertEquals(0, game.pendingBossAttackType);
        assertTrue(game.bossProjectiles.isEmpty());
        game.pauseGame();
        game.updater.update(10);
        assertEquals(2.2, game.bossTimer);
        assertEquals(0, game.pendingBossAttackType);
        game.resumeGame();
        game.bossTimer = 2.9;
        game.updater.generateBossAttacks();
        assertTrue(game.bossProjectiles.isEmpty());
        game.bossTimer = 3.01;
        game.updater.generateBossAttacks();
        assertEquals(-1, game.pendingBossAttackType);
        assertEquals(1, game.bossProjectiles.size());
    }

    @Test
    void collisionAreaIsCenteredOnRenderedCat() {
        MasterCat game = game();
        game.startGame();
        PlayerHitbox.Bounds normal = PlayerHitbox.normal(game);
        assertEquals(game.catX, normal.centerX());
        assertEquals(game.catY, normal.y() + normal.height());
        game.obstacles.add(new Obstacle(112, 460, 10, 20, 0, false));
        game.updater.update(0);
        assertEquals(MasterCat.GameState.GAME_OVER, game.currentState);

        game.startBossFight();
        PlayerHitbox.Bounds boss = PlayerHitbox.boss(game);
        assertEquals(game.catX, boss.centerX());
        assertEquals(game.catY, boss.y() + boss.height());
        game.bossProjectiles.add(game.new BossProjectile(boss.x() - 15, boss.centerY(),
                game.bossX, game.bossY, 0));
        game.updater.update(0);
        assertEquals(MasterCat.GameState.GAME_OVER, game.currentState);
    }

    @Test
    void defeatingBossStopsTheFrameBeforeProjectileDamage() {
        MasterCat game = game();
        game.startBossFight();
        game.bossHealth = 100;
        game.dragonBallSpheres.add(game.new DragonBallSphere(game.bossX, game.bossY - 100));
        game.bossProjectiles.add(game.new BossProjectile(game.catX, game.catY - 30,
                game.bossX, game.bossY, 0));
        game.updater.update(0);
        assertEquals(MasterCat.GameState.VICTORY, game.currentState);
    }

    @Test
    void menuOptionsChangeMusicVolumeAndReducedEffects() {
        MasterCat game = game();
        game.settingsButtonX = 1010;
        game.settingsButtonY = 530;
        game.settingsMenuOpen = true;
        GameInput input = new GameInput(game);
        SettingsLayout.Bounds music = SettingsLayout.optionRow(game, SettingsLayout.Option.MUSIC);
        input.onMenuClick(music.x() + 10, music.y() + 10);
        assertFalse(game.audio.isMusicEnabled());
        SettingsLayout.Bounds effects = SettingsLayout.optionRow(game, SettingsLayout.Option.EFFECTS);
        input.onMenuClick(effects.x() + 10, effects.y() + 10);
        assertTrue(game.reducedEffects);
        SettingsLayout.Bounds track = SettingsLayout.volumeTrack(game);
        SettingsLayout.Bounds volumeRow = SettingsLayout.optionRow(game, SettingsLayout.Option.VOLUME);
        input.onMenuClick(volumeRow.x() + 10, volumeRow.y() + 10);
        assertEquals(1, game.audio.getVolume());
        input.onMenuClick(track.x(), track.y() + track.height() / 2);
        assertEquals(0, game.audio.getVolume());
        input.onMenuDrag(track.x() + track.width(), track.y() + track.height() / 2);
        assertEquals(1, game.audio.getVolume());
        assertTrue(game.settingsMenuOpen);
    }
}
