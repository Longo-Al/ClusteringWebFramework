package Map.Server.src.clustering;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import Map.Server.src.clustering.Interface.ClusterableItem;

/**
 * La classe {@code Cluster} modella un cluster come una collezione di UUID
 * che identificano elementi raggruppati insieme secondo un algoritmo di clustering.
 * 
 * Un {@code Cluster} può essere semplice (contenere un solo elemento) oppure
 * essere il risultato della fusione di due altri cluster.
 * 
 * Implementa {@link Iterable}, {@link Cloneable}, {@link Serializable} e {@link Comparable}.
 * 
 * @author Longo Alex
 */
public class Cluster implements Iterable<UUID>, Cloneable, Serializable, Comparable<Cluster> {
    
    private Set<UUID> clusteredData = new TreeSet<>();
    private final double distance;
    private Cluster parent1;
    private Cluster parent2;

    /**
     * Costruttore di default. Crea un cluster vuoto con distanza zero.
     */
    public Cluster() {
        this.distance = 0;
    }

    /**
     * Costruttore per la creazione di un nuovo cluster
     * come unione di due cluster esistenti.
     *
     * @param o1 Primo cluster genitore
     * @param o2 Secondo cluster genitore
     * @param d  Distanza tra i due cluster
     */
    public Cluster(Cluster o1, Cluster o2, Double d) {
        this.parent1 = o1;
        this.parent2 = o2;
        this.distance = d;
        this.clusteredData.addAll(o1.clusteredData);
        this.clusteredData.addAll(o2.clusteredData);
    }

    /**
     * Aggiunge un elemento identificato da {@code id} al cluster.
     *
     * @param id UUID dell'elemento da aggiungere
     */
    void addData(UUID id) {
        clusteredData.add(id);
    }

    /**
     * Restituisce un iteratore sugli elementi del cluster.
     *
     * @return iteratore di {@link UUID}
     */
    @Override
    public Iterator<UUID> iterator() {
        return clusteredData.iterator();
    }

    /**
     * Crea una copia profonda del cluster.
     *
     * @return una nuova istanza di {@code Cluster} contenente gli stessi dati
     * @throws CloneNotSupportedException se il clone fallisce
     */
    @Override
    public Cluster clone() throws CloneNotSupportedException {
        Cluster clone = (Cluster) super.clone();
        clone.clusteredData = new TreeSet<>(this.clusteredData);
        clone.parent1 = this.parent1;
        clone.parent2 = this.parent2;
        return clone;
    }

    /**
     * Unisce il cluster corrente con un altro cluster, producendo un nuovo cluster.
     *
     * @param c Cluster da unire
     * @param d Distanza associata alla fusione
     * @return Nuovo cluster risultante dalla fusione
     */
    Cluster mergeCluster(Cluster c, Double d) {
        return new Cluster(this, c, d);
    }

    /**
     * Restituisce una rappresentazione testuale del cluster,
     * contenente gli UUID degli elementi.
     *
     * @return stringa rappresentante il cluster
     */
    @Override
    public String toString() {
        return clusteredData.toString();
    }

    /**
     * Restituisce una rappresentazione testuale dettagliata del cluster,
     * usando il dataset per mostrare i dati associati agli UUID.
     *
     * @param data Dataset da cui ottenere la rappresentazione degli elementi
     * @return stringa dettagliata del cluster
     */
    public String toString(ClusterableCollection<? extends ClusterableItem<?>> data) {
        StringBuilder str = new StringBuilder();
        for (UUID clusteredDatum : clusteredData) {
            str.append("[").append(data.getClusterable(clusteredDatum).toString()).append("]");
        }
        return str.toString();
    }

    /**
     * Restituisce il numero di elementi contenuti nel cluster.
     *
     * @return dimensione del cluster
     */
    public int getSize() {
        return clusteredData.size();
    }

    /**
     * Restituisce la distanza associata alla fusione che ha creato questo cluster.
     *
     * @return distanza
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Restituisce il primo cluster genitore.
     *
     * @return primo genitore
     */
    public Cluster getParent1() {
        return parent1;
    }

    /**
     * Restituisce il secondo cluster genitore.
     *
     * @return secondo genitore
     */
    public Cluster getParent2() {
        return parent2;
    }

    /**
     * Restituisce l'insieme degli UUID raggruppati in questo cluster.
     *
     * @return insieme di UUID
     */
    public Set<UUID> getClusteredData() {
        return clusteredData;
    }

    /**
     * Restituisce una rappresentazione degli UUID raggruppati
     * in forma di stringa, con parentesi tonde.
     *
     * @return stringa con UUID tra parentesi tonde
     */
    public String getClusteredDataString() {
        return clusteredData.toString().replace("[", "(").replace("]", ")");
    }

    /**
     * Confronta questo cluster con un altro.
     * Il confronto è effettuato in base alla distanza,
     * poi alla dimensione del cluster,
     * e infine all'hashCode per garantire un ordinamento stabile.
     *
     * @param other altro cluster da confrontare
     * @return valore negativo, zero o positivo a seconda dell'ordinamento
     */
    @Override
    public int compareTo(Cluster other) {
        int distanceComparison = Double.compare(this.distance, other.distance);
        if (distanceComparison != 0) {
            return distanceComparison;
        }

        int sizeComparison = Integer.compare(this.getSize(), other.getSize());
        if (sizeComparison != 0) {
            return sizeComparison;
        }

        return Integer.compare(this.hashCode(), other.hashCode());
    }
}
