@echo off
chcp 65001 >nul
echo ==========================================
echo    🎮 CAT DINO GAME - ESEGUIBILE SEMPLICE
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
if exist "gioco" rmdir /s /q gioco
mkdir target\classes 2>nul
mkdir gioco 2>nul

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

echo 📦 Copia delle risorse e files...
xcopy /E /I /Y src\main\resources target\classes 2>nul
xcopy /E /I /Y target\classes\* gioco\ 2>nul
xcopy /Y lib\javafx-sdk-21.0.2\lib\*.jar gioco\ 2>nul

echo.
echo 🚀 Creazione launcher per Windows...
(
echo @echo off
echo chcp 65001 ^>nul
echo echo ==========================================
echo echo    🎮 CAT DINO GAME - GIOCA ORA!
echo echo ==========================================
echo echo.
echo echo 🎮 Avvio del gioco...
echo java --module-path . --add-modules javafx.controls,javafx.fxml,javafx.media -cp ".;*.jar" com.master.bro.catdinogame.CatDinoGame
echo if %%errorlevel%% neq 0 ^(
echo     echo.
echo     echo ❌ Errore durante l'avvio del gioco!
echo     echo Assicurati di avere Java 21 o superiore installato.
echo     pause
echo ^)
) > gioco\GIOCA.bat

echo.
echo ==========================================
echo           ✅ GIOCO PRONTO!
echo ==========================================
echo.
echo 📁 Il gioco è pronto nella cartella 'gioco'
echo 🎮 Per giocare: apri la cartella 'gioco' e fai doppio click su 'GIOCA.bat'
echo.
echo 💾 Per distribuire il gioco:
echo    Copia l'intera cartella 'gioco' su qualsiasi PC con Java 21+
echo.
echo 🎯 Dimensione del gioco: circa 60MB (include tutte le librerie JavaFX)
echo.
pause
