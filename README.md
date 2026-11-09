# CatGame / MasterCat

CatGame è un gioco 2D in Java 21 e JavaFX. MasterCat supera un livello a nastro trasportatore, attraversa un centro di smistamento platform e affronta il boss finale.

**Per giocare non servono Java, JavaFX o Maven.** Scarica il pacchetto del tuo sistema dalla [pagina delle versioni](https://github.com/AlexMarinucci99/CatGame/releases). La prima versione di prova è [v1.0.0-beta.1](https://github.com/AlexMarinucci99/CatGame/releases/tag/v1.0.0-beta.1).

## Scarica e gioca

Apri la [versione di prova](https://github.com/AlexMarinucci99/CatGame/releases/tag/v1.0.0-beta.1), espandi **Assets** e scegli il file indicato qui sotto. Il nome `1.0.0` nei pacchetti è il numero accettato da `jpackage`; `beta.1` resta nel tag GitHub. Gli ZIP sono alternative portatili agli installer.

| Sistema | Installer | Alternativa portatile |
|---|---|---|
| Windows 10/11, x64 | `CatGame-windows-x64-1.0.0.exe` | `CatGame-windows-x64-1.0.0.zip` |
| Debian 12 / Ubuntu 22.04 o successivi, x64 | `CatGame-linux-x86_64-1.0.0.deb` | `CatGame-linux-x86_64-1.0.0.zip` |
| Mac con Apple Silicon | `CatGame-macos-arm64-1.0.0.dmg` | `CatGame-macos-arm64-1.0.0.zip` |
| Mac Intel | `CatGame-macos-x86_64-1.0.0.dmg` | `CatGame-macos-x86_64-1.0.0.zip` |

**Windows:** esegui l'EXE e apri CatGame dal menu Start. Se usi lo ZIP, estrailo completamente e avvia `CatGame/CatGame.exe`.

**Linux:** apri il DEB con il gestore pacchetti oppure, da terminale nella cartella del download, usa `sudo apt install ./CatGame-linux-x86_64-1.0.0.deb`. Avvia CatGame dal menu applicazioni. Per lo ZIP, estrailo e avvia `CatGame/bin/CatGame`. Servono desktop grafico, GTK 3, ALSA e OpenGL; il DEB dichiara le dipendenze.

**macOS:** scegli il DMG `arm64` per chip Apple Silicon (M1 e successivi) o `x86_64` per Mac Intel. Apri il DMG e copia `CatGame.app` in Applicazioni. Lo ZIP contiene la stessa app.

I pacchetti Windows e macOS **non sono firmati**. SmartScreen e Gatekeeper possono mostrare avvisi; verifica sempre che il download provenga dalla release ufficiale. Per la distribuzione Mac senza avvisi servono firma Developer ID e notarizzazione. La release include `SHA256SUMS.txt` per controllare l'integrità dei file.

Questa è una **versione beta**: la build, le risorse e la struttura dei pacchetti sono state verificate sui runner Windows, Linux e macOS. L'installazione e l'avvio grafico/audio richiedono ancora prove su computer reali dei rispettivi sistemi.

## Come giocare

Dal menu premi **SPAZIO** per iniziare. Il percorso normale è livello 1 (10.000 punti) → livello 2 platform → boss → vittoria. **F2** apre direttamente il livello 2 e **T** il boss: sono scorciatoie di collaudo disponibili solo dal menu.

| Comando | Azione |
|---|---|
| SPAZIO | Salto; doppio salto nel livello 2 e nel boss, wall-jump nel livello 2 |
| A/D oppure frecce | Movimento nel livello 2 e nel boss |
| SHIFT mantenuto | Sprint nel livello 2 |
| Clic sinistro | Onda energetica quando l'energia è piena |
| Clic destro | Pacco esplosivo contro il boss quando la mossa è carica |
| X | Scudo durante il boss, con almeno 25 energia |
| P | Pausa/riprendi |
| R | Ricomincia il livello 2, anche dalla pausa; nelle altre pause riavvia la partita |
| Q | Torna al menu dalla pausa |
| M | Musica |
| U oppure ESC | Fullscreen |
| I | Impostazioni dal menu |

Nel livello 2 tieni premuto SPAZIO per un salto più alto e rilascialo per accorciarlo. Puoi fare un salto aggiuntivo in aria, ricaricato all'atterraggio. I checkpoint permettono di riprovare rapidamente dopo una trappola.

## Per sviluppare

Servono **JDK 21** e **Maven**. JavaFX 21.0.2 è gestito dal progetto. Clona il repository, poi nella cartella CatGame esegui:

```bash
mvn clean verify
mvn javafx:run
```

Gli script `run-windows.bat`, `run-linux.sh` e `run-macos.sh` avviano il gioco dai sorgenti. Per creare nuovi pacchetti, apri **Actions → Native packages → Run workflow**: i quattro artefatti restano scaricabili dalla run per 14 giorni. Il workflow di release su tag pubblica inizialmente una bozza; [procedura e limiti](docs/RELEASING.md).

Approfondimenti: [design del livello 2](docs/LEVEL_2_DESIGN.md), [fisica](docs/PLATFORMER_PHYSICS.md), [collaudo](docs/LEVEL_2_TESTING.md), [arte dello scudo](docs/SHIELD_ART.md).

Progetto di [AlexMarinucci99](https://github.com/AlexMarinucci99/CatGame). Le immagini e l'audio del gioco sono inclusi in `src/main/resources`.
