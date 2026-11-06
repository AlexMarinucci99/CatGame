
# MasterCat

![Java](https://img.shields.io/badge/Java-21-orange)
![JavaFX](https://img.shields.io/badge/JavaFX-21.0.2-blue)
![Maven](https://img.shields.io/badge/Build-Maven-blue)
![Platforms](https://img.shields.io/badge/OS-Windows%20%7C%20Linux%20%7C%20macOS-lightgrey)
[![Build and Test](https://github.com/AlexMarinucci99/CatGame/actions/workflows/ci.yml/badge.svg)](https://github.com/AlexMarinucci99/CatGame/actions/workflows/ci.yml)

**CatGame**, conosciuto anche come **MasterCat**, è un videogioco arcade 2D sviluppato in Java con JavaFX.

Il giocatore controlla un gatto che deve sopravvivere a una serie di ostacoli, utilizzare attacchi energetici e affrontare uno scontro finale contro un boss.

Il progetto comprende animazioni del personaggio, effetti grafici, gestione dell'energia, musiche dedicate e un sistema di gioco a difficoltà progressiva.

Il codice sorgente è disponibile pubblicamente e il gioco può essere avviato su **Windows, Linux e macOS**.

---

## Indice

- [Caratteristiche](#caratteristiche)
- [Scarica e gioca](#scarica-e-gioca)
- [Requisiti di sistema](#requisiti-di-sistema)
- [Scaricare i sorgenti](#scaricare-i-sorgenti)
- [Avvio dai sorgenti su Windows](#avvio-dai-sorgenti-su-windows)
- [Avvio dai sorgenti su Linux](#avvio-dai-sorgenti-su-linux)
- [Avvio dai sorgenti su macOS](#avvio-dai-sorgenti-su-macos)
- [Comandi di gioco](#comandi-di-gioco)
- [Come funziona il gioco](#come-funziona-il-gioco)
- [Impostazioni e accessibilità](#impostazioni-e-accessibilità)
- [Compilazione e sviluppo](#compilazione-e-sviluppo)
- [Struttura del progetto](#struttura-del-progetto)
- [Risoluzione dei problemi](#risoluzione-dei-problemi)
- [Compatibilità e test](#compatibilità-e-test)
- [Autore e risorse](#autore-e-risorse)

---

## Caratteristiche

- Gioco arcade 2D con difficoltà progressiva.
- Personaggio animato con salto e doppio salto.
- Ostacoli normali e doppi.
- Sistema di energia e attacchi a distanza.
- Boss finale con attacchi, barriere e limite di tempo.
- Scudo energetico durante il combattimento finale.
- Animazioni del personaggio e del nastro trasportatore.
- Effetti visivi e avvisi degli attacchi del boss.
- Musiche dedicate alle diverse fasi di gioco.
- Menu delle impostazioni.
- Pausa, riavvio e gestione della perdita di focus.
- Modalità grafica con effetti ridotti.
- Supporto multipiattaforma tramite JavaFX e Maven.

## Requisiti di sistema

Per giocare usando gli installer di GitHub Releases non occorre installare Java, JavaFX o Maven. Per compilare e avviare il progetto dai sorgenti servono:

| Componente | Requisito |
|---|---|
| Sistema operativo | Windows, Linux o macOS |
| Java | JDK 21 consigliato |
| Apache Maven | Versione 3.9 o successiva consigliata |
| JavaFX | 21.0.2, scaricato automaticamente |
| Git | Opzionale |
| Connessione Internet | Necessaria per il primo download delle dipendenze |
| Ambiente grafico | Desktop con supporto JavaFX |
| Audio | Dispositivo audio e librerie multimediali compatibili |

## Scarica e gioca

Le versioni installabili sono pubblicate nella pagina [GitHub Releases](https://github.com/AlexMarinucci99/CatGame/releases). Scaricare il pacchetto per il proprio sistema e la propria architettura; non occorrono Java, JavaFX o Maven.

| Sistema | Pacchetto | Architetture |
|---|---|---|
| Windows | Installer `.exe` oppure ZIP portatile | x64 |
| Linux Debian 12 o Ubuntu 22.04+ | Pacchetto `.deb` oppure ZIP portatile | x64 |
| macOS | Immagine `.dmg` oppure ZIP dell'app | Intel x64 e Apple Silicon arm64, build separate |

Il runtime Java 21 e le dipendenze JavaFX con le librerie native della piattaforma sono inclusi. Serve comunque un ambiente desktop grafico compatibile; su Linux il pacchetto richiede GTK 3, ALSA e OpenGL, installati dal gestore dei pacchetti quando necessario. Per l'audio serve un server ALSA/PulseAudio/PipeWire funzionante.

### Windows

Scaricare `CatGame-windows-x64-<versione>.exe`, eseguirlo e seguire l'installer. È disponibile anche lo ZIP portatile: estrarlo ed eseguire `CatGame.exe` dalla cartella estratta. L'installer crea una voce nel menu Start e una scorciatoia.

I pacchetti non sono firmati digitalmente. Windows può mostrare un avviso SmartScreen: verificare che il file provenga dalla pagina Releases ufficiale e scegliere di proseguire solo se ci si fida della provenienza.

### Linux Debian e Ubuntu

Scaricare `CatGame-linux-x86_64-<versione>.deb` e installarlo con il gestore grafico oppure:

```bash
sudo apt install ./CatGame-linux-x86_64-<versione>.deb
```

Avviare CatGame dal menu applicazioni. Il pacchetto `.deb` dichiara le dipendenze di sistema GTK 3, ALSA e OpenGL. È generato su Ubuntu 22.04 per mantenere compatibilità con Debian 12 e Ubuntu 22.04 o versioni successive. Lo ZIP portatile non installa dipendenze di sistema: estrarlo e avviare `CatGame/bin/CatGame`.

### macOS

Scaricare il `.dmg` che corrisponde al processore: `arm64` per Apple Silicon, `x64` per Mac Intel. Aprire il DMG e trascinare CatGame in Applicazioni. Per lo ZIP portatile, estrarre l'app e spostarla in Applicazioni.

L'app non è firmata con Developer ID né notarizzata. Gatekeeper può impedirne l'apertura o mostrare un avviso. macOS richiede firma e notarizzazione Apple Developer per una distribuzione pubblica senza questi avvisi. Non disattivare Gatekeeper: scaricare soltanto dalla pagina ufficiale e seguire le impostazioni di sicurezza macOS solo dopo avere verificato il file.

I pacchetti macOS sono nativi per ciascuna architettura e non sono universal binary; Rosetta non è richiesta per il pacchetto che corrisponde al Mac.

Ogni release include `SHA256SUMS.txt` per controllare l'integrità dei download. Gli artefatti automatici di collaudo non sono release stabili e non vengono pubblicati come versioni finali.

---

## Scaricare i sorgenti

### Metodo 1 — Tramite Git

Aprire il terminale e digitare:

```bash
git clone https://github.com/AlexMarinucci99/CatGame.git
cd CatGame
```

### Metodo 2 — Download ZIP

1. Aprire la [repository GitHub](https://github.com/AlexMarinucci99/CatGame).
2. Selezionare **Code**.
3. Fare clic su **Download ZIP**.
4. Estrarre l'archivio.
5. Aprire la cartella estratta `CatGame-main`.

Non è necessario scaricare manualmente i file sorgente uno alla volta.

---

## Avvio dai sorgenti su Windows

### 1. Installare Java 21

Scaricare il JDK 21 dal sito:

https://adoptium.net/temurin/releases/?version=21

Selezionare:

- Sistema operativo: Windows
- Architettura: x64, oppure quella del proprio dispositivo
- Versione: 21
- Pacchetto: JDK
- Formato: `.msi`

Avviare l'installer e completare l'installazione.

Quando disponibili, abilitare le opzioni per configurare `PATH` e `JAVA_HOME`.

Aprire PowerShell e verificare:

```powershell
java -version
javac -version
```

Entrambi i comandi devono funzionare e mostrare una versione compatibile.

### 2. Installare Apache Maven

Scaricare la distribuzione binaria ZIP da:

https://maven.apache.org/download.cgi

Procedura:

1. Estrarre l'archivio di Maven.
2. Spostare la cartella in una posizione stabile, ad esempio `C:\Tools\`.
3. Aprire **Modifica le variabili d'ambiente di sistema**.
4. Aggiungere al `PATH` la cartella `bin` di Maven, ad esempio:

```text
C:\Tools\apache-maven-3.9.x\bin
```

Sostituire `3.9.x` con la versione effettivamente scaricata.

Se Java non è stato configurato automaticamente, impostare `JAVA_HOME` sulla cartella di installazione del JDK e aggiungere `%JAVA_HOME%\bin` al `PATH`.

Chiudere e riaprire PowerShell, poi verificare:

```powershell
mvn -version
```

Verificare che Maven utilizzi il JDK corretto.

### 3. Avviare CatGame

Aprire la cartella del progetto.

È possibile avviare il file:

```text
run-windows.bat
```

In alternativa, aprire PowerShell nella cartella principale ed eseguire:

```powershell
.\run-windows.bat
```

Lo script controlla la presenza di Java e Maven, compila il progetto e avvia il gioco.

Al primo avvio Maven scaricherà le dipendenze necessarie.

---

## Avvio dai sorgenti su Linux

CatGame utilizza JavaFX e può essere eseguito su distribuzioni Linux compatibili.

### 1. Installare Java e Maven

Su Ubuntu, Linux Mint e distribuzioni basate su Debian, se i pacchetti sono disponibili nei repository configurati:

```bash
sudo apt update
sudo apt install openjdk-21-jdk maven git
```

Su Fedora:

```bash
sudo dnf install java-21-openjdk-devel maven git
```

Se la distribuzione non fornisce il JDK 21, installare Eclipse Temurin 21 dal sito ufficiale:

https://adoptium.net/temurin/releases/?version=21

Se la versione di Maven distribuita dal sistema è precedente alla versione consigliata, è possibile installare una distribuzione aggiornata seguendo:

https://maven.apache.org/install.html

### 2. Verificare l'installazione

Aprire il terminale:

```bash
java -version
javac -version
mvn -version
```

Verificare che Java sia compatibile e che Maven utilizzi il JDK corretto.

Se necessario, configurare la variabile `JAVA_HOME` in base al percorso del JDK installato.

### 3. Scaricare il progetto

```bash
git clone https://github.com/AlexMarinucci99/CatGame.git
cd CatGame
```

Se il progetto è già stato scaricato tramite ZIP, aprire invece il terminale nella cartella estratta.

### 4. Avviare il gioco

```bash
./run-linux.sh
```

Se il sistema segnala che lo script non dispone dei permessi di esecuzione:

```bash
chmod +x run-linux.sh
./run-linux.sh
```

Lo script esegue la compilazione e avvia automaticamente JavaFX.

**Nota:** su alcune distribuzioni potrebbero essere necessarie librerie di sistema aggiuntive per la riproduzione audio e il funzionamento del backend multimediale JavaFX.

Il gioco richiede una sessione desktop grafica; un terminale privo di display non è sufficiente.

---

## Avvio dai sorgenti su macOS

CatGame può essere compilato e avviato su macOS utilizzando Java 21 e Maven.

### 1. Installare Java 21

Scaricare Eclipse Temurin:

https://adoptium.net/temurin/releases/?version=21

Selezionare:

- Sistema operativo: macOS
- Versione: 21
- Pacchetto: JDK
- Architettura: AArch64 per Apple Silicon, x64 per Mac Intel
- Formato: `.pkg`

Eseguire il programma di installazione.

Verificare dal Terminale:

```bash
java -version
javac -version
```

Per selezionare il JDK 21 nella sessione corrente, se necessario:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
export PATH="$JAVA_HOME/bin:$PATH"
```

### 2. Installare Maven

Se Homebrew è già installato:

```bash
brew install maven
```

Per informazioni su Homebrew:

https://brew.sh

In alternativa, installare Maven manualmente dal sito ufficiale:

https://maven.apache.org/download.cgi

Per l'installazione manuale, estrarre l'archivio e aggiungere la cartella `bin` di Maven al `PATH`.

Verificare:

```bash
mvn -version
```

### 3. Scaricare CatGame

```bash
git clone https://github.com/AlexMarinucci99/CatGame.git
cd CatGame
```

In alternativa, estrarre lo ZIP scaricato da GitHub.

### 4. Avviare il gioco

```bash
./run-macos.sh
```

Se necessario, assegnare i permessi di esecuzione:

```bash
chmod +x run-macos.sh
./run-macos.sh
```

Il gioco verrà compilato e avviato tramite Maven.

---

## Comandi di gioco

### Controlli principali

| Comando | Funzione |
|---|---|
| `SPAZIO` | Avvia la partita dal menu |
| `SPAZIO` | Salto e doppio salto durante il gioco |
| Tasto sinistro del mouse | Lancia una sfera energetica con energia piena |
| Tasto destro del mouse | Lancia il pacco esplosivo durante il boss fight, quando la speciale è carica |
| `X` | Attiva lo scudo durante il boss fight |
| `←` / `A` | Movimento verso sinistra nel boss fight |
| `→` / `D` | Movimento verso destra nel boss fight |
| `P` | Mette in pausa o riprende la partita |
| `M` | Attiva o disattiva la musica |
| `U` / `ESC` | Attiva o disattiva la modalità a schermo intero |
| `I` | Apre o chiude le impostazioni nel menu |

### Durante la pausa

| Comando | Funzione |
|---|---|
| `P` | Riprende la partita |
| `R` | Riavvia una nuova partita |
| `Q` | Torna al menu principale |

### Altri comandi

- Nelle schermate di vittoria o sconfitta, un tasto non riservato ai controlli globali permette di tornare al menu.
- Dal menu è possibile utilizzare il mouse per modificare le impostazioni.
- Il tasto `T`, nel menu principale, attiva una scorciatoia di debug che porta direttamente al boss fight.

> La scorciatoia `T` è destinata principalmente allo sviluppo e ai test.

---

## Come funziona il gioco

### Fase 1 — Sopravvivenza

Il giocatore deve evitare i pacchi e gli altri ostacoli che si muovono verso il personaggio.

Durante questa fase:

- Il punteggio aumenta progressivamente.
- Gli ostacoli diventano più veloci.
- La frequenza degli ostacoli cresce nel tempo.
- Il giocatore può utilizzare il doppio salto.
- L'energia si ricarica durante la partita.
- Una barra di energia piena consente di lanciare una sfera energetica con il tasto sinistro del mouse.
- L’HUD mostra separatamente avanzamento verso il boss e ricarica dell’onda.

Il personaggio dispone di animazioni di corsa, salto e atterraggio.

### Fase 2 — Boss fight

Raggiunti **10.000 punti**, inizia il combattimento finale.

| Parametro | Valore |
|---|---|
| Vita iniziale del boss | 2.000 |
| Tempo massimo | 120 secondi |
| Energia iniziale del giocatore | 50 |
| Costo dello scudo | 25 energia |
| Durata dello scudo | 5 secondi |
| Comparsa della barriera | Dopo 60 secondi |
| Ricarica della speciale | 30 secondi di gioco attivo |
| Danno del pacco esplosivo | 1.000 punti, metà della vita massima del boss |

Durante il combattimento:

- Il giocatore può muoversi orizzontalmente.
- Il boss utilizza attacchi differenti.
- Gli attacchi vengono anticipati da avvisi visivi.
- Le sfere energetiche possono distruggere proiettili nemici e parti della barriera.
- Lo scudo fornisce protezione temporanea.
- Il tasto destro lancia il pacco speciale dopo la carica dedicata; non consuma l’energia della sfera.
- Un lancio bloccato dalla barriera o mancato consuma la carica senza danneggiare il boss.
- Il giocatore deve sconfiggere il boss prima della scadenza del timer.

### Vittoria e sconfitta

La partita termina con una vittoria quando il boss viene sconfitto entro il limite di tempo.

Il giocatore può perdere in seguito a collisioni o al mancato completamento del boss fight entro il tempo disponibile.

Dopo la conclusione è possibile tornare al menu e iniziare una nuova partita.

---

## Impostazioni e accessibilità

Dal menu principale è possibile accedere al pannello delle impostazioni tramite il tasto `I` oppure utilizzando il mouse.

Le opzioni disponibili comprendono:

- Attivazione e disattivazione della musica.
- Regolazione del volume.
- Attivazione e disattivazione del fullscreen.
- Modalità con effetti grafici ridotti.

La modalità con effetti ridotti utilizza una rappresentazione più semplice del personaggio e del nastro trasportatore.

Le impostazioni vengono mantenute durante la sessione corrente del gioco.

Quando la finestra perde il focus durante una partita attiva, il gioco entra automaticamente in pausa e rilascia gli input di movimento.

---

## Compilazione e sviluppo

CatGame è un progetto Maven.

Il file `pom.xml` contiene le dipendenze, la configurazione di compilazione e il plugin JavaFX.

### Compilare ed eseguire i test

Dalla cartella principale:

```bash
mvn clean verify
```

### Avviare il gioco direttamente con Maven

```bash
mvn javafx:run
```

### Compilare senza eseguire i test

```bash
mvn -DskipTests package
```

Gli script di avvio utilizzano Maven con la compilazione del progetto e l'esecuzione tramite il plugin JavaFX.

Il JAR Maven in `target/` è un artefatto di sviluppo e non è autonomo. Le app-image e gli installer jpackage includono le dipendenze JavaFX native e un runtime Java dedicato. Per verificare localmente il packaging, dopo `mvn clean verify` usare `scripts/package.sh` su Linux/macOS o `scripts/package.ps1` su Windows; i file vengono creati in `target/distributions/`.

### Tecnologie

| Tecnologia | Utilizzo |
|---|---|
| Java 21 | Linguaggio e runtime |
| JavaFX 21.0.2 | Grafica, interfaccia e multimedia |
| Apache Maven | Gestione delle dipendenze e build |
| JUnit 5 | Test automatizzati |
| GitHub Actions | Compilazione e test multipiattaforma |

---

## Struttura del progetto

```text
CatGame/
├── .github/
│   └── workflows/
│       └── ci.yml
├── docs/
│   ├── ARCHITECTURE.md
│   ├── ART_ASSETS.md
│   ├── GAMEPLAY_BASELINE.md
│   ├── IMPROVEMENT_ROADMAP.md
│   ├── REFACTOR_AUDIT.md
│   └── TESTING.md
├── legacy-assets/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/master/bro/catdinogame/
│   │   └── resources/
│   │       ├── audio/
│   │       └── images/
│   └── test/
├── .gitignore
├── CHANGELOG.md
├── pom.xml
├── run-windows.bat
├── run-linux.sh
├── run-macos.sh
└── README.md
```

### Componenti principali

| Classe | Responsabilità |
|---|---|
| `GameLauncher` | Punto di ingresso dell'applicazione |
| `MasterCat` | Stato principale e coordinamento del gioco |
| `GameUpdate` | Aggiornamento di fisica, ostacoli e combattimento |
| `GameRenderer` | Rendering delle schermate e degli elementi grafici |
| `GameInput` | Gestione dei controlli |
| `GameAssets` | Caricamento delle risorse grafiche |
| `GameAudio` | Gestione della musica e delle risorse audio |
| `PlayerAnimation` | Animazioni del personaggio |
| `PlayerHitbox` | Area di collisione del personaggio |
| `Obstacle` | Rappresentazione degli ostacoli |
| `BarrierBlock` | Blocchi della barriera |
| `ConveyorRenderer` | Rendering del nastro trasportatore |
| `SettingsLayout` | Layout delle impostazioni |

Per ulteriori dettagli consultare:

- [Architettura](docs/ARCHITECTURE.md)
- [Roadmap di miglioramento](docs/IMPROVEMENT_ROADMAP.md)
- [Test e verifiche](docs/TESTING.md)
- [Registro delle modifiche](CHANGELOG.md)

---

## Risoluzione dei problemi

### Java non viene riconosciuto

Se compare un errore simile a:

```text
java: command not found
```

oppure:

```text
'java' non è riconosciuto come comando
```

Verificare che il JDK sia installato e che la cartella `bin` sia presente nel `PATH`.

Controllare anche la variabile `JAVA_HOME`.

### Maven non viene riconosciuto

Se `mvn -version` non funziona:

1. Verificare che Maven sia stato installato.
2. Controllare che la cartella `bin` di Maven sia nel `PATH`.
3. Riavviare il terminale.
4. Riprovare il comando.

### Errore di versione Java

CatGame viene compilato per Java 21.

Verificare:

```bash
java -version
javac -version
mvn -version
```

Se sono presenti più installazioni Java, configurare `JAVA_HOME` in modo che Maven utilizzi un JDK compatibile.

### Le dipendenze non vengono scaricate

Al primo avvio Maven deve scaricare librerie e plugin.

Controllare:

- Connessione Internet.
- Accesso ai repository Maven.
- Eventuali firewall o proxy.
- Configurazione Maven locale.

In seguito riprovare:

```bash
mvn clean javafx:run
```

### Il gioco non mostra la finestra

Verificare che sia disponibile una sessione grafica funzionante.

Su Linux, avviare il gioco da un ambiente desktop anziché da una sessione esclusivamente testuale.

Verificare eventuali errori JavaFX riportati nel terminale.

### Problemi audio

JavaFX Media utilizza componenti multimediali che possono dipendere dal sistema operativo.

Controllare il dispositivo audio, il volume e le librerie multimediali disponibili.

Su Linux potrebbero essere necessari pacchetti GStreamer aggiuntivi.

### Lo script Linux o macOS non parte

Eseguire:

```bash
chmod +x run-linux.sh
```

oppure:

```bash
chmod +x run-macos.sh
```

Quindi ripetere l'avvio con lo script appropriato.

---

## Compatibilità e test

Il progetto include un workflow GitHub Actions che esegue compilazione e test su:

- Windows
- Ubuntu Linux
- macOS

La configurazione utilizza Java 21 ed esegue:

```bash
mvn -B -ntp clean verify
```

È possibile consultare gli esiti delle esecuzioni nella sezione [Actions](https://github.com/AlexMarinucci99/CatGame/actions).

**Nota:** il superamento dei test automatici non garantisce da solo il corretto funzionamento grafico e audio su ogni computer.

Per una verifica completa sono necessari anche test interattivi sul sistema operativo di destinazione.

---

## Autore e risorse

**Sviluppato e mantenuto da Alessandro Marinucci.**

- GitHub: [AlexMarinucci99](https://github.com/AlexMarinucci99)
- Repository: [CatGame](https://github.com/AlexMarinucci99/CatGame)
- Segnalazioni: [GitHub Issues](https://github.com/AlexMarinucci99/CatGame/issues)

Il progetto contiene immagini, animazioni e tracce audio tutte con copyright.

Le informazioni disponibili sull'origine degli asset sono riportate in [ART_ASSETS.md](docs/ART_ASSETS.md).

I termini di utilizzo e redistribuzione delle risorse originali devono essere verificati prima di un'eventuale distribuzione pubblica.

---

**CatGame — MasterCat**

Progetto JavaFX multipiattaforma in continua evoluzione.
