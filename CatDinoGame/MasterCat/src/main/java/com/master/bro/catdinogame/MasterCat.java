package com.master.bro.catdinogame;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.paint.*;
import javafx.scene.effect.BlendMode;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;
import javafx.animation.AnimationTimer;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Iterator;

public class MasterCat extends Application {
    // Dimensioni base del gioco (design di riferimento)
    private static final int BASE_WIDTH = 1200;
    private static final int BASE_HEIGHT = 600;
    
    // Dimensioni effettive dello schermo
    private double screenWidth;
    private double screenHeight;
    private double scaleX;
    private double scaleY;
    private double uniformScale;
    
    // Dimensioni adattabili del gioco
    private double WIDTH;
    private double HEIGHT;
    
    private static final double GRAVITY = 0.6;
    private static final double JUMP_VELOCITY = -15;
    private double GROUND_HEIGHT; // Ora variabile

    // Game States
    private enum GameState { MENU, PLAYING, BOSS_FIGHT, GAME_OVER, VICTORY }
    private GameState currentState = GameState.MENU;
    
    // Punteggio finale per vincere
    private static final int WINNING_SCORE = 10000;
    
    // Riferimento al Stage per controllo fullscreen
    private Stage gameStage;

    private double catX = 150;
    private double catY = GROUND_HEIGHT;
    private double velocityY = 0;
    private boolean onGround = true;
    private boolean canDoubleJump = false; // Doppio salto disponibile

    // Variabili per effetti grafici
    private int score = 0;
    private double gameSpeed = 1.0;
    
    // Variabili per animazione gatto (solo bodyBounce utilizzato)
    private double bodyBounce = 0;
    
    // Sistema energia e onda
    private double energy = 0;
    private final double MAX_ENERGY = 50;
    private boolean waveActive = false;
    private double waveTimer = 0;
    private final double WAVE_DURATION = 3.0;
    
    // === SISTEMA SFERE ENERGETICHE LIVELLO NORMALE (DRAGON BALL STYLE) ===
    private final ArrayList<DragonBallSphere> dragonBallSpheres = new ArrayList<>();
    
    // === SISTEMA AUDIO CON JAVAFX MEDIA ===
    private boolean isMusicEnabled = true;
    private MediaPlayer menuMusicPlayer;
    private MediaPlayer gameMusicPlayer;
    private MediaPlayer bossMusicPlayer;
    private MediaPlayer victoryMusicPlayer;
    private MediaPlayer currentMusicPlayer;
    
    // === SISTEMA MENU A TENDINA IMPOSTAZIONI ===
    private boolean settingsMenuOpen = false;
    private double settingsButtonX; // Calcolato dinamicamente in basso a destra
    private double settingsButtonY; // Calcolato dinamicamente in basso a destra
    private double settingsButtonWidth = 160;
    private double settingsButtonHeight = 40;
    
    // === SISTEMA IMMAGINI ===
    private Image catImage;
    private Image menuBackgroundImage;
    private Image gameBackgroundImage;
    private Image victoryBackgroundImage;
    private Image gameOverBackgroundImage;
    private Image bossBackgroundImage; // Sfondo per la battaglia contro il boss
    private Image bossImage; // Immagine del corriere Amazon boss
    private Image bossProjectileNormalImage; // Immagine pacchi singoli del boss
    private Image bossProjectileDoubleImage; // Immagine pacchi multipli del boss
    private Image blockNormalImage;
    private Image blockDoubleImage;
    
    // === VARIABILI BOSS FINALE ===
    private double bossTimer = 0;
    private final double BOSS_DURATION = 120.0; // 2 minuti di boss fight
    private double bossX;
    private double bossY;
    private double bossAnimationTimer = 0;
    private long lastBossAttackTime = 0;
    private final ArrayList<BossProjectile> bossProjectiles = new ArrayList<>();
    
    // === NUOVE VARIABILI PER BOSS FIGHT MIGLIORATO ===
    private double bossHealth = 2000.0;
    private final double MAX_BOSS_HEALTH = 2000.0;
    private double catMoveSpeed = 300.0; // Velocità movimento orizzontale nel boss fight
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    
    // === SISTEMA SCUDO ===
    private boolean shieldActive = false;
    private double shieldTimer = 0;
    private final double SHIELD_DURATION = 5.0; // 5 secondi di scudo
    private final double SHIELD_ENERGY_COST = 25.0; // Costa metà energia (25/50)
    
    // === INDICATORI DANNO ===
    private final ArrayList<DamageIndicator> damageIndicators = new ArrayList<>();
    
    // === SISTEMA BARRIERA BOSS ===
    private final ArrayList<BarrierBlock> bossBarrier = new ArrayList<>();
    private boolean barrierCreated = false;

    private final ArrayList<Obstacle> obstacles = new ArrayList<>();
    private long lastObstacleTime = 0;

    @Override
    public void start(Stage primaryStage) {
        this.gameStage = primaryStage; // Memorizza il riferimento al Stage
        
        // === CALCOLO DIMENSIONI SCHERMO E SCALING ===
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        screenWidth = screenBounds.getWidth();
        screenHeight = screenBounds.getHeight();
        
        // Calcola il fattore di scala mantenendo le proporzioni
        scaleX = screenWidth / BASE_WIDTH;
        scaleY = screenHeight / BASE_HEIGHT;
        uniformScale = Math.min(scaleX, scaleY); // Usa il fattore più piccolo per mantenere proporzioni
        
        // Imposta le nuove dimensioni adattate
        WIDTH = screenWidth;
        HEIGHT = screenHeight;
        GROUND_HEIGHT = HEIGHT * 0.83; // Mantiene la proporzione del terreno
        
        System.out.println("🖥️ Risoluzione schermo: " + (int)screenWidth + "x" + (int)screenHeight);
        System.out.println("🎯 Fattore di scala: " + String.format("%.2f", uniformScale));
        
        primaryStage.setTitle("MasterCat - Avventura Spaziale");
        
        // === CONFIGURAZIONE FULLSCREEN ===
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("Premi 'ESC' o 'U' per uscire dal fullscreen");
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        
        Pane root = new Pane();
        Canvas canvas = new Canvas(WIDTH, HEIGHT); // Usa le dimensioni dello schermo
        root.getChildren().add(canvas);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        // Carica le risorse grafiche
        loadResources();
        
        // Inizializza sistema audio con JavaFX Media
        initializeAudio();
        
        // Gestisci la chiusura dell'applicazione
        primaryStage.setOnCloseRequest(e -> {
            stopAllMusic();
        });

        // Controlli basati sullo stato del gioco
        scene.setOnKeyPressed(e -> {
            // Controllo fullscreen globale (funziona in tutti gli stati)
            if (e.getCode() == KeyCode.U || e.getCode() == KeyCode.ESCAPE) {
                toggleFullscreen();
                return;
            }
            
            // Controllo musica globale (funziona in tutti gli stati)
            if (e.getCode() == KeyCode.M) {
                toggleMusic();
                return;
            }
            
            switch (currentState) {
                case MENU:
                    if (e.getCode() == KeyCode.SPACE) {
                        startGame();
                    }
                    // Toggle menu impostazioni solo con tasto I
                    else if (e.getCode() == KeyCode.I) {
                        settingsMenuOpen = !settingsMenuOpen;
                    }
                    // Tasto segreto T per saltare direttamente al boss fight (per test)
                    else if (e.getCode() == KeyCode.T) {
                        score = WINNING_SCORE; // Imposta il punteggio necessario
                        startBossFight(); // Vai direttamente al boss fight
                    }
                    break;
                case PLAYING:
                    if (e.getCode() == KeyCode.SPACE) {
                        // Salto normale se a terra
                        if (onGround) {
                            velocityY = JUMP_VELOCITY;
                            onGround = false;
                            canDoubleJump = true; // Abilita doppio salto
                        }
                        // Doppio salto se in aria e disponibile
                        else if (canDoubleJump) {
                            velocityY = JUMP_VELOCITY * 0.8; // Doppio salto leggermente più debole
                            canDoubleJump = false; // Disabilita ulteriori salti
                        }
                    }
                    // === LANCIO SFERA DRAGON BALL NEL LIVELLO NORMALE ===
                    if (e.getCode() == KeyCode.C && energy >= MAX_ENERGY) {
                        // Lancia una nuova sfera energetica Dragon Ball
                        double sphereStartX = catX + 60; // Parte dalla mano del gatto
                        double sphereStartY = catY - 40; // Centro verticale del gatto
                        dragonBallSpheres.add(new DragonBallSphere(sphereStartX, sphereStartY));
                        energy = 0; // Reset energia
                        waveActive = false; // Non usiamo più waveActive nel normale
                    }
                    break;
                case BOSS_FIGHT:
                    if (e.getCode() == KeyCode.SPACE) {
                        // Salto normale se a terra
                        if (onGround) {
                            velocityY = JUMP_VELOCITY;
                            onGround = false;
                            canDoubleJump = true; // Abilita doppio salto
                        }
                        // Doppio salto se in aria e disponibile
                        else if (canDoubleJump) {
                            velocityY = JUMP_VELOCITY * 0.8; // Doppio salto leggermente più debole
                            canDoubleJump = false; // Disabilita ulteriori salti
                        }
                    }
                    // === LANCIO SFERA DRAGON BALL NEL BOSS FIGHT ===
                    if (e.getCode() == KeyCode.C && energy >= MAX_ENERGY) {
                        // Lancia una nuova sfera energetica Dragon Ball
                        double sphereStartX = catX + 60; // Parte dalla mano del gatto
                        double sphereStartY = catY - 40; // Centro verticale del gatto
                        dragonBallSpheres.add(new DragonBallSphere(sphereStartX, sphereStartY));
                        energy = 0; // Reset energia
                        System.out.println("🌊 SFERA DRAGON BALL LANCIATA!");
                    }
                    // Scudo con X
                    if (e.getCode() == KeyCode.X && energy >= SHIELD_ENERGY_COST && !shieldActive) {
                        shieldActive = true;
                        shieldTimer = 0;
                        energy -= SHIELD_ENERGY_COST;
                        System.out.println("🛡️ SCUDO ATTIVATO!");
                    }
                    // === MOVIMENTO ORIZZONTALE ===
                    if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) {
                        leftPressed = true;
                    }
                    if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) {
                        rightPressed = true;
                    }
                    break;
                case GAME_OVER:
                    // Ritorna al menu con qualsiasi tasto
                    resetToMenu();
                    break;
                case VICTORY:
                    // Ritorna al menu con qualsiasi tasto
                    resetToMenu();
                    break;
            }
        });

        // Gestione rilascio tasti per movimento fluido
        scene.setOnKeyReleased(e -> {
            switch (currentState) {
                case BOSS_FIGHT:
                    if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) {
                        leftPressed = false;
                    }
                    if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) {
                        rightPressed = false;
                    }
                    break;
                case MENU:
                case PLAYING:
                case GAME_OVER:
                case VICTORY:
                    // Nessuna azione speciale per questi stati
                    break;
            }
        });

        // === GESTIONE CLICK MOUSE PER MENU A TENDINA ===
        scene.setOnMouseClicked(e -> {
            if (currentState == GameState.MENU) {
                double mouseX = e.getX();
                double mouseY = e.getY();
                
                // Check se click è sul pulsante Impostazioni
                if (mouseX >= settingsButtonX && mouseX <= settingsButtonX + settingsButtonWidth &&
                    mouseY >= settingsButtonY && mouseY <= settingsButtonY + settingsButtonHeight) {
                    settingsMenuOpen = !settingsMenuOpen; // Toggle menu
                }
                // Check se click è fuori dal menu per chiuderlo
                else if (settingsMenuOpen) {
                    double menuWidth = 450;
                    double menuHeight = 350;
                    
                    // Calcola posizione del menu usando la stessa logica di renderSettingsDropdown
                    double menuX = settingsButtonX - menuWidth + settingsButtonWidth; // Allinea a destra del pulsante
                    double menuY = settingsButtonY - menuHeight; // Apri sopra il pulsante
                    
                    // Assicurati che il menu non esca dai bordi dello schermo
                    if (menuX < 0) menuX = 10; // Non uscire a sinistra
                    if (menuX + menuWidth > WIDTH) menuX = WIDTH - menuWidth - 10; // Non uscire a destra
                    if (menuY < 0) menuY = 10; // Non uscire in alto
                    
                    if (mouseX < menuX || mouseX > menuX + menuWidth ||
                        mouseY < menuY || mouseY > menuY + menuHeight) {
                        settingsMenuOpen = false; // Chiudi menu se click fuori
                    }
                }
            }
        });

        new AnimationTimer() {
            private long lastTime = 0;
            @Override
            public void handle(long now) {
                if (lastTime == 0) lastTime = now;
                double dt = (now - lastTime) / 1e9;
                lastTime = now;

                update(dt);
                render(gc);
            }
        }.start();

        primaryStage.show();
        canvas.requestFocus();
    }

    private void update(double dt) {
        if (currentState == GameState.PLAYING) {
            updatePlaying(dt);
        } else if (currentState == GameState.BOSS_FIGHT) {
            updateBossFight(dt);
        }
    }
    
    private void updatePlaying(double dt) {
        // Aggiorna effetti visivi - accumulo punti più lento
        score += (int)(gameSpeed * 3); // RIDOTTO: era 10, ora 3 (70% più lento)
        gameSpeed += 0.0002; // RIDOTTO: era 0.0005, ora 0.0002 (accelerazione più graduale)
        
        // Controlla se il giocatore ha raggiunto i 10k punti per iniziare il boss fight
        if (score >= WINNING_SCORE) {
            startBossFight();
            return;
        }
        
        // Ricarica energia gradualmente (leggermente più veloce)
        if (energy < MAX_ENERGY) {
            energy += 8.5 * dt; // Ricarica in ~6 secondi (50/8.5) - leggermente più veloce
            if (energy > MAX_ENERGY) energy = MAX_ENERGY;
        }
        
        // === AGGIORNA SFERE DRAGON BALL NEL LIVELLO NORMALE ===
        updateDragonBallSpheres(dt);
        
        // Animazioni del gatto
        bodyBounce += dt * 8;
        
        // Fisica del gatto
        updateCatPhysics(dt);

        // Genera ostacoli con difficoltà incrementale e migliore spaziatura - RALLENTATO
        generateObstacles();
        
        // Aggiorna ostacoli
        updateObstacles(dt);
    }
      private void updateBossFight(double dt) {
        // Timer della boss fight
        bossTimer += dt;
        
        // === CREA BARRIERA A METÀ TEMPO (1 MINUTO) ===
        if (bossTimer >= 60.0 && !barrierCreated) {
            createBossBarrier();
            barrierCreated = true;
            System.out.println("🧱 BARRIERA BOSS CREATA! Distruggila con le onde energetiche!");
        }
        
        // Controlla condizioni di fine boss fight
        if (bossHealth <= 0) {
            // Boss sconfitto = VITTORIA!
            System.out.println("🎯 Boss health reached 0 - calling victory()");
            victory();
            return;
        }
        if (bossTimer >= BOSS_DURATION) {
            // Tempo scaduto e boss ancora vivo = GAME OVER!
            System.out.println("⏰ Boss fight time expired - calling gameOver()");
            gameOver();
            return;
        }
        
        // === MOVIMENTO ORIZZONTALE DEL GATTO ===
        if (leftPressed && catX > 50) { // Limite sinistro
            catX -= catMoveSpeed * dt;
        }
        if (rightPressed && catX < WIDTH - 150) { // Limite destro (non troppo vicino al boss)
            catX += catMoveSpeed * dt;
        }
        
        // Ricarica energia più veloce durante il boss fight
        if (energy < MAX_ENERGY) {
            energy += 15.0 * dt; // Ricarica molto più veloce nel boss fight (~3.3 secondi invece di 7)
            if (energy > MAX_ENERGY) energy = MAX_ENERGY;
        }
        
        // === GESTIONE SCUDO ===
        if (shieldActive) {
            shieldTimer += dt;
            if (shieldTimer >= SHIELD_DURATION) {
                shieldActive = false;
                shieldTimer = 0;
                System.out.println("🛡️ Scudo disattivato");
            }
        }
        
        // === AGGIORNA INDICATORI DANNO ===
        Iterator<DamageIndicator> damageIt = damageIndicators.iterator();
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
        
        // Animazioni del gatto e del boss
        bodyBounce += dt * 8;
        bossAnimationTimer += dt * 3;
        
        // Fisica del gatto
        updateCatPhysics(dt);

        // Genera attacchi del boss
        generateBossAttacks();
        
        // Aggiorna proiettili del boss
        updateBossProjectiles(dt);
    }
    
    private void updateCatPhysics(double dt) {
        velocityY += GRAVITY;
        catY += velocityY;
        if (catY >= GROUND_HEIGHT) {
            catY = GROUND_HEIGHT;
            velocityY = 0;
            onGround = true;
            canDoubleJump = false; // Reset doppio salto quando tocca terra
        }
    }
    
    private void generateObstacles() {
        double obstacleInterval = Math.max(1000, 3000 - (score / 300.0) * 30); // PIÙ DIFFICILE: Min 1s, inizio 3s, progressione più veloce
        if (System.currentTimeMillis() - lastObstacleTime > obstacleInterval) {
            double obstacleSize = 50; // Dimensione fissa appropriata
            double obstacleSpeed = 250 + (score / 300.0) * 150; // PIÙ VELOCE: Velocità base aumentata e incremento più aggressivo
            
            // Probabilità di ostacolo doppio aumenta con il punteggio - RITARDATA
            boolean isDoubleObstacle = (score > 2000) && (Math.random() < 0.2); // RIDOTTO: parte dopo 2000 punti e solo 20% di probabilità
            
            if (isDoubleObstacle) {
                // Crea due ostacoli ravvicinati per richiedere doppio salto
                Obstacle obstacle1 = new Obstacle(WIDTH, GROUND_HEIGHT - obstacleSize, obstacleSize, obstacleSize, obstacleSpeed, false);
                obstacle1.setBlockImages(blockNormalImage, blockDoubleImage);
                obstacles.add(obstacle1);
                
                Obstacle obstacle2 = new Obstacle(WIDTH + 150, GROUND_HEIGHT - obstacleSize, obstacleSize, obstacleSize, obstacleSpeed, true); // AUMENTATO: distanza da 120 a 150
                obstacle2.setBlockImages(blockNormalImage, blockDoubleImage);
                obstacles.add(obstacle2);
            } else {
                Obstacle obstacle = new Obstacle(WIDTH, GROUND_HEIGHT - obstacleSize, obstacleSize, obstacleSize, obstacleSpeed, false);
                obstacle.setBlockImages(blockNormalImage, blockDoubleImage);
                obstacles.add(obstacle);
            }
            lastObstacleTime = System.currentTimeMillis();
        }
    }
    
    private void updateObstacles(double dt) {
        // Aggiorna e collisioni
        Iterator<Obstacle> it = obstacles.iterator();
        while (it.hasNext()) {
            Obstacle o = it.next();
            o.update(dt);
            
            // === CONTROLLO COLLISIONE CON SFERE DRAGON BALL ===
            // Le sfere Dragon Ball possono distruggere TUTTI i tipi di pacchi:
            // - Pacchi normali (marroni)
            // - Pacchi doppi/viola (che normalmente richiederebbero doppio salto)
            // Questo rende le sfere un'arma potente contro qualsiasi ostacolo!
            boolean destroyedBySphere = false;
            for (DragonBallSphere sphere : dragonBallSpheres) {
                if (sphere.intersects(o.x, o.y, o.width, o.height)) {
                    destroyedBySphere = true;
                    sphere.destroy(); // Distrugge anche la sfera (effetto esplosione)
                    break;
                }
            }
            
            if (destroyedBySphere) {
                it.remove();
                score += 50; // Bonus per distruzione
                continue;
            }
            
            if (o.intersects(catX, catY - 50, 50, 50)) {
                gameOver(); // Game over
            }
            if (o.isOffScreen()) {
                it.remove();
            }
        }
    }
    
    private void generateBossAttacks() {
        long currentTime = System.currentTimeMillis();
        
        // Attacchi del boss ogni 1-3 secondi con difficoltà crescente
        double attackInterval = Math.max(1000, 3000 - (bossTimer * 15)); // Attacchi più frequenti col tempo
        
        if (currentTime - lastBossAttackTime > attackInterval) {
            // Tipo di attacco basato sul tempo trascorso
            int attackType;
            if (bossTimer < 30) {
                attackType = 0; // Solo pacchi singoli nei primi 30 secondi
            } else if (bossTimer < 60) {
                attackType = (Math.random() < 0.7) ? 0 : 1; // 70% singoli, 30% doppi
            } else if (bossTimer < 90) {
                double rand = Math.random();
                if (rand < 0.4) attackType = 0;      // 40% singoli
                else if (rand < 0.8) attackType = 1; // 40% doppi  
                else attackType = 2;                  // 20% tripli
            } else {
                // Ultimi 30 secondi: tutti i tipi possibili
                double rand = Math.random();
                if (rand < 0.25) attackType = 0;
                else if (rand < 0.5) attackType = 1;
                else if (rand < 0.75) attackType = 2;
                else attackType = 3; // Pacchi impilati
            }
            
            // Posizione di lancio del boss ingrandito
            double launchX = bossX - 75; // Punto di lancio davanti al boss ingrandito
            double launchY = bossY - 80; // Altezza delle mani del boss
            
            // Target PRECISO sul centro del gatto (ora con tracking dinamico)
            double targetX = catX + 25; // Centro esatto del gatto (larghezza 50/2 = 25)
            double targetY = catY - 25; // Centro verticale del gatto (altezza 50/2 = 25)
            
            // Crea proiettili in base al tipo di attacco (TUTTI CON TRACKING DINAMICO)
            switch (attackType) {
                case 0: // Singolo - mira diretta al centro del gatto
                    bossProjectiles.add(new BossProjectile(launchX, launchY, targetX, targetY, 0));
                    System.out.println("🎯 Boss attacco singolo -> Player pos: (" + (int)catX + ", " + (int)catY + ")");
                    break;
                    
                case 1: // Doppio - due proiettili con offset ridotto per maggiore precisione
                    bossProjectiles.add(new BossProjectile(launchX, launchY, targetX - 15, targetY, 1));
                    bossProjectiles.add(new BossProjectile(launchX, launchY, targetX + 15, targetY, 1));
                    System.out.println("🎯🎯 Boss attacco doppio -> Player pos: (" + (int)catX + ", " + (int)catY + ")");
                    break;
                    
                case 2: // Triplo - spread ridotto per coprire area più precisa
                    bossProjectiles.add(new BossProjectile(launchX, launchY, targetX - 20, targetY, 2));
                    bossProjectiles.add(new BossProjectile(launchX, launchY, targetX, targetY, 2));
                    bossProjectiles.add(new BossProjectile(launchX, launchY, targetX + 20, targetY, 2));
                    System.out.println("🎯🎯🎯 Boss attacco triplo -> Player pos: (" + (int)catX + ", " + (int)catY + ")");
                    break;
                    
                case 3: // Impilato - targeting più preciso per bloccare fuga
                    bossProjectiles.add(new BossProjectile(launchX, launchY, targetX - 12, targetY, 3));
                    bossProjectiles.add(new BossProjectile(launchX, launchY, targetX + 12, targetY, 3));
                    System.out.println("🎯⬆ Boss attacco impilato -> Player pos: (" + (int)catX + ", " + (int)catY + ")");
                    break;
            }
            
            lastBossAttackTime = currentTime;
        }
    }
    
    private void updateBossProjectiles(double dt) {
        Iterator<BossProjectile> it = bossProjectiles.iterator();
        while (it.hasNext()) {
            BossProjectile projectile = it.next();
            projectile.update(dt);
            
            // Controlla collisione con il gatto (solo se lo scudo non è attivo)
            if (!shieldActive && projectile.intersects(catX, catY - 50, 50, 50)) {
                gameOver();
                return;
            }
            
            // Controlla se l'onda distrugge il proiettile
            if (waveActive && !projectile.isDestroyed()) {
                double projX = projectile.x;
                if (projX > catX && projX < catX + 300) {
                    projectile.destroy();
                    score += 25; // Bonus per distruzione proiettile boss
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
        double barrierStartX = bossX - 200; // Posizione davanti al boss
        double barrierStartY = GROUND_HEIGHT - (10 * blockSize); // Inizia dal basso
        
        for (int col = 0; col < 3; col++) {
            for (int row = 0; row < 10; row++) {
                double blockX = barrierStartX + (col * blockSize);
                double blockY = barrierStartY + (row * blockSize);
                bossBarrier.add(new BarrierBlock(blockX, blockY, blockSize, blockSize));
            }
        }
        
        System.out.println("🧱 Barriera boss creata: " + bossBarrier.size() + " blocchi!");
    }
    
    private void updateBossBarrier(double dt) {
        // Controlla collisioni delle sfere Dragon Ball con la barriera
        Iterator<DragonBallSphere> sphereIt = dragonBallSpheres.iterator();
        while (sphereIt.hasNext()) {
            DragonBallSphere sphere = sphereIt.next();
            boolean sphereHitBarrier = false;
            
            // Controlla collisione con ogni blocco della barriera
            Iterator<BarrierBlock> barrierIt = bossBarrier.iterator();
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
                    score += 10; // Bonus per distruzione blocco barriera
                    damageIndicators.add(new DamageIndicator(block.x + block.width/2, block.y, "+10"));
                    System.out.println("💥 Blocco barriera distrutto da sfera Dragon Ball! Rimangono: " + bossBarrier.size());
                }
            }
        }
        
        // Rimuovi blocchi distrutti dalla lista (sicurezza aggiuntiva)
        bossBarrier.removeIf(BarrierBlock::isDestroyed);
    }

    private void render(GraphicsContext gc) {
        switch (currentState) {
            case MENU:
                renderMenu(gc);
                break;
            case PLAYING:
                renderGame(gc);
                break;
            case BOSS_FIGHT:
                renderBossFight(gc);
                break;
            case GAME_OVER:
                renderGameOver(gc);
                break;
            case VICTORY:
                renderVictory(gc);
                break;
        }
    }

    private void renderMenu(GraphicsContext gc) {
        // === SFONDO PERSONALIZZATO PER IL MENU ===
        
        if (menuBackgroundImage != null && !menuBackgroundImage.isError()) {
            // Usa l'immagine di sfondo personalizzata, scalandola per coprire tutto lo schermo
            gc.drawImage(menuBackgroundImage, 0, 0, WIDTH, HEIGHT);
        } else {
            // Fallback: sfondo spaziale generato proceduralmente
            LinearGradient spaceGradient = new LinearGradient(
                0, 0, 0, HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(20, 10, 50)),    // Blu-viola scuro in alto
                new Stop(0.5, Color.rgb(60, 10, 90)),  // Viola medio al centro
                new Stop(1, Color.rgb(10, 5, 30))      // Quasi nero con tinta viola in basso
            );
            gc.setFill(spaceGradient);
            gc.fillRect(0, 0, WIDTH, HEIGHT);
            
            // Stelle piccole e numerose con distribuzione migliorata
            gc.setFill(Color.rgb(255, 255, 255, 0.9));
            for (int i = 0; i < 200; i++) {
                double starX = (i * 127) % WIDTH; // Distribuzione pseudo-casuale
                double starY = (i * 83) % HEIGHT;
                double starSize = 0.8 + (i % 4) * 0.5;
                gc.fillOval(starX, starY, starSize, starSize);
            }
            
            // Stelle medie più luminose
            gc.setFill(Color.rgb(255, 255, 220));
            for (int i = 0; i < 30; i++) {
                double starX = (i * 191) % WIDTH;
                double starY = (i * 107) % HEIGHT;
                double starSize = 2 + (i % 3);
                gc.fillOval(starX, starY, starSize, starSize);
            }
            
            // Stelle brillanti con bagliore
            for (int i = 0; i < 15; i++) {
                double starX = (i * 239) % WIDTH;
                double starY = (i * 167) % HEIGHT;
                double starSize = 4 + (i % 2);
                
                // Bagliore
                RadialGradient starGlow = new RadialGradient(
                    0, 0, 0.5, 0.5, 1.0, true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.rgb(255, 255, 255, 0.8)),
                    new Stop(1, Color.rgb(255, 255, 255, 0))
                );
                gc.setFill(starGlow);
                gc.fillOval(starX - 6, starY - 6, starSize + 12, starSize + 12);
                
                // Stella centrale
                gc.setFill(Color.WHITE);
                gc.fillOval(starX, starY, starSize, starSize);
            }
            
            // Nebulosa distante
            RadialGradient nebula = new RadialGradient(
                0, 0, 0.5, 0.5, 1.0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(100, 50, 150, 0.3)),
                new Stop(0.7, Color.rgb(50, 25, 100, 0.1)),
                new Stop(1, Color.rgb(0, 0, 0, 0))
            );
            gc.setFill(nebula);
            gc.fillOval(WIDTH * 0.7, HEIGHT * 0.2, 400, 300);
        }

        // === TITOLO "MasterCat" PERFETTAMENTE CENTRATO E SCALATO ===
        String title = "MasterCat";
        Font titleFont = scaleFont("Impact", FontWeight.BOLD, 90);
        
        // Calcola la larghezza esatta del testo per centrarlo perfettamente
        gc.setFont(titleFont);
        double titleWidth = titleFont.getSize() * title.length() * 0.6; // Stima approssimativa
        double titleX = (WIDTH - titleWidth) / 2; // Centro perfetto
        double titleY = HEIGHT/2 - scaleY(200); // SPOSTATO PIÙ IN ALTO: da -40 a -200
        
        // Ombra profonda per effetto 3D (più livelli) - scalate
        double shadowOffset1 = scaleSize(8);
        double shadowOffset2 = scaleSize(6);
        double shadowOffset3 = scaleSize(4);
        double shadowOffset4 = scaleSize(2);
        
        gc.setFill(Color.rgb(0, 0, 0, 0.8));
        gc.fillText(title, titleX + shadowOffset1, titleY + shadowOffset1);
        gc.setFill(Color.rgb(50, 50, 50, 0.6));
        gc.fillText(title, titleX + shadowOffset2, titleY + shadowOffset2);
        gc.setFill(Color.rgb(100, 100, 100, 0.4));
        gc.fillText(title, titleX + shadowOffset3, titleY + shadowOffset3);
        gc.setFill(Color.rgb(150, 150, 150, 0.2));
        gc.fillText(title, titleX + shadowOffset4, titleY + shadowOffset4);
        
        // Gradiente principale oro-arancione
        LinearGradient titleGradient = new LinearGradient(
            0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.rgb(255, 230, 100)),    // Oro chiaro in alto
            new Stop(0.3, Color.rgb(255, 200, 50)),   // Oro medio
            new Stop(0.7, Color.rgb(255, 150, 0)),    // Arancione oro
            new Stop(1, Color.rgb(200, 100, 0))       // Arancione scuro in basso
        );
        gc.setFill(titleGradient);
        gc.fillText(title, titleX, titleY);
        
        // Bordo luminoso per effetto 3D - scalato
        gc.setStroke(Color.rgb(255, 255, 255, 0.8));
        gc.setLineWidth(scaleSize(2));
        gc.strokeText(title, titleX, titleY);
        
        // Riflessi luminosi interni - scalati
        gc.setStroke(Color.rgb(255, 255, 200, 0.6));
        gc.setLineWidth(scaleSize(1));
        gc.strokeText(title, titleX - scaleSize(1), titleY - scaleSize(1));
        
        // Bagliore esterno dorato - scalato
        gc.setStroke(Color.rgb(255, 215, 0, 0.3));
        gc.setLineWidth(scaleSize(8));
        gc.strokeText(title, titleX, titleY);
        gc.setLineWidth(scaleSize(12));
        gc.setStroke(Color.rgb(255, 215, 0, 0.15));
        gc.strokeText(title, titleX, titleY);

        // === ISTRUZIONI PERFETTAMENTE CENTRATE E SCALATE ===
        String startText = "Premi SPAZIO per avviare";
        Font startFont = scaleFont("Arial", FontWeight.BOLD, 35);
        gc.setFont(startFont);
        double startTextWidth = startFont.getSize() * startText.length() * 0.6; // Stima per centratura
        double startTextX = (WIDTH - startTextWidth) / 2; // Centro perfetto
        
        gc.setFill(Color.WHITE);
        gc.fillText(startText, startTextX, HEIGHT/2 + scaleY(50));
        
        // === PULSANTE IMPOSTAZIONI ===
        // Calcola posizione in basso a destra con margine
        settingsButtonX = WIDTH - settingsButtonWidth - 30; // 30 pixel di margine dal bordo destro
        settingsButtonY = HEIGHT - settingsButtonHeight - 30; // 30 pixel di margine dal bordo inferiore
        
        // NO background for the settings button - just text
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        gc.fillText("Impostazioni", settingsButtonX, settingsButtonY + 25);
        
        // === MENU A TENDINA IMPOSTAZIONI ===
        if (settingsMenuOpen) {
            renderSettingsDropdown(gc);
        }
    }

    private void renderGame(GraphicsContext gc) {
        // === SFONDO PERSONALIZZATO PER IL GIOCO ===
        
        if (gameBackgroundImage != null && !gameBackgroundImage.isError()) {
            // Usa l'immagine di sfondo personalizzata, scalandola per coprire tutto lo schermo
            gc.drawImage(gameBackgroundImage, 0, 0, WIDTH, HEIGHT);
        } else {
            // Fallback: sfondo spaziale/caverna generato proceduralmente
            // Sfondo con gradiente cosmico profondo
            LinearGradient spaceGradient = new LinearGradient(
                0, 0, 0, HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(5, 5, 30)),      // Blu scurissimo in alto
                new Stop(0.4, Color.rgb(20, 10, 50)),  // Blu-viola scuro
                new Stop(1, Color.rgb(40, 20, 60))     // Viola scuro in basso
            );
            gc.setFill(spaceGradient);
            gc.fillRect(0, 0, WIDTH, HEIGHT);
            
            // Stelle sparse nel cielo con effetto scintillante
            double time = System.currentTimeMillis() * 0.001;
            gc.setFill(Color.WHITE);
            for (int i = 0; i < 150; i++) {
                double starX = (i * 137) % WIDTH; // Distribuzione pseudo-casuale
                double starY = (i * 79) % (HEIGHT - 200);
                // Effetto scintillante per alcune stelle
                double twinkle = 1.0;
                if (i % 5 == 0) {
                    twinkle = 0.7 + Math.sin(time + i) * 0.3;
                }
                double starSize = (1 + (i % 3)) * twinkle;
                gc.fillOval(starX, starY, starSize, starSize);
            }
            
            // Apertura del cielo in lontananza (al centro superiore)
            double skyOpeningWidth = 350; // Più ampia
            double skyOpeningHeight = 250; // Più alta
            double skyX = WIDTH/2 - skyOpeningWidth/2;
            double skyY = 0;
            
            // Cielo blu dietro l'apertura con colori più vivaci
            RadialGradient skyGradient = new RadialGradient(
                0, 0, 0.5, 0.3, 1.0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(135, 206, 250)),    // Azzurro cielo più brillante
                new Stop(0.4, Color.rgb(100, 149, 237)),  // Blu cornflower
                new Stop(0.7, Color.rgb(65, 105, 225)),   // Blu reale
                new Stop(1, Color.rgb(25, 25, 112))       // Blu notte profondo
            );
            gc.setFill(skyGradient);
            gc.fillOval(skyX, skyY - 100, skyOpeningWidth, skyOpeningHeight + 100);
            
            // Nuvole nel cielo lontano con effetto volumetrico
            // Prima nuvola con gradiente
            RadialGradient cloudGradient1 = new RadialGradient(
                0, 0, 0.5, 0.5, 1.0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(255, 255, 255, 0.9)),
                new Stop(0.7, Color.rgb(240, 240, 255, 0.7)),
                new Stop(1, Color.rgb(200, 200, 255, 0))
            );
            gc.setFill(cloudGradient1);
            gc.fillOval(skyX + 50, skyY + 20, 70, 30);
            gc.fillOval(skyX + 80, skyY + 15, 60, 25);
            
            // Seconda nuvola con gradiente
            RadialGradient cloudGradient2 = new RadialGradient(
                0, 0, 0.5, 0.5, 1.0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(255, 255, 255, 0.9)),
                new Stop(0.7, Color.rgb(240, 240, 255, 0.7)),
                new Stop(1, Color.rgb(200, 200, 255, 0))
            );
            gc.setFill(cloudGradient2);
            gc.fillOval(skyX + 180, skyY + 25, 80, 35);
            gc.fillOval(skyX + 210, skyY + 20, 60, 30);
            
            // Sole dorato più brillante e con raggi
            RadialGradient sunGradient = new RadialGradient(
                0, 0, 0.5, 0.5, 0.8, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(255, 255, 200)),      // Bianco-giallo al centro
                new Stop(0.5, Color.rgb(255, 255, 0)),      // Giallo brillante
                new Stop(0.8, Color.rgb(255, 165, 0)),      // Arancione
                new Stop(1, Color.rgb(255, 140, 0, 0.5))    // Arancione scuro con trasparenza
            );
            gc.setFill(sunGradient);
            gc.fillOval(skyX + 200, skyY + 30, 50, 50); // Sole più grande
            
            // Raggi del sole
            gc.setStroke(Color.rgb(255, 255, 150, 0.4));
            gc.setLineWidth(2);
            double sunCenterX = skyX + 225;
            double sunCenterY = skyY + 55;
            for (int i = 0; i < 12; i++) {
                double angle = i * Math.PI / 6;
                double startX = sunCenterX + Math.cos(angle) * 30;
                double startY = sunCenterY + Math.sin(angle) * 30;
                double endX = sunCenterX + Math.cos(angle) * 60;
                double endY = sunCenterY + Math.sin(angle) * 60;
                gc.strokeLine(startX, startY, endX, endY);
            }
            
            // Pareti rocciose della caverna/canyon con texture migliorata
            // Parete sinistra con gradiente
            LinearGradient leftWallGradient = new LinearGradient(
                0, 0, skyX, 0,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(30, 20, 40)),    // Viola scuro
                new Stop(0.7, Color.rgb(50, 30, 60)),  // Viola medio
                new Stop(1, Color.rgb(70, 40, 80))     // Viola chiaro verso l'apertura
            );
            gc.setFill(leftWallGradient);
            double[] leftWallX = {0, 0, skyX - 50, skyX + 20};
            double[] leftWallY = {0, HEIGHT, HEIGHT, 0};
            gc.fillPolygon(leftWallX, leftWallY, 4);
            
            // Parete destra con gradiente
            LinearGradient rightWallGradient = new LinearGradient(
                WIDTH, 0, skyX + skyOpeningWidth, 0,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(30, 20, 40)),    // Viola scuro
                new Stop(0.7, Color.rgb(50, 30, 60)),  // Viola medio
                new Stop(1, Color.rgb(70, 40, 80))     // Viola chiaro verso l'apertura
            );
            gc.setFill(rightWallGradient);
            double[] rightWallX = {WIDTH, WIDTH, skyX + skyOpeningWidth + 50, skyX + skyOpeningWidth - 20};
            double[] rightWallY = {0, HEIGHT, HEIGHT, 0};
            gc.fillPolygon(rightWallX, rightWallY, 4);
            
            // Dettagli rocciosi sulle pareti con texture migliorata
            // Formazioni rocciose sulla parete sinistra
            for (int i = 0; i < 15; i++) {
                double rockX = i * 30;
                double rockY = i * 50 + 100;
                if (rockX < skyX - 50) {
                    // Rocce con gradiente per effetto 3D
                    RadialGradient rockGradient = new RadialGradient(
                        0, 0, rockX + 12, rockY + 7, 20, false, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.rgb(70, 50, 80)),   // Viola chiaro
                        new Stop(0.7, Color.rgb(50, 30, 60)),  // Viola medio
                        new Stop(1, Color.rgb(30, 20, 40))     // Viola scuro
                    );
                    gc.setFill(rockGradient);
                    gc.fillOval(rockX, rockY, 30, 18);
                }
            }
            
            // Formazioni rocciose sulla parete destra
            for (int i = 0; i < 15; i++) {
                double rockX = WIDTH - (i * 30);
                double rockY = i * 50 + 100;
                if (rockX > skyX + skyOpeningWidth + 50) {
                    // Rocce con gradiente per effetto 3D
                    RadialGradient rockGradient = new RadialGradient(
                        0, 0, rockX + 12, rockY + 7, 20, false, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.rgb(70, 50, 80)),   // Viola chiaro
                        new Stop(0.7, Color.rgb(50, 30, 60)),  // Viola medio
                        new Stop(1, Color.rgb(30, 20, 40))     // Viola scuro
                    );
                    gc.setFill(rockGradient);
                    gc.fillOval(rockX, rockY, 30, 18);
                }
            }
            
            // Stalattiti dal soffitto con effetto cristallino
            for (int i = 0; i < 10; i++) {
                double stalX = i * 80 + 40;
                if (stalX < skyX - 20 || stalX > skyX + skyOpeningWidth + 20) {
                    // Stalattiti con gradiente per effetto cristallino
                    LinearGradient stalGradient = new LinearGradient(
                        0, 0, 0, 100,
                        false, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.rgb(40, 30, 50)),    // Viola scuro in alto
                        new Stop(0.5, Color.rgb(60, 40, 80)),  // Viola medio
                        new Stop(1, Color.rgb(80, 60, 100))    // Viola chiaro in basso
                    );
                    gc.setFill(stalGradient);
                    
                    double[] stalactiteX = {stalX, stalX + 15, stalX + 30, stalX + 15};
                    double[] stalactiteY = {0, 0, 90 + i * 10, 100 + i * 10};
                    gc.fillPolygon(stalactiteX, stalactiteY, 4);
                    
                    // Riflessi luminosi sulle stalattiti
                    gc.setFill(Color.rgb(255, 255, 255, 0.3));
                    gc.fillOval(stalX + 10, 40, 5, 20);
                }
            }
            
            // Terreno come linea orizzontale ben visibile
            gc.setStroke(Color.rgb(120, 80, 100));
            gc.setLineWidth(6);
            gc.strokeLine(0, GROUND_HEIGHT, WIDTH, GROUND_HEIGHT);

        }

        // HUD minimal
        drawMinimalHUD(gc);

        // Gatto con immagine caricata - INGRANDITO in gioco - PIÙ VISIBILE
        if (catImage != null) {
            double catWidth = scaleSize(110);  // INGRANDITO: era 78, ora 110 (+40%)
            double catHeight = scaleSize(110); // INGRANDITO: era 78, ora 110 (+40%)
            double bounce = Math.sin(bodyBounce) * 2; // Rimbalzo quando corre
            double catDrawX = catX - catWidth/2;
            double catDrawY = catY + bounce - catHeight;
            
            // === RENDERING NORMALE SENZA EFFETTI ===
            // Disegna il gatto normalmente con piena opacità - NESSUNA SFERA BIANCA
            gc.setGlobalAlpha(1.0);
            gc.setGlobalBlendMode(BlendMode.SRC_OVER);
            gc.drawImage(catImage, catDrawX, catDrawY, catWidth, catHeight);
        } else {
            // Fallback: disegna un cerchio blu per vedere dove dovrebbe essere il gatto - PIÙ GRANDE
            gc.setFill(Color.BLUE);
            double fallbackSize = scaleSize(110); // INGRANDITO: era 78, ora 110
            double bounce = Math.sin(bodyBounce) * 2;
            gc.fillOval(catX - fallbackSize/2, catY + bounce - fallbackSize, fallbackSize, fallbackSize);
        }

        // === SFERE ENERGETICHE DRAGON BALL ===
        for (DragonBallSphere sphere : dragonBallSpheres) {
            sphere.render(gc);
        }

        // Ostacoli migliorati
        for (Obstacle o : obstacles) {
            o.render(gc);
        }
    }

    private void renderBossFight(GraphicsContext gc) {
        // === SFONDO PERSONALIZZATO PER LA BOSS FIGHT ===
        
        if (bossBackgroundImage != null && !bossBackgroundImage.isError()) {
            // Usa l'immagine di sfondo personalizzata per la boss battle
            gc.drawImage(bossBackgroundImage, 0, 0, WIDTH, HEIGHT);
        } else {
            // Fallback: sfondo drammatico per la boss fight
            LinearGradient bossGradient = new LinearGradient(
                0, 0, 0, HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(60, 10, 10)),    // Rosso scuro in alto
                new Stop(0.5, Color.rgb(30, 5, 30)),   // Viola scurissimo al centro
                new Stop(1, Color.rgb(10, 5, 5))       // Quasi nero in basso
            );
            gc.setFill(bossGradient);
            gc.fillRect(0, 0, WIDTH, HEIGHT);
            
            // Fulmini di sfondo per atmosfera drammatica
            gc.setStroke(Color.rgb(255, 255, 100, 0.3));
            gc.setLineWidth(3);
            double time = System.currentTimeMillis() * 0.01;
            for (int i = 0; i < 10; i++) {
                double x = (i * 200 + Math.sin(time + i) * 50) % WIDTH;
                gc.strokeLine(x, 0, x + 20, HEIGHT * 0.3);
            }
        }
        
        // === DISEGNA IL TERRENO ===
        gc.setFill(Color.rgb(139, 90, 43)); // Marrone terreno
        gc.fillRect(0, GROUND_HEIGHT, WIDTH, HEIGHT - GROUND_HEIGHT);
        
        // Linea superiore del terreno
        gc.setStroke(Color.rgb(100, 70, 30));
        gc.setLineWidth(3);
        gc.strokeLine(0, GROUND_HEIGHT, WIDTH, GROUND_HEIGHT);
        
        // === DISEGNA IL BOSS (CORRIERE AMAZON) - INGRANDITO E A TERRA ===
        if (bossImage != null && !bossImage.isError()) {
            // Usa l'immagine personalizzata del boss - INGRANDITA
            double bossWidth = 150; // Aumentato da 120 a 150
            double bossHeight = 200; // Aumentato da 160 a 200
            
            // Boss posizionato a terra come il giocatore
            double bossGroundY = GROUND_HEIGHT - 10; // Stesso livello del giocatore
            
            // Animazione oscillante del boss (movimento più sottile)
            double bobbing = Math.sin(bossAnimationTimer) * 3; // Movimento ridotto
            gc.drawImage(bossImage, bossX - bossWidth/2, bossGroundY - bossHeight + bobbing, bossWidth, bossHeight);
        } else {
            // Fallback: disegna il boss come un rettangolo stilizzato - INGRANDITO
            double bossWidth = 120; // Aumentato da 80 a 120  
            double bossHeight = 160; // Aumentato da 120 a 160
            
            // Boss posizionato a terra
            double bossGroundY = GROUND_HEIGHT - 10;
            
            // Corpo del corriere (in piedi sul terreno)
            gc.setFill(Color.rgb(70, 130, 180)); // Blu uniforme Amazon
            double bobbing = Math.sin(bossAnimationTimer) * 3; // Movimento ridotto
            gc.fillRect(bossX - bossWidth/2, bossGroundY - bossHeight + bobbing, bossWidth, bossHeight);
            
            // Bordo della divisa
            gc.setStroke(Color.rgb(50, 100, 140));
            gc.setLineWidth(3);
            gc.strokeRect(bossX - bossWidth/2, bossGroundY - bossHeight + bobbing, bossWidth, bossHeight);
            
            // Logo Amazon sul petto
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 20)); // Aumentato font
            gc.fillText("amazon", bossX - 30, bossGroundY - bossHeight + bobbing + 50);
            
            // Sorriso caratteristico Amazon
            gc.setStroke(Color.ORANGE);
            gc.setLineWidth(5); // Aumentato spessore
            gc.strokeArc(bossX - 25, bossGroundY - bossHeight + bobbing + 55, 50, 25, 0, 180, javafx.scene.shape.ArcType.OPEN);
            
            // Braccia che lanciano (movimento di lancio verso il basso)
            gc.setStroke(Color.rgb(220, 180, 140)); // Colore pelle
            gc.setLineWidth(10); // Aumentato spessore
            double armAngle = Math.sin(bossAnimationTimer * 3) * 15; // Movimento più contenuto
            
            // Braccio sinistro (quello che lancia verso il basso)
            double armY = bossGroundY - bossHeight + bobbing + 60;
            gc.strokeLine(bossX - bossWidth/2, armY, 
                         bossX - bossWidth/2 - 40, armY + 40 + armAngle); // Lancia verso il basso
            
            // Braccio destro (bilanciamento, anche verso il basso)
            gc.strokeLine(bossX + bossWidth/2, armY, 
                         bossX + bossWidth/2 + 30, armY + 30 - armAngle/2); // Anche verso il basso
        }
        
        // === DISEGNA IL GATTO - INGRANDITO ===
        double catSize = 80; // Aumentato da 50 a 80
        double catDrawX = catX - catSize/2;
        double catDrawY = catY - catSize;
        
        if (catImage != null && !catImage.isError()) {
            gc.drawImage(catImage, catDrawX, catDrawY, catSize, catSize);
        } else {
            // Fallback: disegna il gatto come forme geometriche - INGRANDITO
            double bounce = Math.sin(bodyBounce) * 3;
            
            // Corpo
            gc.setFill(Color.rgb(255, 140, 0));
            gc.fillOval(catDrawX, catDrawY + bounce, catSize * 0.8, catSize * 0.6);
            
            // Testa
            gc.fillOval(catDrawX + catSize * 0.1, catDrawY - catSize * 0.2 + bounce, catSize * 0.6, catSize * 0.5);
            
            // Orecchie
            gc.setFill(Color.rgb(255, 120, 0));
            gc.fillPolygon(
                new double[]{catDrawX + catSize * 0.2, catDrawX + catSize * 0.1, catDrawX + catSize * 0.3},
                new double[]{catDrawY - catSize * 0.2 + bounce, catDrawY - catSize * 0.4 + bounce, catDrawY - catSize * 0.3 + bounce},
                3
            );
            gc.fillPolygon(
                new double[]{catDrawX + catSize * 0.5, catDrawX + catSize * 0.6, catDrawX + catSize * 0.4},
                new double[]{catDrawY - catSize * 0.2 + bounce, catDrawY - catSize * 0.4 + bounce, catDrawY - catSize * 0.3 + bounce},
                3
            );
            
            // Occhi
            gc.setFill(Color.BLACK);
            gc.fillOval(catDrawX + catSize * 0.25, catDrawY - catSize * 0.1 + bounce, 6, 6); // Ingranditi
            gc.fillOval(catDrawX + catSize * 0.45, catDrawY - catSize * 0.1 + bounce, 6, 6); // Ingranditi
        }
        
        // === SCUDO ===
        if (shieldActive) {
            double shieldAlpha = 0.7 * Math.sin(shieldTimer * 8); // Effetto pulsante
            if (shieldAlpha < 0) shieldAlpha = -shieldAlpha;
            
            RadialGradient shieldGradient = new RadialGradient(
                0, 0, 0.5, 0.5, 1.0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(100, 255, 100, shieldAlpha * 0.3)),
                new Stop(0.7, Color.rgb(50, 200, 50, shieldAlpha * 0.6)),
                new Stop(1, Color.rgb(0, 150, 0, shieldAlpha))
            );
            gc.setFill(shieldGradient);
            gc.fillOval(catX - 60, catY - 80, 120, 120);
            
            // Bordo dello scudo
            gc.setStroke(Color.rgb(0, 255, 0, shieldAlpha));
            gc.setLineWidth(4);
            gc.strokeOval(catX - 60, catY - 80, 120, 120);
        }
        
        // === SFERE DRAGON BALL NEL BOSS FIGHT ===
        for (DragonBallSphere sphere : dragonBallSpheres) {
            sphere.render(gc);
        }
        
        // === INDICATORI DANNO ===
        for (DamageIndicator indicator : damageIndicators) {
            indicator.render(gc);
        }
        
        // === DISEGNA PROIETTILI DEL BOSS ===
        for (BossProjectile projectile : bossProjectiles) {
            projectile.render(gc);
        }
        
        // === DISEGNA BARRIERA BOSS ===
        for (BarrierBlock block : bossBarrier) {
            block.render(gc);
        }
        
        // === HUD DELLA BOSS FIGHT ===
        renderBossFightHUD(gc);
    }
    
    private void renderBossFightHUD(GraphicsContext gc) {
        // === TIMER BOSS FIGHT ===
        double remainingTime = BOSS_DURATION - bossTimer;
        int minutes = (int) (remainingTime / 60);
        int seconds = (int) (remainingTime % 60);
        
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, scaleY(36)));
        String timeText = String.format("TEMPO: %d:%02d", minutes, seconds);
        gc.fillText(timeText, scaleX(50), scaleY(50));
        
       
        
        // === BARRA ENERGIA (PIÙ GRANDE E SOTTO IL PUNTEGGIO) ===
        double energyBarX = scaleX(50);
        double energyBarY = scaleY(80);
        double energyBarWidth = scaleX(300);
        double energyBarHeight = scaleY(20);

       
        
        
        
        // Energia attuale
        double energyPercent = energy / MAX_ENERGY;
        Color energyColor = energyPercent >= 1.0 ? Color.CYAN : Color.LIGHTBLUE;
        gc.setFill(energyColor);
        gc.fillRect(energyBarX, energyBarY, energyBarWidth * energyPercent, energyBarHeight);
        
        // Bordo barra energia
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);
        gc.strokeRect(energyBarX, energyBarY, energyBarWidth, energyBarHeight);

        
        // Testo energia
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, scaleY(16)));
        String energyText = String.format("ENERGIA: %.0f/%.0f", energy, MAX_ENERGY);
        if (energy >= MAX_ENERGY) {
            energyText += " [PREMI C]";
        }
        gc.fillText(energyText, energyBarX, energyBarY - scaleY(5));
        
        // === BARRA VITA BOSS ===
        double bossHealthBarX = WIDTH - scaleX(320);
        double bossHealthBarY = scaleY(30);
        double bossHealthBarWidth = scaleX(250);
        double bossHealthBarHeight = scaleY(20);
        
     
        
        // Vita attuale del boss
        double bossHealthPercent = bossHealth / MAX_BOSS_HEALTH;
        gc.setFill(Color.RED);
        gc.fillRect(bossHealthBarX, bossHealthBarY, bossHealthBarWidth * bossHealthPercent, bossHealthBarHeight);
        
        // Bordo barra vita boss
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeRect(bossHealthBarX, bossHealthBarY, bossHealthBarWidth, bossHealthBarHeight);
        
        // Etichetta vita boss
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, scaleY(14)));
        gc.fillText(String.format("VITA BOSS: %.0f/%.0f", bossHealth, MAX_BOSS_HEALTH), 
                   bossHealthBarX, bossHealthBarY - scaleY(5));
        
        // === ISTRUZIONI BOSS FIGHT ===
        gc.setFill(Color.YELLOW);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, scaleY(16)));
        gc.fillText("← → (A/D) Movimento| X Scudo", 
                   scaleX(50), scaleY(120));
        
        // === INDICATORE SCUDO ===
        if (shieldActive) {
            double shieldTimeLeft = SHIELD_DURATION - shieldTimer;
            gc.setFill(Color.GREEN);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, scaleY(14)));
            gc.fillText("🛡️ SCUDO ATTIVO: " + String.format("%.1f", shieldTimeLeft) + "s", 
                       scaleX(50), scaleY(260));
        }
          
    }

    private void renderGameOver(GraphicsContext gc) {
        // === SFONDO PERSONALIZZATO PER IL GAME OVER ===
        
        if (gameOverBackgroundImage != null && !gameOverBackgroundImage.isError()) {
            // Usa l'immagine di sfondo personalizzata, scalandola per coprire tutto lo schermo
            gc.drawImage(gameOverBackgroundImage, 0, 0, WIDTH, HEIGHT);
        } else {
            // Fallback: sfondo nero semplice
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, WIDTH, HEIGHT);
        }
        
        // Istruzioni centrate e posizionate nella parte centrale in basso
        gc.setFill(Color.WHITE);
        gc.setFont(scaleFont("Arial", FontWeight.BOLD, 25));
        String instructionText = "Premi qualsiasi tasto per tornare al menu";
        
        // Calcolo per centrare il testo orizzontalmente
        double instructionTextWidth = gc.getFont().getSize() * instructionText.length() * 0.6; // Stima approssimativa
        double instructionX = WIDTH/2 - instructionTextWidth/2;
        
        // Posizione verticale: circa 3/4 dell'altezza dello schermo (parte centrale in basso)
        double instructionY = HEIGHT * 0.75;
        
        gc.fillText(instructionText, instructionX, instructionY);
    }

    private void renderVictory(GraphicsContext gc) {
        try {
            // === SFONDO PERSONALIZZATO PER LA VITTORIA ===
            
            if (victoryBackgroundImage != null && !victoryBackgroundImage.isError()) {
                // Usa l'immagine di sfondo personalizzata, scalandola per coprire tutto lo schermo
                gc.drawImage(victoryBackgroundImage, 0, 0, WIDTH, HEIGHT);
            } else {
                // Fallback: sfondo nero semplice
                gc.setFill(Color.BLACK);
                gc.fillRect(0, 0, WIDTH, HEIGHT);
            }

            // Punteggio finale centrato e spostato più in basso
            gc.setFill(Color.WHITE);
            gc.setFont(scaleFont("Arial", FontWeight.BOLD, 36));
            String finalScoreText = "Punteggio Finale: " + score;
            // Calcolo per centrare il testo orizzontalmente
            double textWidth = gc.getFont().getSize() * finalScoreText.length() * 0.6; // Stima approssimativa
            gc.fillText(finalScoreText, WIDTH/2 - textWidth/2, HEIGHT/2 + scaleSize(100));
            
            // Istruzioni centrate sotto il punteggio
            gc.setFill(Color.WHITE);
            gc.setFont(scaleFont("Arial", FontWeight.NORMAL, 20));
            String instructionText = "Premi qualsiasi tasto per tornare al menu";
            double instructionWidth = gc.getFont().getSize() * instructionText.length() * 0.6; // Stima approssimativa
            gc.fillText(instructionText, WIDTH/2 - instructionWidth/2, HEIGHT/2 + scaleSize(170));
            
        } catch (Exception e) {
            System.out.println("❌ Errore nel rendering della vittoria: " + e.getMessage());
            // Fallback: schermata nera con testo semplice
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, WIDTH, HEIGHT);
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", 24));
            gc.fillText("VITTORIA! Punteggio: " + score, WIDTH/2 - 100, HEIGHT/2);
            gc.fillText("Premi qualsiasi tasto per continuare", WIDTH/2 - 150, HEIGHT/2 + 50);
        }
    }

    private void drawMinimalHUD(GraphicsContext gc) {
        // === PUNTEGGIO INGRANDITO IN ALTO A SINISTRA ===

        
        // === BARRA ENERGIA DINAMICA E GRAFICA COME NEL BOSS FIGHT ===
        double energyBarX = 20;
        double energyBarY = 55;
        double energyBarWidth = 300; // Aumentata per essere più prominente
        double energyBarHeight = 20; // Più alta come nel boss fight
        
        // Testo energia sopra la barra
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Impact", FontWeight.BOLD, 18));
        String energyText = String.format("ENERGIA: %.0f/%.0f", energy, MAX_ENERGY);
        if (energy >= MAX_ENERGY) {
            energyText += " [PREMI C]";
        }
        gc.fillText(energyText, energyBarX, energyBarY - 5);
        
      
        
        // Energia attuale con gradiente dinamico
        if (energy > 0) {
            double energyPercent = energy / MAX_ENERGY;
            Color energyColor = energyPercent >= 1.0 ? Color.CYAN : Color.LIGHTBLUE;
            
            // Effetto pulsante quando energia è al massimo
            if (energyPercent >= 1.0) {
                double pulse = Math.sin(System.currentTimeMillis() * 0.01) * 0.3 + 0.7;
                energyColor = Color.rgb((int)(255 * pulse), (int)(255 * pulse), 255);
            }
            
            gc.setFill(energyColor);
            gc.fillRect(energyBarX, energyBarY, energyBarWidth * energyPercent, energyBarHeight);
            
            // Effetto brillante quando energia è al massimo
            if (energyPercent >= 1.0) {
                gc.setFill(Color.rgb(255, 255, 255, 0.4));
                gc.fillRect(energyBarX, energyBarY, energyBarWidth * energyPercent, energyBarHeight / 2);
            }
        }
        
        // Bordo barra energia
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);
        gc.strokeRect(energyBarX, energyBarY, energyBarWidth, energyBarHeight);
        
        // === BARRA PROGRESSO VITTORIA SPOSTATA PIU' IN BASSO ===
        double progressBarWidth = 300;
        double progressBarHeight = 15;
        double progressX = 20;
        double progressY = 100; // SPOSTATA: più in basso per fare spazio all'energia più grande
        
        // Testo progresso sopra la barra
        gc.setFill(Color.GOLD);
        gc.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 16));
        gc.fillText("Obiettivo: " + score + "/" + WINNING_SCORE, progressX, progressY - 8);
        
        // Sfondo barra progresso
        gc.setFill(Color.rgb(40, 40, 40, 0.8));
        gc.fillRoundRect(progressX, progressY, progressBarWidth, progressBarHeight, 8, 8);
        
        // Progresso con gradiente
        double progress = Math.min(1.0, (double)score / WINNING_SCORE);
        if (progress > 0) {
            Color progressColor = progress >= 1.0 ? Color.GOLD : Color.LIME;
            LinearGradient progressGradient = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, progressColor.brighter()),
                new Stop(1, progressColor.darker())
            );
            gc.setFill(progressGradient);
            gc.fillRoundRect(progressX + 2, progressY + 2, (progressBarWidth - 4) * progress, progressBarHeight - 4, 6, 6);
        }
        
        // Bordo della barra progresso
        gc.setStroke(Color.GOLD);
        gc.setLineWidth(1);
        gc.strokeRoundRect(progressX, progressY, progressBarWidth, progressBarHeight, 8, 8);
    }

    // Classe Obstacle migliorata con supporto per immagini
    static class Obstacle {
        double x, y, width, height, speed;
        boolean isPartOfDouble; // Se fa parte di un ostacolo doppio
        private Image normalBlockImage;
        private Image doubleBlockImage;

        Obstacle(double x, double y, double width, double height, double speed, boolean isPartOfDouble) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.speed = speed;
            this.isPartOfDouble = isPartOfDouble;
        }
        
        // Metodo per impostare le immagini dei blocchi
        void setBlockImages(Image normalImage, Image doubleImage) {
            this.normalBlockImage = normalImage;
            this.doubleBlockImage = doubleImage;
        }

        void update(double dt) {
            x -= speed * dt;
        }

        void render(GraphicsContext gc) {
            // === PRIORITÀ: IMMAGINE PERSONALIZZATA ===
            Image imageToUse = isPartOfDouble ? doubleBlockImage : normalBlockImage;
            
            if (imageToUse != null && !imageToUse.isError()) {
                // Usa l'immagine personalizzata caricata dall'utente
                gc.drawImage(imageToUse, x, y, width, height);
                return; // Esce qui se l'immagine è disponibile
            }
            // === PACCO AMAZON FEDELE ALL'ORIGINALE ===
            
            // Colore marrone Amazon esatto dall'immagine
            gc.setFill(Color.rgb(168, 124, 86)); // Marrone Amazon uniforme
            gc.fillRect(x, y, width, height); // Spigoli netti
            
            // === LOGO AMAZON SEMPLICE E FEDELE ===
            
            // Sorriso Amazon - curva semplice e pulita
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(Math.max(2, height * 0.08)); // Spessore proporzionale
            
            // Curva del sorriso che va da A a Z (sinistra a destra)
            double smileStartX = x + width * 0.2;
            double smileEndX = x + width * 0.8;
            double smileY = y + height * 0.5;
            
            // Sorriso curvo semplice
            gc.beginPath();
            gc.moveTo(smileStartX, smileY);
            gc.quadraticCurveTo(x + width * 0.5, smileY + height * 0.15, smileEndX, smileY - height * 0.05);
            gc.stroke();
            
            // Freccia finale pulita
            double arrowSize = Math.max(3, height * 0.12);
            gc.strokeLine(smileEndX - arrowSize, smileY - height * 0.05 - arrowSize/2, smileEndX, smileY - height * 0.05);
            gc.strokeLine(smileEndX - arrowSize, smileY - height * 0.05 + arrowSize/2, smileEndX, smileY - height * 0.05);
            
            // === ETICHETTA PRIME AUTENTICA (solo per pacchi doppi) ===
            if (isPartOfDouble) {
                // Etichetta Prime blu Amazon
                double primeWidth = width * 0.35;
                double primeHeight = height * 0.18;
                double primeX = x + width * 0.03;
                double primeY = y + height * 0.05;
                
                gc.setFill(Color.rgb(0, 113, 189)); // Blu Prime ufficiale
                gc.fillRect(primeX, primeY, primeWidth, primeHeight);
                
                // Testo "prime" bianco
                gc.setFill(Color.WHITE);
                gc.setFont(Font.font("Arial", FontWeight.BOLD, Math.max(5, height * 0.1)));
                gc.fillText("prime", primeX + 2, primeY + primeHeight * 0.75);
                
                // Piccolo "day" sotto prime
                gc.setFont(Font.font("Arial", FontWeight.NORMAL, Math.max(3, height * 0.06)));
                gc.fillText("day", primeX + primeWidth * 0.7, primeY + primeHeight * 0.75);
            }
            
            // === EFFETTO 3D REALISTICO ===
            // Faccia laterale destra (più scura)
            gc.setFill(Color.rgb(145, 105, 70)); // Marrone più scuro per lato
            gc.fillRect(x + width - 4, y, 4, height);
            
            // Faccia inferiore (più scura)
            gc.setFill(Color.rgb(145, 105, 70));
            gc.fillRect(x, y + height - 4, width, 4);
            
            // === TEXTURE CARTONE SOTTILE ===
            gc.setStroke(Color.rgb(150, 110, 75, 0.4));
            gc.setLineWidth(0.3);
            // Linee orizzontali molto sottili per texture cartone
            for (int i = 1; i < 4; i++) {
                double lineY = y + height * (i / 4.0);
                gc.strokeLine(x, lineY, x + width, lineY);
            }
            
            // === BORDO DEFINITO ===
            gc.setStroke(Color.rgb(130, 95, 60));
            gc.setLineWidth(1);
            gc.strokeRect(x, y, width, height);
        }

        boolean isOffScreen() {
            return x + width < 0;
        }

        boolean intersects(double px, double py, double pw, double ph) {
            return px < x + width && px + pw > x && py < y + height && py + ph > y;
        }
    }
    
    // === CLASSE BLOCCO BARRIERA BOSS ===
    static class BarrierBlock {
        double x, y, width, height;
        boolean isDestroyed = false;
        
        BarrierBlock(double x, double y, double width, double height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
        
        void render(GraphicsContext gc) {
            if (isDestroyed) return;
            
            // Blocco della barriera con colore distintivo (grigio scuro)
            gc.setFill(Color.rgb(80, 80, 80));
            gc.fillRect(x, y, width, height);
            
            // Bordo per effetto 3D
            gc.setFill(Color.rgb(60, 60, 60));
            gc.fillRect(x + width - 3, y, 3, height); // Lato destro più scuro
            gc.fillRect(x, y + height - 3, width, 3); // Fondo più scuro
            
            // Bordo esterno
            gc.setStroke(Color.rgb(40, 40, 40));
            gc.setLineWidth(1);
            gc.strokeRect(x, y, width, height);
            
            // Dettagli per distinguerlo dai pacchi normali
            gc.setFill(Color.rgb(120, 120, 120));
            gc.fillRect(x + 2, y + 2, width - 4, 2); // Linea orizzontale in alto
            gc.fillRect(x + 2, y + 2, 2, height - 4); // Linea verticale a sinistra
        }
        
        boolean intersects(double px, double py, double pw, double ph) {
            if (isDestroyed) return false;
            return px < x + width && px + pw > x && py < y + height && py + ph > y;
        }
        
        void destroy() {
            isDestroyed = true;
        }
        
        boolean isDestroyed() {
            return isDestroyed;
        }
    }
    
    // Metodi per gestire gli stati del gioco
    private void startGame() {
        currentState = GameState.PLAYING;
        // Reset delle variabili del gioco
        score = 0;
        gameSpeed = 1.0;
        energy = 0;
        waveActive = false;
        waveTimer = 0;
        catX = scaleX(150); // Posizione scalata del gatto
        catY = GROUND_HEIGHT;
        velocityY = 0;
        onGround = true;
        canDoubleJump = false; // Reset doppio salto
        obstacles.clear();
        dragonBallSpheres.clear(); // Reset sfere Dragon Ball
        lastObstacleTime = System.currentTimeMillis();
        
        // Cambia alla musica del gioco
        switchToGameMusic();
        System.out.println("🚀 Gioco avviato! Buona fortuna!");
    }
    
    private void startBossFight() {
        currentState = GameState.BOSS_FIGHT;
        System.out.println("⚔️ BOSS FIGHT INIZIATO! Sopravvivi per 2 minuti!");
        
        // Inizializza variabili del boss
        bossTimer = 0;
        bossHealth = MAX_BOSS_HEALTH; // Reset vita boss
        bossX = WIDTH - 200; // Posizione del boss sul lato destro
        bossY = GROUND_HEIGHT; // STESSO LIVELLO DEL GIOCATORE
        bossAnimationTimer = 0;
        lastBossAttackTime = System.currentTimeMillis();
        
        // Reset movimento orizzontale
        leftPressed = false;
        rightPressed = false;
        
        // === RESET POSIZIONE GIOCATORE PER BOSS FIGHT ===
        // Posiziona il giocatore in una zona centrale e sicura
        catX = WIDTH / 3; // Posizione centrale-sinistra (1/3 dello schermo)
        catY = GROUND_HEIGHT - 10; // Sul terreno
        velocityY = 0; // Reset velocità verticale
        onGround = true; // Assicurati che sia a terra
        canDoubleJump = true; // Reset doppio salto
        System.out.println("🐱 Player posizionato a: (" + (int)catX + ", " + (int)catY + ") per il boss fight");
        
        // Reset sistemi
        damageIndicators.clear();
        shieldActive = false;
        shieldTimer = 0;
        
        // Pulisci ostacoli normali
        obstacles.clear();
        bossProjectiles.clear();
        dragonBallSpheres.clear(); // Reset sfere Dragon Ball
        
        // Reset barriera boss
        bossBarrier.clear();
        barrierCreated = false;
        
        // Reset energia
        energy = MAX_ENERGY; // Inizia con energia piena per il boss fight
        
        // Cambia alla musica del boss finale
        switchToBossMusic();
    }
    
    private void gameOver() {
        currentState = GameState.GAME_OVER;
        // Torna alla musica del menu
        switchToMenuMusic();
    }
    
    private void victory() {
        try {
            System.out.println("🏆 VITTORIA! Passaggio alla schermata della vittoria...");
            currentState = GameState.VICTORY;
            
            // Attiva la musica della vittoria
            switchToVictoryMusic();
            
            System.out.println("✅ Transizione alla vittoria completata");
        } catch (Exception e) {
            System.out.println("❌ Errore nella vittoria: " + e.getMessage());
            e.printStackTrace();
            // Fallback: torna al menu in caso di errore
            resetToMenu();
        }
    }
    
    private void resetToMenu() {
        currentState = GameState.MENU;
        // Reset animazioni
        bodyBounce = 0;
        // Torna alla musica del menu
        switchToMenuMusic();
    }
    
    // === SISTEMA CARICAMENTO RISORSE ===
    
    private void loadResources() {
        System.out.println("🔍 Caricamento risorse grafiche...");
        
        // === CARICA IMMAGINE DEL GATTO ===
        try {
            catImage = new Image(getClass().getResourceAsStream("/images/player/cat.png"));
            if (catImage != null && !catImage.isError()) {
                System.out.println("✅ Immagine del gatto caricata correttamente!");
                System.out.println("📏 Dimensioni gatto: " + (int)catImage.getWidth() + "x" + (int)catImage.getHeight());
            } else {
                System.out.println("❌ Errore nel caricamento del gatto!");
                catImage = null;
            }
        } catch (Exception e) {
            System.out.println("❌ Fallimento caricamento gatto: " + e.getMessage());
            catImage = null;
        }
        
        // === CARICA SFONDI (OPZIONALI) ===
        // Prova prima PNG poi JPG
        try {
            menuBackgroundImage = new Image(getClass().getResourceAsStream("/images/backgrounds/menu_background.png"));
            if (menuBackgroundImage != null && !menuBackgroundImage.isError()) {
                System.out.println("✅ Sfondo menu PNG caricato!");
            } else {
                throw new Exception("PNG non trovato, provo JPG");
            }
        } catch (Exception e) {
            try {
                menuBackgroundImage = new Image(getClass().getResourceAsStream("/images/backgrounds/menu_background.jpg"));
                if (menuBackgroundImage != null && !menuBackgroundImage.isError()) {
                    System.out.println("✅ Sfondo menu JPG caricato!");
                } else {
                    menuBackgroundImage = null;
                }
            } catch (Exception e2) {
                System.out.println("⚠️ Sfondo menu non trovato (opzionale) - provato PNG e JPG");
                menuBackgroundImage = null;
            }
        }
        
        try {
            gameBackgroundImage = new Image(getClass().getResourceAsStream("/images/backgrounds/game_background.png"));
            if (gameBackgroundImage != null && !gameBackgroundImage.isError()) {
                System.out.println("✅ Sfondo gioco PNG caricato!");
            } else {
                throw new Exception("PNG non trovato, provo JPG");
            }
        } catch (Exception e) {
            try {
                gameBackgroundImage = new Image(getClass().getResourceAsStream("/images/backgrounds/game_background.jpg"));
                if (gameBackgroundImage != null && !gameBackgroundImage.isError()) {
                    System.out.println("✅ Sfondo gioco JPG caricato!");
                } else {
                    gameBackgroundImage = null;
                }
            } catch (Exception e2) {
                System.out.println("⚠️ Sfondo gioco non trovato (opzionale) - provato PNG e JPG");
                gameBackgroundImage = null;
            }
        }
        
        try {
            victoryBackgroundImage = new Image(getClass().getResourceAsStream("/images/backgrounds/victory_background.png"));
            if (victoryBackgroundImage != null && !victoryBackgroundImage.isError()) {
                System.out.println("✅ Sfondo vittoria PNG caricato!");
            } else {
                throw new Exception("PNG non trovato, provo JPG");
            }
        } catch (Exception e) {
            try {
                victoryBackgroundImage = new Image(getClass().getResourceAsStream("/images/backgrounds/victory_background.jpg"));
                if (victoryBackgroundImage != null && !victoryBackgroundImage.isError()) {
                    System.out.println("✅ Sfondo vittoria JPG caricato!");
                } else {
                    victoryBackgroundImage = null;
                }
            } catch (Exception e2) {
                System.out.println("⚠️ Sfondo vittoria non trovato (opzionale) - provato PNG e JPG");
                victoryBackgroundImage = null;
            }
        }
        
        // === CARICAMENTO SFONDO GAME OVER ===
        try {
            gameOverBackgroundImage = new Image(getClass().getResourceAsStream("/images/backgrounds/gameover_background.png"));
            if (gameOverBackgroundImage != null && !gameOverBackgroundImage.isError()) {
                System.out.println("✅ Sfondo Game Over PNG caricato!");
            } else {
                throw new Exception("PNG non trovato, provo JPG");
            }
        } catch (Exception e) {
            try {
                gameOverBackgroundImage = new Image(getClass().getResourceAsStream("/images/backgrounds/gameover_background.jpg"));
                if (gameOverBackgroundImage != null && !gameOverBackgroundImage.isError()) {
                    System.out.println("✅ Sfondo Game Over JPG caricato!");
                } else {
                    gameOverBackgroundImage = null;
                }
            } catch (Exception e2) {
                System.out.println("⚠️ Sfondo Game Over non trovato (opzionale) - provato PNG e JPG");
                gameOverBackgroundImage = null;
            }
        }
        
        // === CARICAMENTO SFONDO BOSS FINALE ===
        try {
            bossBackgroundImage = new Image(getClass().getResourceAsStream("/images/backgrounds/boss_background.png"));
            if (bossBackgroundImage != null && !bossBackgroundImage.isError()) {
                System.out.println("✅ Sfondo Boss PNG caricato!");
            } else {
                throw new Exception("PNG non trovato, provo JPG");
            }
        } catch (Exception e) {
            try {
                bossBackgroundImage = new Image(getClass().getResourceAsStream("/images/backgrounds/boss_background.jpg"));
                if (bossBackgroundImage != null && !bossBackgroundImage.isError()) {
                    System.out.println("✅ Sfondo Boss JPG caricato!");
                } else {
                    bossBackgroundImage = null;
                }
            } catch (Exception e2) {
                System.out.println("⚠️ Sfondo Boss non trovato (opzionale) - provato PNG e JPG");
                bossBackgroundImage = null;
            }
        }
        
        // === CARICAMENTO IMMAGINE BOSS ===
        try {
            bossImage = new Image(getClass().getResourceAsStream("/images/boss/amazon_courier_boss.png"));
            if (bossImage != null && !bossImage.isError()) {
                System.out.println("✅ Boss Corriere Amazon PNG caricato!");
            } else {
                throw new Exception("PNG non trovato, provo JPG");
            }
        } catch (Exception e) {
            try {
                bossImage = new Image(getClass().getResourceAsStream("/images/boss/amazon_courier_boss.jpg"));
                if (bossImage != null && !bossImage.isError()) {
                    System.out.println("✅ Boss Corriere Amazon JPG caricato!");
                } else {
                    bossImage = null;
                }
            } catch (Exception e2) {
                System.out.println("⚠️ Boss Corriere Amazon non trovato (opzionale) - provato PNG e JPG");
                bossImage = null;
            }
        }
        
        // === CARICAMENTO IMMAGINI PROIETTILI BOSS (OPZIONALI) ===
        try {
            bossProjectileNormalImage = new Image(getClass().getResourceAsStream("/images/boss/boss_projectile_normal.png"));
            if (bossProjectileNormalImage != null && !bossProjectileNormalImage.isError()) {
                System.out.println("✅ Proiettili boss singoli caricati!");
            } else {
                bossProjectileNormalImage = null;
            }
        } catch (Exception e) {
            System.out.println("⚠️ Proiettili boss singoli non trovati (opzionale) - usando blocchi normali");
            bossProjectileNormalImage = null;
        }
        
        try {
            bossProjectileDoubleImage = new Image(getClass().getResourceAsStream("/images/boss/boss_projectile_double.png"));
            if (bossProjectileDoubleImage != null && !bossProjectileDoubleImage.isError()) {
                System.out.println("✅ Proiettili boss multipli caricati!");
            } else {
                bossProjectileDoubleImage = null;
            }
        } catch (Exception e) {
            System.out.println("⚠️ Proiettili boss multipli non trovati (opzionale) - usando blocchi doppi");
            bossProjectileDoubleImage = null;
        }
        
        // === CARICA TEXTURE BLOCCHI (OPZIONALI) ===
        try {
            blockNormalImage = new Image(getClass().getResourceAsStream("/images/blocks/block_normal.png"));
            if (blockNormalImage != null && !blockNormalImage.isError()) {
                System.out.println("✅ Texture blocco normale caricata!");
            } else {
                blockNormalImage = null;
            }
        } catch (Exception e) {
            System.out.println("⚠️ Texture blocco normale non trovata (opzionale)");
            blockNormalImage = null;
        }
        
        try {
            blockDoubleImage = new Image(getClass().getResourceAsStream("/images/blocks/block_double.png"));
            if (blockDoubleImage != null && !blockDoubleImage.isError()) {
                System.out.println("✅ Texture blocco doppio caricata!");
            } else {
                blockDoubleImage = null;
            }
        } catch (Exception e) {
            System.out.println("⚠️ Texture blocco doppio non trovata (opzionale)");
            blockDoubleImage = null;
        }
        
        System.out.println("🎨 Caricamento risorse completato!");
    }

    // === METODI DI SCALING ===
    
    private Font scaleFont(String family, FontWeight weight, double size) {
        return Font.font(family, weight, size * uniformScale);
    }
    
    private double scaleX(double value) {
        return value * scaleX;
    }
    
    private double scaleY(double value) {
        return value * scaleY;
    }
    
    private double scaleSize(double value) {
        return value * uniformScale;
    }

    // === SISTEMA AUDIO CON JAVAFX MEDIA ===
    
    private void initializeAudio() {
        try {
            // Carica la musica del menu
            String menuMusicPath = getClass().getResource("/audio/menu_music.mp3").toExternalForm();
            Media menuMedia = new Media(menuMusicPath);
            menuMusicPlayer = new MediaPlayer(menuMedia);
            menuMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop infinito
            menuMusicPlayer.setVolume(0.5); // Volume al 50%
            
            // Carica la musica del gioco
            String gameMusicPath = getClass().getResource("/audio/game_music.mp3").toExternalForm();
            Media gameMedia = new Media(gameMusicPath);
            gameMusicPlayer = new MediaPlayer(gameMedia);
            gameMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop infinito
            gameMusicPlayer.setVolume(0.5); // Volume al 50%
            
            // Carica la musica del boss finale
            String bossMusicPath = getClass().getResource("/audio/boss_music.mp3").toExternalForm();
            Media bossMedia = new Media(bossMusicPath);
            bossMusicPlayer = new MediaPlayer(bossMedia);
            bossMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop infinito
            bossMusicPlayer.setVolume(0.6); // Volume leggermente più alto per l'epicità
            
            // Carica la musica della vittoria
            try {
                String victoryMusicPath = getClass().getResource("/audio/victory_music.mp3").toExternalForm();
                Media victoryMedia = new Media(victoryMusicPath);
                victoryMusicPlayer = new MediaPlayer(victoryMedia);
                victoryMusicPlayer.setCycleCount(1); // Suona una sola volta (non loop infinito)
                victoryMusicPlayer.setVolume(0.7); // Volume alto per celebrare la vittoria
                System.out.println("✅ Musica della vittoria caricata!");
            } catch (Exception victoryException) {
                System.out.println("⚠️ Musica della vittoria non trovata (opzionale) - il gioco continuerà normalmente");
                victoryMusicPlayer = null;
            }
            
            // Inizia con la musica del menu
            currentMusicPlayer = menuMusicPlayer;
            if (isMusicEnabled) {
                currentMusicPlayer.play();
            }
            
            System.out.println("🎵 Sistema audio: INIZIALIZZATO CORRETTAMENTE!");
            System.out.println("🔊 Musica del menu, gioco, boss e vittoria caricate!");
            
        } catch (Exception e) {
            System.out.println("❌ Errore nell'inizializzazione audio: " + e.getMessage());
            System.out.println("💡 Assicurati che i file audio siano nella cartella resources/audio/");
            System.out.println("📂 File richiesti: menu_music.mp3, game_music.mp3, boss_music.mp3, victory_music.mp3");
        }
    }
    
    private void toggleMusic() {
        isMusicEnabled = !isMusicEnabled;
        
        if (isMusicEnabled) {
            if (currentMusicPlayer != null) {
                currentMusicPlayer.play();
            }
            System.out.println("🎵 Musica attivata");
        } else {
            if (currentMusicPlayer != null) {
                currentMusicPlayer.pause();
            }
            System.out.println("🔇 Musica disattivata");
        }
    }
    
    private void switchToMenuMusic() {
        if (currentMusicPlayer != null) {
            currentMusicPlayer.stop();
        }
        currentMusicPlayer = menuMusicPlayer;
        if (isMusicEnabled && currentMusicPlayer != null) {
            currentMusicPlayer.play();
        }
    }
    
    private void switchToGameMusic() {
        if (currentMusicPlayer != null) {
            currentMusicPlayer.stop();
        }
        currentMusicPlayer = gameMusicPlayer;
        if (isMusicEnabled && currentMusicPlayer != null) {
            currentMusicPlayer.play();
        }
    }
    
    private void switchToBossMusic() {
        if (currentMusicPlayer != null) {
            currentMusicPlayer.stop();
        }
        currentMusicPlayer = bossMusicPlayer;
        if (isMusicEnabled && currentMusicPlayer != null) {
            currentMusicPlayer.play();
        }
        System.out.println("🎼 Musica del boss finale attivata!");
    }
    
    private void stopAllMusic() {
        if (menuMusicPlayer != null) {
            menuMusicPlayer.stop();
        }
        if (gameMusicPlayer != null) {
            gameMusicPlayer.stop();
        }
        if (bossMusicPlayer != null) {
            bossMusicPlayer.stop();
        }
        if (victoryMusicPlayer != null) {
            victoryMusicPlayer.stop();
        }
    }
    
    private void switchToVictoryMusic() {
        try {
            if (currentMusicPlayer != null) {
                currentMusicPlayer.stop();
            }
            currentMusicPlayer = victoryMusicPlayer;
            if (isMusicEnabled && currentMusicPlayer != null) {
                currentMusicPlayer.play();
                System.out.println("🏆 Musica della vittoria attivata!");
            } else if (victoryMusicPlayer == null) {
                System.out.println("⚠️ Musica della vittoria non disponibile - continuo in silenzio");
            }
        } catch (Exception e) {
            System.out.println("❌ Errore nell'attivazione musica vittoria: " + e.getMessage());
        }
    }
    
    private void toggleFullscreen() {
        if (gameStage != null) {
            gameStage.setFullScreen(!gameStage.isFullScreen());
            System.out.println("🖥️ Fullscreen: " + (gameStage.isFullScreen() ? "ON" : "OFF"));
            System.out.println("💡 Usa ESC o U per cambiare modalità schermo");
        }
    }
    
    // === CLASSE BOSS PROJECTILE ===
    private class BossProjectile {
        private double x, y;
        private double velocityX, velocityY;
        private double size;
        private int type; // 0 = singolo, 1 = doppio, 2 = triplo, 3 = impilato
        private boolean isDestroyed = false;
        private double trackingTimer = 0.0; // Timer per limitare il tracking
        private final double TRACKING_DURATION = 1.5; // 1.5 secondi di tracking dinamico
        
        public BossProjectile(double startX, double startY, double targetX, double targetY, int projectileType) {
            this.x = startX;
            this.y = startY;
            this.type = projectileType;
            
            // Velocità iniziale verso il target
            double deltaX = targetX - startX;
            double deltaY = targetY - startY;
            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
            
            // Velocità base aumentata per rendere più pericolosi
            double speed = 400; // Aumentata da 350 a 400
            
            // Direzione iniziale
            this.velocityX = (deltaX / distance) * speed;
            this.velocityY = (deltaY / distance) * speed; // Rimuove abs() per permettere movimento verso l'alto
            
            // Dimensione in base al tipo
            this.size = 40 + (type * 15);
        }
        
        public void update(double dt) {
            if (!isDestroyed) {
                trackingTimer += dt;
                
                // === TRACKING DINAMICO MIGLIORATO CON PREDIZIONE ===
                if (trackingTimer < TRACKING_DURATION) {
                    // Predizione del movimento del giocatore per un targeting più preciso
                    double predictedCatX = catX + (leftPressed ? -catMoveSpeed * 0.3 : rightPressed ? catMoveSpeed * 0.3 : 0);
                    double predictedCatY = catY + (velocityY * 0.2); // Predizione basata sulla velocità verticale
                    
                    // Calcola direzione verso la posizione predetta del giocatore
                    double deltaX = (predictedCatX + 25) - x; // Centro del gatto predetto
                    double deltaY = (predictedCatY - 25) - y; // Centro verticale predetto
                    double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                    
                    if (distance > 0) {
                        // Velocità di tracking aumentata per tipi di proiettili più avanzati
                        double trackingSpeed = 340 + (type * 25); // Da 340 a 415 in base al tipo
                        
                        // Aggiorna velocità per puntare verso la posizione predetta
                        velocityX = (deltaX / distance) * trackingSpeed;
                        velocityY = (deltaY / distance) * trackingSpeed;
                    }
                    
                    // Gravità minima durante il tracking attivo
                    velocityY += GRAVITY * 0.05; 
                } else {
                    // Dopo il tracking, movimento balistico naturale
                    velocityY += GRAVITY * 0.7; // Gravità normale dopo il tracking
                }
                
                // Movimento con velocità aggiornata
                x += velocityX * dt;
                y += velocityY * dt;
            }
        }
        
        public void render(GraphicsContext gc) {
            if (isDestroyed) return;
            
            // === PRIORITÀ 1: IMMAGINI DEDICATE DEI PROIETTILI BOSS ===
            Image dedicatedProjectileImage = (type >= 1) ? bossProjectileDoubleImage : bossProjectileNormalImage;
            
            if (dedicatedProjectileImage != null && !dedicatedProjectileImage.isError()) {
                // Usa le immagini dedicate per i proiettili del boss
                double drawX = x - size/2;
                double drawY = y - size/2;
                gc.drawImage(dedicatedProjectileImage, drawX, drawY, size, size);
                
                // Indicatore del tipo di proiettile
                gc.setFill(Color.YELLOW);
                gc.setFont(Font.font("Arial", FontWeight.BOLD, size * 0.2));
                String symbol = switch(type) {
                    case 0 -> "•";
                    case 1 -> "••";
                    case 2 -> "•••";
                    case 3 -> "⬆";
                    default -> "?";
                };
                gc.fillText(symbol, x - size/6, y - size/3);
                return; // Esce qui se l'immagine dedicata è disponibile
            }
            
            // === PRIORITÀ 2: USA LE STESSE IMMAGINI DEI PACCHI DEL GIOCO ===
            Image gameBlockImage = (type >= 1) ? blockDoubleImage : blockNormalImage;
            
            if (gameBlockImage != null && !gameBlockImage.isError()) {
                // Usa le immagini dei pacchi del gioco normale per i proiettili del boss
                double drawX = x - size/2;
                double drawY = y - size/2;
                gc.drawImage(gameBlockImage, drawX, drawY, size, size);
                
                // Aggiungi un indicatore del tipo di proiettile sopra
                gc.setFill(Color.YELLOW);
                gc.setFont(Font.font("Arial", FontWeight.BOLD, size * 0.2));
                String symbol = switch(type) {
                    case 0 -> "•";
                    case 1 -> "••";
                    case 2 -> "•••";
                    case 3 -> "⬆";
                    default -> "?";
                };
                gc.fillText(symbol, x - size/6, y - size/3);
                return; // Esce qui se l'immagine del gioco è disponibile
            }
            
            // === PRIORITÀ 3: FALLBACK PROCEDURALE SE NON CI SONO IMMAGINI ===
            // Disegna il proiettile come un pacco Amazon procedurale
            Color packageColor = switch(type) {
                case 0 -> Color.rgb(139, 90, 43); // Marrone normale
                case 1 -> Color.rgb(100, 60, 30); // Marrone più scuro
                case 2 -> Color.rgb(180, 120, 60); // Marrone chiaro
                case 3 -> Color.rgb(160, 100, 50); // Marrone medio
                default -> Color.BROWN;
            };
            
            gc.setFill(packageColor);
            gc.fillRect(x - size/2, y - size/2, size, size);
            
            // Bordo scuro
            gc.setStroke(Color.rgb(80, 50, 20));
            gc.setLineWidth(2);
            gc.strokeRect(x - size/2, y - size/2, size, size);
            
            // Logo Amazon semplificato
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, size * 0.3));
            gc.fillText("📦", x - size/4, y + size/6);
            
            // Indica il tipo con simboli
            gc.setFill(Color.YELLOW);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, size * 0.2));
            String symbol = switch(type) {
                case 0 -> "•";
                case 1 -> "••";
                case 2 -> "•••";
                case 3 -> "⬆";
                default -> "?";
            };
            gc.fillText(symbol, x - size/6, y - size/3);
        }
        
        public boolean intersects(double catX, double catY, double catWidth, double catHeight) {
            if (isDestroyed) return false;
            return x - size/2 < catX + catWidth && 
                   x + size/2 > catX && 
                   y - size/2 < catY + catHeight && 
                   y + size/2 > catY;
        }
        
        public boolean isOffScreen() {
            return x < -size || x > WIDTH + size || y > HEIGHT + size;
        }
        
        public void destroy() {
            isDestroyed = true;
        }
        
        public boolean isDestroyed() {
            return isDestroyed;
        }
    }
    
    // === CLASSE ONDA ENERGETICA COME PROIETTILE ===
    private class EnergyBlast {
        private double x, y;
        private double velocityX, velocityY;
        private double size;
        private boolean isDestroyed = false;
        private double animationTimer = 0;
        
        public EnergyBlast(double startX, double startY) {
            this.x = startX;
            this.y = startY;
            this.velocityX = 800; // Velocità orizzontale alta
            this.velocityY = -100; // Leggera curvatura verso l'alto
            this.size = 40;
        }
        
        public void update(double dt) {
            if (!isDestroyed) {
                x += velocityX * dt;
                y += velocityY * dt;
                animationTimer += dt * 10;
                
                // Controlla collisione con il boss - AREA INGRANDITA per tutto il corpo
                // Boss dimensions: width=120, height=160 (fallback) o width=150, height=200 (con immagine)
                double bossWidth = 150; // Larghezza massima
                double bossHeight = 200; // Altezza massima
                
                // Boss è disegnato a terra: bossGroundY - bossHeight, quindi il centro è a bossY - bossHeight/2
                double bossCenterY = bossY - bossHeight/2;
                
                if (Math.abs(x - bossX) < (bossWidth/2 + size/2) && 
                    Math.abs(y - bossCenterY) < (bossHeight/2 + size/2)) {
                    // Colpisce il boss!
                    bossHealth -= 100;
                    damageIndicators.add(new DamageIndicator(bossX, bossY - 100, "-100"));
                    System.out.println("💥 BOSS COLPITO! Vita rimanente: " + bossHealth);
                    isDestroyed = true;
                    
                    if (bossHealth <= 0) {
                        System.out.println("💥 Boss defeated by energy blast - calling victory()");
                        victory(); // Vittoria immediata
                    }
                }
            }
        }
        
        public void render(GraphicsContext gc) {
            if (isDestroyed) return;
            
            // Effetto onda energetica stile Dragon Ball
            double phase = Math.sin(animationTimer) * 0.3 + 1.0;
            
            // Core centrale luminoso
            RadialGradient coreGradient = new RadialGradient(
                0, 0, 0.5, 0.5, 1.0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(255, 255, 255, 0.9)),
                new Stop(0.3, Color.rgb(100, 200, 255, 0.8)),
                new Stop(0.7, Color.rgb(50, 150, 255, 0.6)),
                new Stop(1, Color.rgb(0, 100, 200, 0.2))
            );
            gc.setFill(coreGradient);
            gc.fillOval(x - size/2 * phase, y - size/2 * phase, size * phase, size * phase);
            
            // Anello esterno pulsante
            gc.setStroke(Color.rgb(255, 255, 150, 0.7));
            gc.setLineWidth(4);
            gc.strokeOval(x - size/2 * phase * 1.2, y - size/2 * phase * 1.2, 
                         size * phase * 1.2, size * phase * 1.2);
            
            // Particelle di energia
            for (int i = 0; i < 8; i++) {
                double angle = animationTimer + i * 45;
                double particleDistance = size * 0.6;
                double particleX = x + Math.cos(Math.toRadians(angle)) * particleDistance;
                double particleY = y + Math.sin(Math.toRadians(angle)) * particleDistance;
                
                gc.setFill(Color.rgb(255, 255, 200, 0.8));
                gc.fillOval(particleX - 2, particleY - 2, 4, 4);
            }
        }
        
        public boolean isOffScreen() {
            return x > WIDTH + size || y < -size || y > HEIGHT + size;
        }
        
        public void destroy() {
            isDestroyed = true;
        }
        
        public boolean isDestroyed() {
            return isDestroyed;
        }
    }
    
    // === CLASSE INDICATORE DANNO ===
    private class DamageIndicator {
        private double x, y;
        private String text;
        private double timer = 0;
        private final double DURATION = 2.0;
        
        public DamageIndicator(double x, double y, String text) {
            this.x = x;
            this.y = y;
            this.text = text;
        }
        
        public void update(double dt) {
            timer += dt;
            y -= 50 * dt; // Sale verso l'alto
        }
        
        public void render(GraphicsContext gc) {
            if (timer < DURATION) {
                double alpha = 1.0 - (timer / DURATION);
                gc.setFill(Color.rgb(255, 100, 100, alpha));
                gc.setFont(Font.font("Arial", FontWeight.BOLD, 30));
                gc.fillText(text, x, y);
            }
        }
        
        public boolean isExpired() {
            return timer >= DURATION;
        }
    }
    
    // === METODO PER AGGIORNARE LE SFERE DRAGON BALL ===
    private void updateDragonBallSpheres(double dt) {
        Iterator<DragonBallSphere> it = dragonBallSpheres.iterator();
        while (it.hasNext()) {
            DragonBallSphere sphere = it.next();
            sphere.update(dt);
            
            // === COLLISIONE CON IL BOSS (solo durante boss fight) ===
            if (currentState == GameState.BOSS_FIGHT) {
                // Boss dimensions: width=150, height=200 (con immagine)
                double bossWidth = 150; 
                double bossHeight = 200; 
                double bossCenterY = bossY - bossHeight/2;
                
                if (sphere.intersects(bossX - bossWidth/2, bossCenterY - bossHeight/2, bossWidth, bossHeight)) {
                    // Danneggia il boss
                    double damage = 100; // Danno delle sfere Dragon Ball
                    bossHealth -= damage;
                    
                    // Indicatore danno
                    damageIndicators.add(new DamageIndicator(bossX, bossY - 100, "-" + (int)damage));
                    
                    // Distruggi la sfera con esplosione
                    sphere.destroy();
                    
                    System.out.println("💥 Sfera Dragon Ball colpisce il boss! Danno: " + (int)damage + 
                                     " - Vita boss: " + (int)bossHealth + "/" + (int)MAX_BOSS_HEALTH);
                    
                    // Controlla se il boss è stato sconfitto
                    if (bossHealth <= 0) {
                        victory();
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
    
    // === CLASSE SFERA DRAGON BALL ===
    private class DragonBallSphere {
        private double x, y;
        private double velocityX;
        private double size = 40;
        private boolean isDestroyed = false;
        private double animationTimer = 0;
        private double explosionTimer = 0;
        private final double EXPLOSION_DURATION = 0.5;
        private boolean exploding = false;
        
        public DragonBallSphere(double startX, double startY) {
            this.x = startX;
            this.y = startY;
            this.velocityX = 800; // Velocità molto alta per attraversare lo schermo
        }
        
        public void update(double dt) {
            animationTimer += dt * 10; // Per effetti rotanti
            
            if (exploding) {
                explosionTimer += dt;
                if (explosionTimer >= EXPLOSION_DURATION) {
                    isDestroyed = true;
                }
            } else {
                // Movimento orizzontale costante
                x += velocityX * dt;
            }
        }
        
        public void render(GraphicsContext gc) {
            if (isDestroyed) return;
            
            if (exploding) {
                // === EFFETTO ESPLOSIONE ===
                double explosionProgress = explosionTimer / EXPLOSION_DURATION;
                double explosionRadius = size * (1 + explosionProgress * 3);
                double explosionAlpha = 1.0 - explosionProgress;
                
                // Esplosione principale
                RadialGradient explosionGradient = new RadialGradient(
                    0, 0, 0.5, 0.5, 1.0, true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.rgb(255, 255, 0, explosionAlpha)),
                    new Stop(0.5, Color.rgb(255, 150, 0, explosionAlpha * 0.7)),
                    new Stop(1, Color.rgb(255, 0, 0, 0))
                );
                gc.setFill(explosionGradient);
                gc.fillOval(x - explosionRadius/2, y - explosionRadius/2, explosionRadius, explosionRadius);
                
                // Particelle esplosive
                for (int i = 0; i < 8; i++) {
                    double angle = i * 45 + animationTimer * 5;
                    double particleDistance = explosionRadius * 0.6;
                    double particleX = x + Math.cos(Math.toRadians(angle)) * particleDistance;
                    double particleY = y + Math.sin(Math.toRadians(angle)) * particleDistance;
                    
                    gc.setFill(Color.rgb(255, 255, 0, explosionAlpha * 0.8));
                    gc.fillOval(particleX - 3, particleY - 3, 6, 6);
                }
            } else {
                // === SFERA DRAGON BALL NORMALE ===
                
                // Effetto pulsante
                double pulse = Math.sin(animationTimer) * 0.1 + 1.0;
                double currentSize = size * pulse;
                
                // Core centrale luminoso (giallo/dorato)
                RadialGradient coreGradient = new RadialGradient(
                    0, 0, 0.3, 0.3, 1.0, true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.rgb(255, 255, 255, 0.9)),
                    new Stop(0.3, Color.rgb(255, 255, 0, 0.8)),
                    new Stop(0.7, Color.rgb(255, 200, 0, 0.7)),
                    new Stop(1, Color.rgb(255, 150, 0, 0.4))
                );
                gc.setFill(coreGradient);
                gc.fillOval(x - currentSize/2, y - currentSize/2, currentSize, currentSize);
                
                // Anello esterno rotante
                gc.setStroke(Color.rgb(255, 255, 100, 0.8));
                gc.setLineWidth(3);
                double ringSize = currentSize * 1.2;
                gc.strokeOval(x - ringSize/2, y - ringSize/2, ringSize, ringSize);
                
                // Particelle orbitanti stile Dragon Ball
                for (int i = 0; i < 6; i++) {
                    double orbitAngle = animationTimer + i * 60;
                    double orbitRadius = currentSize * 0.7;
                    double particleX = x + Math.cos(Math.toRadians(orbitAngle)) * orbitRadius;
                    double particleY = y + Math.sin(Math.toRadians(orbitAngle)) * orbitRadius;
                    
                    gc.setFill(Color.rgb(255, 255, 200, 0.9));
                    gc.fillOval(particleX - 2, particleY - 2, 4, 4);
                }
                
                // Scia energetica dietro la sfera
                for (int trail = 1; trail <= 5; trail++) {
                    double trailX = x - (trail * 15);
                    double trailSize = currentSize * (1.0 - trail * 0.15);
                    double trailAlpha = 0.6 - (trail * 0.1);
                    
                    gc.setFill(Color.rgb(255, 200, 0, trailAlpha));
                    gc.fillOval(trailX - trailSize/2, y - trailSize/2, trailSize, trailSize);
                }
            }
        }
        
        public void destroy() {
            exploding = true;
            explosionTimer = 0;
        }
        
        public boolean isDestroyed() {
            return isDestroyed;
        }
        
        public boolean isOffScreen() {
            return x > WIDTH + size || exploding; // Considera off-screen anche se sta esplodendo
        }
        
        public boolean intersects(double objX, double objY, double objWidth, double objHeight) {
            if (exploding || isDestroyed) return false;
            
            // Collisione semplice rettangolo-cerchio
            double centerX = x;
            double centerY = y;
            double radius = size / 2;
            
            // Trova il punto più vicino del rettangolo al centro del cerchio
            double closestX = Math.max(objX, Math.min(centerX, objX + objWidth));
            double closestY = Math.max(objY, Math.min(centerY, objY + objHeight));
            
            // Calcola la distanza dal centro del cerchio al punto più vicino
            double distanceX = centerX - closestX;
            double distanceY = centerY - closestY;
            
            return (distanceX * distanceX + distanceY * distanceY) <= (radius * radius);
        }
    }
    
    // === METODO PER RENDERIZZARE IL MENU A TENDINA IMPOSTAZIONI ===
    private void renderSettingsDropdown(GraphicsContext gc) {
        double menuWidth = 350;
        double menuHeight = 350;
        
        // Calcola posizione del menu: se il pulsante è in basso, aprilo verso l'alto
        double menuX = settingsButtonX - menuWidth + settingsButtonWidth; // Allinea a destra del pulsante
        double menuY = settingsButtonY - menuHeight; // Apri sopra il pulsante
        
        // Assicurati che il menu non esca dai bordi dello schermo
        if (menuX < 0) menuX = 10; // Non uscire a sinistra
        if (menuX + menuWidth > WIDTH) menuX = WIDTH - menuWidth - 10; // Non uscire a destra
        if (menuY < 0) menuY = 10; // Non uscire in alto
        
        // Sfondo del menu a tendina
        gc.setFill(Color.rgb(0, 0, 0, 0.95));
        gc.fillRoundRect(menuX, menuY, menuWidth, menuHeight, 10, 10);
        
        // Bordo del menu
        gc.setStroke(Color.CYAN);
        gc.setLineWidth(2);
        gc.strokeRoundRect(menuX, menuY, menuWidth, menuHeight, 10, 10);
        
        // Titolo del menu
        gc.setFill(Color.CYAN);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        gc.fillText("🎮COMANDI DI GIOCO", menuX + 15, menuY + 30);
        
        // Linea di separazione
        gc.setStroke(Color.CYAN);
        gc.setLineWidth(1);
        gc.strokeLine(menuX + 15, menuY + 40, menuX + menuWidth - 15, menuY + 40);
        
        double currentY = menuY + 60;
        double lineHeight = 25;
        
        // === COMANDI ESSENZIALI ===
        
        // Comando SPAZIO per saltare
        gc.setFill(Color.YELLOW);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        gc.fillText("SPAZIO", menuX + 20, currentY);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        gc.fillText("→ Salto singolo", menuX + 75, currentY);
        currentY += lineHeight;
        
        // Comando doppio SPAZIO
        gc.setFill(Color.ORANGE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        gc.fillText("SPAZIO x2", menuX + 20, currentY);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        gc.fillText("→ Doppio salto", menuX + 95, currentY);
        currentY += lineHeight;
        
        // Comando C per sfera energetica
        gc.setFill(Color.CYAN);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        gc.fillText("C", menuX + 20, currentY);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        gc.fillText("→ Lancia Onda Energetica", menuX + 35, currentY);
        currentY += lineHeight;
        // Comando X per scudo
        gc.setFill(Color.GREEN);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        gc.fillText("X", menuX + 20, currentY);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        gc.fillText("→ Scudo (Boss Fight)", menuX + 35, currentY);
        currentY += lineHeight;
        
        // Comando frecce per movimento nel boss fight
        gc.setFill(Color.LIGHTCORAL);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        gc.fillText("← → (A/D)", menuX + 20, currentY);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        gc.fillText("→ Movimento Boss Fight", menuX + 90, currentY);
        currentY += lineHeight;
        
        // Comando M per toggle audio
        gc.setFill(Color.CYAN);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        gc.fillText("🎵 M", menuX + 20, currentY);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        String musicStatus = isMusicEnabled ? "Audio ON" : "Audio OFF";
        gc.fillText("→ " + musicStatus, menuX + 55, currentY);
        currentY += lineHeight;
        
        // Comando U/ESC per toggle fullscreen
        gc.setFill(Color.MAGENTA);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        gc.fillText("ESC / U", menuX + 20, currentY);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        String fullscreenStatus = gameStage != null && gameStage.isFullScreen() ? "Fullscreen ON" : "Fullscreen OFF";
        gc.fillText("→ " + fullscreenStatus, menuX + 75, currentY);
        currentY += lineHeight;

        // Istruzioni per chiudere
        gc.setFill(Color.GRAY);
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        gc.fillText("Clicca fuori dal menu per chiudere", menuX + 20, menuY + menuHeight - 15);
    }
}