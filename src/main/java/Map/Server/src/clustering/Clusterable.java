package Map.Server.src.clustering;

import java.util.UUID;

import java.util.List;
import java.util.Iterator;

import Map.Server.src.clustering.Exceptions.InvalidSizeException;
import Map.Server.src.clustering.Interface.ClusterableItem;

public class Clusterable implements Iterable<Double> {
    private List<Double> valutation;
    private final ClusterableItem<?> instance;
    private final UUID id;

    public Clusterable(ClusterableItem<?> adapter) {
        this.instance = adapter;
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public final ClusterableItem<?> getInstance(){
        return this.instance;
    }

    private void prepareCluster() {
        this.valutation = instance.evaluate();
    }

    @Override
    public String toString(){
            return this.instance.toString();
    }

    @Override
    public Iterator<Double> iterator() {
        if (valutation == null) {
            prepareCluster();
        }
        return valutation.iterator();
    }

    /**
     * Calcola la distanza euclidea tra l'istanza corrente e un'altra.
     */
    public static double distance(Clusterable a, Clusterable b) throws InvalidSizeException {
        if (a.valutation == null) a.prepareCluster();
        if (b.valutation == null) b.prepareCluster();
        if (a.valutation.size() != b.valutation.size()) {
            throw new InvalidSizeException("Le dimensioni delle valutazioni non coincidono.");
        }
        double sum = 0.0;
        for (int i = 0; i < a.valutation.size(); i++) {
            double diff = a.valutation.get(i) - b.valutation.get(i);
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

}
