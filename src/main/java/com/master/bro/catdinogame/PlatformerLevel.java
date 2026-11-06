package com.master.bro.catdinogame;

import static com.master.bro.catdinogame.PlatformerConfig.*;
import com.master.bro.catdinogame.PlatformerMap.*;

/** Pure simulation: no JavaFX, screen coordinates or wall-clock dependencies. */
final class PlatformerLevel {
    final PlatformerMap map;
    final PlatformerConfig config;
    double x=100, y=1048, vx, vy, time, accumulator, deathTimer, animation;
    double spawnX=100, spawnY=1048, coyote, buffer, wallLock;
    boolean left, right, jumpHeld, grounded, finished, checkpointsEnabled=true, facingLeft;
    int wall, deaths, checkpoint=-1;
    Body support;

    PlatformerLevel() { this(PlatformerMap.load(), DEFAULT); }
    PlatformerLevel(PlatformerMap map, PlatformerConfig config) { this.map=map; this.config=config; }
    Rect bounds() { return new Rect(x,y,WIDTH,HEIGHT); }
    void jumpPressed() { jumpHeld=true; buffer=config.buffer(); }
    void jumpReleased() { jumpHeld=false; if(vy<0) vy*=config.jumpCut(); }
    void clearInput() { left=false; right=false; jumpHeld=false; buffer=0; }
    void restart() {
        spawnX=100; spawnY=1048; checkpoint=-1; deaths=0; time=0; accumulator=0;
        finished=false; respawn();
    }
    void respawn() {
        x=spawnX; y=spawnY; vx=vy=0; coyote=buffer=wallLock=0; wall=0;
        grounded=false; support=null; deathTimer=0; clearInput();
    }
    void die() {
        if(deathTimer>0 || finished) return;
        deaths++; deathTimer=.22; vx=vy=0; clearInput();
    }
    void advance(double dt) {
        if(!Double.isFinite(dt) || dt<=0 || finished) return;
        accumulator+=Math.min(dt,.05);
        while(accumulator+1e-12>=STEP && !finished) { step(STEP); accumulator-=STEP; }
    }
    private void step(double dt) {
        double previous=time;
        time+=dt;
        if(deathTimer>0) { deathTimer-=dt; if(deathTimer<=0) respawn(); return; }
        // A rider inherits platform displacement, resolved through the same solids.
        if(support!=null) {
            Rect a=support.at(previous), b=support.at(time);
            move(b.x()-a.x()+support.belt()*dt,0,support);
            move(0,b.y()-a.y(),support);
        }
        wall=touchingWall();
        coyote=grounded ? config.coyote() : Math.max(0,coyote-dt);
        buffer=Math.max(0,buffer-dt);
        wallLock=Math.max(0,wallLock-dt);
        int direction=(right?1:0)-(left?1:0);
        if(direction!=0) facingLeft=direction<0;
        if(wallLock==0) vx=approach(vx,direction*config.speed(),
                (direction==0 ? config.braking() : grounded?config.groundAcceleration():config.airAcceleration())*dt);
        if(buffer>0 && (coyote>0 || wall!=0)) {
            if(coyote<=0 && wall!=0) { vx=-wall*config.wallX(); vy=-config.wallY(); wallLock=.16; }
            else vy=-config.jump();
            if(!jumpHeld) vy*=config.jumpCut();
            buffer=0; coyote=0; grounded=false; support=null;
        }
        vy=Math.min(config.terminalSpeed(),vy+config.gravity()*dt);
        if(wall!=0 && direction==wall && vy>0) vy=Math.min(vy,config.slideSpeed());
        grounded=false; support=null;
        move(vx*dt,0,null);
        move(0,vy*dt,null);
        // A moving body that traps the player is lethal rather than allowing penetration.
        for(Body b:map.bodies()) {
            Rect r=b.at(time);
            if((b.lethal() || b.solid()) && bounds().overlaps(r)) { die(); return; }
        }
        if(y>map.height() || x<0 || x+WIDTH>map.width()) { die(); return; }
        for(int i=0;i<map.bodies().size();i++) {
            Body b=map.bodies().get(i);
            if(!bounds().overlaps(b.at(time))) continue;
            if(b.kind().equals("checkpoint") && checkpointsEnabled && i>checkpoint) {
                checkpoint=i; spawnX=b.x()+12; spawnY=b.y()+b.h()-HEIGHT;
            }
            if(b.kind().equals("exit")) { finished=true; clearInput(); }
        }
        if (Math.abs(vx)>5) animation=(animation+dt*Math.max(10,Math.min(17,10+Math.abs(vx)/100)))%6;
    }
    private int touchingWall() {
        for(Body b:map.bodies()) if(b.solid()) {
            Rect r=b.at(time);
            if(y+HEIGHT>r.y()+.1 && y<r.y()+r.h()-.1) {
                if(Math.abs(x+WIDTH-r.x())<1) return 1;
                if(Math.abs(x-r.x()-r.w())<1) return -1;
            }
        }
        return 0;
    }
    /** Axis sweeps select the nearest surface, independent of map row ordering. */
    private void move(double dx, double dy, Body ignore) {
        double allowed=dx!=0?dx:dy;
        Rect p=bounds();
        for(Body b:map.bodies()) if(b.solid() && b!=ignore) {
            Rect r=b.at(time);
            if(dx!=0 && p.y()+p.h()>r.y()+.001 && p.y()<r.y()+r.h()-.001) {
                if(dx>0 && p.x()+p.w()<=r.x()+.001 && p.x()+p.w()+allowed>=r.x()) allowed=Math.min(allowed,r.x()-p.x()-p.w());
                if(dx<0 && p.x()>=r.x()+r.w()-.001 && p.x()+allowed<=r.x()+r.w()) allowed=Math.max(allowed,r.x()+r.w()-p.x());
            }
            if(dy!=0 && p.x()+p.w()>r.x()+.001 && p.x()<r.x()+r.w()-.001) {
                if(dy>0 && p.y()+p.h()<=r.y()+.001 && p.y()+p.h()+allowed>=r.y()) {
                    allowed=Math.min(allowed,r.y()-p.y()-p.h()); grounded=true; support=b;
                }
                if(dy<0 && p.y()>=r.y()+r.h()-.001 && p.y()+allowed<=r.y()+r.h()) allowed=Math.max(allowed,r.y()+r.h()-p.y());
            }
        }
        if(dx!=0) { x+=allowed; if(Math.abs(allowed-dx)>1e-9) vx=0; }
        else { y+=allowed; if(Math.abs(allowed-dy)>1e-9) vy=0; }
    }
    private static double approach(double a,double b,double amount) { return a<b?Math.min(b,a+amount):Math.max(b,a-amount); }
}
