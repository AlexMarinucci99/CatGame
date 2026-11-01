@echo off
chcp 65001 >nul
echo ==========================================
echo    🎮 CAT DINO GAME - GIOCA ORA!
echo ==========================================
echo.
echo 🎮 Avvio del gioco...
java --module-path "javafx.base.jar;javafx.controls.jar;javafx.fxml.jar;javafx.graphics.jar;javafx.media.jar" --add-modules javafx.controls,javafx.fxml,javafx.media -cp "." com.master.bro.catdinogame.CatDinoGame
if %errorlevel% neq 0 (
    echo.
    echo ❌ Errore durante l'avvio del gioco!
    echo 💡 Prova il file GIOCA_SEMPLICE.bat per una versione più compatibile
    pause
)
