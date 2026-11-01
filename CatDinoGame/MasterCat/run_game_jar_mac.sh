#!/bin/bash

echo "=========================================="
echo "       🐱 CAT DINO GAME - LAUNCHER"
echo "=========================================="
echo ""

JAR_FILE="target/CatDinoGame-1.0-SNAPSHOT-jar-with-dependencies.jar"

if [ -f "$JAR_FILE" ]; then
    echo "🚀 Avvio Cat Dino Game..."
    echo ""
    java -jar "$JAR_FILE"
else
    echo "❌ ERRORE: JAR eseguibile non trovato!"
    echo ""
    echo "💡 SOLUZIONE:"
    echo "   1. Esegui './build_executable_mac.sh' per creare l'eseguibile"
    echo "   2. Oppure usa './compile_and_run_mac.sh' per il gioco in sviluppo"
    echo ""
fi

echo ""
echo "👋 Gioco terminato!"
