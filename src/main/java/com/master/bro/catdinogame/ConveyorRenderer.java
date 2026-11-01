package com.master.bro.catdinogame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/** Industrial conveyor artwork anchored to the existing gameplay ground plane. */
final class ConveyorRenderer {
    private ConveyorRenderer() {}

    static void render(GraphicsContext gc, MasterCat game) {
        double scale = Math.max(0.5, game.uniformScale);
        double y = game.GROUND_HEIGHT;
        double available = game.HEIGHT - y;
        double deck = Math.min(72 * scale, available * 0.68);
        double surface = Math.min(16 * scale, deck * 0.24);
        double travel = game.reducedEffects ? 0 : game.conveyorTravel;
        double treadSpacing = 48 * scale;

        gc.setFill(Color.rgb(23, 29, 35));
        gc.fillRect(0, y, game.WIDTH, available);
        // Feet and parcels meet this exact upper edge.
        gc.setFill(Color.rgb(47, 55, 62));
        gc.fillRect(0, y, game.WIDTH, surface);
        gc.setStroke(Color.rgb(105, 115, 122));
        gc.setLineWidth(2 * scale);
        gc.strokeLine(0, y, game.WIDTH, y);
        gc.setStroke(Color.rgb(80, 89, 96));
        gc.setLineWidth(2 * scale);
        for (double x = -treadSpacing - travel % treadSpacing; x < game.WIDTH; x += treadSpacing) {
            gc.strokeLine(x, y + 2 * scale, x + 10 * scale, y + surface - 2 * scale);
        }

        gc.setFill(Color.rgb(39, 47, 56));
        gc.fillRect(0, y + surface, game.WIDTH, deck - surface);
        double radius = Math.min(13 * scale, (deck - surface) * 0.35);
        double rollerY = y + surface + (deck - surface) / 2;
        double angle = -travel / Math.max(1, radius);
        for (double x = 24 * scale; x < game.WIDTH; x += 84 * scale) {
            gc.setFill(Color.rgb(23, 29, 35));
            gc.fillOval(x - radius - 3 * scale, rollerY - radius - 3 * scale,
                    2 * radius + 6 * scale, 2 * radius + 6 * scale);
            gc.setFill(Color.rgb(111, 122, 130));
            gc.fillOval(x - radius, rollerY - radius, radius * 2, radius * 2);
            gc.setStroke(Color.rgb(52, 62, 71));
            gc.setLineWidth(2 * scale);
            for (int spoke = 0; spoke < 3; spoke++) {
                double a = angle + spoke * Math.PI * 2 / 3;
                gc.strokeLine(x, rollerY, x + Math.cos(a) * radius * 0.78,
                        rollerY + Math.sin(a) * radius * 0.78);
            }
            gc.setFill(Color.rgb(37, 46, 54));
            gc.fillOval(x - 3 * scale, rollerY - 3 * scale, 6 * scale, 6 * scale);
        }

        gc.setFill(Color.rgb(82, 91, 98));
        gc.fillRect(0, y + deck - 5 * scale, game.WIDTH, 5 * scale);
        for (double x = 70 * scale; x < game.WIDTH; x += 240 * scale) {
            gc.setFill(Color.rgb(54, 63, 70));
            gc.fillRect(x, y + deck, 17 * scale, Math.max(0, available - deck - 9 * scale));
            gc.setFill(Color.rgb(78, 87, 94));
            gc.fillRoundRect(x - 12 * scale, game.HEIGHT - 12 * scale, 41 * scale, 6 * scale, 3, 3);
        }
    }
}
