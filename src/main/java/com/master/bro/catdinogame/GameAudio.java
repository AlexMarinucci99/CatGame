package com.master.bro.catdinogame;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

final class GameAudio {
    private boolean isMusicEnabled = true;
    private MediaPlayer menuMusicPlayer;
    private MediaPlayer gameMusicPlayer;
    private MediaPlayer bossMusicPlayer;
    private MediaPlayer victoryMusicPlayer;
    private MediaPlayer currentMusicPlayer;

    void initializeAudio() {
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
    
    void toggleMusic() {
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
    
    void switchToMenuMusic() {
        if (currentMusicPlayer != null) {
            currentMusicPlayer.stop();
        }
        currentMusicPlayer = menuMusicPlayer;
        if (isMusicEnabled && currentMusicPlayer != null) {
            currentMusicPlayer.play();
        }
    }
    
    void switchToGameMusic() {
        if (currentMusicPlayer != null) {
            currentMusicPlayer.stop();
        }
        currentMusicPlayer = gameMusicPlayer;
        if (isMusicEnabled && currentMusicPlayer != null) {
            currentMusicPlayer.play();
        }
    }
    
    void switchToBossMusic() {
        if (currentMusicPlayer != null) {
            currentMusicPlayer.stop();
        }
        currentMusicPlayer = bossMusicPlayer;
        if (isMusicEnabled && currentMusicPlayer != null) {
            currentMusicPlayer.play();
        }
        System.out.println("🎼 Musica del boss finale attivata!");
    }
    
    void stopAllMusic() {
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
    
    void switchToVictoryMusic() {
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
    
    boolean isMusicEnabled() { return isMusicEnabled; }

    void dispose() {
        stopAllMusic();
        if (menuMusicPlayer != null) menuMusicPlayer.dispose();
        if (gameMusicPlayer != null) gameMusicPlayer.dispose();
        if (bossMusicPlayer != null) bossMusicPlayer.dispose();
        if (victoryMusicPlayer != null) victoryMusicPlayer.dispose();
    }
}
