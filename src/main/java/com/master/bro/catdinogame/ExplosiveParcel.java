package com.master.bro.catdinogame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/** One-shot special projectile with a separate, non-colliding explosion lifetime. */
final class ExplosiveParcel {
    double x,y;
    private final double direction;
    private double verticalSpeed=-110;
    private boolean exploding;
    private double explosionAge;
    ExplosiveParcel(double x,double y,double targetX,double targetY) {
        this.x=x;this.y=y;direction=targetX>=x?1:-1;
        double flightTime=Math.max(0.1,Math.abs(targetX-x)/520);
        verticalSpeed=(targetY-y)/flightTime-160*flightTime;
    }
    void update(double dt) {
        if(exploding) { explosionAge+=dt;return; }
        x+=direction*520*dt;y+=verticalSpeed*dt+160*dt*dt;verticalSpeed+=320*dt;
    }
    boolean intersects(double rx,double ry,double w,double h) {
        return !exploding && x+22>rx && x-22<rx+w && y+22>ry && y-22<ry+h;
    }
    void explode() { if(!exploding) {exploding=true;explosionAge=0;} }
    boolean isExploding() {return exploding;}
    boolean expired(double width,double height) {
        return exploding?explosionAge>=0.65:x < -70 || x>width+70 || y>height+70;
    }
    void render(GraphicsContext gc,MasterCat game) {
        if(exploding) {
            if(game.assets.energySprites!=null && !game.reducedEffects)
                game.assets.energySprites.cell(gc,4+Math.min(3,(int)(explosionAge/0.65*4)),x-65,y-65,130,130);
            else {gc.setStroke(Color.rgb(200,155,92));gc.setLineWidth(3);gc.strokeOval(x-35,y-35,70,70);}
            return;
        }
        if(game.assets.parcelSprites!=null)game.assets.parcelSprites.grounded(gc,0,x-24,y-24,48,48);
        else {gc.setFill(Color.TAN);gc.fillRect(x-22,y-22,44,44);}
        gc.setFill(Color.rgb(164,62,65));gc.fillRoundRect(x-15,y-10,30,20,5,5);
        gc.setFill(Color.WHITE);gc.setFont(javafx.scene.text.Font.font("Arial",javafx.scene.text.FontWeight.BOLD,11));
        gc.fillText("MC",x-9,y+4);
    }
}
