# 🐱 MasterCat - Avventura Spaziale (macOS)

## 🍎 Guida di Installazione e Esecuzione per Mac

### 📋 Prerequisiti

1. **Java 11 o superiore** (consigliato Java 21)
   ```bash
   # Verifica la versione di Java
   java -version
   
   # Se non hai Java, installalo con Homebrew:
   brew install openjdk@21
   
   # Oppure scarica da: https://adoptium.net/
   ```

2. **JavaFX 21** (già incluso nella cartella `lib/`)
   - Se mancante, scarica da: https://openjfx.io/

3. **Maven** (per creare l'eseguibile JAR)
   ```bash
   # Installa Maven con Homebrew:
   brew install maven
   
   # Verifica l'installazione:
   mvn -version
   ```

### 🚀 Metodi di Esecuzione

#### 🎯 Metodo 1: Launcher Interattivo (Consigliato)
```bash
# Rendi eseguibile e avvia il launcher
chmod +x run_game_mac.sh
./run_game_mac.sh
```

#### ⚡ Metodo 2: Esecuzione Rapida
```bash
# Compila ed esegui direttamente
chmod +x compile_and_run_mac.sh
./compile_and_run_mac.sh
```

#### 📦 Metodo 3: Crea JAR Eseguibile
```bash
# Crea l'eseguibile JAR
chmod +x build_executable_mac.sh
./build_executable_mac.sh

# Poi esegui il JAR
chmod +x run_game_jar_mac.sh
./run_game_jar_mac.sh
```

#### 🔧 Metodo 4: Manuale
```bash
# Compilazione manuale
javac --module-path lib/javafx-sdk-21.0.2/lib \
      --add-modules javafx.controls,javafx.fxml,javafx.media \
      -cp src/main/java \
      -d target/classes \
      src/main/java/com/master/bro/catdinogame/*.java

# Esecuzione manuale
java --module-path lib/javafx-sdk-21.0.2/lib \
     --add-modules javafx.controls,javafx.fxml,javafx.media \
     -cp target/classes \
     com.master.bro.catdinogame.GameLauncher
```

### 🎮 Controlli di Gioco

| Tasto | Azione |
|-------|--------|
| **SPAZIO** | Salto singolo |
| **SPAZIO x2** | Doppio salto |
| **C** | Lancia Onda Energetica |
| **X** | Scudo (Boss Fight) |
| **← → (A/D)** | Movimento (Boss Fight) |
| **M** | Toggle Audio |
| **ESC / U** | Fullscreen ON/OFF |
| **I** | Impostazioni (Menu) |

### 🗂️ Struttura File Mac

```
MasterCat/
├── run_game_mac.sh              # 🎯 Launcher principale (interattivo)
├── compile_and_run_mac.sh       # ⚡ Compila ed esegui
├── build_executable_mac.sh      # 📦 Crea JAR eseguibile
├── run_game_jar_mac.sh          # 🚀 Esegui JAR
├── README_MAC.md                # 📖 Questa guida
└── ...
```

### 🔧 Risoluzione Problemi

#### ❌ "Permission denied"
```bash
# Rendi eseguibili tutti gli script:
chmod +x *.sh
```

#### ❌ "Java not found"
```bash
# Installa Java:
brew install openjdk@21

# Aggiungi al PATH (nel tuo ~/.zshrc o ~/.bash_profile):
export PATH="/opt/homebrew/opt/openjdk@21/bin:$PATH"
```

#### ❌ "JavaFX not found"
```bash
# Verifica che la cartella esista:
ls -la lib/javafx-sdk-21.0.2/lib

# Se mancante, scarica JavaFX 21 da https://openjfx.io/
# ed estrailo nella cartella lib/
```

#### ❌ "Maven not found"
```bash
# Installa Maven:
brew install maven

# Verifica:
mvn -version
```

### 🎯 Avvio Veloce (TL;DR)

1. Apri il Terminale
2. Naviga nella cartella del gioco
3. Esegui: `chmod +x *.sh && ./run_game_mac.sh`
4. Scegli l'opzione 1 e divertiti! 🎮

### 🆘 Supporto

Se hai problemi:
1. Verifica che Java 11+ sia installato
2. Controlla che JavaFX sia presente in `lib/javafx-sdk-21.0.2/`
3. Assicurati che gli script abbiano i permessi di esecuzione
4. Usa `./run_game_mac.sh` per diagnostica automatica

---

**🎮 Buon divertimento con MasterCat - Avventura Spaziale!** 🚀
