package com.master.bro.catdinogame;

/** Shared hit area for the menu panel and its mouse handler. */
final class SettingsLayout {
    private SettingsLayout() {}

    enum Option { MUSIC, VOLUME, EFFECTS, FULLSCREEN }

    record Bounds(double x, double y, double width, double height) {
        boolean contains(double px, double py) {
            return px >= x && px <= x + width && py >= y && py <= y + height;
        }
    }

    static Bounds panel(MasterCat game) {
        double width = Math.min(430, game.WIDTH - 24);
        double height = Math.min(500, game.HEIGHT - 82);
        double x = Math.max(12, Math.min(game.settingsButtonX + game.settingsButtonWidth - width,
                game.WIDTH - width - 12));
        double y = Math.max(12, game.settingsButtonY - height - 12);
        return new Bounds(x, y, width, height);
    }

    static Bounds optionRow(MasterCat game, Option option) {
        Bounds panel = panel(game);
        double scale = panel.height() / 500.0;
        double baseline = switch (option) {
            case MUSIC -> 309;
            case VOLUME -> 350;
            case EFFECTS -> 393;
            case FULLSCREEN -> 435;
        };
        return new Bounds(panel.x() + 20, panel.y() + (baseline - 25) * scale,
                panel.width() - 40, 32 * scale);
    }

    static Bounds volumeTrack(MasterCat game) {
        Bounds panel = panel(game);
        double scale = panel.height() / 500.0;
        return new Bounds(panel.x() + 150, panel.y() + 342 * scale,
                panel.width() - 230, 12 * scale);
    }
}
