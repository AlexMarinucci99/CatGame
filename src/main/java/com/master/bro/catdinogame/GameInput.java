package com.master.bro.catdinogame;

import java.util.EnumSet;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

/** Handles gameplay keys, pause, focus loss, and interactive menu settings. */
final class GameInput {
    private final MasterCat game;
    private final EnumSet<KeyCode> held = EnumSet.noneOf(KeyCode.class);
    GameInput(MasterCat game) { this.game = game; }

    void onKeyPressed(KeyCode code) {
            if (!held.add(code)) return;
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
                if (game.state() == MasterCat.GameState.PAUSED) game.resumeGame();
                else game.pauseGame();
                return;
            }
            
            switch (game.state()) {
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
            held.remove(code);
            switch (game.state()) {
                case BOSS_FIGHT:
                    if (code == KeyCode.LEFT || code == KeyCode.A) {
                        game.leftPressed = held.contains(KeyCode.LEFT) || held.contains(KeyCode.A);
                    }
                    if (code == KeyCode.RIGHT || code == KeyCode.D) {
                        game.rightPressed = held.contains(KeyCode.RIGHT) || held.contains(KeyCode.D);
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
        held.clear();
        game.leftPressed = false;
        game.rightPressed = false;
        game.pauseGame();
    }

    void onMenuClick(double mouseX, double mouseY) {
        if (game.state() != MasterCat.GameState.MENU) return;
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
        if (game.state() == MasterCat.GameState.MENU && game.settingsMenuOpen &&
            SettingsLayout.optionRow(game, SettingsLayout.Option.VOLUME).contains(mouseX, mouseY) &&
            mouseX >= SettingsLayout.volumeTrack(game).x() - 10) {
            updateVolume(mouseX);
        }
    }

    private void updateVolume(double mouseX) {
        SettingsLayout.Bounds track = SettingsLayout.volumeTrack(game);
        game.audio.setVolume((mouseX - track.x()) / track.width());
    }


    void onMouseAttack(MouseButton button) {
        boolean boss=game.state()==MasterCat.GameState.BOSS_FIGHT;
        if(!boss && game.state()!=MasterCat.GameState.PLAYING)return;
        if(button==MouseButton.PRIMARY && game.energy>=game.MAX_ENERGY) {
            game.dragonBallSpheres.add(game.new DragonBallSphere(game.catX+60,game.catY-40));
            game.energy=0;game.waveActive=false;
        } else if(button==MouseButton.SECONDARY && boss && game.specialCharge>=game.SPECIAL_CHARGE_SECONDS
                && game.specialParcel==null) {
            game.specialParcel=new ExplosiveParcel(game.catX,game.catY-50,game.bossX,game.bossY-100);
            game.specialCharge=0;
        }
    }

    void install(Scene scene) {
        scene.setOnKeyPressed(e -> onKeyPressed(e.getCode()));
        scene.setOnKeyReleased(e -> onKeyReleased(e.getCode()));

        scene.setOnMouseClicked(e -> {
            if(e.getButton()==MouseButton.PRIMARY) onMenuClick(e.getX(),e.getY());
        });
        scene.setOnMousePressed(e -> onMouseAttack(e.getButton()));
        scene.setOnContextMenuRequested(e -> e.consume());
        scene.setOnMouseDragged(e -> onMenuDrag(e.getX(), e.getY()));

    }
}
