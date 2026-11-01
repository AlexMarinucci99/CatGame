#!/bin/bash

# ==================================================
#    🐱 CAT DINO GAME - COMPILE & RUN (MAC)
# ==================================================

echo "🔨 Compilazione e esecuzione con Java + JavaFX..."

# Percorso JavaFX
JAVAFX_PATH="lib/javafx-sdk-21.0.2/lib"

# Verifica se JavaFX è presente
if [ ! -d "$JAVAFX_PATH" ]; then
    echo "❌ JavaFX non trovato! Scarica JavaFX 21 e mettilo in lib/"
    echo "💡 Download: https://openjfx.io/"
    exit 1
fi

# Pulizia directory target
if [ -d "target" ]; then
    rm -rf target
fi
mkdir -p target/classes

# Copia le risorse (inclusi i file audio)
if [ -d "src/main/resources" ]; then
    echo "📁 Copia delle risorse..."
    cp -r src/main/resources/* target/classes/
fi

echo "🔨 Compilazione..."
javac --module-path "$JAVAFX_PATH" \
      --add-modules javafx.controls,javafx.fxml,javafx.media \
      -cp src/main/java \
      -d target/classes \
      src/main/java/com/master/bro/catdinogame/*.java

if [ $? -ne 0 ]; then
    echo "❌ Errore durante la compilazione!"
    exit 1
fi

echo "✅ Compilazione completata!"
echo "🚀 Avvio del gioco..."

# Esecuzione tramite GameLauncher
java --module-path "$JAVAFX_PATH" \
     --add-modules javafx.controls,javafx.fxml,javafx.media \
     -cp target/classes \
     com.master.bro.catdinogame.GameLauncher

echo "👋 Gioco terminato!"
