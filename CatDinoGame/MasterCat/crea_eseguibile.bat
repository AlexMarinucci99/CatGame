@echo off
chcp 65001 >nul
echo ==========================================
echo    🎮 CAT DINO GAME - CREA ESEGUIBILE
echo ==========================================
echo.

echo 🔍 Verifica Java...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java non trovato! Installa Java 21 o superiore.
    pause
    exit /b 1
)

echo ✅ Java trovato!
echo.

echo 🧹 Pulizia cartelle precedenti...
if exist "target" rmdir /s /q target
if exist "dist" rmdir /s /q dist
mkdir target\classes 2>nul
mkdir dist 2>nul

echo.
echo 🔨 Compilazione del codice Java...
echo   Compilazione in corso...

:: Trova tutti i file .java nella cartella src
set CLASSPATH=lib\javafx-sdk-21.0.2\lib\*
set MODULE_PATH=lib\javafx-sdk-21.0.2\lib

:: Compila tutti i file Java
javac -cp "%CLASSPATH%" --module-path "%MODULE_PATH%" --add-modules javafx.controls,javafx.fxml,javafx.media -d target\classes src\main\java\com\master\bro\catdinogame\*.java

if %errorlevel% neq 0 (
    echo ❌ Errore durante la compilazione!
    pause
    exit /b 1
)

echo ✅ Compilazione completata!
echo.

echo 📦 Copia delle risorse...
xcopy /E /I /Y src\main\resources target\classes 2>nul

echo.
echo 📋 Creazione file manifest...
echo Main-Class: com.master.bro.catdinogame.CatDinoGame > manifest.txt
echo Class-Path: javafx-controls.jar javafx-fxml.jar javafx-media.jar javafx-base.jar javafx-graphics.jar >> manifest.txt

echo.
echo 🎯 Creazione JAR eseguibile...

:: Trova il percorso di Java per usare jar
for /f "tokens=*" %%i in ('where java') do set JAVA_PATH=%%i
set JAR_PATH=%JAVA_PATH:\java.exe=\jar.exe%

:: Crea un JAR con le classi compilate
cd target\classes
"%JAR_PATH%" -cfm ..\..\dist\CatDinoGame.jar ..\..\manifest.txt com\master\bro\catdinogame\*.class audio\* images\*
cd ..\..

if not exist "dist\CatDinoGame.jar" (
    echo ❌ Errore nella creazione del JAR!
    echo 💡 Provando con percorso alternativo...
    :: Prova con il percorso alternativo
    cd target\classes
    "C:\Program Files\Common Files\Oracle\Java\javapath\jar.exe" -cfm ..\..\dist\CatDinoGame.jar ..\..\manifest.txt com\master\bro\catdinogame\*.class audio\* images\*
    cd ..\..
)

if not exist "dist\CatDinoGame.jar" (
    echo ❌ Errore nella creazione del JAR! Comando jar non trovato.
    pause
    exit /b 1
)

echo ✅ JAR eseguibile creato con successo!

echo.
echo 📁 Copia delle librerie JavaFX...
copy "lib\javafx-sdk-21.0.2\lib\*.jar" "dist\" >nul

echo.
echo 🚀 Creazione launcher per Windows...
(
echo @echo off
echo chcp 65001 ^>nul
echo echo ==========================================
echo echo    🎮 CAT DINO GAME - AVVIO GIOCO
echo echo ==========================================
echo echo.
echo echo 🎮 Avvio del gioco...
echo java --module-path . --add-modules javafx.controls,javafx.fxml,javafx.media -jar CatDinoGame.jar
echo if %%errorlevel%% neq 0 ^(
echo     echo.
echo     echo ❌ Errore durante l'avvio del gioco!
echo     echo Assicurati di avere Java 21 o superiore installato.
echo     pause
echo ^)
) > dist\Avvia_CatDinoGame.bat

echo.
echo ==========================================
echo           ✅ ESEGUIBILE CREATO!
echo ==========================================
echo.
echo 📁 Il gioco è pronto nella cartella 'dist'
echo 🎮 Per giocare: apri la cartella 'dist' e fai doppio click su 'Avvia_CatDinoGame.bat'
echo.
echo 💾 Per distribuire il gioco:
echo    Copia l'intera cartella 'dist' su qualsiasi PC con Java 21+
echo.
pause
