# Verifiche del livello 2

Verifica locale del 6 novembre 2026: `mvn -B -ntp clean verify` completato con **60 test, 0 errori, 0 fallimenti, 0 test saltati** su Linux/JDK 21.

## Test automatici

Eseguire dalla radice del repository:

```sh
mvn clean verify
```

`PlatformerTest` verifica accelerazione, frenata, inversione, salto, salto variabile, coyote time e sua scadenza, buffering, wall-slide/jump, pareti/pavimenti/soffitti, contatti letali, reset input, checkpoint e modalità senza checkpoint, trasporto su piattaforme e nastri, schiacciamento, pausa e debounce, focus, riavvio, camera, progressione e protezione da un Game Over tardivo durante la transizione.

`PlatformerTraversalTest` riproduce `src/test/resources/levels/sorting-replay.txt`: 212 comandi di 0,1 s, generati con una ricerca deterministica sul modello. Il replay parte dallo spawn reale, usa soltanto direzione e pressione/rilascio del salto, attraversa le quattro sezioni e raggiunge l'uscita senza morti in circa 21,19 secondi simulati. Non modifica coordinate, hitbox o trappole. Viene ripetuto a 30, 60 e 144 FPS. La codifica di ogni carattere è `(direzione + 1) * 2 + saltoPremuto`, con direzione -1/0/+1 e salto 0/1.

Questo replay dimostra la raggiungibilità della mappa e protegge da regressioni di collisione/fisica. Non è un playthrough umano e non dimostra da solo che difficoltà, finestre di reazione e feeling siano ottimali.

Il test del classpath include il CSV del livello e gli asset esistenti. Le regressioni del runner, del boss e dello scudo restano nella suite; il solo test della vecchia progressione immediata al boss è stato aggiornato alla nuova transizione richiesta.

## Verifica grafica

Sono stati eseguiti snapshot reali JavaFX/Linux delle quattro sezioni, con gli asset caricati dal classpath. Immagini in `docs/screenshots/level2-*.png`. Le posizioni sono impostate appositamente per documentare i quattro ambienti. Nessun playthrough manuale completo dichiarato.

## Collaudo manuale riproducibile

1. Avviare `./run-linux.sh`, `run-windows.bat` oppure `./run-macos.sh` (o `mvn javafx:run`).
2. Nel menu premere F2 per il solo collaudo del livello 2. La scorciatoia T al boss resta disponibile; entrambe funzionano solo dal menu.
3. Provare A/D e frecce, salto breve/lungo, inversione in aria, wall-slide e wall-jump nei pozzi.
4. Toccare una trappola e verificare respawn rapido, contatore morti e checkpoint.
5. Premere P durante un salto; tenere premuto P per verificare il debounce. Cambiare finestra e riprendere: non devono restare direzioni attive.
6. Premere R nel platform, poi ripetere dalla pausa: ripartenza dall'ingresso con timer e morti azzerati.
7. Provare ridimensionamento e fullscreen U/ESC, musica M, opzione effetti ridotti.
8. Attraversare l'uscita e verificare boss, sfera al clic sinistro, scudo X e pacco speciale al clic destro.
9. Collaudare anche il percorso normale con SPAZIO dal menu: solo a 10.000 punti deve iniziare il livello 2.

## Limiti

Prototipo con ambiente procedurale e geometrie semplici, una sola mappa, checkpoint in memoria, nessun salvataggio di speedrun. Bilanciamento da rifinire con playtest umano; il replay automatico sfrutta input precisi e non rappresenta il tempo atteso per una prima partita. Firma, installer e certificazione grafica su Windows/macOS restano nel ciclo di distribuzione esistente.
