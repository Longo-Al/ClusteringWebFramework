package Map.Server.src.clustering;

import java.util.UUID;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import Map.Server.src.clustering.Interface.ClusterableItem;
import Map.Server.src.clustering.Exceptions.InvalidSizeException;

public class ClusterableCollection<T> implements Iterable<UUID> {
    private final Map<UUID, Clusterable> clusters;

    public ClusterableCollection() {
        this.clusters = new HashMap<>();
    }

    public ClusterableCollection(List<? extends ClusterableItem<?>> dataset) {
        this.clusters = new HashMap<>();
        
        Iterator<? extends ClusterableItem<?>> it = dataset.iterator();
        while(it.hasNext()) {
            ClusterableItem<?> toConvert = it.next();
            Clusterable toLoad = new Clusterable(toConvert);
            this.addClusterable(toLoad);
        }

    }

    public int size() {
        return clusters.size();
    }

    public void addClusterable(Clusterable clusterable) {
        clusters.put(clusterable.getId(), clusterable);
    }

    public void removeClusterable(UUID id) {
        clusters.remove(id);
    }

    public Clusterable getClusterable(UUID id) {
        return clusters.get(id);
    }

    public List<Clusterable> getAllClusterables() {
        return List.copyOf(clusters.values());
    }

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

    @Override
    public Iterator<UUID> iterator() {
        return clusters.keySet().iterator();
    }

    public final String getRawData() {
        List<Clusterable> toConvert = this.getAllClusterables();
        // Usa Gson per serializzare la lista di istanze
        Gson gson = new Gson();
        // Converte ogni ClusterableItem in JSON
        return gson.toJson(
            toConvert.stream()
                .map(Clusterable::getInstance)
                .toArray()
        );
    }

    public ClusterableCollection<T> loadfromDatabase(String datasetID){
        
        return null;
    }
}
