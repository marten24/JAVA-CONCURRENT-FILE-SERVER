# Java Concurrent File Server

Progetto del corso di **Laboratorio di Sistemi Operativi – A.A. 2023/2024**.

Applicazione client-server in Java che permette a più client connessi via socket di creare, leggere, modificare, rinominare ed eliminare file di testo condivisi sul server. L'accesso concorrente ai file è gestito con semafori secondo il modello **lettori/scrittori**, con una politica fair che evita la starvation degli scrittori.

## Funzionalità

- Server multi-client: ogni client connesso è gestito da un thread dedicato
- Più lettori possono leggere lo stesso file contemporaneamente, mentre uno scrittore ha accesso esclusivo
- Code dei lettori e degli scrittori bloccati, servite in ordine di arrivo
- Rename e delete consentiti solo quando nessuno sta usando il file; in caso contrario il client viene avvisato e può riprovare senza restare bloccato
- Console di amministrazione del server con statistiche su file e sessioni attive
- Chiusura ordinata del server con disconnessione di tutti i client

## Architettura

| Classe | Ruolo |
|---|---|
| `Server` | Apre il `ServerSocket` sulla porta indicata, carica i file e accetta le connessioni, creando un `ClientHandler` per ogni client |
| `ServerCommands` | Thread che legge i comandi dell'amministratore dalla console del server (`info`, `quit`) |
| `ClientHandler` | Thread che riceve le richieste di un singolo client e le esegue sui file |
| `Client` | Applicazione lato utente: invia i comandi al server e mostra le risposte |
| `FileBox` | Rappresenta un file gestito dal server, con i metodi per leggerlo e modificarlo; ogni istanza ha il proprio `ReadersWritersHandler` |
| `ReadersWritersHandler` | Lock associato a ogni file: gestisce con semafori (`p()` / `v()`) gli accessi in lettura, scrittura, rename e delete |

### Gestione della concorrenza

Ogni file ha il proprio `ReadersWritersHandler`, quindi operazioni su file diversi non si bloccano a vicenda.

- **Lettura** (`beginRead` / `endRead`): consentita se sul file non ci sono scrittori attivi o in attesa, altrimenti il client entra nella coda dei lettori bloccati.
- **Scrittura** (`beginWrite` / `endWrite`): consentita solo se non ci sono né lettori né scrittori attivi, altrimenti il client entra nella coda degli scrittori bloccati.
- **Politica fair**: quando uno scrittore chiede l'accesso, i nuovi lettori vengono fermati e quelli attivi terminano la propria sessione; finita la scrittura, i lettori in coda accedono al file in ordine di richiesta.
- **Rename e delete**: vengono trattati come scritture e protetti da un semaforo inizializzato a 1, così un solo client alla volta può eseguirli. Se il file è in uso, l'operazione viene annullata con un messaggio di errore.

## Requisiti

- Java JDK 8 o superiore

## Compilazione ed esecuzione

Le classi appartengono al package `CodiceV2`, quindi i file `.java` devono trovarsi in una cartella con questo nome.

```bash
# 1. Metti i sorgenti nella cartella CodiceV2 e compila
mkdir CodiceV2 && mv *.java CodiceV2/
cd CodiceV2
javac *.java
cd ..

# 2. Avvia il server indicando la porta
java CodiceV2.Server 1234

# 3. In un altro terminale, avvia uno o più client
java CodiceV2.Client localhost 1234
```

Dopo la connessione, ogni client inserisce il proprio username e può iniziare a lavorare sui file. Per lavorare sui file basta indicarne il nome, senza estensione.

## Comandi

### Client

| Comando | Descrizione |
|---|---|
| `create <nomeFile>` | Crea un nuovo file |
| `read <nomeFile>` | Apre il file in lettura; `:close` termina la sessione |
| `edit <nomeFile>` | Apre il file in scrittura |
| `rename <nomeFile> <nomeNuovo>` | Rinomina il file |
| `delete <nomeFile>` | Elimina il file |
| `quit` | Chiude la connessione e il client |

Durante una sessione `edit`:

| Comando | Descrizione |
|---|---|
| testo qualsiasi | Viene aggiunto in coda al file |
| `:backspace` | Elimina l'ultima riga del file |
| `:close` | Chiude la sessione di scrittura |

### Server

| Comando | Descrizione |
|---|---|
| `info` | Mostra il numero di file gestiti e le sessioni di lettura e scrittura attive |
| `quit` | Disconnette tutti i client e chiude il server |

## Documentazione

La relazione completa del progetto, con lo schema dell'architettura, la descrizione delle classi e i problemi affrontati durante lo sviluppo, è disponibile nel file [`report.pdf`](report.pdf).

## Autori

- Marco Tenace @marten24
- Eugenio Maria De Rosa @EugenioDeRosa
