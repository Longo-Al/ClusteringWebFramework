package clustering;

import data.Data;
import defaultpackage.exceptions.NegativeNumberException;
import distance.ClusterDistance;

/**
 * Classe che esegue il clustering gerarchico.
 */
public class HierachicalClusterMiner {
    private Dendrogram dendrogram;

    /**
     * Costruttore della classe HierachicalClusterMiner.
     * 
     * @param depth profondità del dendrogramma
     * @throws NegativeNumberException se la profondità è minore o uguale a zero
     */
    public HierachicalClusterMiner(int depth) {
        if (depth <= 0) {
            throw new NegativeNumberException("Depth must be greater than 0");
        }
        dendrogram = new Dendrogram(depth);
    }

    /**
     * Esegue il processo di clustering sui dati forniti utilizzando la distanza specificata.
     * 
     * @param data dati da clusterizzare
     * @param distance misura di distanza da utilizzare
     */
    public void mine(Data data, ClusterDistance distance) {
        ClusterSet level = new ClusterSet(data.getNumberOfExamples());
        for (int i = 0; i < data.getNumberOfExamples(); i++) {
            Cluster c = new Cluster();
            c.addData(i);
            level.add(c);
        }
        dendrogram.setClusterSet(level, 0);
        for (int i = 1; i < dendrogram.getDepth(); i++) {
            ClusterSet prev_level = dendrogram.getClusterSet(i - 1);
            dendrogram.setClusterSet(prev_level.mergeClosestClusterSet(distance, data), i);
        }
    }

    @Override
    public String toString() {
        return dendrogram.toString();
    }

    public String toString(Data data) {
        return dendrogram.toString(data);
    }
}
