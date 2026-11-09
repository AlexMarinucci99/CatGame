package com.master.bro.catdinogame;

final class PlatformerCamera {
    double x,y;
    void follow(PlatformerLevel level, double dt, boolean snap) {
        double targetX=Math.max(0,Math.min(level.map.width()-1000,level.x-420));
        double targetY=Math.max(0,Math.min(level.map.height()-650,level.y-340));
        double blend=snap?1:1-Math.exp(-8*dt);
        x+=(targetX-x)*blend; y+=(targetY-y)*blend;
    }
}
