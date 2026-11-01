@echo off
chcp 65001 >nul
echo ==========================================
echo    🎮 CAT DINO GAME - AVVIO SEMPLICE
echo ==========================================
echo.
echo 🎮 Avvio del gioco con rendering software...
java -Dprism.order=sw --module-path "javafx.base.jar;javafx.controls.jar;javafx.graphics.jar;javafx.fxml.jar;javafx.media.jar" --add-modules javafx.controls,javafx.fxml,javafx.media -cp "." com.master.bro.catdinogame.CatDinoGame
if %errorlevel% neq 0 (
    echo.
    echo ❌ Errore! Provo metodo alternativo...
    java -Dprism.order=sw -cp ".;javafx.base.jar;javafx.controls.jar;javafx.graphics.jar;javafx.fxml.jar;javafx.media.jar" com.master.bro.catdinogame.CatDinoGame
    if %errorlevel% neq 0 (
        echo ❌ Impossibile avviare il gioco!
        pause
    )
)
