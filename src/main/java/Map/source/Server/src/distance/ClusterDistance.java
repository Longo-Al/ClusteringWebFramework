package  Map.source.Server.src.distance;

import Map.source.Server.src.clustering.Cluster;
import Map.source.Server.src.data.Data;
import Map.source.Server.src.data.InvalidSizeException;

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
	double distance(Cluster c1, Cluster c2, Data d) throws InvalidSizeException;
}
