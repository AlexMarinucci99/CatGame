#!/bin/bash

echo "🔧 Configurazione permessi script per Mac..."
echo ""

# Lista degli script da rendere eseguibili
scripts=(
    "run_game_mac.sh"
    "compile_and_run_mac.sh" 
    "build_executable_mac.sh"
    "run_game_jar_mac.sh"
    "GIOCA_MAC.sh"
    "setup_mac.sh"
)

# Rendi eseguibili tutti gli script
for script in "${scripts[@]}"; do
    if [ -f "$script" ]; then
        chmod +x "$script"
        echo "✅ $script - ora eseguibile"
    else
        echo "⚠️  $script - file non trovato"
    fi
done

echo ""
echo "🎯 Configurazione completata!"
echo ""
echo "🚀 Avvia il gioco con uno di questi comandi:"
echo "   ./GIOCA_MAC.sh              (più semplice)"
echo "   ./run_game_mac.sh           (completo)"
echo "   ./compile_and_run_mac.sh    (compila sempre)"
echo ""
