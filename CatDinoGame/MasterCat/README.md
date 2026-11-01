# 🎮 Cat Dino Game - Eseguibile Semplice

## 🚀 SOLUZIONE PIÙ SEMPLICE (UN SOLO FILE)

**Per avviare il gioco con UN SOLO CLICK:**

1. **Vai nella cartella principale** (CatDinoGame)
2. **Fai doppio click su `AVVIA_GIOCO.bat`**
3. **Il gioco si compilerà e partirà automaticamente!**

Questo file fa tutto: compila il gioco se necessario e lo avvia subito!

## 🎯 SOLUZIONI ALTERNATIVE

### Metodo 1: Compila una volta, gioca sempre
1. **Fai doppio click su `crea_gioco.bat`** (nella cartella CatDinoGame)
2. **Aspetta la compilazione** (30 secondi)
3. **Vai nella cartella `gioco`** e usa uno di questi:
   - `GIOCA.bat` - Versione principale
   - `GIOCA_SEMPLICE.bat` - Versione più compatibile (risolve errori grafici)
   - `GIOCA_AVANZATO.bat` - Versione con diagnostica completa

### Metodo 2: Usa il launcher principale
- **Fai doppio click su `run_game.bat`** (cartella principale)

## 🔧 RISOLUZIONE ERRORI JAVAFX

Se vedi errori come "Graphics Device initialization failed" o "QuantumRenderer error":

1. **Usa `GIOCA_SEMPLICE.bat`** invece di `GIOCA.bat`
2. **Esegui come amministratore**
3. **Aggiorna i driver grafici**
4. **Il rendering software è più compatibile ma più lento**

## 📦 Come Distribuire

1. **Compila il gioco** con `crea_gioco.bat` o `AVVIA_GIOCO.bat`
2. **Copia l'intera cartella `gioco`** su qualsiasi PC
3. **L'altro PC deve avere Java 21+**
4. **Usa `GIOCA_SEMPLICE.bat`** per massima compatibilità

## ✅ Requisiti

- **Java 21 o superiore**
- **Windows** (i file .bat funzionano solo su Windows)

## 🎮 Controlli del Gioco

- **SPAZIO:** Salto
- **Frecce sinistra/destra:** Movimento laterale (nel boss fight)
- **ESC:** Pausa/Menu

## 📏 Informazioni Tecniche

- **Dimensione:** circa 60MB (include tutte le librerie JavaFX)
- **Tipo:** Cartella con eseguibile batch + classi Java + librerie
- **Compatibilità:** Funziona su qualsiasi PC Windows con Java 21+
- **Rendering:** Supporta hardware e software rendering

---

**✨ RACCOMANDAZIONE: Usa `AVVIA_GIOCO.bat` per la massima semplicità - UN SOLO FILE per tutto!**
