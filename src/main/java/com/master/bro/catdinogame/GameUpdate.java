package com.master.bro.catdinogame;

import java.util.Iterator;
import com.master.bro.catdinogame.MasterCat.BossProjectile;
import com.master.bro.catdinogame.MasterCat.DamageIndicator;
import com.master.bro.catdinogame.MasterCat.DragonBallSphere;

/** Advances gameplay physics, spawning, attacks, and collisions. */
final class GameUpdate {
    private final MasterCat game;
    private final ProgressionClock progressionClock = new ProgressionClock();
    GameUpdate(MasterCat game) { this.game = game; }

    void resetProgression() {
        progressionClock.reset();
    }

    void update(double dt) {
        if (game.currentState == MasterCat.GameState.PLAYING || game.currentState == MasterCat.GameState.BOSS_FIGHT) {
            game.landingTimer = Math.max(0, game.landingTimer - dt);
        }
        if (game.currentState == MasterCat.GameState.PLAYING) {
            updatePlaying(dt);
        } else if (game.currentState == MasterCat.GameState.BOSS_FIGHT) {
            updateBossFight(dt);
        }
    }
    
    private void updatePlaying(double dt) {
        // Aggiorna effetti visivi - accumulo punti più lento
        for (int step = progressionClock.steps(dt); step > 0; step--) {
            game.score += (int)(game.gameSpeed * 3);
            game.gameSpeed += 0.0002;
        }
        
        // Controlla se il giocatore ha raggiunto i 10k punti per iniziare il boss fight
        if (game.score >= game.WINNING_SCORE) {
            game.startBossFight();
            return;
        }
        
        // Ricarica energia gradualmente (leggermente più veloce)
        if (game.energy < game.MAX_ENERGY) {
            game.energy += 8.5 * dt; // Ricarica in ~6 secondi (50/8.5) - leggermente più veloce
            if (game.energy > game.MAX_ENERGY) game.energy = game.MAX_ENERGY;
        }
        
        // === AGGIORNA SFERE DRAGON BALL NEL LIVELLO NORMALE ===
        updateDragonBallSpheres(dt);
        
        // Animazioni del gatto
        game.bodyBounce += dt * 8;
        
        // Fisica del gatto
        updateCatPhysics(dt);
        double beltSpeed = ObstaclePacing.speedPixelsPerSecond(game.score);
        game.conveyorTravel = (game.conveyorTravel + beltSpeed * Math.max(0, Math.min(dt, 0.1))) % 100000;
        if (game.onGround) game.runFramePhase = PlayerAnimation.advance(game.runFramePhase, dt, beltSpeed);

        // Genera ostacoli con difficoltà incrementale e migliore spaziatura - RALLENTATO
        generateObstacles();
        
        // Aggiorna ostacoli
        updateObstacles(dt);
    }
      private void updateBossFight(double dt) {
        // Timer della boss fight
        game.bossTimer += dt;
        
        // === CREA BARRIERA A METÀ TEMPO (1 MINUTO) ===
        if (game.bossTimer >= 60.0 && !game.barrierCreated) {
            createBossBarrier();
            game.barrierCreated = true;
            System.out.println("🧱 BARRIERA BOSS CREATA! Distruggila con le onde energetiche!");
        }
        
        // Controlla condizioni di fine boss fight
        if (game.bossHealth <= 0) {
            // Boss sconfitto = VITTORIA!
            System.out.println("🎯 Boss health reached 0 - calling victory()");
            game.victory();
            return;
        }
        if (game.bossTimer >= game.BOSS_DURATION) {
            // Tempo scaduto e boss ancora vivo = GAME OVER!
            System.out.println("⏰ Boss fight time expired - calling gameOver()");
            game.gameOver();
            return;
        }
        
        // === MOVIMENTO ORIZZONTALE DEL GATTO ===
        if (game.leftPressed && game.catX > 50) { // Limite sinistro
            game.catX -= game.catMoveSpeed * dt;
            game.facingLeft = true;
        }
        if (game.rightPressed && game.catX < game.WIDTH - 150) { // Limite destro (non troppo vicino al boss)
            game.catX += game.catMoveSpeed * dt;
            game.facingLeft = false;
        }
        
        // Ricarica energia più veloce durante il boss fight
        if (game.energy < game.MAX_ENERGY) {
            game.energy += 15.0 * dt; // Ricarica molto più veloce nel boss fight (~3.3 secondi invece di 7)
            if (game.energy > game.MAX_ENERGY) game.energy = game.MAX_ENERGY;
        }
        
        // === GESTIONE SCUDO ===
        if (game.shieldActive) {
            game.shieldTimer += dt;
            if (game.shieldTimer >= game.SHIELD_DURATION) {
                game.shieldActive = false;
                game.shieldTimer = 0;
                System.out.println("🛡️ Scudo disattivato");
            }
        }
        
        // === AGGIORNA INDICATORI DANNO ===
        Iterator<DamageIndicator> damageIt = game.damageIndicators.iterator();
        while (damageIt.hasNext()) {
            DamageIndicator indicator = damageIt.next();
            indicator.update(dt);
            if (indicator.isExpired()) {
                damageIt.remove();
            }
        }
        
        // === AGGIORNA BARRIERA BOSS ===
        updateBossBarrier(dt);
        
        // === AGGIORNA SFERE DRAGON BALL NEL BOSS FIGHT ===
        updateDragonBallSpheres(dt);
        if (game.currentState != MasterCat.GameState.BOSS_FIGHT) return;
        
        // Animazioni del gatto e del boss
        game.bodyBounce += dt * 8;
        game.bossAnimationTimer += dt * 3;
        
        // Fisica del gatto
        updateCatPhysics(dt);
        if (game.onGround && (game.leftPressed || game.rightPressed)) {
            game.runFramePhase = PlayerAnimation.advance(game.runFramePhase, dt, game.catMoveSpeed);
        }

        // Genera attacchi del boss
        generateBossAttacks();
        
        // Aggiorna proiettili del boss
        updateBossProjectiles(dt);
    }
    
    private void updateCatPhysics(double dt) {
        boolean wasOnGround = game.onGround;
        // Exact extension of the original discrete 60 Hz rule to fractional frames:
        // n*v0 + gravity*n*(n+1)/2 is the sum of n original frame steps.
        double frames = Math.max(0.0, Math.min(dt, 0.05)) * 60.0;
        game.catY += game.velocityY * frames
            + game.GRAVITY * frames * (frames + 1.0) / 2.0;
        game.velocityY += game.GRAVITY * frames;
        if (game.catY >= game.GROUND_HEIGHT) {
            if (!wasOnGround) game.landingTimer = 0.16;
            game.catY = game.GROUND_HEIGHT;
            game.velocityY = 0;
            game.onGround = true;
            game.canDoubleJump = false; // Reset doppio salto quando tocca terra
        }
    }
    
    private void generateObstacles() {
        long now = game.nowMillis();
        if (ObstaclePacing.ready(now, game.lastObstacleTime, game.score)) {
            double obstacleSize = 50; // Dimensione fissa appropriata
            double obstacleSpeed = ObstaclePacing.speedPixelsPerSecond(game.score);
            
            // Probabilità di ostacolo doppio aumenta con il punteggio - RITARDATA
            boolean isDoubleObstacle = (game.score > 2000) && (Math.random() < 0.2); // RIDOTTO: parte dopo 2000 punti e solo 20% di probabilità
            
            if (isDoubleObstacle) {
                // Crea due ostacoli ravvicinati per richiedere doppio salto
                Obstacle obstacle1 = new Obstacle(game.WIDTH, game.GROUND_HEIGHT - obstacleSize, obstacleSize, obstacleSize, obstacleSpeed, false);
                obstacle1.setBlockImages(game.assets.blockNormalImage, game.assets.blockDoubleImage);
                game.obstacles.add(obstacle1);
                
                Obstacle obstacle2 = new Obstacle(game.WIDTH + 150, game.GROUND_HEIGHT - obstacleSize, obstacleSize, obstacleSize, obstacleSpeed, true); // AUMENTATO: distanza da 120 a 150
                obstacle2.setBlockImages(game.assets.blockNormalImage, game.assets.blockDoubleImage);
                game.obstacles.add(obstacle2);
            } else {
                Obstacle obstacle = new Obstacle(game.WIDTH, game.GROUND_HEIGHT - obstacleSize, obstacleSize, obstacleSize, obstacleSpeed, false);
                obstacle.setBlockImages(game.assets.blockNormalImage, game.assets.blockDoubleImage);
                game.obstacles.add(obstacle);
            }
            game.lastObstacleTime = now;
        }
    }
    
    private void updateObstacles(double dt) {
        // Aggiorna e collisioni
        Iterator<Obstacle> it = game.obstacles.iterator();
        while (it.hasNext()) {
            Obstacle o = it.next();
            o.update(dt);
            
            // === CONTROLLO COLLISIONE CON SFERE DRAGON BALL ===
            // Le sfere Dragon Ball possono distruggere TUTTI i tipi di pacchi:
            // - Pacchi normali (marroni)
            // - Pacchi doppi/viola (che normalmente richiederebbero doppio salto)
            // Questo rende le sfere un'arma potente contro qualsiasi ostacolo!
            boolean destroyedBySphere = false;
            for (DragonBallSphere sphere : game.dragonBallSpheres) {
                if (sphere.intersects(o.x, o.y, o.width, o.height)) {
                    destroyedBySphere = true;
                    sphere.destroy(); // Distrugge anche la sfera (effetto esplosione)
                    break;
                }
            }
            
            if (destroyedBySphere) {
                it.remove();
                game.score += 50; // Bonus per distruzione
                continue;
            }
            
            PlayerHitbox.Bounds cat = PlayerHitbox.normal(game);
            if (o.intersects(cat.x(), cat.y(), cat.width(), cat.height())) {
                game.gameOver(); // Game over
            }
            if (o.isOffScreen()) {
                it.remove();
            }
        }
    }
    
    private static final double BOSS_WARNING_SECONDS = 0.8;

    void generateBossAttacks() {
        double attackInterval = Math.max(1.0, 3.0 - game.bossTimer * 0.015);
        if (game.pendingBossAttackType < 0) {
            if (game.bossTimer - game.lastBossAttackAt >= attackInterval - BOSS_WARNING_SECONDS) {
                game.pendingBossAttackType = chooseBossAttackType();
                game.bossWarningEndsAt = game.bossTimer + BOSS_WARNING_SECONDS;
            }
            return;
        }
        if (game.bossTimer < game.bossWarningEndsAt) return;

        spawnBossAttack(game.pendingBossAttackType);
        game.pendingBossAttackType = -1;
        game.lastBossAttackAt = game.bossTimer;
    }

    private int chooseBossAttackType() {
        if (game.bossTimer < 30) return 0;
        double roll = Math.random();
        if (game.bossTimer < 60) return roll < 0.7 ? 0 : 1;
        if (game.bossTimer < 90) return roll < 0.4 ? 0 : roll < 0.8 ? 1 : 2;
        return roll < 0.25 ? 0 : roll < 0.5 ? 1 : roll < 0.75 ? 2 : 3;
    }

    private void spawnBossAttack(int type) {
        double launchX = game.bossX - 75;
        double launchY = game.bossY - 80;
        PlayerHitbox.Bounds cat = PlayerHitbox.boss(game);
        double targetX = cat.centerX();
        double targetY = cat.centerY();
        switch (type) {
            case 0 -> game.bossProjectiles.add(game.new BossProjectile(launchX, launchY, targetX, targetY, 0));
            case 1 -> {
                game.bossProjectiles.add(game.new BossProjectile(launchX, launchY, targetX - 15, targetY, 1));
                game.bossProjectiles.add(game.new BossProjectile(launchX, launchY, targetX + 15, targetY, 1));
            }
            case 2 -> {
                game.bossProjectiles.add(game.new BossProjectile(launchX, launchY, targetX - 20, targetY, 2));
                game.bossProjectiles.add(game.new BossProjectile(launchX, launchY, targetX, targetY, 2));
                game.bossProjectiles.add(game.new BossProjectile(launchX, launchY, targetX + 20, targetY, 2));
            }
            case 3 -> {
                game.bossProjectiles.add(game.new BossProjectile(launchX, launchY, targetX - 12, targetY, 3));
                game.bossProjectiles.add(game.new BossProjectile(launchX, launchY, targetX + 12, targetY, 3));
            }
            default -> throw new IllegalArgumentException("Unknown boss attack: " + type);
        }
    }

    private void updateBossProjectiles(double dt) {
        Iterator<BossProjectile> it = game.bossProjectiles.iterator();
        while (it.hasNext()) {
            BossProjectile projectile = it.next();
            projectile.update(dt);

            // A charged sphere can clear every incoming attack, including all
            // boss projectile variants. Consume the sphere on the first hit.
            boolean hitBySphere = false;
            for (DragonBallSphere sphere : game.dragonBallSpheres) {
                if (projectile.intersects(sphere)) {
                    sphere.destroy();
                    hitBySphere = true;
                    break;
                }
            }
            if (hitBySphere) {
                projectile.destroy();
                game.score += 25;
                it.remove();
                continue;
            }
            
            // Controlla collisione con il gatto (solo se lo scudo non è attivo)
            PlayerHitbox.Bounds cat = PlayerHitbox.boss(game);
            if (!game.shieldActive && projectile.intersects(cat.x(), cat.y(), cat.width(), cat.height())) {
                game.gameOver();
                return;
            }
            
            // Controlla se l'onda distrugge il proiettile
            if (game.waveActive && !projectile.isDestroyed()) {
                double projX = projectile.x;
                if (projX > game.catX && projX < game.catX + 300) {
                    projectile.destroy();
                    game.score += 25; // Bonus per distruzione proiettile boss
                }
            }
            
            // Rimuovi proiettili fuori dallo schermo o distrutti
            if (projectile.isOffScreen() || projectile.isDestroyed()) {
                it.remove();
            }
        }
    }
    
    // === METODI GESTIONE BARRIERA BOSS ===
    
    private void createBossBarrier() {
        // Crea barriera di 3 colonne x 10 righe davanti al boss
        double blockSize = 30; // Blocchi piccoli
        double barrierStartX = game.bossX - 200; // Posizione davanti al boss
        double barrierStartY = game.GROUND_HEIGHT - (10 * blockSize); // Inizia dal basso
        
        for (int col = 0; col < 3; col++) {
            for (int row = 0; row < 10; row++) {
                double blockX = barrierStartX + (col * blockSize);
                double blockY = barrierStartY + (row * blockSize);
                game.bossBarrier.add(new BarrierBlock(blockX, blockY, blockSize, blockSize));
            }
        }
        
        System.out.println("🧱 Barriera boss creata: " + game.bossBarrier.size() + " blocchi!");
    }
    
    private void updateBossBarrier(double dt) {
        // Controlla collisioni delle sfere Dragon Ball con la barriera
        Iterator<DragonBallSphere> sphereIt = game.dragonBallSpheres.iterator();
        while (sphereIt.hasNext()) {
            DragonBallSphere sphere = sphereIt.next();
            if (sphere.isExploding() || sphere.isDestroyed()) continue;
            boolean sphereHitBarrier = false;
            
            // Controlla collisione con ogni blocco della barriera
            Iterator<BarrierBlock> barrierIt = game.bossBarrier.iterator();
            while (barrierIt.hasNext() && !sphereHitBarrier) {
                BarrierBlock block = barrierIt.next();
                
                // Collisione semplice cerchio-rettangolo
                double sphereCenterX = sphere.x;
                double sphereCenterY = sphere.y;
                double sphereRadius = sphere.size / 2; // Raggio della sfera Dragon Ball
                
                // Trova il punto più vicino del rettangolo al centro del cerchio
                double closestX = Math.max(block.x, Math.min(sphereCenterX, block.x + block.width));
                double closestY = Math.max(block.y, Math.min(sphereCenterY, block.y + block.height));
                
                // Calcola la distanza
                double distanceX = sphereCenterX - closestX;
                double distanceY = sphereCenterY - closestY;
                double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY);
                
                if (distance <= sphereRadius) {
                    // Collisione! Distruggi il blocco
                    block.destroy();
                    barrierIt.remove();
                    sphere.destroy(); // Distruggi anche la sfera
                    sphereHitBarrier = true;
                    game.score += 10; // Bonus per distruzione blocco barriera
                    game.damageIndicators.add(game.new DamageIndicator(block.x + block.width/2, block.y, "+10"));
                    System.out.println("💥 Blocco barriera distrutto da sfera Dragon Ball! Rimangono: " + game.bossBarrier.size());
                }
            }
        }
        
        // Rimuovi blocchi distrutti dalla lista (sicurezza aggiuntiva)
        game.bossBarrier.removeIf(BarrierBlock::isDestroyed);
    }


    private void updateDragonBallSpheres(double dt) {
        Iterator<DragonBallSphere> it = game.dragonBallSpheres.iterator();
        while (it.hasNext()) {
            DragonBallSphere sphere = it.next();
            sphere.update(dt);
            
            // === COLLISIONE CON IL BOSS (solo durante boss fight) ===
            if (game.currentState == MasterCat.GameState.BOSS_FIGHT) {
                // Boss dimensions: width=150, height=200 (con immagine)
                double bossWidth = 150; 
                double bossHeight = 200; 
                double bossCenterY = game.bossY - bossHeight/2;
                
                if (sphere.intersects(game.bossX - bossWidth/2, bossCenterY - bossHeight/2, bossWidth, bossHeight)) {
                    // Danneggia il boss
                    double damage = 100; // Danno delle sfere Dragon Ball
                    game.bossHealth -= damage;
                    
                    // Indicatore danno
                    game.damageIndicators.add(game.new DamageIndicator(game.bossX, game.bossY - 100, "-" + (int)damage));
                    
                    // Distruggi la sfera con esplosione
                    sphere.destroy();
                    
                    System.out.println("💥 Sfera Dragon Ball colpisce il boss! Danno: " + (int)damage + 
                                     " - Vita boss: " + (int)game.bossHealth + "/" + (int)game.MAX_BOSS_HEALTH);
                    
                    // Controlla se il boss è stato sconfitto
                    if (game.bossHealth <= 0) {
                        game.victory();
                        return;
                    }
                }
            }
            
            // Rimuovi sfere fuori schermo o distrutte
            if (sphere.isOffScreen() || sphere.isDestroyed()) {
                it.remove();
            }
        }
    }
    
}
