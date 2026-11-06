package com.master.bro.catdinogame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import com.master.bro.catdinogame.PlatformerMap.*;

/** Industrial scenery and HUD, entirely separate from collision geometry. */
final class PlatformerRenderer {
    static void render(GraphicsContext g, MasterCat game) {
        PlatformerLevel l=game.platformer;
        if(l==null)return;
        var camera=game.platformCamera;
        double width=g.getCanvas().getWidth(),height=g.getCanvas().getHeight();
        double scale=Math.min(width/1000,height/650);
        g.save();
        g.setFill(Color.web("#0c1117"));g.fillRect(0,0,width,height);
        g.translate((width-1000*scale)/2,(height-650*scale)/2);g.scale(scale,scale);
        g.beginPath();g.rect(0,0,1000,650);g.clip();
        g.setFill(new LinearGradient(0,0,0,650,false,CycleMethod.NO_CYCLE,
                new Stop(0,Color.web("#101823")),new Stop(1,Color.web("#3b2924"))));
        g.fillRect(0,0,1000,650);
        // Distant storage racks, crates and pipework move more slowly than the world.
        for(int i=-1;i<9;i++) {
            double sx=i*180-(camera.x*.25)%180,sy=120-(camera.y*.12)%100;
            g.setFill(Color.web("#27303a"));g.fillRect(sx,sy,9,600);
            for(int row=0;row<5;row++) {
                double yy=sy+row*110;
                g.fillRect(sx,yy,170,7);
                g.setFill(Color.web("#483b30"));g.fillRect(sx+20,yy-58,62,55);g.fillRect(sx+90,yy-45,54,42);
                g.setFill(Color.web("#27303a"));
            }
            g.setFill(Color.web("#ab4a35"));g.fillRect(sx+4,sy+10,8,5);
        }
        g.save();g.translate(-camera.x,-camera.y);
        for(Body b:l.map.bodies()) {
            Rect r=b.at(l.time);
            if(r.x()+r.w()<camera.x || r.x()>camera.x+1000)continue;
            if(b.solid()) {
                if(b.kind().equals("moving")) {
                    g.setStroke(Color.web("#66717a"));g.setLineDashes(5,7);
                    g.strokeLine(b.x()+b.w()/2,b.y()+12,b.x()+b.ax()+b.w()/2,b.y()+b.ay()+12);g.setLineDashes();
                }
                g.setFill(Color.web("#303d48"));g.fillRect(r.x(),r.y(),r.w(),r.h());
                g.setFill(Color.web("#a4afb2"));g.fillRect(r.x(),r.y(),r.w(),5);
                g.setFill(Color.web("#56616b"));
                for(double xx=r.x()+12;xx<r.x()+r.w();xx+=32)g.fillOval(xx,r.y()+12,4,4);
                if(b.kind().equals("belt")) {
                    g.setStroke(Color.web("#e3a74e"));
                    for(double xx=r.x()+((l.time*100)%24);xx<r.x()+r.w()-12;xx+=24) {
                        g.strokeLine(xx,r.y()+8,xx+10,r.y()+12);g.strokeLine(xx+10,r.y()+12,xx,r.y()+16);
                    }
                }
            } else if(b.kind().equals("saw")) {
                g.setStroke(Color.web("#a16952"));g.setLineDashes(4,6);
                g.strokeLine(b.x()+b.w()/2,b.y()+b.h()/2,b.x()+b.ax()+b.w()/2,b.y()+b.ay()+b.h()/2);g.setLineDashes();
                g.save();g.translate(r.x()+r.w()/2,r.y()+r.h()/2);g.rotate(game.reducedEffects?0:l.time*220);
                g.setFill(Color.web("#d7b49b"));
                for(int t=0;t<12;t++) {g.rotate(30);g.fillPolygon(new double[]{-5,0,6},new double[]{-r.h()/2+8,-r.h()/2,-r.h()/2+8},3);}
                g.fillOval(-r.w()/2+7,-r.h()/2+7,r.w()-14,r.h()-14);
                g.setFill(Color.web("#8e3b30"));g.fillOval(-6,-6,12,12);g.restore();
            } else if(b.kind().equals("press")) {
                g.setFill(Color.web("#67727a"));g.fillRect(r.x()+r.w()/2-6,b.y()-80,12,r.y()-b.y()+80);
                g.setFill(Color.web("#864138"));g.fillRect(r.x(),r.y(),r.w(),r.h());
                g.setFill(Color.web("#e3a74e"));for(int j=0;j<6;j++)g.fillRect(r.x()+j*12,r.y()+r.h()-12,6,12);
                g.setFill(Color.web("#f18a66"));g.fillOval(b.x()+b.w()/2-4,b.y()-25,8,8);
            } else if(b.kind().equals("spikes")) {
                g.setFill(Color.web("#e1aa87"));
                for(double xx=r.x();xx<r.x()+r.w();xx+=16)g.fillPolygon(new double[]{xx,xx+8,xx+16},new double[]{r.y()+r.h(),r.y(),r.y()+r.h()},3);
            } else if(b.kind().equals("checkpoint")) {
                g.setFill(Color.web("#9aa6a8"));g.fillRect(r.x()+10,r.y()+15,3,r.h()-15);
                g.setFill(l.checkpoint>=l.map.bodies().indexOf(b)?Color.web("#83c4a5"):Color.web("#e3a74e"));
                g.fillRect(r.x()+13,r.y()+15,32,20);
            } else if(b.kind().equals("exit")) {
                g.setFill(Color.web("#111a22"));g.fillRect(r.x(),r.y(),r.w(),r.h());
                g.setStroke(Color.web("#c69855"));g.setLineWidth(5);g.strokeRect(r.x(),r.y(),r.w(),r.h());
                g.setFill(Color.web("#ddd4c3"));g.setFont(Font.font(15));g.fillText("BOSS →",r.x()+20,r.y()+45);
            }
        }
        drawCat(g,game,l);
        g.restore();
        g.setFill(Color.web("#111a22",.94));g.fillRoundRect(18,16,964,72,10,10);
        g.setFill(Color.web("#e6ddd0"));g.setFont(Font.font("SansSerif",19));
        String section=l.x<1450?"A · Ingresso":l.x<2400?"B · Le pareti":l.x<4070?"C · Le macchine":"D · La fuga";
        g.fillText("02 / CENTRO DI SMISTAMENTO INFERNALE",34,43);
        g.setFont(Font.font(14));g.setFill(Color.web("#b5bdc4"));
        g.fillText(section+"     Morti: "+l.deaths+"     Tempo: "+String.format(java.util.Locale.ROOT,"%.1f s",l.time),34,69);
        g.fillText("A/D o ←/→  Muovi    SPAZIO  Salto / wall-jump    P  Pausa    R  Ricomincia",34,625);
        String tip=l.x<820?"Tieni SPAZIO per saltare più in alto":l.x<1760?"Nel pozzo: salta verso la parete opposta e premi di nuovo SPAZIO":l.x<3780?"Osserva le traiettorie e il ritmo delle presse":"Raggiungi il portellone del boss";
        g.fillText(tip,34,112);
        if(l.deathTimer>0 && !game.reducedEffects) {
            g.setFill(Color.web("#c98461",.12));g.fillRect(0,0,1000,650);
        }
        if(game.state()==MasterCat.GameState.LEVEL_TRANSITION || game.state()==MasterCat.GameState.BOSS_TRANSITION) {
            g.setFill(Color.web("#0b1119",.9));g.fillRect(0,0,1000,650);
            g.setFill(Color.web("#e7d7b9"));g.setFont(Font.font(30));
            g.fillText(game.state()==MasterCat.GameState.LEVEL_TRANSITION?"Livello 1 completato":"Centro di smistamento superato",200,280);
            g.setFont(Font.font(20));g.fillText(game.state()==MasterCat.GameState.LEVEL_TRANSITION?"02 / Il centro di smistamento infernale":"Il boss ti aspetta",200,330);
        }
        g.restore();
    }
    private static void drawCat(GraphicsContext g,MasterCat game,PlatformerLevel l) {
        g.save();g.translate(l.x+18,l.y+52);g.scale(l.facingLeft?-1:1,1);
        if(l.deathTimer>0)g.setGlobalAlpha(Math.max(0,l.deathTimer/.22));
        if(!game.reducedEffects && l.wall!=0 && l.vy>0)g.rotate(-l.wall*9);
        var image=game.assets.catRunImage;
        if(image!=null && !image.isError()) {
            int frame=game.reducedEffects?0:PlayerAnimation.frame(l.animation,l.grounded,l.vy);
            double[] feet={491,486,477,439,471,472};double size=64.0/458;
            g.drawImage(image,(frame%3)*512,(frame/3)*512,512,512,-256*size,-feet[frame]*size,512*size,512*size);
        } else if(game.assets.catImage!=null)g.drawImage(game.assets.catImage,-32,-64,64,64);
        g.restore();
    }
}
