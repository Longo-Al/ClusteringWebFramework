package com.example.distance;

import com.example.clustering.Cluster;
import com.example.data.Data;

/**
 * Interfaccia per calcolare la distanza tra cluster.
 */
public interface ClusterDistance {
    /**
     * Calcola la distanza tra due cluster.
     * 
     * @param c1 primo cluster
     * @param c2 secondo cluster
     * @param d dati associati ai cluster
     * @return distanza tra i due cluster
     */
    double distance(Cluster c1, Cluster c2, Data d);
}
