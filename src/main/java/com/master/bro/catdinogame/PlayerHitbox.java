package com.master.bro.catdinogame;

/** The collision area follows the visible center of each rendered cat sprite. */
final class PlayerHitbox {
    static final double NORMAL_SPRITE_SIZE = 110;
    static final double BOSS_SPRITE_SIZE = 80;

    private PlayerHitbox() {}

    record Bounds(double x, double y, double width, double height) {
        double centerX() { return x + width / 2; }
        double centerY() { return y + height / 2; }
    }

    static Bounds normal(MasterCat game) {
        return centeredAtFeet(game.catX, game.catY, game.scaleSize(NORMAL_SPRITE_SIZE));
    }

    static Bounds boss(MasterCat game) {
        return centeredAtFeet(game.catX, game.catY, BOSS_SPRITE_SIZE);
    }

    private static Bounds centeredAtFeet(double centerX, double feetY, double spriteSize) {
        double width = spriteSize * 0.56;
        double height = spriteSize * 0.76;
        return new Bounds(centerX - width / 2, feetY - height, width, height);
    }
}
