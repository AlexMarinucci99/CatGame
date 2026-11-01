#!/bin/bash

echo "=========================================="
echo "   🎮 CAT DINO GAME - CREA ESEGUIBILE"
echo "=========================================="
echo ""

# Colori per output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Verifica se Maven è installato
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}❌ ERRORE: Maven non trovato!${NC}"
    echo ""
    echo -e "${YELLOW}💡 SOLUZIONI:${NC}"
    echo "   1. Installa Maven da: https://maven.apache.org/"
    echo "   2. Oppure installa tramite Homebrew: brew install maven"
    echo ""
    exit 1
fi

echo -e "${BLUE}🔄 Pulizia build precedenti...${NC}"
mvn clean

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Errore durante la pulizia!${NC}"
    exit 1
fi

echo -e "${BLUE}🔨 Compilazione progetto...${NC}"
mvn compile

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Errore durante la compilazione!${NC}"
    exit 1
fi

echo -e "${BLUE}📦 Creazione JAR eseguibile...${NC}"
mvn package

echo ""
JAR_FILE="target/CatDinoGame-1.0-SNAPSHOT-jar-with-dependencies.jar"

if [ -f "$JAR_FILE" ]; then
    echo "=========================================="
    echo -e "           ${GREEN}✅ SUCCESSO!${NC}"
    echo "=========================================="
    echo ""
    echo -e "${GREEN}🎮 ESEGUIBILE CREATO:${NC}"
    echo -e "   📁 $JAR_FILE"
    echo ""
    echo -e "${YELLOW}🚀 PER ESEGUIRE IL GIOCO:${NC}"
    echo "   java -jar $JAR_FILE"
    echo ""
    echo -e "${BLUE}💡 O usa il launcher: ./run_game_jar_mac.sh${NC}"
    echo ""
    
    # Rendi eseguibili tutti gli script sh
    chmod +x *.sh
    echo -e "${GREEN}✅ Script Mac resi eseguibili${NC}"
    echo ""
else
    echo -e "${RED}❌ ERRORE: JAR non creato!${NC}"
    echo -e "${YELLOW}💡 Controlla che Maven sia installato e configurato${NC}"
    exit 1
fi
