@echo off
echo ==========================================
echo   🎮 CAT DINO GAME - ESEGUIBILE SEMPLICE
echo ==========================================
echo.

echo 🔍 Verifica Java...
java -version 2>nul
if errorlevel 1 (
    echo ❌ ERRORE: Java non trovato!
    pause
    exit /b 1
)

echo ✅ Java trovato!
echo.

echo 🔍 Verifica JavaFX...
if not exist "lib\javafx-sdk-21.0.2\lib" (
    echo ❌ ERRORE: JavaFX non trovato!
    echo 💡 Esegui 'download_javafx.bat' prima
    pause
    exit /b 1
)

echo ✅ JavaFX trovato!
echo.

echo 🗂️ Preparazione cartelle...
if not exist "dist" mkdir dist
if not exist "dist\lib" mkdir dist\lib

echo 🔨 Compilazione...
javac -cp "lib\javafx-sdk-21.0.2\lib\*" -d dist src\main\java\com\master\bro\catdinogame\*.java

if errorlevel 1 (
    echo ❌ ERRORE nella compilazione!
    pause
    exit /b 1
)

echo 📂 Copia risorse...
if exist "src\main\resources" (
    xcopy "src\main\resources\*" "dist\" /E /I /Q
)

echo 📚 Copia librerie JavaFX...
copy "lib\javafx-sdk-21.0.2\lib\*.jar" "dist\lib\"

echo 🚀 Creazione launcher...
echo @echo off > dist\CatDinoGame.bat
echo echo ====================================== >> dist\CatDinoGame.bat
echo echo       🐱 CAT DINO GAME >> dist\CatDinoGame.bat
echo echo ====================================== >> dist\CatDinoGame.bat
echo echo. >> dist\CatDinoGame.bat
echo echo 🚀 Avvio del gioco... >> dist\CatDinoGame.bat
echo java -cp ".;lib\*" com.master.bro.catdinogame.CatDinoGame >> dist\CatDinoGame.bat
echo if errorlevel 1 echo ❌ Errore! Controlla Java >> dist\CatDinoGame.bat
echo pause >> dist\CatDinoGame.bat

echo.
echo ==========================================
echo           ✅ ESEGUIBILE CREATO!
echo ==========================================
echo.
echo 📁 Cartella: dist\
echo 🎮 Launcher: dist\CatDinoGame.bat
echo.
echo 🚀 Per eseguire: entra in 'dist' e fai doppio click su CatDinoGame.bat
echo 📤 Per distribuire: comprimi la cartella 'dist' in ZIP
echo.

pause
