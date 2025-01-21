package  Map.Server.src.distance;

import java.util.UUID;

import Map.Server.src.clustering.Cluster;
import Map.Server.src.clustering.Clusterable;
import Map.Server.src.clustering.ClusterableCollection;
import Map.Server.src.clustering.Exceptions.InvalidSizeException;

/**
 * Classe AverageLinkDistance
 * Implementa l'interfaccia ClusterDistance per calcolare
 * la media della distanza tra due cluster
 *
 * @author Team MAP Que Nada
 */

public class AverageLinkDistance implements ClusterDistance {
    /**
     * Metodo distance
     * restituisce la media delle distanze minime tra i cluster
     * con la distanza AverageLink
     *
     * @param c1 primo cluster
     * @param c2 secondo cluster
     * @param d dataset
     * @return media selle distanze tra i cluster
     */
    @SuppressWarnings("rawtypes")
    public double distance(Cluster c1, Cluster c2, ClusterableCollection d) throws InvalidSizeException {
        double sum = 0.0;

        for (UUID id1 : c1) {
			Clusterable e1 = d.getClusterable(id1);
            for (UUID id2 : c2) {
				Clusterable e2 = d.getClusterable(id2);
				sum += Clusterable.distance(e1,e2);
            }
        }

        return sum / (c1.getSize() * c2.getSize());
    }
}
