package  Map.Server.src.clustering.distance;

import Map.Server.src.clustering.Cluster;
import Map.Server.src.clustering.ClusterableCollection;
import Map.Server.src.clustering.Exceptions.InvalidSizeException;
import Map.Server.src.clustering.Interface.ClusterableItem;

/**
 * Interfaccia ClusterDistance
 * contiene metodo per
 * calcolare la distanza tra due cluster
 *
 * @author Team MAP Que Nada
 */
public interface ClusterDistance {
	/**
	 * metodo distance
	 *
	 * @param c1 primo cluster
	 * @param c2 secondo cluster
	 * @param d dataset
	 * @throws InvalidSizeException se la dimensione del cluster è minore di 2
	 * @return double
	 */
	double distance(Cluster c1, Cluster c2, ClusterableCollection<? extends ClusterableItem<?>> d) throws InvalidSizeException;
	
}
