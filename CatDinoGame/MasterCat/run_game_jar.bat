@echo off
echo ==========================================
echo      🐱 CAT DINO GAME - LAUNCHER
echo ==========================================
echo.

if exist "target\CatDinoGame-1.0-SNAPSHOT-jar-with-dependencies.jar" (
    echo 🚀 Avvio Cat Dino Game...
    echo.
    java -jar target\CatDinoGame-1.0-SNAPSHOT-jar-with-dependencies.jar
) else (
    echo ❌ ERRORE: JAR eseguibile non trovato!
    echo.
    echo 💡 SOLUZIONE:
    echo    1. Esegui 'build_executable.bat' per creare l'eseguibile
    echo    2. Oppure usa 'run_game.bat' per il gioco in sviluppo
    echo.
    pause
)

echo.
echo 👋 Gioco terminato!
pause
