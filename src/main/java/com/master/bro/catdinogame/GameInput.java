package com.master.bro.catdinogame;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

/** Handles gameplay keys, pause, focus loss, and interactive menu settings. */
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

            if (code == KeyCode.P) {
                if (game.currentState == MasterCat.GameState.PAUSED) game.resumeGame();
                else game.pauseGame();
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
                case PAUSED:
                    if (code == KeyCode.R) game.startGame();
                    else if (code == KeyCode.Q) game.resetToMenu();
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
                case PAUSED:
                case GAME_OVER:
                case VICTORY:
                    // Nessuna azione speciale per questi stati
                    break;
            }
    }

    void onFocusLost() {
        game.leftPressed = false;
        game.rightPressed = false;
        game.pauseGame();
    }

    void onMenuClick(double mouseX, double mouseY) {
        if (game.currentState != MasterCat.GameState.MENU) return;
        if (mouseX >= game.settingsButtonX && mouseX <= game.settingsButtonX + game.settingsButtonWidth &&
            mouseY >= game.settingsButtonY && mouseY <= game.settingsButtonY + game.settingsButtonHeight) {
            game.settingsMenuOpen = !game.settingsMenuOpen;
            return;
        }
        if (!game.settingsMenuOpen) return;

        if (SettingsLayout.optionRow(game, SettingsLayout.Option.MUSIC).contains(mouseX, mouseY)) {
            game.audio.toggleMusic();
        } else if (SettingsLayout.optionRow(game, SettingsLayout.Option.EFFECTS).contains(mouseX, mouseY)) {
            game.reducedEffects = !game.reducedEffects;
        } else if (SettingsLayout.optionRow(game, SettingsLayout.Option.FULLSCREEN).contains(mouseX, mouseY)) {
            game.toggleFullscreen();
        } else if (SettingsLayout.optionRow(game, SettingsLayout.Option.VOLUME).contains(mouseX, mouseY)) {
            if (mouseX >= SettingsLayout.volumeTrack(game).x() - 10) updateVolume(mouseX);
        } else if (!SettingsLayout.panel(game).contains(mouseX, mouseY)) {
            game.settingsMenuOpen = false;
        }
    }

    void onMenuDrag(double mouseX, double mouseY) {
        if (game.currentState == MasterCat.GameState.MENU && game.settingsMenuOpen &&
            SettingsLayout.optionRow(game, SettingsLayout.Option.VOLUME).contains(mouseX, mouseY) &&
            mouseX >= SettingsLayout.volumeTrack(game).x() - 10) {
            updateVolume(mouseX);
        }
    }

    private void updateVolume(double mouseX) {
        SettingsLayout.Bounds track = SettingsLayout.volumeTrack(game);
        game.audio.setVolume((mouseX - track.x()) / track.width());
    }


    void install(Scene scene) {
        scene.setOnKeyPressed(e -> onKeyPressed(e.getCode()));
        scene.setOnKeyReleased(e -> onKeyReleased(e.getCode()));

        scene.setOnMouseClicked(e -> onMenuClick(e.getX(), e.getY()));
        scene.setOnMouseDragged(e -> onMenuDrag(e.getX(), e.getY()));

    }
}
