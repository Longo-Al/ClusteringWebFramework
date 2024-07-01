package com.example.clustering;

import com.example.data.Data;
import com.example.distance.ClusterDistance;

/**
 * Classe che gestisce un insieme di cluster.
 */
public class ClusterSet {
    private Cluster[] clusters;
    private int lastClusterIndex = 0;

    /**
     * Costruttore della classe ClusterSet.
     * 
     * @param k numero di cluster
     */
    public ClusterSet(int k) {
        clusters = new Cluster[k];
    }

    /**
     * Aggiunge un cluster all'insieme.
     * 
     * @param c cluster da aggiungere
     */
    public void add(Cluster c) {
        for (int j = 0; j < lastClusterIndex; j++)
            if (c == clusters[j]) // to avoid duplicates
                return;
        clusters[lastClusterIndex] = c;
        lastClusterIndex++;
    }

    public Cluster get(int i) {
        return clusters[i];
    }

    public int getSize() {
        return lastClusterIndex;
    }

    /**
     * Unisce i due cluster più vicini e restituisce un nuovo livello di cluster.
     * 
     * @param distance misura di distanza
     * @param data dati da clusterizzare
     * @return nuovo livello di cluster
     */
    public ClusterSet mergeClosestClusterSet(ClusterDistance distance, Data data) {
        ClusterSet newLevel = new ClusterSet(this.lastClusterIndex - 1);

        // trovo la minima distanza tra i cluster.
        Cluster examinated_cluster;
        double min_distance = Double.MAX_VALUE;
        int C1_index = 0, C2_index = 0;

        for (int i = 0; i < lastClusterIndex; i++) {
            examinated_cluster = clusters[i];
            for (int j = i + 1; j < lastClusterIndex; j++) {
                double act_distance = distance.distance(examinated_cluster, clusters[j], data);
                if (act_distance < min_distance) {
                    min_distance = act_distance;
                    C1_index = i;
                    C2_index = j;
                }
            }
        }
        // trovati i due cluster più vicini gli unisco e gli aggiungo al nuovo set.
        Cluster merged_cluster = clusters[C1_index].mergeCluster(clusters[C2_index]);
        newLevel.add(merged_cluster);
        // aggiungo i restanti cluster nel set.
        for (int i = 0; i < lastClusterIndex; i++) {
            if (i != C2_index && i != C1_index) {
                newLevel.add(clusters[i]);
            }
        }
        return newLevel;
    }

    @Override
    public String toString() {
        String str = "";
        for (int i = 0; i < clusters.length; i++) {
            if (clusters[i] != null) {
                str += "cluster" + i + ":" + clusters[i] + "\n";
            }
        }
        return str;
    }

    public String toString(Data data) {
        String str = "";
        for (int i = 0; i < clusters.length; i++) {
            if (clusters[i] != null) {
                str += "cluster" + i + ":" + clusters[i].toString(data) + "\n";
            }
        }
        return str;
    }
}

