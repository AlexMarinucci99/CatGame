package com.master.bro.catdinogame;

import javafx.scene.image.Image;

final class GameAssets {
    Image catImage;
    Image catRunImage;
    Image menuBackgroundImage;
    Image gameBackgroundImage;
    Image victoryBackgroundImage;
    Image gameOverBackgroundImage;
    Image bossBackgroundImage;
    Image bossImage;
    Image bossProjectileNormalImage;
    Image bossProjectileDoubleImage;
    Image blockNormalImage;
    Image blockDoubleImage;

    void loadResources() {
        System.out.println("🔍 Caricamento risorse grafiche...");
        try (var stream = getClass().getResourceAsStream("/images/player/cat-run-v1.png")) {
            if (stream != null) catRunImage = new Image(stream);
        } catch (Exception e) {
            catRunImage = null;
        }
        
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

}
