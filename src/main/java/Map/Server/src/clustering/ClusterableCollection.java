package Map.Server.src.clustering;

import java.util.UUID;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import Map.Server.src.clustering.Interface.ClusterableItem;
import Map.Server.src.clustering.Exceptions.InvalidSizeException;

/**
 * Collezione di oggetti {@link Clusterable}, rappresentati e gestiti tramite un identificatore UUID.
 * Permette l'aggiunta, la rimozione, la ricerca e il calcolo di distanze tra elementi clusterizzabili.
 *
 * @param <T> tipo degli elementi gestiti, che devono implementare {@link ClusterableItem}.
 * 
 * @author Alex Longo
 */
public class ClusterableCollection<T extends ClusterableItem<?>> implements Iterable<UUID> {

    /**
     * Mappa che associa l'UUID di ogni oggetto al relativo {@link Clusterable}.
     */
    private final Map<UUID, Clusterable> clusters;

    /**
     * Costruttore di una collezione vuota.
     */
    public ClusterableCollection() {
        this.clusters = new HashMap<>();
    }

    /**
     * Costruttore che carica una lista iniziale di elementi clusterizzabili.
     *
     * @param dataset lista di elementi da convertire e caricare nella collezione.
     */
    public ClusterableCollection(List<? extends ClusterableItem<?>> dataset) {
        this.clusters = new HashMap<>();
        
        Iterator<? extends ClusterableItem<?>> it = dataset.iterator();
        while (it.hasNext()) {
            ClusterableItem<?> toConvert = it.next();
            Clusterable toLoad = new Clusterable(toConvert);
            this.addClusterable(toLoad);
        }
    }

    /**
     * Restituisce il numero di elementi nella collezione.
     *
     * @return numero di clusterabili.
     */
    public int size() {
        return clusters.size();
    }

    /**
     * Aggiunge un nuovo oggetto {@link Clusterable} alla collezione.
     *
     * @param clusterable oggetto da aggiungere.
     */
    public void addClusterable(Clusterable clusterable) {
        clusters.put(clusterable.getId(), clusterable);
    }

    /**
     * Rimuove un oggetto dalla collezione tramite il suo UUID.
     *
     * @param id identificatore dell'elemento da rimuovere.
     */
    public void removeClusterable(UUID id) {
        clusters.remove(id);
    }

    /**
     * Recupera un oggetto {@link Clusterable} dato il suo UUID.
     *
     * @param id identificatore dell'elemento da recuperare.
     * @return l'elemento associato all'UUID, oppure {@code null} se non trovato.
     */
    public Clusterable getClusterable(UUID id) {
        return clusters.get(id);
    }

    /**
     * Restituisce tutti gli oggetti {@link Clusterable} presenti nella collezione.
     *
     * @return lista di clusterabili.
     */
    public List<Clusterable> getAllClusterables() {
        return List.copyOf(clusters.values());
    }

    /**
     * Calcola la distanza minima tra un dato {@link Clusterable} e tutti gli altri presenti nella collezione.
     *
     * @param clusterable elemento di riferimento.
     * @return distanza minima trovata.
     * @throws InvalidSizeException se le dimensioni dei vettori di valutazione non coincidono.
     */
    public double minDistanceTo(Clusterable clusterable) throws InvalidSizeException {
        double minDistance = Double.MAX_VALUE;
        for (Clusterable other : clusters.values()) {
            if (!other.getId().equals(clusterable.getId())) {
                double distance = Clusterable.distance(clusterable, other);
                minDistance = Math.min(minDistance, distance);
            }
        }
        return minDistance;
    }

    /**
     * Calcola tutte le distanze tra ogni coppia distinta di elementi nella collezione.
     *
     * @return mappa delle distanze tra i cluster (UUID -> {UUID -> distanza}).
     * @throws InvalidSizeException se le dimensioni dei vettori di valutazione non coincidono.
     */
    public Map<UUID, Map<UUID, Double>> computeAllDistances() throws InvalidSizeException {
        Map<UUID, Map<UUID, Double>> distances = new HashMap<>();
        for (Clusterable clusterA : clusters.values()) {
            Map<UUID, Double> distancesFromA = new HashMap<>();
            for (Clusterable clusterB : clusters.values()) {
                if (!clusterA.getId().equals(clusterB.getId())) {
                    distancesFromA.put(clusterB.getId(), Clusterable.distance(clusterA, clusterB));
                }
            }
            distances.put(clusterA.getId(), distancesFromA);
        }
        return distances;
    }

    /**
     * Restituisce un iteratore sugli UUID degli elementi presenti nella collezione.
     *
     * @return iteratore sugli UUID.
     */
    @Override
    public Iterator<UUID> iterator() {
        return clusters.keySet().iterator();
    }

    /**
     * Serializza i dati originali degli elementi clusterabili in formato JSON.
     *
     * @return stringa JSON contenente i dati grezzi.
     */
    public final String getRawData() {
        List<Clusterable> toConvert = this.getAllClusterables();
        Gson gson = new Gson();
        return gson.toJson(
            toConvert.stream()
                .map(Clusterable::getInstance)
                .toArray()
        );
    }
    
}
