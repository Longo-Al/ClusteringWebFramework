package com.example.distance;

import com.example.clustering.Cluster;
import com.example.data.Data;

/**
 * Classe per calcolare la distanza del singolo collegamento tra cluster.
 */
public class SingleLinkDistance implements ClusterDistance {
    /**
     * Calcola la distanza del singolo collegamento tra due cluster.
     * 
     * @param c1 primo cluster
     * @param c2 secondo cluster
     * @param d dati associati ai cluster
     * @return distanza del singolo collegamento tra i due cluster
     */
    public double distance(Cluster c1, Cluster c2, Data d) {
        double min = Double.MAX_VALUE;
        for (Integer e1 : c1) {
            for (Integer targetE : c2) {
                double distance = d.getExample(e1).distance(d.getExample(targetE));
                if (distance < min) {
                    min = distance;
                }
            }
        }
        return min;
    }
}
