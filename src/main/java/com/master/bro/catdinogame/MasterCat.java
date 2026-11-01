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
import java.util.function.LongSupplier;

public class MasterCat extends Application {
    // Dimensioni base del gioco (design di riferimento)
    static final int BASE_WIDTH = 1200;
    static final int BASE_HEIGHT = 600;
    
    // Dimensioni effettive dello schermo
    double screenWidth;
    double screenHeight;
    double scaleX;
    double scaleY;
    double uniformScale;
    
    // Dimensioni adattabili del gioco
    double WIDTH;
    double HEIGHT;
    
    static final double GRAVITY = 0.6;
    static final double JUMP_VELOCITY = -15;
    double GROUND_HEIGHT; // Ora variabile

    // Game States
    enum GameState { MENU, PLAYING, BOSS_FIGHT, PAUSED, GAME_OVER, VICTORY }
    GameState currentState = GameState.MENU;
    GameState pausedFrom = GameState.MENU;
    long pausedAtMillis;
    LongSupplier clock = System::currentTimeMillis;
    boolean reducedEffects = false;
    boolean resetFrameClock;
    
    // Punteggio finale per vincere
    static final int WINNING_SCORE = 10000;
    
    // Riferimento al Stage per controllo fullscreen
    Stage gameStage;

    double catX = 150;
    double catY = GROUND_HEIGHT;
    double velocityY = 0;
    boolean onGround = true;
    boolean canDoubleJump = false; // Doppio salto disponibile

    // Variabili per effetti grafici
    int score = 0;
    double gameSpeed = 1.0;
    
    // Variabili per animazione gatto (solo bodyBounce utilizzato)
    double bodyBounce = 0;
    double conveyorTravel;
    double runFramePhase;
    double landingTimer;
    boolean facingLeft;
    
    // Sistema energia e onda
    double energy = 0;
    final double MAX_ENERGY = 50;
    boolean waveActive = false;
    double waveTimer = 0;
    final double WAVE_DURATION = 3.0;
    
    // === SISTEMA SFERE ENERGETICHE LIVELLO NORMALE (DRAGON BALL STYLE) ===
    final ArrayList<DragonBallSphere> dragonBallSpheres = new ArrayList<>();
    
    // === SISTEMA AUDIO CON JAVAFX MEDIA ===
    
    // === SISTEMA MENU A TENDINA IMPOSTAZIONI ===
    boolean settingsMenuOpen = false;
    double settingsButtonX; // Calcolato dinamicamente in basso a destra
    double settingsButtonY; // Calcolato dinamicamente in basso a destra
    double settingsButtonWidth = 160;
    double settingsButtonHeight = 40;
    
    // === SISTEMA IMMAGINI ===
    
    // === VARIABILI BOSS FINALE ===
    double bossTimer = 0;
    final double BOSS_DURATION = 120.0; // 2 minuti di boss fight
    double bossX;
    double bossY;
    double bossAnimationTimer = 0;
    double lastBossAttackAt = 0;
    double bossWarningEndsAt = 0;
    int pendingBossAttackType = -1;
    final ArrayList<BossProjectile> bossProjectiles = new ArrayList<>();
    
    // === NUOVE VARIABILI PER BOSS FIGHT MIGLIORATO ===
    double bossHealth = 2000.0;
    final double MAX_BOSS_HEALTH = 2000.0;
    double catMoveSpeed = 300.0; // Velocità movimento orizzontale nel boss fight
    boolean leftPressed = false;
    boolean rightPressed = false;
    
    // === SISTEMA SCUDO ===
    boolean shieldActive = false;
    double shieldTimer = 0;
    final double SHIELD_DURATION = 5.0; // 5 secondi di scudo
    final double SHIELD_ENERGY_COST = 25.0; // Costa metà energia (25/50)
    
    // === INDICATORI DANNO ===
    final ArrayList<DamageIndicator> damageIndicators = new ArrayList<>();
    
    // === SISTEMA BARRIERA BOSS ===
    final ArrayList<BarrierBlock> bossBarrier = new ArrayList<>();
    boolean barrierCreated = false;

    final ArrayList<Obstacle> obstacles = new ArrayList<>();
    long lastObstacleTime = 0;

    final GameAssets assets = new GameAssets();
    final GameAudio audio = new GameAudio();
    private final GameRenderer renderer = new GameRenderer(this);
    final GameUpdate updater = new GameUpdate(this);

    long nowMillis() { return clock.getAsLong(); }

    @Override
    public void start(Stage primaryStage) {
        this.gameStage = primaryStage; // Memorizza il riferimento al Stage
        
        // === CALCOLO DIMENSIONI SCHERMO E SCALING ===
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
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
        root.setStyle("-fx-background-color: black;");
        Canvas canvas = new Canvas(WIDTH, HEIGHT); // Usa le dimensioni dello schermo
        root.getChildren().add(canvas);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        // Carica le risorse grafiche
        assets.loadResources();
        
        // Inizializza sistema audio con JavaFX Media
        audio.initializeAudio();
        
        // Gestisci la chiusura dell'applicazione
        primaryStage.setOnCloseRequest(e -> {
            audio.stopAllMusic();
        });

        // Controlli basati sullo stato del gioco
        GameInput input = new GameInput(this);
        input.install(scene);
        primaryStage.focusedProperty().addListener((observation, wasFocused, isFocused) -> {
            if (!isFocused) input.onFocusLost();
        });

        new AnimationTimer() {
            private long lastTime = 0;
            @Override
            public void handle(long now) {
                if (lastTime == 0 || resetFrameClock) {
                    lastTime = now;
                    resetFrameClock = false;
                }
                double dt = (now - lastTime) / 1e9;
                lastTime = now;

                updater.update(dt);
                renderer.render(gc);
            }
        }.start();

        primaryStage.show();
        canvas.requestFocus();
    }

    // Metodi per gestire gli stati del gioco
    void startGame() {
        currentState = GameState.PLAYING;
        resetFrameClock = true;
        pausedFrom = GameState.MENU;
        // Reset delle variabili del gioco
        score = 0;
        gameSpeed = 1.0;
        updater.resetProgression();
        conveyorTravel = 0;
        runFramePhase = 0;
        landingTimer = 0;
        facingLeft = false;
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
        lastObstacleTime = nowMillis();
        
        // Cambia alla musica del gioco
        audio.switchToGameMusic();
        audio.setGameplayPaused(false);
        System.out.println("🚀 Gioco avviato! Buona fortuna!");
    }
    
    void startBossFight() {
        currentState = GameState.BOSS_FIGHT;
        resetFrameClock = true;
        pausedFrom = GameState.MENU;
        System.out.println("⚔️ BOSS FIGHT INIZIATO! Sopravvivi per 2 minuti!");
        
        // Inizializza variabili del boss
        bossTimer = 0;
        bossHealth = MAX_BOSS_HEALTH; // Reset vita boss
        bossX = WIDTH - 200; // Posizione del boss sul lato destro
        bossY = GROUND_HEIGHT; // STESSO LIVELLO DEL GIOCATORE
        bossAnimationTimer = 0;
        runFramePhase = 0;
        landingTimer = 0;
        facingLeft = false;
        lastBossAttackAt = 0;
        bossWarningEndsAt = 0;
        pendingBossAttackType = -1;
        
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
        audio.switchToBossMusic();
        audio.setGameplayPaused(false);
    }
    
    void gameOver() {
        currentState = GameState.GAME_OVER;
        // Torna alla musica del menu
        audio.switchToMenuMusic();
        audio.setGameplayPaused(false);
    }
    
    void victory() {
        try {
            System.out.println("🏆 VITTORIA! Passaggio alla schermata della vittoria...");
            currentState = GameState.VICTORY;
            
            // Attiva la musica della vittoria
            audio.switchToVictoryMusic();
            audio.setGameplayPaused(false);
            
            System.out.println("✅ Transizione alla vittoria completata");
        } catch (Exception e) {
            System.out.println("❌ Errore nella vittoria: " + e.getMessage());
            e.printStackTrace();
            // Fallback: torna al menu in caso di errore
            resetToMenu();
        }
    }
    
    void resetToMenu() {
        currentState = GameState.MENU;
        pausedFrom = GameState.MENU;
        settingsMenuOpen = false;
        leftPressed = false;
        rightPressed = false;
        // Reset animazioni
        bodyBounce = 0;
        // Torna alla musica del menu
        audio.switchToMenuMusic();
        audio.setGameplayPaused(false);
    }

    void pauseGame() {
        if (currentState != GameState.PLAYING && currentState != GameState.BOSS_FIGHT) return;
        pausedFrom = currentState;
        currentState = GameState.PAUSED;
        resetFrameClock = true;
        pausedAtMillis = nowMillis();
        leftPressed = false;
        rightPressed = false;
        audio.setGameplayPaused(true);
    }

    void resumeGame() {
        if (currentState != GameState.PAUSED) return;
        if (pausedFrom == GameState.PLAYING) {
            lastObstacleTime += Math.max(0, nowMillis() - pausedAtMillis);
        }
        currentState = pausedFrom;
        resetFrameClock = true;
        pausedFrom = GameState.MENU;
        audio.setGameplayPaused(false);
    }
    
    // === SISTEMA CARICAMENTO RISORSE ===
    
    // === METODI DI SCALING ===
    
    Font scaleFont(String family, FontWeight weight, double size) {
        return Font.font(family, weight, size * uniformScale);
    }
    
    double scaleX(double value) {
        return value * scaleX;
    }
    
    double scaleY(double value) {
        return value * scaleY;
    }
    
    double scaleSize(double value) {
        return value * uniformScale;
    }

    // === SISTEMA AUDIO CON JAVAFX MEDIA ===
    
    void toggleFullscreen() {
        if (gameStage != null) {
            gameStage.setFullScreen(!gameStage.isFullScreen());
            System.out.println("🖥️ Fullscreen: " + (gameStage.isFullScreen() ? "ON" : "OFF"));
            System.out.println("💡 Usa ESC o U per cambiare modalità schermo");
        }
    }
    
    // === CLASSE BOSS PROJECTILE ===
    class BossProjectile {
        double x, y;
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
                    double predictedCatY = PlayerHitbox.boss(MasterCat.this).centerY() + (velocityY * 0.2);
                    
                    // Calcola direzione verso la posizione predetta del giocatore
                    double deltaX = predictedCatX - x;
                    double deltaY = predictedCatY - y;
                    double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                    
                    if (distance > 0) {
                        // Velocità di tracking aumentata per tipi di proiettili più avanzati
                        double trackingSpeed = 340 + (type * 25); // Da 340 a 415 in base al tipo
                        
                        // Aggiorna velocità per puntare verso la posizione predetta
                        velocityX = (deltaX / distance) * trackingSpeed;
                        velocityY = (deltaY / distance) * trackingSpeed;
                    }
                    
                    // Gravità minima durante il tracking attivo
                    velocityY += GRAVITY * 0.05 * dt * 60; 
                } else {
                    // Dopo il tracking, movimento balistico naturale
                    velocityY += GRAVITY * 0.7 * dt * 60; // Gravità normale dopo il tracking
                }
                
                // Movimento con velocità aggiornata
                x += velocityX * dt;
                double acceleration = GRAVITY * (trackingTimer < TRACKING_DURATION ? 0.05 : 0.7) * 60;
                y += velocityY * dt - acceleration * dt * (dt - 1.0 / 60) / 2;
            }
        }
        
        public void render(GraphicsContext gc) {
            if (isDestroyed) return;
            
            // === PRIORITÀ 1: IMMAGINI DEDICATE DEI PROIETTILI BOSS ===
            Image dedicatedProjectileImage = (type >= 1) ? assets.bossProjectileDoubleImage : assets.bossProjectileNormalImage;
            
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
            Image gameBlockImage = (type >= 1) ? assets.blockDoubleImage : assets.blockNormalImage;
            
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

        public boolean intersects(DragonBallSphere sphere) {
            return !isDestroyed && sphere.intersects(x - size/2, y - size/2, size, size);
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
    class EnergyBlast {
        double x, y;
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
    class DamageIndicator {
        double x, y;
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
            if (!reducedEffects) y -= 50 * dt;
        }
        
        public void render(GraphicsContext gc) {
            if (timer < DURATION) {
                double alpha = reducedEffects ? 1.0 : 1.0 - (timer / DURATION);
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
    // === CLASSE SFERA DRAGON BALL ===
    class DragonBallSphere {
        double x, y;
        private double velocityX;
        double size = 40;
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

            if (reducedEffects) {
                if (exploding) {
                    gc.setStroke(Color.rgb(236, 190, 95));
                    gc.setLineWidth(2);
                    gc.strokeOval(x - size/2, y - size/2, size, size);
                } else {
                    gc.setFill(Color.rgb(236, 190, 95));
                    gc.fillOval(x - size/2, y - size/2, size, size);
                }
                return;
            }
            
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
            if (exploding || isDestroyed) return;
            exploding = true;
            explosionTimer = 0;
        }
        
        public boolean isDestroyed() {
            return isDestroyed;
        }
        
        public boolean isOffScreen() {
            return !exploding && x > WIDTH + size;
        }
        
        boolean isExploding() { return exploding; }

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
    @Override
    public void stop() {
        audio.dispose();
    }
}
