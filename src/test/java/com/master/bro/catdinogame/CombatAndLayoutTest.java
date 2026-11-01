package com.master.bro.catdinogame;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class CombatAndLayoutTest {
    private MasterCat game() {
        MasterCat game = new MasterCat();
        game.WIDTH = 1200;
        game.HEIGHT = 600;
        game.GROUND_HEIGHT = 498;
        game.catX = 150;
        game.catY = 498;
        return game;
    }

    @Test
    void energySphereDestroysNormalAndDoubleObstacles() {
        for (boolean partOfDouble : new boolean[] {false, true}) {
            MasterCat game = game();
            game.startGame();

            game.obstacles.add(new Obstacle(390, 430, 50, 50, 190, partOfDouble));
            MasterCat.DragonBallSphere sphere = game.new DragonBallSphere(410, 450);
            game.dragonBallSpheres.add(sphere);

            game.updater.update(0);

            assertTrue(game.obstacles.isEmpty());
            assertEquals(50, game.score);
            assertFalse(sphere.intersects(390, 430, 50, 50));
        }
    }

    @Test
    void energySphereDestroysEveryBossProjectileVariant() {
        for (int type = 0; type < 4; type++) {
            MasterCat game = game();
            game.startBossFight();
            game.bossProjectiles.add(game.new BossProjectile(350, 400, 150, 450, type));
            MasterCat.DragonBallSphere sphere = game.new DragonBallSphere(350, 400);
            game.dragonBallSpheres.add(sphere);

            game.updater.update(0);

            assertTrue(game.bossProjectiles.isEmpty());
            assertEquals(25, game.score);
            assertFalse(sphere.intersects(330, 380, 40, 40));
        }
    }

    @Test
    void settingsPanelHitAreaMatchesVisibleBounds() {
        MasterCat game = game();
        game.settingsButtonX = 1010;
        game.settingsButtonY = 530;
        SettingsLayout.Bounds panel = SettingsLayout.panel(game);
        assertEquals(430, panel.width());
        assertEquals(500, panel.height());
        assertTrue(panel.contains(panel.x() + 10, panel.y() + 10));
        assertFalse(panel.contains(panel.x() - 1, panel.y() + 10));
        assertTrue(panel.x() + panel.width() <= game.WIDTH);
        assertTrue(panel.y() + panel.height() <= game.settingsButtonY);
    }
}
