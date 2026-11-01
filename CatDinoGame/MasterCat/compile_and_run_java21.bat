@echo off
echo Compilazione e esecuzione con Java 21 + JavaFX...

REM Imposta il percorso per JavaFX
set JAVAFX_PATH=lib\javafx-sdk-21.0.2\lib

REM Verifica se JavaFX è presente
if not exist %JAVAFX_PATH% (
    echo JavaFX non trovato! Esegui prima download_javafx.bat
    pause
    exit /b 1
)

REM Pulizia directory target
if exist target rmdir /s /q target
mkdir target\classes

REM Copia le risorse (inclusi i file audio)
if exist src\main\resources (
    echo Copia delle risorse...
    xcopy /E /I /Y src\main\resources target\classes
)

echo Compilazione...
javac --module-path %JAVAFX_PATH% --add-modules javafx.controls,javafx.fxml,javafx.media -cp src\main\java -d target\classes src\main\java\com\master\bro\catdinogame\*.java

if %errorlevel% neq 0 (
    echo Errore durante la compilazione!
    pause
    exit /b 1
)

echo Compilazione completata!
echo Avvio del gioco...

REM Esecuzione tramite GameLauncher
java --module-path %JAVAFX_PATH% --add-modules javafx.controls,javafx.fxml,javafx.media -cp target\classes com.master.bro.catdinogame.GameLauncher

pause
