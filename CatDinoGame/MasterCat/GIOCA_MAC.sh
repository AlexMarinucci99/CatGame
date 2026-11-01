#!/bin/bash

# ==================================================
#     🐱 CAT DINO GAME - GIOCA SUBITO! (MAC)
# ==================================================

clear
echo "🐱 ============================================= 🐱"
echo "        MASTERCAT - AVVENTURA SPAZIALE"
echo "🐱 ============================================= 🐱"
echo ""
echo "🚀 Preparazione del gioco in corso..."
echo ""

# Rendi eseguibili tutti gli script
chmod +x *.sh 2>/dev/null

# Verifica Java
if ! command -v java &> /dev/null; then
    echo "❌ Java non trovato!"
    echo ""
    echo "💡 Installa Java con:"
    echo "   brew install openjdk@21"
    echo ""
    echo "📥 Oppure scarica da: https://adoptium.net/"
    exit 1
fi

# Verifica JavaFX
if [ ! -d "lib/javafx-sdk-21.0.2/lib" ]; then
    echo "❌ JavaFX non trovato!"
    echo ""
    echo "📥 Scarica JavaFX 21 da: https://openjfx.io/"
    echo "📁 Mettilo nella cartella lib/"
    exit 1
fi

# Prova a eseguire il JAR se esiste
if [ -f "target/CatDinoGame-1.0-SNAPSHOT-jar-with-dependencies.jar" ]; then
    echo "✅ Eseguibile trovato! Avvio..."
    echo ""
    java -jar target/CatDinoGame-1.0-SNAPSHOT-jar-with-dependencies.jar
else
    echo "🔨 Compilazione necessaria..."
    echo ""
    
    # Compila il gioco
    JAVAFX_PATH="lib/javafx-sdk-21.0.2/lib"
    
    # Pulizia e creazione directory
    rm -rf target 2>/dev/null
    mkdir -p target/classes
    
    # Copia risorse
    if [ -d "src/main/resources" ]; then
        cp -r src/main/resources/* target/classes/ 2>/dev/null
    fi
    
    # Compilazione
    echo "⚙️  Compilazione..."
    javac --module-path "$JAVAFX_PATH" \
          --add-modules javafx.controls,javafx.fxml,javafx.media \
          -cp src/main/java \
          -d target/classes \
          src/main/java/com/master/bro/catdinogame/*.java
    
    if [ $? -eq 0 ]; then
        echo "✅ Compilazione riuscita!"
        echo "🚀 Avvio del gioco..."
        echo ""
        
        # Esecuzione
        java --module-path "$JAVAFX_PATH" \
             --add-modules javafx.controls,javafx.fxml,javafx.media \
             -cp target/classes \
             com.master.bro.catdinogame.GameLauncher
    else
        echo "❌ Errore di compilazione!"
        echo ""
        echo "💡 Prova a eseguire: ./run_game_mac.sh"
        exit 1
    fi
fi

echo ""
echo "🎮 Grazie per aver giocato!"
echo "👋 Alla prossima avventura spaziale!"
