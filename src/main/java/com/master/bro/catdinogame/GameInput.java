package com.master.bro.catdinogame;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

/** Installs the original keyboard and menu mouse handlers. */
final class GameInput {
    private final MasterCat game;
    GameInput(MasterCat game) { this.game = game; }

    void onKeyPressed(KeyCode code) {
            // Controllo fullscreen globale (funziona in tutti gli stati)
            if (code == KeyCode.U || code == KeyCode.ESCAPE) {
                game.toggleFullscreen();
                return;
            }
            
            // Controllo musica globale (funziona in tutti gli stati)
            if (code == KeyCode.M) {
                game.audio.toggleMusic();
                return;
            }
            
            switch (game.currentState) {
                case MENU:
                    if (code == KeyCode.SPACE) {
                        game.startGame();
                    }
                    // Toggle menu impostazioni solo con tasto I
                    else if (code == KeyCode.I) {
                        game.settingsMenuOpen = !game.settingsMenuOpen;
                    }
                    // Tasto segreto T per saltare direttamente al boss fight (per test)
                    else if (code == KeyCode.T) {
                        game.score = game.WINNING_SCORE; // Imposta il punteggio necessario
                        game.startBossFight(); // Vai direttamente al boss fight
                    }
                    break;
                case PLAYING:
                    if (code == KeyCode.SPACE) {
                        // Salto normale se a terra
                        if (game.onGround) {
                            game.velocityY = game.JUMP_VELOCITY;
                            game.onGround = false;
                            game.canDoubleJump = true; // Abilita doppio salto
                        }
                        // Doppio salto se in aria e disponibile
                        else if (game.canDoubleJump) {
                            game.velocityY = game.JUMP_VELOCITY * 0.8; // Doppio salto leggermente più debole
                            game.canDoubleJump = false; // Disabilita ulteriori salti
                        }
                    }
                    // === LANCIO SFERA DRAGON BALL NEL LIVELLO NORMALE ===
                    if (code == KeyCode.C && game.energy >= game.MAX_ENERGY) {
                        // Lancia una nuova sfera energetica Dragon Ball
                        double sphereStartX = game.catX + 60; // Parte dalla mano del gatto
                        double sphereStartY = game.catY - 40; // Centro verticale del gatto
                        game.dragonBallSpheres.add(game.new DragonBallSphere(sphereStartX, sphereStartY));
                        game.energy = 0; // Reset energia
                        game.waveActive = false; // Non usiamo più game.waveActive nel normale
                    }
                    break;
                case BOSS_FIGHT:
                    if (code == KeyCode.SPACE) {
                        // Salto normale se a terra
                        if (game.onGround) {
                            game.velocityY = game.JUMP_VELOCITY;
                            game.onGround = false;
                            game.canDoubleJump = true; // Abilita doppio salto
                        }
                        // Doppio salto se in aria e disponibile
                        else if (game.canDoubleJump) {
                            game.velocityY = game.JUMP_VELOCITY * 0.8; // Doppio salto leggermente più debole
                            game.canDoubleJump = false; // Disabilita ulteriori salti
                        }
                    }
                    // === LANCIO SFERA DRAGON BALL NEL BOSS FIGHT ===
                    if (code == KeyCode.C && game.energy >= game.MAX_ENERGY) {
                        // Lancia una nuova sfera energetica Dragon Ball
                        double sphereStartX = game.catX + 60; // Parte dalla mano del gatto
                        double sphereStartY = game.catY - 40; // Centro verticale del gatto
                        game.dragonBallSpheres.add(game.new DragonBallSphere(sphereStartX, sphereStartY));
                        game.energy = 0; // Reset energia
                        System.out.println("🌊 SFERA DRAGON BALL LANCIATA!");
                    }
                    // Scudo con X
                    if (code == KeyCode.X && game.energy >= game.SHIELD_ENERGY_COST && !game.shieldActive) {
                        game.shieldActive = true;
                        game.shieldTimer = 0;
                        game.energy -= game.SHIELD_ENERGY_COST;
                        System.out.println("🛡️ SCUDO ATTIVATO!");
                    }
                    // === MOVIMENTO ORIZZONTALE ===
                    if (code == KeyCode.LEFT || code == KeyCode.A) {
                        game.leftPressed = true;
                    }
                    if (code == KeyCode.RIGHT || code == KeyCode.D) {
                        game.rightPressed = true;
                    }
                    break;
                case GAME_OVER:
                    // Ritorna al menu con qualsiasi tasto
                    game.resetToMenu();
                    break;
                case VICTORY:
                    // Ritorna al menu con qualsiasi tasto
                    game.resetToMenu();
                    break;
            }
    }


        // Gestione rilascio tasti per movimento fluido
    void onKeyReleased(KeyCode code) {
            switch (game.currentState) {
                case BOSS_FIGHT:
                    if (code == KeyCode.LEFT || code == KeyCode.A) {
                        game.leftPressed = false;
                    }
                    if (code == KeyCode.RIGHT || code == KeyCode.D) {
                        game.rightPressed = false;
                    }
                    break;
                case MENU:
                case PLAYING:
                case GAME_OVER:
                case VICTORY:
                    // Nessuna azione speciale per questi stati
                    break;
            }
    }


    void install(Scene scene) {
        scene.setOnKeyPressed(e -> onKeyPressed(e.getCode()));
        scene.setOnKeyReleased(e -> onKeyReleased(e.getCode()));

        // === GESTIONE CLICK MOUSE PER MENU A TENDINA ===
        // === GESTIONE CLICK MOUSE PER MENU A TENDINA ===
        scene.setOnMouseClicked(e -> {
            if (game.currentState == MasterCat.GameState.MENU) {
                double mouseX = e.getX();
                double mouseY = e.getY();
                
                // Check se click è sul pulsante Impostazioni
                if (mouseX >= game.settingsButtonX && mouseX <= game.settingsButtonX + game.settingsButtonWidth &&
                    mouseY >= game.settingsButtonY && mouseY <= game.settingsButtonY + game.settingsButtonHeight) {
                    game.settingsMenuOpen = !game.settingsMenuOpen; // Toggle menu
                }
                // Check se click è fuori dal menu per chiuderlo
                else if (game.settingsMenuOpen) {
                    if (!SettingsLayout.panel(game).contains(mouseX, mouseY)) {
                        game.settingsMenuOpen = false; // Chiudi menu se click fuori
                    }
                }
            }
        });

    }
}
