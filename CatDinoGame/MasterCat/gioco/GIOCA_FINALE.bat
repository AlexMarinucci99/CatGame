@echo off
chcp 65001 >nul
echo ==========================================
echo    🎮 CAT DINO GAME - SOLUZIONE FINALE
echo ==========================================
echo.
echo 🎮 Avvio con parametri ottimizzati per JavaFX...

:: Metodo 1: Module path esplicito
echo 🔄 Tentativo 1: Module path standard...
java --module-path "javafx.base.jar;javafx.controls.jar;javafx.graphics.jar;javafx.fxml.jar;javafx.media.jar" --add-modules javafx.controls,javafx.fxml,javafx.media -cp "." com.master.bro.catdinogame.CatDinoGame

if %errorlevel% equ 0 (
    echo ✅ Gioco avviato con successo!
    goto :end
)

echo ❌ Tentativo 1 fallito!
echo.

:: Metodo 2: Tutto nel classpath con rendering software
echo 🔄 Tentativo 2: Classpath completo + rendering software...
java -Dprism.order=sw -Dprism.verbose=true -cp ".;javafx.base.jar;javafx.controls.jar;javafx.graphics.jar;javafx.fxml.jar;javafx.media.jar;javafx.swing.jar;javafx.web.jar" com.master.bro.catdinogame.CatDinoGame

if %errorlevel% equ 0 (
    echo ✅ Gioco avviato con rendering software!
    goto :end
)

echo ❌ Tentativo 2 fallito!
echo.

:: Metodo 3: Solo classpath senza moduli
echo 🔄 Tentativo 3: Classpath semplice...
java -cp ".;javafx.base.jar;javafx.controls.jar;javafx.graphics.jar;javafx.fxml.jar;javafx.media.jar" com.master.bro.catdinogame.CatDinoGame

if %errorlevel% equ 0 (
    echo ✅ Gioco avviato senza moduli!
    goto :end
)

echo ❌ Tentativo 3 fallito!
echo.

:: Metodo 4: Forza il rendering software con parametri estesi
echo 🔄 Tentativo 4: Rendering software forzato...
java -Dprism.order=sw -Djavafx.platform=desktop -Dglass.platform=windows -cp ".;javafx.base.jar;javafx.controls.jar;javafx.graphics.jar;javafx.fxml.jar;javafx.media.jar" com.master.bro.catdinogame.CatDinoGame

if %errorlevel% equ 0 (
    echo ✅ Gioco avviato con rendering forzato!
    goto :end
)

echo.
echo ==========================================
echo           ❌ TUTTI I TENTATIVI FALLITI
echo ==========================================
echo.
echo 💡 Possibili soluzioni:
echo    1. Aggiorna Java alla versione più recente
echo    2. Installa JavaFX SDK separatamente
echo    3. Aggiorna i driver grafici
echo    4. Esegui come amministratore
echo    5. Prova su un altro PC
echo.
echo 📁 File presenti nella cartella:
dir /b *.jar
echo.
echo 📁 Classe principale:
if exist "com\master\bro\catdinogame\CatDinoGame.class" (
    echo ✅ CatDinoGame.class trovata
) else (
    echo ❌ CatDinoGame.class NON trovata!
)

:end
pause
