package com.master.bro.catdinogame;

import org.junit.jupiter.api.Test;
import javafx.scene.input.KeyCode;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static com.master.bro.catdinogame.PlatformerConfig.*;
import com.master.bro.catdinogame.PlatformerMap.*;

class PlatformerTest {
    static Body body(String kind,double x,double y,double w,double h) {return new Body(kind,x,y,w,h,0,0,0,0);}
    static PlatformerLevel level(Body... bodies) {
        var l=new PlatformerLevel(new PlatformerMap(6200,1400,List.of(bodies)),DEFAULT);
        l.x=100;l.y=448;return l;
    }
    static PlatformerLevel floor() { return level(body("solid",0,500,3000,80)); }
    static void run(PlatformerLevel l,int steps) {for(int i=0;i<steps;i++)l.advance(STEP);}
    @Test void accelerationBrakingReversalAndGroundJump() {
        var l=floor();run(l,2);assertTrue(l.grounded);
        l.right=true;run(l,10);assertTrue(l.vx>0 && l.vx<DEFAULT.speed());
        run(l,30);assertEquals(DEFAULT.speed(),l.vx);
        l.right=false;run(l,20);assertEquals(0,l.vx);
        l.left=true;run(l,30);assertEquals(-DEFAULT.speed(),l.vx);
        l.jumpPressed();run(l,1);assertTrue(l.vy<0);assertFalse(l.grounded);
    }
    @Test void releasedJumpHasLowerApex() {
        var held=floor();var tap=floor();run(held,2);run(tap,2);
        held.jumpPressed();tap.jumpPressed();run(held,1);run(tap,1);tap.jumpReleased();
        double h=held.y,t=tap.y;
        for(int i=0;i<60;i++){run(held,1);run(tap,1);h=Math.min(h,held.y);t=Math.min(t,tap.y);}
        assertTrue(h<t-70);
    }
    @Test void coyoteWorksOnlyDuringGraceWindow() {
        var l=level(body("solid",0,500,160,40));run(l,2);l.x=170;
        run(l,5);l.jumpPressed();run(l,1);assertTrue(l.vy<0);
        var late=level(body("solid",0,500,160,40));run(late,2);late.x=170;
        run(late,20);late.airJumpAvailable=false;late.jumpPressed();run(late,1);assertTrue(late.vy>0);
    }
    @Test void bufferedJumpExecutesOnLandingWithoutExtraPress() {
        var l=floor();l.y=430;l.vy=200;l.jumpPressed();run(l,12);assertTrue(l.vy<0);
    }
    @Test void wallSlideAndJumpPushAwayWithoutGroundJump() {
        var l=level(body("solid",200,0,40,700));l.x=164;l.y=100;l.vy=500;l.right=true;
        run(l,1);assertEquals(DEFAULT.slideSpeed(),l.vy);assertEquals(1,l.wall);
        l.jumpPressed();run(l,1);assertTrue(l.vx<0);assertTrue(l.vy<0);assertTrue(l.x<164);
    }
    @Test void highSpeedWallFloorAndCeilingAreSolid() {
        var l=level(body("solid",200,0,20,600),body("solid",0,500,500,40),body("solid",0,300,180,20));
        l.x=160;l.vx=10000;l.wallLock=1;run(l,1);assertEquals(164,l.x,.001);
        l.x=100;l.y=400;l.vy=1000;run(l,20);assertEquals(448,l.y,.001);assertTrue(l.grounded);
        l.jumpPressed();run(l,35);assertTrue(l.y>=320);
    }
    @Test void lethalContactRespawnAndInputsReset() {
        var l=level(body("spikes",100,440,50,60));l.right=true;l.jumpHeld=true;run(l,1);
        assertEquals(1,l.deaths);assertTrue(l.deathTimer>0);assertFalse(l.right);
        run(l,30);assertEquals(100,l.x);assertEquals(0,l.vx);assertFalse(l.jumpHeld);
    }
    @Test void checkpointsAndDisabledMode() {
        var l=level(body("solid",0,500,3000,80),body("checkpoint",100,400,80,100));
        run(l,1);assertEquals(112,l.spawnX);l.die();run(l,28);assertEquals(112,l.x);
        l.restart();assertEquals(-1,l.checkpoint);assertEquals(0,l.deaths);assertFalse(l.finished);
        l.checkpointsEnabled=false;l.x=100;l.y=448;run(l,1);assertEquals(-1,l.checkpoint);
    }
    @Test void movingPlatformCarriesRiderAndCrushRespawns() {
        var platform=new Body("moving",0,500,400,30,100,60,4,0);
        var l=level(platform);run(l,2);double x=l.x;
        run(l,100);assertTrue(l.x>x+20);assertEquals(platform.at(l.time).y()-HEIGHT,l.y,.01);
        assertEquals(0,l.deaths);
        assertEquals(platform.at(0),platform.at(4));
    }
    @Test void renderRatesAndStallsUseSameAcceptedTime() {
        var a=floor();var b=floor();a.right=b.right=true;
        for(int i=0;i<60;i++)a.advance(1.0/30);
        for(int i=0;i<288;i++)b.advance(1.0/144);
        assertEquals(a.x,b.x,1e-8);assertEquals(a.y,b.y,1e-8);assertEquals(a.time,b.time,1e-8);
        var stall=floor();stall.advance(9);assertEquals(.05,stall.time,1e-9);
        stall.advance(Double.NaN);assertEquals(.05,stall.time,1e-9);
    }
    static MasterCat platformGame() {
        var g=new MasterCat();g.WIDTH=1200;g.HEIGHT=600;g.GROUND_HEIGHT=498;
        g.startGame();g.score=10000;g.updater.update(.02);
        assertEquals(MasterCat.GameState.LEVEL_TRANSITION,g.state());
        for(int i=0;i<30;i++)g.updater.update(.05);
        assertEquals(MasterCat.GameState.PLATFORMER,g.state());return g;
    }
    @Test void completeProgressionPauseExitAndReset() {
        var g=platformGame();var input=new GameInput(g);input.onKeyPressed(KeyCode.D);
        input.onKeyPressed(KeyCode.P);double t=g.platformer.time;g.updater.update(.05);
        assertEquals(t,g.platformer.time);assertFalse(g.platformer.right);
        input.onKeyPressed(KeyCode.P);assertEquals(MasterCat.GameState.PAUSED,g.state());
        input.onKeyReleased(KeyCode.P);input.onKeyPressed(KeyCode.P);
        assertEquals(MasterCat.GameState.PLATFORMER,g.state());
        input.onKeyReleased(KeyCode.SPACE);assertFalse(g.platformer.right);
        g.platformer.x=5940;g.platformer.y=248;g.updater.update(.02);
        assertEquals(MasterCat.GameState.BOSS_TRANSITION,g.state());assertTrue(g.platformerCompleted);
        g.gameOver();assertEquals(MasterCat.GameState.BOSS_TRANSITION,g.state());
        for(int i=0;i<24;i++)g.updater.update(.05);
        assertEquals(MasterCat.GameState.BOSS_FIGHT,g.state());assertEquals(2000,g.bossHealth);
        g.startGame();assertNull(g.platformer);assertFalse(g.platformerCompleted);
    }
    @Test void focusLossReleaseAliasesAndQuickRestart() {
        var g=platformGame();var i=new GameInput(g);
        i.onKeyPressed(KeyCode.A);i.onKeyPressed(KeyCode.LEFT);i.onKeyReleased(KeyCode.A);
        assertTrue(g.platformer.left);i.onFocusLost();assertFalse(g.platformer.left);
        g.resumeGame();i.onKeyPressed(KeyCode.R);assertEquals(0,g.platformer.time);
        assertEquals(MasterCat.GameState.PLATFORMER,g.state());
    }
    @Test void cameraClampsAndMapHasFourSectionsAndExit() {
        var l=new PlatformerLevel();var c=new PlatformerCamera();c.follow(l,0,true);assertEquals(0,c.x);
        l.x=6100;l.y=1390;c.follow(l,0,true);assertEquals(5200,c.x);assertEquals(750,c.y);
        assertEquals(3,l.map.bodies().stream().filter(b->b.kind().equals("checkpoint")).count());
        assertEquals(1,l.map.bodies().stream().filter(b->b.kind().equals("exit")).count());
    }
    @Test void pauseDoesNotChangeAirborneVelocityAndDebugEntryIsMenuOnly() {
        var g=platformGame();g.platformer.vy=-500;
        g.pauseGame();assertEquals(-500,g.platformer.vy);
        new GameInput(g).onKeyReleased(KeyCode.SPACE);assertEquals(-500,g.platformer.vy);
        g.resetToMenu();var input=new GameInput(g);input.onKeyPressed(KeyCode.F2);
        assertEquals(MasterCat.GameState.LEVEL_TRANSITION,g.state());
        input.onKeyReleased(KeyCode.F2);input.onKeyPressed(KeyCode.F2);
        assertEquals(MasterCat.GameState.LEVEL_TRANSITION,g.state());
    }

    @Test void liftCrushAndBeltMovementAreHandled() {
        var lift=new Body("moving",0,500,400,30,0,-160,2,0);
        var l=level(lift,body("solid",0,360,400,30));run(l,2);
        for(int n=0;n<100 && l.deaths==0;n++)run(l,1);
        assertEquals(1,l.deaths);
        var belt=new Body("belt",0,500,400,30,0,0,0,100);
        l=level(belt);run(l,2);double start=l.x;run(l,60);
        assertEquals(start+50,l.x,.001);
    }


    @Test void onlyOneAirJumpAndLandingRechargesIt() {
        var l=floor();run(l,2);l.jumpPressed();run(l,6);
        l.jumpReleased();l.jumpPressed();run(l,1);
        assertFalse(l.airJumpAvailable);assertTrue(l.vy<0);
        l.jumpReleased();run(l,2);double velocity=l.vy;
        l.jumpPressed();run(l,1);assertTrue(l.vy>velocity);
        l.jumpReleased();run(l,180);assertTrue(l.grounded);assertTrue(l.airJumpAvailable);
        l.die();run(l,28);assertTrue(l.airJumpAvailable);
    }
    @Test void sprintIsHeldAndClearedAcrossPauseAndFocusLoss() {
        var l=floor();l.right=true;l.sprint=true;run(l,50);
        assertEquals(SPRINT_SPEED,l.vx,.001);l.sprint=false;run(l,30);
        assertEquals(DEFAULT.speed(),l.vx,.001);
        var g=platformGame();var i=new GameInput(g);i.onKeyPressed(KeyCode.SHIFT);
        assertTrue(g.platformer.sprint);i.onKeyReleased(KeyCode.SHIFT);assertFalse(g.platformer.sprint);
        i.onKeyPressed(KeyCode.SHIFT);i.onFocusLost();assertFalse(g.platformer.sprint);
    }
}
