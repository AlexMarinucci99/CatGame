package com.master.bro.catdinogame;

/** Shared hit area for the menu panel and its mouse handler. */
final class SettingsLayout {
    private SettingsLayout() {}

    record Bounds(double x, double y, double width, double height) {
        boolean contains(double px, double py) {
            return px >= x && px <= x + width && py >= y && py <= y + height;
        }
    }

    static Bounds panel(MasterCat game) {
        double width = Math.min(410, game.WIDTH - 24);
        double height = Math.min(410, game.HEIGHT - 82);
        double x = Math.max(12, Math.min(game.settingsButtonX + game.settingsButtonWidth - width,
                game.WIDTH - width - 12));
        double y = Math.max(12, game.settingsButtonY - height - 12);
        return new Bounds(x, y, width, height);
    }
}
