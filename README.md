# Esercitazione 1: Classi, Aggregazione, Interfacce

## Task 1: Definizione di Classi e Metodi

### Classe `Example`

Questa classe modella una entità esempio, intesa come vettore di valori reali.

**Attributi**

- `Double[] example;` // Vettore di valori reali

**Costruttore**

- `Example(int length)`
  - **Input:** `length` - dimensione dell'esempio.
  - **Comportamento:** Inizializza `example` come vettore di dimensione `length`.

**Metodi**

- `void set(int index, Double v)`
  - **Input:**
    - `index` - Posizione del valore.
    - `v` - Valore da inserire.
  - **Comportamento:** Modifica `example` inserendo `v` in posizione `index`.

- `Double get(int index)`
  - **Input:** `index` - Posizione di `example`.
  - **Output:** Valore memorizzato in `example[index]`.
  - **Comportamento:** Restituisce `example[index]`.

- `double distance(Example newE)`
  - **Input:** `newE` - Istanza di `Example`.
  - **Output:** Calcola la distanza euclidea tra `this.example` e `newE.example`.
  - **Comportamento:** Restituisce il valore calcolato.

- `public String toString()`
  - **Output:** La stringa che rappresenta il contenuto di `example`.
  - **Comportamento:** Restituisce la stringa.

### Classe `Data`

Questa classe aggrega la classe `Example`.

**Attributi**

- `Example[] data;` // Rappresenta il dataset
- `int numberOfExamples;` // Numero di esempi nel dataset

**Costruttore**

- `Data()`
  - **Comportamento:** Avvalora un oggetto `data` predefinito (fornito dal docente).

**Metodi**

- `int getNumberOfExamples()`
  - **Output:** Numero di esempi memorizzati in `data`.
  - **Comportamento:** Restituisce `numberOfExamples`.

- `Example getExample(int exampleIndex)`
  - **Input:** `exampleIndex` - Indice di un esempio memorizzato in `data`.
  - **Output:** L'esempio memorizzato in `data[exampleIndex]`.
  - **Comportamento:** Restituisce `data[exampleIndex]`.

- `double[][] distance()`
  - **Output:** Matrice triangolare superiore delle distanze Euclidee calcolate tra gli esempi memorizzati in `data`.
  - **Comportamento:** Restituisce la matrice triangolare superiore delle distanze.

- `public String toString()`
  - **Output:** Stringa che modella lo stato dell'oggetto.
  - **Comportamento:** Crea una stringa in cui memorizza gli esempi memorizzati nell'attributo `data`, opportunamente enumerati. Restituisce tale stringa.

### Utilizzo del Metodo Main

Si usi il metodo main in `Data` fornito dal docente per ottenere il seguente output:
makefile:

0:[1.0,2.0,0.0] 1:[0.0,1.0,-1.0] 2:[1.0,3.0,5.0] 3:[1.0,3.0,4.0] 4:[2.0,2.0,0.0]

Distance matrix:
0.0 3.0 26.0 17.0 1.0
0.0 0.0 41.0 30.0 6.0
0.0 0.0 0.0 1.0 27.0
0.0 0.0 0.0 0.0 18.0
0.0 0.0 0.0 0.0 0.0

### Interfaccia `ClusterDistance` e Classi Correlate

- `Cluster`: Modella un cluster come la collezione delle posizioni occupate dagli esempi raggruppati nel cluster nel vettore `data` dell'oggetto che modella il dataset su cui il clustering è calcolato (istanza di `Data`).
- `SingleLinkDistance`: Implementa l'interfaccia `ClusterDistance`.
- `AverageLinkDistance`: Implementa l'interfaccia `ClusterDistance` e implementa la distanza average-link tra cluster.

### Classe `ClusterSet`

Modella un insieme di cluster.

**Metodo Aggiunto**

- `ClusterSet mergeClosestClusters(ClusterDistance distance, Data data)`
  - **Input:**
    - `distance` - Oggetto per il calcolo della distanza tra cluster.
    - `data` - Oggetto istanza che rappresenta il dataset.
  - **Output:** Nuova istanza di `ClusterSet`.
  - **Comportamento:** Determina la coppia di cluster più simili usando `distance` e li fonde in un unico cluster; crea una nuova istanza di `ClusterSet` che contiene tutti i cluster dell'oggetto this a meno dei due cluster fusi.

### Classe `Dendrogram`

Contiene:

**Attributi**

- `private ClusterSet[] tree;` // Modella il dendrogramma

**Costruttore**

- `Dendrogram(int depth)`
  - **Input:** `depth` - Profondità del dendrogramma.
  - **Comportamento:** Crea un vettore di dimensione `depth` con cui inizializza `tree`.

**Metodi**

- `void setClusterSet(ClusterSet c, int level)`
  - **Comportamento:** Memorizza `c` nella posizione `level` di `tree`.

- `ClusterSet getClusterSet(int level)`
  - **Comportamento:** Restituisce `tree[level]`.

- `int getDepth()`
  - **Comportamento:** Restituisce la profondità del dendrogramma.

- `public String toString()`
  - **Comportamento:** Restituisce una rappresentazione stringa del dendrogramma.

- `public String toString(Data data)`
  - **Comportamento:** Restituisce una rappresentazione stringa del dendrogramma utilizzando i dati del dataset.

### Classe `HierarchicalClusterMiner`

Implementa il metodo per eseguire il clustering gerarchico.

**Metodo**

- `void mine(Data data, ClusterDistance distance)`
  - **Comportamento:** Crea il livello base del dendrogramma che rappresenta ogni esempio in un cluster separato; per tutti i livelli successivi del dendrogramma costruisce l'istanza di `ClusterSet` che realizza la fusione dei due cluster più vicini.

### Utilizzo della Classe `MainTest`

Usare la classe `MainTest` fornita dal docente per calcolare l'output mostrato nel file `output.txt`.
