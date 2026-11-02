package com.master.bro.catdinogame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/** Visual equipment only; protection and duration remain owned by the simulation. */
final class ShieldRenderer {
    private ShieldRenderer() {}

    static void render(GraphicsContext gc, MasterCat game) {
        if (!game.shieldActive) return;
        double side = game.bossX >= game.catX ? 1 : -1;
        double height = 70;
        double width = 55;
        double x = game.catX + (side > 0 ? 7 : -width - 7);
        double y = game.catY - height - 3;
        int frame = game.reducedEffects ? 0 : (int) (game.shieldTimer * 5) % 4;
        gc.save();
        // The printed logo stays readable when the boss is to the player's left.
        if (!game.reducedEffects) {
            double remaining = game.SHIELD_DURATION - game.shieldTimer;
            gc.setGlobalAlpha(Math.min(1, 0.45 + Math.max(0, remaining) / 0.6));
        }
        if (game.assets.shieldSprites != null) {
            game.assets.shieldSprites.grounded(gc, frame, x, y, width, height);
        } else {
            gc.setFill(Color.web("#b88b54"));
            gc.setStroke(Color.web("#624324"));
            gc.setLineWidth(3);
            double[] xs = {x, x + width, x + width * 0.88, x + width / 2, x + width * 0.12};
            double[] ys = {y, y, y + height * 0.6, y + height, y + height * 0.6};
            gc.fillPolygon(xs, ys, 5);
            gc.strokePolygon(xs, ys, 5);
        }
        gc.restore();
    }
}
