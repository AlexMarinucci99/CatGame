@echo off
echo Scaricamento JavaFX 21...

REM Crea directory lib se non esiste
if not exist lib mkdir lib

REM Download JavaFX SDK 21 per Windows
echo Scaricando JavaFX SDK 21...
powershell -Command "Invoke-WebRequest -Uri 'https://download2.gluonhq.com/openjfx/21.0.2/openjfx-21.0.2_windows-x64_bin-sdk.zip' -OutFile 'lib/javafx-sdk.zip'"

REM Estrai il file
echo Estraendo JavaFX...
powershell -Command "Expand-Archive -Path 'lib/javafx-sdk.zip' -DestinationPath 'lib/' -Force"

echo JavaFX scaricato e estratto in lib/
echo Ora puoi eseguire compile_and_run_java21.bat
pause
