@echo off
chcp 65001 >nul
echo ==========================================
echo    🎮 CAT DINO GAME - AVVIO AVANZATO
echo ==========================================
echo.

echo 🔍 Controllo file necessari...

:: Verifica che tutti i JAR di JavaFX siano presenti
if not exist "javafx.base.jar" (
    echo ❌ javafx.base.jar non trovato!
    goto :error
)
if not exist "javafx.controls.jar" (
    echo ❌ javafx.controls.jar non trovato!
    goto :error
)
if not exist "javafx.graphics.jar" (
    echo ❌ javafx.graphics.jar non trovato!
    goto :error
)
if not exist "javafx.fxml.jar" (
    echo ❌ javafx.fxml.jar non trovato!
    goto :error
)
if not exist "javafx.media.jar" (
    echo ❌ javafx.media.jar non trovato!
    goto :error
)

echo ✅ Tutti i file JavaFX trovati!

echo 🔍 Verifica delle classi del gioco...
if not exist "com\master\bro\catdinogame\CatDinoGame.class" (
    echo ❌ Classe principale del gioco non trovata!
    goto :error
)

echo ✅ Classe del gioco trovata!
echo.
echo 🎮 Avvio del gioco con parametri ottimizzati...

:: Comando Java con parametri specifici per JavaFX
java ^
    --module-path "javafx.base.jar;javafx.controls.jar;javafx.fxml.jar;javafx.graphics.jar;javafx.media.jar;javafx.swing.jar;javafx.web.jar" ^
    --add-modules javafx.controls,javafx.fxml,javafx.media ^
    --add-exports javafx.base/com.sun.javafx.runtime=ALL-UNNAMED ^
    -Dprism.order=sw ^
    -Dprism.verbose=true ^
    -cp "." ^
    com.master.bro.catdinogame.CatDinoGame

if %errorlevel% equ 0 (
    echo.
    echo ✅ Gioco terminato correttamente!
) else (
    echo.
    echo ❌ Errore durante l'esecuzione! Codice errore: %errorlevel%
    goto :error
)

pause
exit /b 0

:error
echo.
echo ==========================================
echo           ❌ ERRORE RILEVATO
echo ==========================================
echo.
echo 💡 Soluzioni possibili:
echo    1. Controlla che Java 21+ sia installato
echo    2. Esegui come amministratore
echo    3. Controlla che tutti i file siano presenti
echo    4. Ricompila il gioco con 'crea_gioco.bat'
echo.
pause
exit /b 1
