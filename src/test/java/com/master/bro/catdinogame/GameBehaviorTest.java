package com.master.bro.catdinogame;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

class GameBehaviorTest {
    private static Object field(Object target, String name) throws Exception {
        Field field = MasterCat.class.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(target);
    }

    private static void field(Object target, String name, Object value) throws Exception {
        Field field = MasterCat.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static void call(Object game, String method, Class<?> type, Object argument) throws Exception {
        Method m = type == null
            ? game.getClass().getDeclaredMethod(method)
            : game.getClass().getDeclaredMethod(method, type);
        m.setAccessible(true);
        if (type == null) m.invoke(game);
        else m.invoke(game, argument);
    }

    private static MasterCat game() throws Exception {
        MasterCat game = new MasterCat();
        field(game, "WIDTH", 1200.0);
        field(game, "HEIGHT", 600.0);
        field(game, "GROUND_HEIGHT", 498.0);
        return game;
    }

    @Test
    void scoringAndEnergyUseOriginalFrameAndTimeRules() throws Exception {
        MasterCat game = game();
        call(game, "startGame", null, null);
        call(game.updater, "updatePlaying", double.class, 1.0 / 60.0);
        assertEquals(3, field(game, "score"));
        assertEquals(8.5 / 60.0, (double) field(game, "energy"), 1e-9);
        assertEquals(1.0002, (double) field(game, "gameSpeed"), 1e-9);
    }

    @Test
    void scoreThresholdStartsBossWithOriginalValues() throws Exception {
        MasterCat game = game();
        call(game, "startGame", null, null);
        field(game, "score", 9998);
        call(game.updater, "updatePlaying", double.class, 1.0 / 60.0);
        assertEquals("BOSS_FIGHT", field(game, "currentState").toString());
        assertEquals(2000.0, (double) field(game, "bossHealth"));
        assertEquals(50.0, (double) field(game, "energy"));
    }

    @Test
    void bossTimerAndHealthHaveOriginalPriority() throws Exception {
        MasterCat game = game();
        call(game, "startBossFight", null, null);
        call(game.updater, "updateBossFight", double.class, 120.0);
        assertEquals("GAME_OVER", field(game, "currentState").toString());
        call(game, "startBossFight", null, null);
        field(game, "bossHealth", 0.0);
        call(game.updater, "updateBossFight", double.class, 120.0);
        assertEquals("VICTORY", field(game, "currentState").toString());
    }

    @Test
    void jumpPhysicsAndDoubleJumpResetOnLanding() throws Exception {
        MasterCat game = game();
        field(game, "catY", 400.0);
        field(game, "velocityY", -15.0);
        field(game, "onGround", false);
        field(game, "canDoubleJump", true);
        call(game.updater, "updateCatPhysics", double.class, 0.016);
        assertFalse((boolean) field(game, "onGround"));
        assertTrue((boolean) field(game, "canDoubleJump"));
        field(game, "catY", 497.0);
        field(game, "velocityY", 20.0);
        call(game.updater, "updateCatPhysics", double.class, 0.016);
        assertTrue((boolean) field(game, "onGround"));
        assertFalse((boolean) field(game, "canDoubleJump"));
    }

    @Test
    void obstacleAndBarrierCollisionBoundariesStayTheSame() {
        Obstacle obstacle = new Obstacle(10, 10, 50, 50, 20, false);
        obstacle.update(0.5);
        assertEquals(0.0, obstacle.x);
        assertTrue(obstacle.intersects(49, 10, 10, 10));
        assertFalse(obstacle.intersects(50, 10, 10, 10));
        BarrierBlock block = new BarrierBlock(10, 10, 30, 30);
        assertTrue(block.intersects(20, 20, 5, 5));
        block.destroy();
        assertFalse(block.intersects(20, 20, 5, 5));
    }
}
