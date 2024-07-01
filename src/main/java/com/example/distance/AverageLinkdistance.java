package com.example.distance;

import com.example.clustering.Cluster;
import com.example.data.Data;

/**
 * Classe per calcolare la distanza media del collegamento tra cluster.
 */
public class AverageLinkdistance implements ClusterDistance {
    /**
     * Calcola la distanza media del collegamento tra due cluster.
     * 
     * @param c1 primo cluster
     * @param c2 secondo cluster
     * @param d dati associati ai cluster
     * @return distanza media del collegamento tra i due cluster
     */
    public double distance(Cluster c1, Cluster c2, Data d) {
        double totalDistance = 0;
        int count = 0;

        for (Integer e1 : c1) {
            for (Integer targetE : c2) {
                double distance = d.getExample(e1).distance(d.getExample(targetE));
                totalDistance += distance;
                count += 1;
            }
        }
        return totalDistance / count;
    }
}
