@echo off
echo ==========================================
echo    🎮 CAT DINO GAME - CREA ESEGUIBILE
echo ==========================================
echo.

echo 🔄 Pulizia build precedenti...
call mvn clean

echo 🔨 Compilazione progetto...
call mvn compile

echo 📦 Creazione JAR eseguibile...
call mvn package

echo.
if exist "target\CatDinoGame-1.0-SNAPSHOT-jar-with-dependencies.jar" (
    echo ==========================================
    echo           ✅ SUCCESSO!
    echo ==========================================
    echo.
    echo 🎮 ESEGUIBILE CREATO:
    echo    📁 target\CatDinoGame-1.0-SNAPSHOT-jar-with-dependencies.jar
    echo.
    echo 🚀 PER ESEGUIRE IL GIOCO:
    echo    java -jar target\CatDinoGame-1.0-SNAPSHOT-jar-with-dependencies.jar
    echo.
    echo 💡 O usa il launcher: run_game_jar.bat
    echo.
) else (
    echo ❌ ERRORE: JAR non creato!
    echo 💡 Controlla che Maven sia installato
)

pause
