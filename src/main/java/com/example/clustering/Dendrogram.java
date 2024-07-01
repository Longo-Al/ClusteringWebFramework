package com.example.clustering;

import com.example.data.Data;

/**
 * Classe che rappresenta un dendrogramma per la visualizzazione del clustering gerarchico.
 */
public class Dendrogram {
    private ClusterSet[] tree;

    /**
     * Costruttore della classe Dendrogram.
     * 
     * @param depth profondità del dendrogramma
     */
    public Dendrogram(int depth) {
        tree = new ClusterSet[depth];
    }

    /**
     * Imposta il livello del cluster set nel dendrogramma.
     * 
     * @param c cluster set da impostare
     * @param level livello del dendrogramma
     */
    public void setClusterSet(ClusterSet c, int level) {
        tree[level] = c;
    }

    /**
     * Restituisce il cluster set al livello specificato.
     * 
     * @param level livello del dendrogramma
     * @return cluster set al livello specificato
     */
    public ClusterSet getClusterSet(int level) {
        return tree[level];
    }

    /**
     * Restituisce la profondità del dendrogramma.
     * 
     * @return profondità del dendrogramma
     */
    public int getDepth() {
        return tree.length;
    }

    @Override
    public String toString() {
        StringBuilder v = new StringBuilder();
        for (int i = 0; i < tree.length; i++) {
            v.append("level").append(i+1).append(":\n").append(tree[i]).append("\n");
        }
        return v.toString();
    }

    public String toString(Data data) {
        StringBuilder v = new StringBuilder();
        for (int i = 0; i < tree.length; i++) {
            v.append("level").append(i+1).append(":\n").append(tree[i].toString(data)).append("\n");
        }
        return v.toString();
    }
}
