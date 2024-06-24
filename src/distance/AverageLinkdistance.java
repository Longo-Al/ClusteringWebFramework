package distance;

import clustering.Cluster;
import data.Data;
import data.Example; // Add this import statement

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

        for (int i = 0; i < c1.getSize(); i++) {
            Example e1 = d.getExample(c1.getElement(i));
            for (int j = 0; j < c2.getSize(); j++) {
                int targetE = c2.getElement(j);
                double distance = e1.distance(d.getExample(targetE));
                totalDistance += distance;
                count += 1;
            }
        }

        return totalDistance / count;
    }
}
