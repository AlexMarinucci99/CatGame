# Fisica platform

La fisica del livello 2 è separata da quella del runner e del boss. Posizione del modello: angolo superiore sinistro di un AABB di 36 × 52 unità. Il gatto è disegnato con sprite alto 64 unità, con piedi ancorati alla base dell'hitbox; rotazioni, orientamento e animazioni non cambiano le collisioni.

## Parametri iniziali

Tutti i valori sono centralizzati in `PlatformerConfig.DEFAULT`.

| Parametro | Valore |
|---|---:|
| Velocità orizzontale massima | 340 unità/s |
| Accelerazione a terra | 2600 unità/s² |
| Accelerazione in aria | 1800 unità/s² |
| Frenata senza direzione | 3200 unità/s² |
| Gravità | 2300 unità/s² |
| Impulso salto | 820 unità/s verso l'alto |
| Coyote time | 0,10 s |
| Jump buffer | 0,12 s |
| Discesa massima | 1000 unità/s |
| Wall-slide | 125 unità/s |
| Wall-jump orizzontale / verticale | 360 / 850 unità/s |
| Frazione di velocità conservata al rilascio del salto | 0,45 |
| Blocco direzionale iniziale dopo wall-jump | 0,16 s |
| Timestep | 1/120 s |

Il salto mantenuto raggiunge circa 143 unità di altezza con l'integrazione discreta, con circa 240 unità di portata orizzontale. Il rilascio di SPAZIO riduce la velocità ascendente; un salto rilasciato prima che il buffer venga consumato parte già corto. Non è previsto doppio salto nel platform: i muri sono necessari per risalire i pozzi.

## Collisioni e macchine

Ogni asse esegue uno sweep e seleziona la superficie più vicina: non si attraversano pareti sottili anche con velocità iniziali molto alte. Le piattaforme sono solide su tutti i lati; non sono piattaforme attraversabili dal basso. Il wall-slide richiede una direzione premuta verso il muro e una velocità discendente. Il wall-jump consuma il buffer e allontana il gatto dalla parete.

Le piattaforme seguono `(1-cos(2πt/periodo))/2` lungo lo spostamento indicato nel CSV. Un passeggero eredita il delta della piattaforma e l'eventuale velocità del nastro. Anche questo spostamento viene risolto contro le superfici solide. Se un macchinario comprime il personaggio contro una superficie, avviene il respawn anziché lasciare il gatto compenetrato.

Punte, seghe e presse usano AABB letali, con collisione controllata a ogni passo. Le seghe hanno quindi un margine conservativo agli angoli del rettangolo. Il controllo discreto dei pericoli è adeguato alle dimensioni e velocità della mappa corrente; nuove trappole minuscole o estremamente veloci richiederebbero uno sweep dedicato anche per i pericoli.

## Tempo, pausa e telecamera

L'accumulatore conserva le frazioni di passo. Ogni frame accetta al massimo 50 ms: uno stallo non provoca salti improvvisi né una spirale di recupero. Sotto 20 FPS il tempo di gioco rallenta intenzionalmente; il cronometro misura il tempo simulato. Delta negativi o non finiti vengono ignorati.

Pausa e perdita del focus disattivano gli input e non avanzano la simulazione. La pausa non modifica la velocità del salto. La ripetizione automatica dei tasti non genera salti ripetuti né riapre la pausa. Dopo respawn, pausa o cambio livello occorre rilasciare e ripremere un tasto rimasto premuto.

La camera segue esponenzialmente il giocatore, è limitata alla mappa e usa una vista logica 1000 × 650. Il Canvas si adatta alla finestra; il platform mantiene le proporzioni con bande laterali quando necessario. Il suo HUD e la pausa usano la dimensione della vista. La camera non influenza la fisica. Non viene aggiunto camera shake in questa prima versione.
