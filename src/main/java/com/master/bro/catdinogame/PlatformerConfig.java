package com.master.bro.catdinogame;

/** World pixels and seconds; deliberately independent of runner/boss physics. */
record PlatformerConfig(double speed, double groundAcceleration, double airAcceleration,
        double braking, double gravity, double jump, double coyote, double buffer,
        double terminalSpeed, double slideSpeed, double wallX, double wallY, double jumpCut) {
    static final PlatformerConfig DEFAULT = new PlatformerConfig(
            340, 2600, 1800, 3200, 2300, 820, .10, .12, 1000, 125, 360, 850, .45);
    static final double STEP = 1.0 / 120;
    static final double WIDTH = 36, HEIGHT = 52;
}
