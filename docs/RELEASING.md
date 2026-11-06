# Build e pubblicazione delle release native

Il workflow `.github/workflows/release.yml` è separato dal workflow CI. Usa JDK 21, Maven, jpackage e i runner nativi GitHub Actions:

- Windows x64: installer EXE con voci nel menu Start e scorciatoia, più ZIP dell'app-image.
- Ubuntu 22.04 x64: pacchetto DEB compatibile con Debian 12/Ubuntu 22.04+, con launcher desktop, più ZIP dell'app-image.
- macOS Intel x64 e Apple Silicon arm64: DMG e ZIP dell'app-image distinti.

Il runner Windows installa WiX Toolset 3.14.1, necessario a jpackage 21 per creare l'installer EXE. Il runner Ubuntu è fissato a 22.04 così i nomi delle dipendenze native del `.deb` restano compatibili con Debian 12; non usare `ubuntu-latest` per questa build senza prima verificare i metadati delle dipendenze.

Il packaging usa classpath Java, senza `module-info.java`. Maven copia nel bundle il JAR e tutte le dipendenze runtime risolte sul runner, compresi i classifier JavaFX nativi per quel sistema. jpackage include un runtime Java 21. Il controllo di packaging confronta ogni file di `src/main/resources` con le voci del JAR e verifica app-image, launcher, runtime e installer.

## Collaudo manuale

1. Integrare il workflow su `main`.
2. Aprire GitHub → **Actions** → **Native packages** → **Run workflow**.
3. Selezionare il branch da collaudare e avviare **Run workflow**. Questo percorso non crea una Release.
4. Attendere i quattro job Windows/Linux/macOS Intel/macOS ARM e scaricare gli artefatti `catgame-*` dalla run.
5. Installare e avviare ogni formato su sistemi compatibili; il job CI verifica la build e la struttura del bundle, ma non sostituisce il test grafico/audio interattivo sui sistemi reali.

La pipeline di distribuzione viene eseguita anche quando si invia un tag `v*`. Il job di release controlla che il commit del tag sia già antenato di `main`, cioè integrato nella cronologia approvata. Non vengono creati tag automaticamente.

## Prima prerelease

Creare il tag soltanto dopo che il packaging manuale è riuscito e i pacchetti sono stati provati. Per esempio:

```bash
git switch main
git pull --ff-only
git tag -a v1.0.0-beta.1 -m "CatGame 1.0.0 beta 1"
git push origin v1.0.0-beta.1
```

Il tag conserva il suffisso prerelease per GitHub. jpackage accetta soltanto una versione numerica da uno a tre componenti e richiede che la prima sia maggiore di zero; quindi `v1.0.0-beta.1` diventa `1.0.0` nel nome e nei metadati del pacchetto. Dopo i quattro job, il workflow carica installer, ZIP e `SHA256SUMS.txt` in una GitHub Release **draft prerelease**.

Controllare i file, i checksum, le note generate e la compatibilità sui sistemi target nella bozza. Pubblicare la prerelease manualmente dalla pagina GitHub Releases solo dopo questa verifica. Per una versione successiva si ripete lo stesso processo con un nuovo tag su un commit già presente su `main`.

## Firma e requisiti residui

Il repository non contiene certificati o chiavi. Gli installer Windows non sono firmati e possono mostrare SmartScreen. Le app macOS non sono firmate Developer ID né notarizzate e possono ricevere avvisi Gatekeeper; una distribuzione pubblica macOS senza questi avvisi richiede un account Apple Developer e la gestione esterna delle credenziali di firma/notarizzazione. Linux usa un `.deb` x64 che dichiara GTK 3, ALSA e OpenGL come dipendenze; un ambiente grafico e un server audio funzionante restano necessari.

Le build macOS sono native separate per Intel e Apple Silicon, non universal binary. Al momento Windows e Linux sono distribuiti solo per x64; Linux supportato dal pacchetto installabile è Debian 12/Ubuntu 22.04 o successivi con le dipendenze dichiarate.
