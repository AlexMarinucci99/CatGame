#!/bin/bash

# ==================================================
#          🐱 CAT DINO GAME - MAC LAUNCHER  
# ==================================================

echo "=========================================="
echo "       🐱 CAT DINO GAME - LAUNCHER"
echo "=========================================="
echo ""

# Colori per output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Percorso JavaFX
JAVAFX_PATH="lib/javafx-sdk-21.0.2/lib"

# Funzione per verificare se Java è installato
check_java() {
    if ! command -v java &> /dev/null; then
        echo -e "${RED}❌ ERRORE: Java non trovato!${NC}"
        echo ""
        echo -e "${YELLOW}💡 SOLUZIONI:${NC}"
        echo "   1. Installa Java 21 da: https://openjdk.org/"
        echo "   2. Oppure installa tramite Homebrew: brew install openjdk@21"
        echo ""
        exit 1
    fi
    
    # Verifica versione Java
    JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -lt 11 ]; then
        echo -e "${YELLOW}⚠️  ATTENZIONE: Java $JAVA_VERSION rilevato. Consigliato Java 11+${NC}"
    else
        echo -e "${GREEN}✅ Java $JAVA_VERSION rilevato${NC}"
    fi
}

# Funzione per verificare JavaFX
check_javafx() {
    if [ ! -d "$JAVAFX_PATH" ]; then
        echo -e "${RED}❌ ERRORE: JavaFX non trovato in $JAVAFX_PATH${NC}"
        echo ""
        echo -e "${YELLOW}💡 SOLUZIONI:${NC}"
        echo "   1. Scarica JavaFX 21 da: https://openjfx.io/"
        echo "   2. Estrai il contenuto nella cartella lib/"
        echo "   3. Assicurati che il percorso sia: lib/javafx-sdk-21.0.2/"
        echo ""
        exit 1
    fi
    echo -e "${GREEN}✅ JavaFX trovato${NC}"
}

# Funzione per compilare il gioco
compile_game() {
    echo ""
    echo -e "${BLUE}🔨 Compilazione in corso...${NC}"
    
    # Pulizia directory target
    if [ -d "target" ]; then
        rm -rf target
    fi
    mkdir -p target/classes
    
    # Copia le risorse
    if [ -d "src/main/resources" ]; then
        echo -e "${BLUE}📁 Copia delle risorse...${NC}"
        cp -r src/main/resources/* target/classes/
    fi
    
    # Compilazione
    javac --module-path "$JAVAFX_PATH" \
          --add-modules javafx.controls,javafx.fxml,javafx.media \
          -cp src/main/java \
          -d target/classes \
          src/main/java/com/master/bro/catdinogame/*.java
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ Compilazione completata!${NC}"
        return 0
    else
        echo -e "${RED}❌ Errore durante la compilazione!${NC}"
        exit 1
    fi
}

# Funzione per eseguire il gioco
run_game() {
    echo ""
    echo -e "${GREEN}🚀 Avvio del gioco...${NC}"
    echo ""
    
    # Esegui il gioco tramite GameLauncher
    java --module-path "$JAVAFX_PATH" \
         --add-modules javafx.controls,javafx.fxml,javafx.media \
         -cp target/classes \
         com.master.bro.catdinogame.GameLauncher
    
    echo ""
    echo -e "${BLUE}👋 Gioco terminato!${NC}"
}

# Funzione per eseguire il JAR (se esiste)
run_jar() {
    JAR_FILE="target/CatDinoGame-1.0-SNAPSHOT-jar-with-dependencies.jar"
    
    if [ -f "$JAR_FILE" ]; then
        echo -e "${GREEN}🚀 Avvio Cat Dino Game (JAR)...${NC}"
        echo ""
        java -jar "$JAR_FILE"
        echo ""
        echo -e "${BLUE}👋 Gioco terminato!${NC}"
        return 0
    else
        return 1
    fi
}

# Funzione principale
main() {
    echo -e "${BLUE}🔍 Controllo prerequisiti...${NC}"
    check_java
    check_javafx
    
    echo ""
    echo -e "${YELLOW}Scegli il metodo di avvio:${NC}"
    echo "1. Esegui JAR precompilato (più veloce)"
    echo "2. Compila ed esegui dal sorgente"
    echo "3. Solo compilazione"
    echo ""
    echo -n "Inserisci la tua scelta (1-3) [default: 1]: "
    read -r choice
    
    case ${choice:-1} in
        1)
            echo ""
            if ! run_jar; then
                echo -e "${YELLOW}⚠️  JAR non trovato. Compilo dal sorgente...${NC}"
                compile_game
                run_game
            fi
            ;;
        2)
            compile_game
            run_game
            ;;
        3)
            compile_game
            echo -e "${GREEN}✅ Compilazione completata! Usa l'opzione 1 per eseguire.${NC}"
            ;;
        *)
            echo -e "${RED}❌ Scelta non valida!${NC}"
            exit 1
            ;;
    esac
}

# Gestione Ctrl+C
trap 'echo -e "\n${YELLOW}🛑 Operazione interrotta dall\'utente${NC}"; exit 130' INT

# Esegui lo script
main "$@"
