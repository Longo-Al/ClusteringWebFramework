package Map.Server.src.clustering;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import Map.Server.src.clustering.Interface.ClusterableItem;

/**
 * Classe Cluster
 * Modella un cluster come la collezione delle posizioni occupate
 * dagli esempi raggruppati nel Cluster nel vettore data dell’oggetto
 * che modella il dataset su cui il clustering è calcolato (istanza di Data).
 */
public class Cluster implements Iterable<UUID>, Cloneable, Serializable, Comparable<Cluster> {
    private Set<UUID> clusteredData = new TreeSet<>();
    private final double distance;
    private Cluster parent1;
    private Cluster parent2;

    /**
     * Costruttore di default per un cluster vuoto.
     */
    public Cluster() {
        this.distance = 0;
    }

    /**
     * Costruttore per unire due cluster.
     *
     * @param o1 primo cluster genitore
     * @param o2 secondo cluster genitore
     */
    public Cluster(Cluster o1, Cluster o2, Double d) {
        this.parent1 = o1;
        this.parent2 = o2;
        this.distance = d;
        this.clusteredData.addAll(o1.clusteredData);
        this.clusteredData.addAll(o2.clusteredData);
    }

    /**
     * Metodo addData
     * Aggiunge l'indice di posizione id al cluster.
     *
     * @param id indice da aggiungere al cluster
     */
    void addData(UUID id) {
        clusteredData.add(id);
    }

    /**
     * Metodo iterator
     * Restituisce un iteratore per scorrere gli elementi del cluster.
     *
     * @return clusteredData.iterator() iterator per scorrere gli elementi del cluster
     */
    public Iterator<UUID> iterator() {
        return clusteredData.iterator();
    }

    /**
     * Metodo clone
     * Crea una copia del cluster.
     *
     * @return copia del cluster
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
     * Metodo mergeCluster
     * Crea un nuovo cluster che è la fusione del cluster corrente e del cluster c.
     *
     * @param c cluster da unire al cluster corrente
     * @return newC cluster che è la fusione del cluster corrente e del cluster c
     */
    Cluster mergeCluster(Cluster c, Double d) {
        return new Cluster(this, c, d);
    }

    /**
     * Metodo toString
     * Restituisce una stringa contenente gli indici degli esempi raggruppati nel cluster.
     *
     * @return stringa contenente gli indici degli esempi raggruppati nel cluster
     */
    @Override
    public String toString() {
        return clusteredData.toString();
    }

    /**
     * Metodo per rappresentare il cluster con riferimenti al dataset.
     *
     * @param data oggetto di classe Data che modella il dataset su cui il clustering è calcolato
     * @return stringa contenente gli esempi raggruppati nel cluster
     */
    public String toString(ClusterableCollection<? extends ClusterableItem<?>> data) {
        StringBuilder str = new StringBuilder();
        for (UUID clusteredDatum : clusteredData) {
            str.append("[").append(data.getClusterable(clusteredDatum).toString()).append("]");
        }
        return str.toString();
    }

    /**
     * Metodo getSize
     * Restituisce la dimensione del cluster.
     *
     * @return dimensione del cluster
     */
    public int getSize() {
        return clusteredData.size();
    }

    public double getDistance() {
        return distance;
    }

    public Cluster getParent1() {
        return parent1;
    }

    public Cluster getParent2() {
        return parent2;
    }

    public Set<UUID> getClusteredData() {
        return clusteredData;
    }

    public String getClusteredDataString() {
        return clusteredData.toString().replace("[", "(").replace("]", ")");
    }

    @Override
    public int compareTo(Cluster other) {
        // 1. Confronto sulla distanza
        int distanceComparison = Double.compare(this.distance, other.distance);
        if (distanceComparison != 0) {
            return distanceComparison;
        }

        // 2. Se la distanza è uguale, confrontiamo la dimensione del cluster
        int sizeComparison = Integer.compare(this.getSize(), other.getSize());
        if (sizeComparison != 0) {
            return sizeComparison;
        }

        // 3. Se anche la dimensione è uguale, confrontiamo il loro hash per un ordine stabile
        return Integer.compare(this.hashCode(), other.hashCode());
    }  
}
