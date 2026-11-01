package com.master.bro.catdinogame;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
class LifecycleRegressionTest {
 static MasterCat game() {
  MasterCat g=new MasterCat(); g.WIDTH=1200;g.HEIGHT=600;g.GROUND_HEIGHT=498;
  g.scaleX=g.scaleY=g.uniformScale=1; return g;
 }
 @Test void explosionStaysUntilItsHalfSecondLifetimeEnds() {
  MasterCat g=game();g.startGame();var s=g.new DragonBallSphere(500,450);
  g.dragonBallSpheres.add(s);s.destroy();
  for(int i=0;i<15;i++)g.updater.update(1.0/60);assertTrue(g.dragonBallSpheres.contains(s));
  s.destroy();for(int i=0;i<14;i++)g.updater.update(1.0/60);assertTrue(g.dragonBallSpheres.contains(s));
  g.updater.update(.02);assertTrue(g.dragonBallSpheres.isEmpty());
 }
 @Test void explodingSphereCannotDestroyAnotherBarrierBlock() {
  MasterCat g=game();g.startBossFight();var s=g.new DragonBallSphere(600,450);
  g.dragonBallSpheres.add(s);g.bossBarrier.add(new BarrierBlock(590,440,30,30));
  g.bossBarrier.add(new BarrierBlock(590,440,30,30));
  g.updater.update(0);g.updater.update(0);assertEquals(1,g.bossBarrier.size());
 }
 @Test void autoRepeatCannotDoubleJumpOrTogglePauseAgain() {
  MasterCat g=game();g.startGame();GameInput in=new GameInput(g);
  in.onKeyPressed(javafx.scene.input.KeyCode.SPACE);
  in.onKeyPressed(javafx.scene.input.KeyCode.SPACE);assertEquals(-15,g.velocityY);
  in.onKeyReleased(javafx.scene.input.KeyCode.SPACE);
  in.onKeyPressed(javafx.scene.input.KeyCode.SPACE);assertEquals(-12,g.velocityY);
  in.onKeyPressed(javafx.scene.input.KeyCode.P);
  in.onKeyPressed(javafx.scene.input.KeyCode.P);assertEquals(MasterCat.GameState.PAUSED,g.state());
  in.onKeyReleased(javafx.scene.input.KeyCode.P);
  in.onKeyPressed(javafx.scene.input.KeyCode.P);assertEquals(MasterCat.GameState.PLAYING,g.state());
 }
 @Test void movementAliasesAndFocusLossTrackPhysicalKeys() {
  MasterCat g=game();g.startBossFight();GameInput in=new GameInput(g);
  in.onKeyPressed(javafx.scene.input.KeyCode.A);in.onKeyPressed(javafx.scene.input.KeyCode.LEFT);
  in.onKeyReleased(javafx.scene.input.KeyCode.A);assertTrue(g.leftPressed);
  in.onFocusLost();assertFalse(g.leftPressed);
  in.onKeyPressed(javafx.scene.input.KeyCode.P); // deliberate new press resumes
  in.onKeyReleased(javafx.scene.input.KeyCode.P);
  in.onKeyPressed(javafx.scene.input.KeyCode.A);assertTrue(g.leftPressed);
 }
 @Test void ballisticGravityMatchesSixtyHzAtEveryRefreshRate() throws Exception {
  for(int hz:new int[]{30,60,120,144}) {
   MasterCat g=game();var p=g.new BossProjectile(500,100,400,100,0);
   set(p,"trackingTimer",2.0);set(p,"velocityX",0.0);set(p,"velocityY",100.0);
   for(int n=0;n<hz;n++)p.update(1.0/hz);
   assertEquals(212.81,p.y,1e-8);
  }
 }
 private static void set(Object o,String name,double value) throws Exception {
  var f=o.getClass().getDeclaredField(name);f.setAccessible(true);f.setDouble(o,value);
 }
 @Test void stallsAndInvalidDeltasCannotAdvanceOnlySomeSubsystems() {
  MasterCat a=game(),b=game();a.startGame();b.startGame();
  a.dragonBallSpheres.add(a.new DragonBallSphere(500,300));
  b.dragonBallSpheres.add(b.new DragonBallSphere(500,300));
  a.updater.update(10);b.updater.update(.05);
  assertEquals(b.score,a.score);assertEquals(b.energy,a.energy,1e-9);
  assertEquals(b.conveyorTravel,a.conveyorTravel,1e-9);
  assertEquals(b.dragonBallSpheres.get(0).x,a.dragonBallSpheres.get(0).x,1e-9);
  double x=a.dragonBallSpheres.get(0).x;
  for(double dt:new double[]{-1,Double.NaN,Double.POSITIVE_INFINITY})a.updater.update(dt);
  assertEquals(x,a.dragonBallSpheres.get(0).x);
 }
 @Test void terminalStatesCannotBeOverwrittenByLateCallbacks() {
  MasterCat g=game();g.startBossFight();g.victory();g.gameOver();
  assertEquals(MasterCat.GameState.VICTORY,g.state());
  g.resetToMenu();g.gameOver();g.victory();assertEquals(MasterCat.GameState.MENU,g.state());
 }
 @Test void heldMenuKeyDoesNotLeakIntoGameplayAndMusicRepeatIsIgnored() {
  MasterCat g=game();GameInput in=new GameInput(g);
  in.onKeyPressed(javafx.scene.input.KeyCode.SPACE);
  in.onKeyPressed(javafx.scene.input.KeyCode.SPACE);assertTrue(g.onGround);
  in.onKeyPressed(javafx.scene.input.KeyCode.M);in.onKeyPressed(javafx.scene.input.KeyCode.M);
  assertFalse(g.audio.isMusicEnabled());
 }
 @Test void stallCapAlsoAppliesToBossAndShieldTimers() {
  MasterCat g=game();g.startBossFight();g.shieldActive=true;
  g.updater.update(20);assertEquals(.05,g.bossTimer,1e-9);assertEquals(.05,g.shieldTimer,1e-9);
 }
}
