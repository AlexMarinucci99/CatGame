package com.master.bro.catdinogame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;
import javafx.scene.effect.BlendMode;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.image.Image;
import com.master.bro.catdinogame.MasterCat.BossProjectile;
import com.master.bro.catdinogame.MasterCat.DamageIndicator;
import com.master.bro.catdinogame.MasterCat.DragonBallSphere;

/** Draws the game screens and their state-dependent overlays. */
final class GameRenderer {
    private final MasterCat game;
    GameRenderer(MasterCat game) { this.game = game; }

    void render(GraphicsContext gc) {
        switch (game.state()) {
            case MENU:
                renderMenu(gc);
                break;
            case PLAYING:
                renderGame(gc);
                break;
            case PLATFORMER:
            case LEVEL_TRANSITION:
            case BOSS_TRANSITION:
                PlatformerRenderer.render(gc, game);
                break;
            case BOSS_FIGHT:
                renderBossFight(gc);
                break;
            case PAUSED:
                renderPaused(gc);
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
        
        if (game.assets.menuBackgroundImage != null && !game.assets.menuBackgroundImage.isError()) {
            // Usa l'immagine di sfondo personalizzata, scalandola per coprire tutto lo schermo
            gc.drawImage(game.assets.menuBackgroundImage, 0, 0, game.WIDTH, game.HEIGHT);
        } else {
            // Fallback: sfondo spaziale generato proceduralmente
            LinearGradient spaceGradient = new LinearGradient(
                0, 0, 0, game.HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(20, 10, 50)),    // Blu-viola scuro in alto
                new Stop(0.5, Color.rgb(60, 10, 90)),  // Viola medio al centro
                new Stop(1, Color.rgb(10, 5, 30))      // Quasi nero con tinta viola in basso
            );
            gc.setFill(spaceGradient);
            gc.fillRect(0, 0, game.WIDTH, game.HEIGHT);
            
            // Stelle piccole e numerose con distribuzione migliorata
            gc.setFill(Color.rgb(255, 255, 255, 0.9));
            for (int i = 0; i < 200; i++) {
                double starX = (i * 127) % game.WIDTH; // Distribuzione pseudo-casuale
                double starY = (i * 83) % game.HEIGHT;
                double starSize = 0.8 + (i % 4) * 0.5;
                gc.fillOval(starX, starY, starSize, starSize);
            }
            
            // Stelle medie più luminose
            gc.setFill(Color.rgb(255, 255, 220));
            for (int i = 0; i < 30; i++) {
                double starX = (i * 191) % game.WIDTH;
                double starY = (i * 107) % game.HEIGHT;
                double starSize = 2 + (i % 3);
                gc.fillOval(starX, starY, starSize, starSize);
            }
            
            // Stelle brillanti con bagliore
            for (int i = 0; i < 15; i++) {
                double starX = (i * 239) % game.WIDTH;
                double starY = (i * 167) % game.HEIGHT;
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
            gc.fillOval(game.WIDTH * 0.7, game.HEIGHT * 0.2, 400, 300);
        }

        if (!game.settingsMenuOpen) {
            // === TITOLO "MasterCat" PERFETTAMENTE CENTRATO E SCALATO ===
            String title = "MasterCat";
            Font titleFont = game.scaleFont("Impact", FontWeight.BOLD, 90);

            // Calcola la larghezza esatta del testo per centrarlo perfettamente
            gc.setFont(titleFont);
            double titleWidth = titleFont.getSize() * title.length() * 0.6; // Stima approssimativa
            double titleX = (game.WIDTH - titleWidth) / 2; // Centro perfetto
            double titleY = game.HEIGHT/2 - game.scaleY(200); // SPOSTATO PIÙ IN ALTO: da -40 a -200

            // Una sola ombra mantiene il titolo leggibile sullo sfondo illustrato.
            double shadowOffset = game.scaleSize(3);
            gc.setFill(Color.rgb(0, 0, 0, 0.65));
            gc.fillText(title, titleX + shadowOffset, titleY + shadowOffset);

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

            gc.setStroke(Color.rgb(57, 36, 22, 0.8));
            gc.setLineWidth(game.scaleSize(1.5));
            gc.strokeText(title, titleX, titleY);

            // === ISTRUZIONI PERFETTAMENTE CENTRATE E SCALATE ===
            String startText = "Premi SPAZIO per avviare";
            Font startFont = game.scaleFont("Arial", FontWeight.BOLD, 35);
            gc.setFont(startFont);
            double startTextWidth = startFont.getSize() * startText.length() * 0.6; // Stima per centratura
            double startTextX = (game.WIDTH - startTextWidth) / 2; // Centro perfetto

            gc.setFill(Color.WHITE);
            gc.fillText(startText, startTextX, game.HEIGHT/2 + game.scaleY(50));
        }

        // === PULSANTE IMPOSTAZIONI ===
        // Calcola posizione in basso a destra con margine
        game.settingsButtonX = game.WIDTH - game.settingsButtonWidth - 30; // 30 pixel di margine dal bordo destro
        game.settingsButtonY = game.HEIGHT - game.settingsButtonHeight - 30; // 30 pixel di margine dal bordo inferiore
        
        gc.setFill(Color.rgb(20, 26, 35, 0.93));
        gc.fillRoundRect(game.settingsButtonX, game.settingsButtonY,
                game.settingsButtonWidth, game.settingsButtonHeight, 12, 12);
        gc.setStroke(game.settingsMenuOpen ? Color.rgb(184, 197, 208) : Color.rgb(107, 121, 135));
        gc.setLineWidth(1);
        gc.strokeRoundRect(game.settingsButtonX, game.settingsButtonY,
                game.settingsButtonWidth, game.settingsButtonHeight, 12, 12);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        gc.fillText("IMPOSTAZIONI", game.settingsButtonX + 18, game.settingsButtonY + 26);
        
        // === MENU A TENDINA IMPOSTAZIONI ===
        if (game.settingsMenuOpen) {
            renderSettingsDropdown(gc);
        }
    }

    private void renderGame(GraphicsContext gc) {
        // === SFONDO PERSONALIZZATO PER IL GIOCO ===
        
        if (game.assets.gameBackgroundImage != null && !game.assets.gameBackgroundImage.isError()) {
            // Usa l'immagine di sfondo personalizzata, scalandola per coprire tutto lo schermo
            gc.drawImage(game.assets.gameBackgroundImage, 0, 0, game.WIDTH, game.HEIGHT);
        } else {
            // Fallback: sfondo spaziale/caverna generato proceduralmente
            // Sfondo con gradiente cosmico profondo
            LinearGradient spaceGradient = new LinearGradient(
                0, 0, 0, game.HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(5, 5, 30)),      // Blu scurissimo in alto
                new Stop(0.4, Color.rgb(20, 10, 50)),  // Blu-viola scuro
                new Stop(1, Color.rgb(40, 20, 60))     // Viola scuro in basso
            );
            gc.setFill(spaceGradient);
            gc.fillRect(0, 0, game.WIDTH, game.HEIGHT);
            
            // Stelle sparse nel cielo con effetto scintillante
            double time = game.bodyBounce * 0.125;
            gc.setFill(Color.WHITE);
            for (int i = 0; i < 150; i++) {
                double starX = (i * 137) % game.WIDTH; // Distribuzione pseudo-casuale
                double starY = (i * 79) % (game.HEIGHT - 200);
                // Effetto scintillante per alcune stelle
                double twinkle = 1.0;
                if (!game.reducedEffects && i % 5 == 0) {
                    twinkle = 0.7 + Math.sin(time + i) * 0.3;
                }
                double starSize = (1 + (i % 3)) * twinkle;
                gc.fillOval(starX, starY, starSize, starSize);
            }
            
            // Apertura del cielo in lontananza (al centro superiore)
            double skyOpeningWidth = 350; // Più ampia
            double skyOpeningHeight = 250; // Più alta
            double skyX = game.WIDTH/2 - skyOpeningWidth/2;
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
            double[] leftWallY = {0, game.HEIGHT, game.HEIGHT, 0};
            gc.fillPolygon(leftWallX, leftWallY, 4);
            
            // Parete destra con gradiente
            LinearGradient rightWallGradient = new LinearGradient(
                game.WIDTH, 0, skyX + skyOpeningWidth, 0,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(30, 20, 40)),    // Viola scuro
                new Stop(0.7, Color.rgb(50, 30, 60)),  // Viola medio
                new Stop(1, Color.rgb(70, 40, 80))     // Viola chiaro verso l'apertura
            );
            gc.setFill(rightWallGradient);
            double[] rightWallX = {game.WIDTH, game.WIDTH, skyX + skyOpeningWidth + 50, skyX + skyOpeningWidth - 20};
            double[] rightWallY = {0, game.HEIGHT, game.HEIGHT, 0};
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
                double rockX = game.WIDTH - (i * 30);
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
            gc.strokeLine(0, game.GROUND_HEIGHT, game.WIDTH, game.GROUND_HEIGHT);

        }

        ConveyorRenderer.render(gc, game);

        // HUD minimal
        drawMinimalHUD(gc);

        // Gatto con immagine caricata - INGRANDITO in gioco - PIÙ VISIBILE
        if (game.assets.catImage != null || game.assets.catRunImage != null) {
            double catWidth = game.scaleSize(PlayerHitbox.NORMAL_SPRITE_SIZE);
            gc.setFill(Color.rgb(0, 0, 0, 0.25));
            gc.fillOval(game.catX - catWidth * 0.28, game.GROUND_HEIGHT - 3,
                    catWidth * 0.56, 7);
            PlayerAnimation.render(gc, game, catWidth, true);
        } else {
            // Fallback: disegna un cerchio blu per vedere dove dovrebbe essere il gatto - PIÙ GRANDE
            gc.setFill(Color.BLUE);
            double fallbackSize = game.scaleSize(PlayerHitbox.NORMAL_SPRITE_SIZE);
            double bounce = game.reducedEffects ? 0 : Math.sin(game.bodyBounce) * 2;
            gc.fillOval(game.catX - fallbackSize/2, game.catY + bounce - fallbackSize, fallbackSize, fallbackSize);
        }

        // === SFERE ENERGETICHE DRAGON BALL ===
        for (DragonBallSphere sphere : game.dragonBallSpheres) {
            sphere.render(gc);
        }

        // Ostacoli migliorati
        for (Obstacle o : game.obstacles) {
            gc.setFill(Color.rgb(0, 0, 0, 0.24));
            gc.fillOval(o.x - 3, game.GROUND_HEIGHT - 3, o.width + 6, 6);
            o.render(gc);
        }
    }

    private void renderBossFight(GraphicsContext gc) {
        // === SFONDO PERSONALIZZATO PER LA BOSS FIGHT ===
        
        if (game.assets.bossBackgroundImage != null && !game.assets.bossBackgroundImage.isError()) {
            // Usa l'immagine di sfondo personalizzata per la boss battle
            gc.drawImage(game.assets.bossBackgroundImage, 0, 0, game.WIDTH, game.HEIGHT);
        } else {
            // Fallback: sfondo drammatico per la boss fight
            LinearGradient bossGradient = new LinearGradient(
                0, 0, 0, game.HEIGHT,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(60, 10, 10)),    // Rosso scuro in alto
                new Stop(0.5, Color.rgb(30, 5, 30)),   // Viola scurissimo al centro
                new Stop(1, Color.rgb(10, 5, 5))       // Quasi nero in basso
            );
            gc.setFill(bossGradient);
            gc.fillRect(0, 0, game.WIDTH, game.HEIGHT);
            
            // Fulmini di sfondo per atmosfera drammatica
            gc.setStroke(Color.rgb(255, 255, 100, 0.3));
            gc.setLineWidth(3);
            double time = game.bossAnimationTimer;
            for (int i = 0; !game.reducedEffects && i < 10; i++) {
                double x = (i * 200 + Math.sin(time + i) * 50) % game.WIDTH;
                gc.strokeLine(x, 0, x + 20, game.HEIGHT * 0.3);
            }
        }
        
        // === DISEGNA IL TERRENO ===
        gc.setFill(Color.rgb(139, 90, 43)); // Marrone terreno
        gc.fillRect(0, game.GROUND_HEIGHT, game.WIDTH, game.HEIGHT - game.GROUND_HEIGHT);
        
        // Linea superiore del terreno
        gc.setStroke(Color.rgb(100, 70, 30));
        gc.setLineWidth(3);
        gc.strokeLine(0, game.GROUND_HEIGHT, game.WIDTH, game.GROUND_HEIGHT);
        
        // === DISEGNA IL BOSS (CORRIERE AMAZON) - INGRANDITO E A TERRA ===
        if (game.assets.bossImage != null && !game.assets.bossImage.isError()) {
            // Usa l'immagine personalizzata del boss - INGRANDITA
            double bossWidth = 150; // Aumentato da 120 a 150
            double bossHeight = 200; // Aumentato da 160 a 200
            
            // Boss posizionato a terra come il giocatore
            double bossGroundY = game.GROUND_HEIGHT - 10; // Stesso livello del giocatore
            
            // Animazione oscillante del boss (movimento più sottile)
            double bobbing = game.reducedEffects ? 0 : Math.sin(game.bossAnimationTimer) * 3;
            gc.drawImage(game.assets.bossImage, game.bossX - bossWidth/2, bossGroundY - bossHeight + bobbing, bossWidth, bossHeight);
        } else {
            // Fallback: disegna il boss come un rettangolo stilizzato - INGRANDITO
            double bossWidth = 120; // Aumentato da 80 a 120  
            double bossHeight = 160; // Aumentato da 120 a 160
            
            // Boss posizionato a terra
            double bossGroundY = game.GROUND_HEIGHT - 10;
            
            // Corpo del corriere (in piedi sul terreno)
            gc.setFill(Color.rgb(70, 130, 180)); // Blu uniforme Amazon
            double bobbing = Math.sin(game.bossAnimationTimer) * 3; // Movimento ridotto
            gc.fillRect(game.bossX - bossWidth/2, bossGroundY - bossHeight + bobbing, bossWidth, bossHeight);
            
            // Bordo della divisa
            gc.setStroke(Color.rgb(50, 100, 140));
            gc.setLineWidth(3);
            gc.strokeRect(game.bossX - bossWidth/2, bossGroundY - bossHeight + bobbing, bossWidth, bossHeight);
            
            // Logo Amazon sul petto
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 20)); // Aumentato font
            gc.fillText("amazon", game.bossX - 30, bossGroundY - bossHeight + bobbing + 50);
            
            // Sorriso caratteristico Amazon
            gc.setStroke(Color.ORANGE);
            gc.setLineWidth(5); // Aumentato spessore
            gc.strokeArc(game.bossX - 25, bossGroundY - bossHeight + bobbing + 55, 50, 25, 0, 180, javafx.scene.shape.ArcType.OPEN);
            
            // Braccia che lanciano (movimento di lancio verso il basso)
            gc.setStroke(Color.rgb(220, 180, 140)); // Colore pelle
            gc.setLineWidth(10); // Aumentato spessore
            double armAngle = Math.sin(game.bossAnimationTimer * 3) * 15; // Movimento più contenuto
            
            // Braccio sinistro (quello che lancia verso il basso)
            double armY = bossGroundY - bossHeight + bobbing + 60;
            gc.strokeLine(game.bossX - bossWidth/2, armY, 
                         game.bossX - bossWidth/2 - 40, armY + 40 + armAngle); // Lancia verso il basso
            
            // Braccio destro (bilanciamento, anche verso il basso)
            gc.strokeLine(game.bossX + bossWidth/2, armY, 
                         game.bossX + bossWidth/2 + 30, armY + 30 - armAngle/2); // Anche verso il basso
        }
        
        // === DISEGNA IL GATTO - INGRANDITO ===
        double catSize = PlayerHitbox.BOSS_SPRITE_SIZE;
        double catDrawX = game.catX - catSize/2;
        double catDrawY = game.catY - catSize;
        
        if (game.assets.catImage != null || game.assets.catRunImage != null) {
            PlayerAnimation.render(gc, game, catSize, game.leftPressed || game.rightPressed);
        } else {
            // Fallback: disegna il gatto come forme geometriche - INGRANDITO
            double bounce = Math.sin(game.bodyBounce) * 3;
            
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
        
        ShieldRenderer.render(gc, game);

        // === SFERE DRAGON BALL NEL BOSS FIGHT ===
        for (DragonBallSphere sphere : game.dragonBallSpheres) {
            sphere.render(gc);
        }
        
        // === INDICATORI DANNO ===
        for (DamageIndicator indicator : game.damageIndicators) {
            indicator.render(gc);
        }
        
        // === DISEGNA PROIETTILI DEL BOSS ===
        for (BossProjectile projectile : game.bossProjectiles) {
            projectile.render(gc);
        }
        
        // === DISEGNA BARRIERA BOSS ===
        for (BarrierBlock block : game.bossBarrier) {
            block.render(gc);
        }
        
        // === HUD DELLA BOSS FIGHT ===
        if(game.specialParcel!=null)game.specialParcel.render(gc,game);
        renderBossFightHUD(gc);
        renderBossAttackWarning(gc);
    }
    
    private void renderBossFightHUD(GraphicsContext gc) {
        double scale = Math.max(0.78, Math.min(1.25, game.uniformScale));
        double margin = 22 * scale;
        double panelWidth = Math.min(340 * scale, (game.WIDTH - 3 * margin) / 2);
        double panelHeight = 130 * scale;
        double leftX = margin;
        double rightX = game.WIDTH - margin - panelWidth;
        double topY = margin;

        hudPanel(gc, leftX, topY, panelWidth, panelHeight);
        hudPanel(gc, rightX, topY, panelWidth, panelHeight);

        double remainingTime = Math.max(0, game.BOSS_DURATION - game.bossTimer);
        int minutes = (int) (remainingTime / 60);
        int seconds = (int) (remainingTime % 60);
        gc.setFill(Color.rgb(174, 187, 199));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 12 * scale));
        gc.fillText("TEMPO RIMASTO", leftX + 18 * scale, topY + 24 * scale);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 30 * scale));
        gc.fillText(String.format("%d:%02d", minutes, seconds), leftX + 18 * scale, topY + 58 * scale);

        gc.setFill(Color.rgb(174, 187, 199));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 12 * scale));
        gc.fillText("ENERGIA", leftX + 18 * scale, topY + 84 * scale);
        gc.setFill(game.energy >= game.MAX_ENERGY ? Color.rgb(137, 190, 194) : Color.WHITE);
        gc.fillText(game.energy >= game.MAX_ENERGY ? "CLIC SX  PRONTA" :
                String.format("%.0f / %.0f", game.energy, game.MAX_ENERGY),
                leftX + panelWidth - 145 * scale, topY + 84 * scale);
        hudBar(gc, leftX + 18 * scale, topY + 96 * scale,
                panelWidth - 36 * scale, 12 * scale,
                game.energy / game.MAX_ENERGY, Color.rgb(93, 157, 163));

        gc.setFill(Color.rgb(174, 187, 199));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 12 * scale));
        gc.fillText("SCONTRO FINALE", rightX + 18 * scale, topY + 24 * scale);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 24 * scale));
        gc.fillText("IL BOSS", rightX + 18 * scale, topY + 57 * scale);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 12 * scale));
        gc.fillText(String.format("VITA  %.0f / %.0f", Math.max(0, game.bossHealth), game.MAX_BOSS_HEALTH),
                rightX + 18 * scale, topY + 84 * scale);
        hudBar(gc, rightX + 18 * scale, topY + 96 * scale,
                panelWidth - 36 * scale, 12 * scale,
                game.bossHealth / game.MAX_BOSS_HEALTH, Color.rgb(174, 88, 98));

        double specialY = topY + panelHeight + 12 * scale;
        hudPanel(gc,leftX,specialY,panelWidth,76*scale);
        gc.setFill(Color.rgb(190,200,209));
        gc.setFont(Font.font("Arial",FontWeight.BOLD,11*scale));
        gc.fillText("SPECIALE · PACCO ESPLOSIVO",leftX+18*scale,specialY+22*scale);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial",FontWeight.NORMAL,12*scale));
        gc.fillText(game.specialCharge>=game.SPECIAL_CHARGE_SECONDS ? "PRONTO · CLIC DESTRO" :
                String.format("Pronto tra %.0f s",Math.ceil(game.SPECIAL_CHARGE_SECONDS-game.specialCharge)),
                leftX+18*scale,specialY+43*scale);
        hudBar(gc,leftX+18*scale,specialY+55*scale,panelWidth-36*scale,8*scale,
                game.specialCharge/game.SPECIAL_CHARGE_SECONDS,Color.rgb(176,125,80));

        double stripWidth = Math.min(650 * scale, game.WIDTH - 2 * margin);
        double stripX = (game.WIDTH - stripWidth) / 2;
        double stripY = game.HEIGHT - 58 * scale;
        hudPanel(gc, stripX, stripY, stripWidth, 42 * scale);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 12 * scale));
        gc.fillText("A / D   MUOVI", stripX + 18 * scale, stripY + 26 * scale);
        gc.fillText("SPAZIO   SALTA", stripX + 160 * scale, stripY + 26 * scale);
        gc.fillText("SX   ONDA", stripX + 336 * scale, stripY + 26 * scale);
        gc.setFill(game.shieldActive ? Color.rgb(210, 177, 125) : Color.rgb(183, 194, 205));
        gc.fillText(game.shieldActive
                ? String.format("SCUDO  %.1fs", Math.max(0, game.SHIELD_DURATION - game.shieldTimer))
                : "X   SCUDO", stripX + 462 * scale, stripY + 26 * scale);
    }

    private void hudPanel(GraphicsContext gc, double x, double y, double width, double height) {
        gc.setFill(Color.rgb(17, 23, 32, 0.9));
        gc.fillRoundRect(x, y, width, height, 18, 18);
        gc.setStroke(Color.rgb(112, 126, 140, 0.72));
        gc.setLineWidth(1);
        gc.strokeRoundRect(x, y, width, height, 18, 18);
    }

    private void hudBar(GraphicsContext gc, double x, double y, double width,
                        double height, double fraction, Color color) {
        gc.setFill(Color.rgb(55, 65, 76));
        gc.fillRoundRect(x, y, width, height, height, height);
        gc.setFill(color);
        gc.fillRoundRect(x, y, width * Math.max(0, Math.min(1, fraction)), height, height, height);
    }

    private void renderBossAttackWarning(GraphicsContext gc) {
        if (game.pendingBossAttackType < 0) return;
        String[] attackNames = {"SINGOLO", "DOPPIO", "TRIPLO", "IMPILATO"};
        int type = game.pendingBossAttackType;
        if (type >= attackNames.length) return;
        double scale = Math.max(0.78, Math.min(1.25, game.uniformScale));
        double width = 280 * scale;
        double x = (game.WIDTH - width) / 2;
        double topPanelEdge = 22 * scale + Math.min(340 * scale, (game.WIDTH - 66 * scale) / 2);
        double y = x > topPanelEdge + 10 * scale ? 34 * scale : 175 * scale;
        gc.setFill(Color.rgb(27, 28, 31, 0.94));
        gc.fillRoundRect(x, y, width, 45 * scale, 12, 12);
        gc.setStroke(Color.rgb(171, 137, 90));
        gc.setLineWidth(1);
        gc.strokeRoundRect(x, y, width, 45 * scale, 12, 12);
        gc.setFill(Color.rgb(238, 218, 184));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 13 * scale));
        gc.fillText("ATTACCO IN ARRIVO", x + 16 * scale, y + 18 * scale);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 12 * scale));
        gc.fillText(attackNames[type], x + 16 * scale, y + 34 * scale);
    }

    private void renderPaused(GraphicsContext gc) {
        if (game.pausedFrom == MasterCat.GameState.PLATFORMER || game.pausedFrom == MasterCat.GameState.LEVEL_TRANSITION
                || game.pausedFrom == MasterCat.GameState.BOSS_TRANSITION) PlatformerRenderer.render(gc, game);
        else if (game.pausedFrom == MasterCat.GameState.BOSS_FIGHT) renderBossFight(gc);
        else renderGame(gc);
        gc.setFill(Color.rgb(4, 8, 14, 0.62));
        gc.fillRect(0, 0, game.WIDTH, game.HEIGHT);
        double width = Math.min(440, game.WIDTH - 36);
        double height = 208;
        double x = (game.WIDTH - width) / 2;
        double y = (game.HEIGHT - height) / 2;
        hudPanel(gc, x, y, width, height);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        gc.fillText("IN PAUSA", x + 30, y + 52);
        gc.setFill(Color.rgb(178, 190, 202));
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        gc.fillText("P   Riprendi", x + 32, y + 100);
        gc.fillText("R   Ricomincia", x + 32, y + 136);
        gc.fillText("Q   Torna al menu", x + 32, y + 172);
    }

    private void renderGameOver(GraphicsContext gc) {
        // === SFONDO PERSONALIZZATO PER IL GAME OVER ===
        
        if (game.assets.gameOverBackgroundImage != null && !game.assets.gameOverBackgroundImage.isError()) {
            // Usa l'immagine di sfondo personalizzata, scalandola per coprire tutto lo schermo
            gc.drawImage(game.assets.gameOverBackgroundImage, 0, 0, game.WIDTH, game.HEIGHT);
        } else {
            // Fallback: sfondo nero semplice
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, game.WIDTH, game.HEIGHT);
        }
        
        // Istruzioni centrate e posizionate nella parte centrale in basso
        gc.setFill(Color.WHITE);
        gc.setFont(game.scaleFont("Arial", FontWeight.BOLD, 25));
        String instructionText = "Premi qualsiasi tasto per tornare al menu";
        
        // Calcolo per centrare il testo orizzontalmente
        double instructionTextWidth = gc.getFont().getSize() * instructionText.length() * 0.6; // Stima approssimativa
        double instructionX = game.WIDTH/2 - instructionTextWidth/2;
        
        // Posizione verticale: circa 3/4 dell'altezza dello schermo (parte centrale in basso)
        double instructionY = game.HEIGHT * 0.75;
        
        gc.fillText(instructionText, instructionX, instructionY);
    }

    private void renderVictory(GraphicsContext gc) {
        try {
            // === SFONDO PERSONALIZZATO PER LA VITTORIA ===
            
            if (game.assets.victoryBackgroundImage != null && !game.assets.victoryBackgroundImage.isError()) {
                // Usa l'immagine di sfondo personalizzata, scalandola per coprire tutto lo schermo
                gc.drawImage(game.assets.victoryBackgroundImage, 0, 0, game.WIDTH, game.HEIGHT);
            } else {
                // Fallback: sfondo nero semplice
                gc.setFill(Color.BLACK);
                gc.fillRect(0, 0, game.WIDTH, game.HEIGHT);
            }

            // Punteggio finale centrato e spostato più in basso
            gc.setFill(Color.WHITE);
            gc.setFont(game.scaleFont("Arial", FontWeight.BOLD, 36));
            String finalScoreText = "Punteggio Finale: " + game.score;
            // Calcolo per centrare il testo orizzontalmente
            double textWidth = gc.getFont().getSize() * finalScoreText.length() * 0.6; // Stima approssimativa
            gc.fillText(finalScoreText, game.WIDTH/2 - textWidth/2, game.HEIGHT/2 + game.scaleSize(100));
            
            // Istruzioni centrate sotto il punteggio
            gc.setFill(Color.WHITE);
            gc.setFont(game.scaleFont("Arial", FontWeight.NORMAL, 20));
            String instructionText = "Premi qualsiasi tasto per tornare al menu";
            double instructionWidth = gc.getFont().getSize() * instructionText.length() * 0.6; // Stima approssimativa
            gc.fillText(instructionText, game.WIDTH/2 - instructionWidth/2, game.HEIGHT/2 + game.scaleSize(170));
            
        } catch (Exception e) {
            System.out.println("❌ Errore nel rendering della vittoria: " + e.getMessage());
            // Fallback: schermata nera con testo semplice
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, game.WIDTH, game.HEIGHT);
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", 24));
            gc.fillText("VITTORIA! Punteggio: " + game.score, game.WIDTH/2 - 100, game.HEIGHT/2);
            gc.fillText("Premi qualsiasi tasto per continuare", game.WIDTH/2 - 150, game.HEIGHT/2 + 50);
        }
    }

    private void drawMinimalHUD(GraphicsContext gc) {
        double scale=Math.max(0.78,Math.min(1.25,game.uniformScale));
        double margin=22*scale;
        double width=Math.min(330*scale,(game.WIDTH-3*margin)/2);
        double right=game.WIDTH-margin-width;
        hudPanel(gc,margin,margin,width,109*scale);
        hudPanel(gc,right,margin,width,109*scale);
        gc.setFont(Font.font("Arial",FontWeight.BOLD,12*scale));
        gc.setFill(Color.rgb(174,187,199));
        gc.fillText("ONDA ENERGETICA",margin+18*scale,margin+25*scale);
        gc.fillText("AVANZAMENTO",right+18*scale,margin+25*scale);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial",FontWeight.BOLD,17*scale));
        gc.fillText(game.energy>=game.MAX_ENERGY ? "PRONTA · CLIC SINISTRO" :
                String.format("Ricarica %.0f%%",game.energy/game.MAX_ENERGY*100),
                margin+18*scale,margin+55*scale);
        gc.fillText(String.format("%d / %d",game.score,game.WINNING_SCORE),right+18*scale,margin+55*scale);
        hudBar(gc,margin+18*scale,margin+73*scale,width-36*scale,10*scale,
                game.energy/game.MAX_ENERGY,Color.rgb(93,157,163));
        hudBar(gc,right+18*scale,margin+73*scale,width-36*scale,10*scale,
                (double)game.score/game.WINNING_SCORE,Color.rgb(153,155,127));
        gc.setFill(Color.rgb(181,193,204));
        gc.setFont(Font.font("Arial",FontWeight.NORMAL,11*scale));
        gc.fillText("SPAZIO  Salto / doppio salto     P  Pausa",margin,game.HEIGHT-18*scale);
    }

    private void renderSettingsDropdown(GraphicsContext gc) {
        SettingsLayout.Bounds bounds = SettingsLayout.panel(game);
        double x = bounds.x();
        double y = bounds.y();
        double width = bounds.width();
        double height = bounds.height();
        double scale = height / 500.0;

        gc.setFill(Color.rgb(18, 24, 33, 0.97));
        gc.fillRoundRect(x, y, width, height, 20, 20);
        gc.setStroke(Color.rgb(111, 125, 139, 0.82));
        gc.setLineWidth(1);
        gc.strokeRoundRect(x, y, width, height, 20, 20);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 21 * scale));
        gc.fillText("IMPOSTAZIONI", x + 23, y + 38 * scale);
        gc.setFill(Color.rgb(163, 177, 190));
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 12 * scale));
        gc.fillText("COMANDI E PREFERENZE", x + 23, y + 57 * scale);

        settingsSection(gc, x, y, scale, 84, "COMANDI");
        settingsRow(gc, x, y, scale, 109, "SPAZIO", "Salto");
        settingsRow(gc, x, y, scale, 139, "SPAZIO x2", "Doppio salto");
        settingsRow(gc, x, y, scale, 169, "MOUSE SX", "Onda energetica");
        settingsRow(gc, x, y, scale, 199, "A / D", "Movimento · X scudo");
        settingsRow(gc, x, y, scale, 229, "MOUSE DX", "Pacco speciale (boss)");
        settingsRow(gc, x, y, scale, 259, "P", "Pausa / riprendi");

        settingsSection(gc, x, y, scale, 286, "OPZIONI");
        settingsRow(gc, x, y, scale, 309, "M", game.audio.isMusicEnabled() ?
                "Musica: attiva" : "Musica: disattivata");
        gc.setFill(Color.rgb(227, 234, 240));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 12 * scale));
        gc.fillText("VOLUME", x + 23, y + 350 * scale);
        SettingsLayout.Bounds track = SettingsLayout.volumeTrack(game);
        gc.setFill(Color.rgb(55, 65, 76));
        gc.fillRoundRect(track.x(), track.y(), track.width(), track.height(), 10, 10);
        gc.setFill(Color.rgb(116, 159, 166));
        gc.fillRoundRect(track.x(), track.y(), track.width() * game.audio.getVolume(), track.height(), 10, 10);
        double thumbX = track.x() + track.width() * game.audio.getVolume();
        gc.setFill(Color.rgb(231, 237, 241));
        gc.fillOval(thumbX - 8 * scale, track.y() - 2 * scale, 16 * scale, 16 * scale);
        gc.setFill(Color.rgb(184, 196, 207));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 12 * scale));
        gc.fillText(String.format("%.0f%%", game.audio.getVolume() * 100), x + width - 59, y + 350 * scale);
        settingsRow(gc, x, y, scale, 393, "FX", game.reducedEffects ?
                "Effetti: ridotti" : "Effetti: normali");
        settingsRow(gc, x, y, scale, 435, "U / ESC", game.gameStage != null && game.gameStage.isFullScreen() ?
                "Schermo intero: attivo" : "Schermo intero: inattivo");

        gc.setFill(Color.rgb(152, 165, 178));
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 11 * scale));
        gc.fillText("Clicca un'opzione o premi I per chiudere", x + 23, y + 475 * scale);
    }

    private void settingsSection(GraphicsContext gc, double x, double y, double scale,
                                 double baseline, String title) {
        gc.setFill(Color.rgb(176, 190, 202));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 11 * scale));
        gc.fillText(title, x + 23, y + baseline * scale);
    }

    private void settingsRow(GraphicsContext gc, double x, double y, double scale,
                             double baseline, String key, String label) {
        double rowY = y + (baseline - 21) * scale;
        gc.setFill(Color.rgb(39, 49, 60));
        gc.fillRoundRect(x + 22, rowY, 103, 27 * scale, 7, 7);
        gc.setStroke(Color.rgb(101, 116, 130));
        gc.setLineWidth(1);
        gc.strokeRoundRect(x + 22, rowY, 103, 27 * scale, 7, 7);
        gc.setFill(Color.rgb(227, 234, 240));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 12 * scale));
        gc.fillText(key, x + 31, y + baseline * scale);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 13 * scale));
        gc.fillText(label, x + 144, y + baseline * scale);
    }
}
