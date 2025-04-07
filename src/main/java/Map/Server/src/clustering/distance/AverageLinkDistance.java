package Map.Server.src.clustering.distance;

import java.util.UUID;

import Map.Server.src.clustering.Cluster;
import Map.Server.src.clustering.Clusterable;
import Map.Server.src.clustering.ClusterableCollection;
import Map.Server.src.clustering.Exceptions.InvalidSizeException;
import Map.Server.src.clustering.Interface.ClusterableItem;

/**
 * Implementazione della distanza tra cluster utilizzando il metodo
 * Average Link (collegamento medio), ovvero la media di tutte le
 * distanze tra le coppie di elementi appartenenti ai due cluster.
 * <p>
 * Utilizza la distanza definita dalla classe {@link Clusterable}.
 * </p>
 *
 * @author Longo Alex
 */
public class AverageLinkDistance implements ClusterDistance {

    /**
     * Calcola la distanza media (Average Link) tra due cluster.
     * La distanza è data dalla media di tutte le distanze tra
     * coppie di elementi (uno per ciascun cluster).
     *
     * @param c1 primo cluster
     * @param c2 secondo cluster
     * @param d  dataset che contiene gli elementi clusterabili
     * @return la distanza media tra i due cluster
     * @throws InvalidSizeException se un cluster ha dimensione non valida
     */
    public double distance(Cluster c1, Cluster c2, ClusterableCollection<? extends ClusterableItem<?>> d)
            throws InvalidSizeException {
        double sum = 0.0;

        for (UUID id1 : c1) {
            Clusterable e1 = d.getClusterable(id1);
            for (UUID id2 : c2) {
                Clusterable e2 = d.getClusterable(id2);
                sum += Clusterable.distance(e1, e2);
            }
        }

        return sum / (c1.getSize() * c2.getSize());
    }
}
