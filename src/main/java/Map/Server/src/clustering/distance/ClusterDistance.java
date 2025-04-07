package Map.Server.src.clustering.distance;

import Map.Server.src.clustering.Cluster;
import Map.Server.src.clustering.ClusterableCollection;
import Map.Server.src.clustering.Exceptions.InvalidSizeException;
import Map.Server.src.clustering.Interface.ClusterableItem;

/**
 * Interfaccia che definisce un contratto per il calcolo della distanza tra due cluster.
 * Implementazioni concrete possono utilizzare diverse strategie di misura (es. Average Link, Complete Link, ecc.).
 * 
 * @author Longo Alex
 */
public interface ClusterDistance {

    /**
     * Calcola la distanza tra due cluster in base agli elementi contenuti e al dataset di riferimento.
     *
     * @param c1 il primo cluster
     * @param c2 il secondo cluster
     * @param d  la collezione di elementi clusterabili (dataset)
     * @return la distanza calcolata come valore double
     * @throws InvalidSizeException se uno dei cluster ha dimensione non valida per la distanza
     */
    double distance(Cluster c1, Cluster c2, ClusterableCollection<? extends ClusterableItem<?>> d)
            throws InvalidSizeException;
}
