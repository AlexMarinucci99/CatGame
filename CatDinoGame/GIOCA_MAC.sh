#!/bin/bash

# ==================================================
#     🐱 CAT DINO GAME - LAUNCHER PRINCIPALE MAC
# ==================================================

clear
echo "🐱 ============================================= 🐱"
echo "        MASTERCAT - AVVENTURA SPAZIALE"
echo "🐱 ============================================= 🐱"
echo ""
echo "🚀 Avvio del gioco per Mac..."
echo ""

# Directory del gioco
GAME_DIR="MasterCat"

# Verifica che la directory del gioco esista
if [ ! -d "$GAME_DIR" ]; then
    echo "❌ ERRORE: Cartella del gioco non trovata!"
    echo ""
    echo "💡 Assicurati che la cartella 'MasterCat' sia presente"
    echo "📁 Struttura richiesta:"
    echo "   CatDinoGame/"
    echo "   └── MasterCat/     ← deve essere qui"
    echo "       ├── src/"
    echo "       ├── lib/"
    echo "       └── ..."
    exit 1
fi

# Naviga nella directory del gioco
cd "$GAME_DIR"

# Verifica Java
if ! command -v java &> /dev/null; then
    echo "❌ Java non trovato!"
    echo ""
    echo "💡 INSTALLAZIONE JAVA:"
    echo "   1. Homebrew: brew install openjdk@21"
    echo "   2. Download: https://adoptium.net/"
    echo ""
    echo "🔄 Dopo l'installazione, riavvia questo script"
    exit 1
fi

echo "✅ Java trovato: $(java -version 2>&1 | head -1 | cut -d'"' -f2)"

# Verifica JavaFX
if [ ! -d "lib/javafx-sdk-21.0.2/lib" ]; then
    echo "❌ JavaFX non trovato!"
    echo ""
    echo "💡 INSTALLAZIONE JAVAFX:"
    echo "   1. Scarica JavaFX 21: https://openjfx.io/"
    echo "   2. Estrai in: MasterCat/lib/javafx-sdk-21.0.2/"
    echo ""
    exit 1
fi

echo "✅ JavaFX trovato"

# Rendi eseguibili tutti gli script nella cartella MasterCat
chmod +x *.sh 2>/dev/null

echo "🎮 Controllo modalità di esecuzione..."

# Prova a eseguire il JAR se esiste (più veloce)
JAR_FILE="target/CatDinoGame-1.0-SNAPSHOT-jar-with-dependencies.jar"
if [ -f "$JAR_FILE" ]; then
    echo "✅ Eseguibile JAR trovato!"
    echo "🚀 Avvio veloce..."
    echo ""
    java -jar "$JAR_FILE"
else
    echo "🔨 JAR non trovato, compilo dal sorgente..."
    echo ""
    
    # Percorso JavaFX
    JAVAFX_PATH="lib/javafx-sdk-21.0.2/lib"
    
    # Pulizia e creazione directory
    rm -rf target 2>/dev/null
    mkdir -p target/classes
    
    # Copia risorse se esistono
    if [ -d "src/main/resources" ]; then
        echo "📁 Copia risorse..."
        cp -r src/main/resources/* target/classes/ 2>/dev/null
    fi
    
    # Compilazione
    echo "⚙️  Compilazione in corso..."
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
        echo "💡 SOLUZIONI:"
        echo "   1. Controlla che Java 11+ sia installato"
        echo "   2. Verifica che JavaFX sia in lib/javafx-sdk-21.0.2/"
        echo "   3. Prova: cd MasterCat && ./run_game_mac.sh"
        exit 1
    fi
fi

# Torna alla directory principale
cd ..

echo ""
echo "🎮 Grazie per aver giocato a MasterCat!"
echo "🚀 Alla prossima avventura spaziale!"
echo ""
