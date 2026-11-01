package com.master.bro.catdinogame;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

class PackagedAssetsTest {
    @Test
    void requiredAssetsAreOnClasspath() {
        String[] resources = {
            "/images/player/cat.png",
            "/images/player/cat-run-v1.png",
            "/images/backgrounds/menu_background.png",
            "/images/backgrounds/game_background.png",
            "/images/backgrounds/gameover_background.png",
            "/images/backgrounds/victory_background.png",
            "/images/backgrounds/boss_background.png",
            "/images/boss/amazon_courier_boss.png",
            "/images/blocks/block_normal.png",
            "/images/blocks/block_double.png",
            "/audio/menu_music.mp3",
            "/audio/game_music.mp3",
            "/audio/boss_music.mp3",
            "/audio/victory_music.mp3"
        };
        for (String resource : resources) {
            assertNotNull(getClass().getResource(resource), resource);
        }
    }
}
