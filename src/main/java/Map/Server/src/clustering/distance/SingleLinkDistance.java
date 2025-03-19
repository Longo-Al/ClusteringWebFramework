package Map.Server.src.clustering.distance;

import java.util.UUID;

import Map.Server.src.clustering.Cluster;
import Map.Server.src.clustering.Clusterable;
import Map.Server.src.clustering.ClusterableCollection;
import Map.Server.src.clustering.Exceptions.InvalidSizeException;
import Map.Server.src.clustering.Interface.ClusterableItem;

/**
 * classe SingleLinkDistance
 * Implementa il metodo distance dell'interfaccia
 * ClusterDistance per calcolare la distanza tra due cluster
 * @author Team MAP Que Nada
 */
public class SingleLinkDistance implements ClusterDistance {
	/**
	 * metodo distance
	 * restituisce la minima distanza tra due cluster
	 * con la distanza singlelink
	 *
	 * @param c1 primo cluster
	 * @param c2 secondo cluster
	 * @param d dataset
	 * @return min (un double)
	 */
	public double distance(Cluster c1, Cluster c2, ClusterableCollection<? extends ClusterableItem<?>>  d) throws InvalidSizeException {
		double min=Double.MAX_VALUE;

        for (UUID id1 : c1) {
			Clusterable e1 = d.getClusterable(id1);
            for (UUID id2 : c2) {
				Clusterable e2 = d.getClusterable(id2);
				double distance = Clusterable.distance(e1,e2);
                if (distance < min)
                    min = distance;
            }
        }
		return min;
	}
}

