package com.master.bro.catdinogame;
import static org.junit.jupiter.api.Assertions.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Test;
class EnergyCombatTest {
    MasterCat game() {return LifecycleRegressionTest.game();}
    @Test void primaryMouseReplacesCAndRespectsChargeAndPause() {
        MasterCat g=game();g.startGame();g.energy=50;GameInput in=new GameInput(g);
        in.onKeyPressed(KeyCode.C);assertTrue(g.dragonBallSpheres.isEmpty());
        in.onMouseAttack(MouseButton.PRIMARY);assertEquals(1,g.dragonBallSpheres.size());assertEquals(0,g.energy);
        in.onMouseAttack(MouseButton.PRIMARY);assertEquals(1,g.dragonBallSpheres.size());
        g.energy=50;g.pauseGame();in.onMouseAttack(MouseButton.PRIMARY);
        assertEquals(1,g.dragonBallSpheres.size());assertEquals(50,g.energy);
        g.resetToMenu();in.onMouseAttack(MouseButton.PRIMARY);assertEquals(50,g.energy);
    }
    @Test void specialChargesOnlyDuringActiveBossFightAndResets() {
        MasterCat g=game();g.startGame();g.updater.update(.05);assertEquals(0,g.specialCharge);
        g.startBossFight();g.specialCharge=29.98;g.updater.update(.02);
        assertEquals(30,g.specialCharge,1e-8);
        g.pauseGame();g.updater.update(10);assertEquals(30,g.specialCharge);
        g.startBossFight();assertEquals(0,g.specialCharge);assertNull(g.specialParcel);
    }
    @Test void specialCostsItsChargeAndDealsHalfMaximumHealthOnceOnHit() {
        MasterCat g=game();g.startBossFight();GameInput in=new GameInput(g);
        in.onMouseAttack(MouseButton.SECONDARY);assertNull(g.specialParcel);
        g.specialCharge=30;in.onMouseAttack(MouseButton.SECONDARY);
        assertNotNull(g.specialParcel);assertEquals(0,g.specialCharge);
        g.specialParcel.x=g.bossX;g.specialParcel.y=g.bossY-100;
        g.updater.update(0);assertEquals(1000,g.bossHealth);
        g.updater.update(0);assertEquals(1000,g.bossHealth);
        g.specialCharge=30;var active=g.specialParcel;in.onMouseAttack(MouseButton.SECONDARY);
        assertSame(active,g.specialParcel);assertEquals(30,g.specialCharge);
    }
    @Test void barrierInterceptionDoesNotDamageBossAndExplosionExpires() {
        MasterCat g=game();g.startBossFight();g.specialCharge=30;
        new GameInput(g).onMouseAttack(MouseButton.SECONDARY);
        g.specialParcel.x=600;g.specialParcel.y=450;
        g.bossBarrier.add(new BarrierBlock(590,440,30,30));
        g.updater.update(0);assertTrue(g.specialParcel.isExploding());assertEquals(2000,g.bossHealth);
        for(int i=0;i<45;i++)g.updater.update(1.0/60);
        assertNull(g.specialParcel);
    }
    @Test void ballisticSpecialReachesBossHeightAtItsTarget() {
        var p=new ExplosiveParcel(400,448,1000,398);
        double t=600.0/520;
        for(int i=0;i<120;i++)p.update(t/120);
        assertEquals(1000,p.x,1e-7);assertEquals(398,p.y,1e-7);
    }
    @Test void specialCanWinButCannotFireOutsideBoss() {
        MasterCat g=game();g.startGame();g.specialCharge=30;GameInput in=new GameInput(g);
        in.onMouseAttack(MouseButton.SECONDARY);assertNull(g.specialParcel);
        g.startBossFight();g.bossHealth=800;g.specialCharge=30;
        in.onMouseAttack(MouseButton.SECONDARY);g.specialParcel.x=g.bossX;g.specialParcel.y=g.bossY-100;
        g.updater.update(0);assertEquals(MasterCat.GameState.VICTORY,g.state());assertEquals(0,g.bossHealth);
    }
}
