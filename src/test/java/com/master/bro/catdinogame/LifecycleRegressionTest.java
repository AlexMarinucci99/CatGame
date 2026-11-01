package com.master.bro.catdinogame;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
class LifecycleRegressionTest {
 static MasterCat game() {
  MasterCat g=new MasterCat(); g.WIDTH=1200;g.HEIGHT=600;g.GROUND_HEIGHT=498;
  g.scaleX=g.scaleY=g.uniformScale=1;g.clock=()->10000; return g;
 }
 @Test void explosionStaysUntilItsHalfSecondLifetimeEnds() {
  MasterCat g=game();g.startGame();var s=g.new DragonBallSphere(500,450);
  g.dragonBallSpheres.add(s);s.destroy();
  g.updater.update(.25);assertTrue(g.dragonBallSpheres.contains(s));
  s.destroy();g.updater.update(.24);assertTrue(g.dragonBallSpheres.contains(s));
  g.updater.update(.02);assertTrue(g.dragonBallSpheres.isEmpty());
 }
 @Test void explodingSphereCannotDestroyAnotherBarrierBlock() {
  MasterCat g=game();g.startBossFight();var s=g.new DragonBallSphere(600,450);
  g.dragonBallSpheres.add(s);g.bossBarrier.add(new BarrierBlock(590,440,30,30));
  g.bossBarrier.add(new BarrierBlock(590,440,30,30));
  g.updater.update(0);g.updater.update(0);assertEquals(1,g.bossBarrier.size());
 }
}
