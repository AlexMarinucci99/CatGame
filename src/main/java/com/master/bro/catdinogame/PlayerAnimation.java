package com.master.bro.catdinogame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/** Six-frame atlas playback; the render pose never changes the collision box. */
final class PlayerAnimation {
    static final int FRAME_COUNT = 6;
    private static final double CELL = 512;
    private static final double[] FOOT_BASELINES = {491, 486, 477, 439, 471, 472};
    private PlayerAnimation() {}

    static double advance(double phase, double dt, double speed) {
        double fps = Math.max(10, Math.min(17, 10 + speed / 100));
        return (phase + Math.max(0, Math.min(dt, 0.1)) * fps) % FRAME_COUNT;
    }

    static int frame(double phase, boolean onGround, double velocityY) {
        if (!onGround) return velocityY < 0 ? 2 : 3;
        return Math.floorMod((int) Math.floor(phase), FRAME_COUNT);
    }

    static void render(GraphicsContext gc, MasterCat game, double size, boolean running) {
        Image atlas = game.assets.catRunImage;
        boolean hasStill = game.assets.catImage != null && !game.assets.catImage.isError();
        boolean animated = atlas != null && !atlas.isError()
                && (!hasStill || (!game.reducedEffects && (running || !game.onGround)));
        int frame = game.reducedEffects ? 0 : frame(game.runFramePhase, game.onGround, game.velocityY);
        double squash = game.reducedEffects ? 0 : Math.max(0, game.landingTimer / 0.16);
        gc.save();
        gc.translate(game.catX, game.catY);
        gc.scale(game.facingLeft ? -1 : 1, 1);
        gc.scale(1 + squash * 0.07, 1 - squash * 0.08);
        if (animated) {
            double factor = size / 458;
            gc.drawImage(atlas, (frame % 3) * CELL, (frame / 3) * CELL, CELL, CELL,
                    -CELL / 2 * factor, -FOOT_BASELINES[frame] * factor,
                    CELL * factor, CELL * factor);
        } else if (game.assets.catImage != null && !game.assets.catImage.isError()) {
            gc.drawImage(game.assets.catImage, -size / 2, -size, size, size);
        }
        gc.restore();
    }
}
