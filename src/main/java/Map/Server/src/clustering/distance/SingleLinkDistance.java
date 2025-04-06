package Map.Server.src.clustering.distance;

import java.util.UUID;

import Map.Server.src.clustering.Cluster;
import Map.Server.src.clustering.Clusterable;
import Map.Server.src.clustering.ClusterableCollection;
import Map.Server.src.clustering.Exceptions.InvalidSizeException;
import Map.Server.src.clustering.Interface.ClusterableItem;

/**
 * Implementazione della distanza Single Link (collegamento singolo),
 * che rappresenta la minima distanza tra due elementi appartenenti
 * a cluster diversi.
 *
 * @author Longo Alex
 */
public class SingleLinkDistance implements ClusterDistance {

    /**
     * Calcola la distanza Single Link tra due cluster.
     * Restituisce la distanza minima tra tutti i possibili
     * accoppiamenti di elementi appartenenti ai due cluster.
     *
     * @param c1 il primo cluster
     * @param c2 il secondo cluster
     * @param d  la collezione degli elementi clusterabili
     * @return la distanza minima trovata tra i due cluster
     * @throws InvalidSizeException se uno dei cluster è malformato
     */
    public double distance(Cluster c1, Cluster c2,
            ClusterableCollection<? extends ClusterableItem<?>> d) throws InvalidSizeException {
        
        double min = Double.MAX_VALUE;

        for (UUID id1 : c1) {
            Clusterable e1 = d.getClusterable(id1);
            for (UUID id2 : c2) {
                Clusterable e2 = d.getClusterable(id2);
                double distance = Clusterable.distance(e1, e2);
                if (distance < min)
                    min = distance;
            }
        }

        return min;
    }
}
