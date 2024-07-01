package com.example.clustering;

import com.example.data.Data;
import java.util.Set;
import java.util.TreeSet;
import java.util.Iterator;

/**
 * Classe che rappresenta un cluster di dati.
 */
public class Cluster implements Iterable<Integer>, Cloneable{
    private Set<Integer> clusteredData;

    public Cluster(){
        this.clusteredData = new TreeSet<>();
    }
    /**
     * Aggiunge un nuovo dato al cluster.
     * 
     * @param id identificatore del dato
     */
    public void addData(int id) {
        clusteredData.add(id);
    }

    /**
     * Restituisce la dimensione del cluster.
     * 
     * @return numero di elementi nel cluster
     */
    public int getSize() {
        return clusteredData.size();
    }

    /**
     * Crea un nuovo cluster che è la fusione dei due cluster preesistenti.
     * 
     * @param c cluster da unire
     * @return nuovo cluster risultante dalla fusione
     */
    public Cluster mergeCluster(Cluster c) {
        Cluster newC = new Cluster();
        newC.clusteredData.addAll(this.clusteredData);
        newC.clusteredData.addAll(c.clusteredData);
        return newC;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        Iterator<Integer> iterator = clusteredData.iterator();
        while (iterator.hasNext()) {
            str.append(iterator.next());
            if (iterator.hasNext()) {
                str.append(",");
            }
        }
        return str.toString();
    }

    public String toString(Data data) {
        StringBuilder str = new StringBuilder();
        for (Integer i : clusteredData) {
            str.append("<").append(data.getExample(i)).append(">");
        }
        return str.toString();
    }

    @Override
    public Iterator<Integer> iterator() {
        return clusteredData.iterator();
    }

    @Override
    public Cluster clone() {
        try {
            Cluster clone = (Cluster) super.clone();
            clone.clusteredData = new TreeSet<>(this.clusteredData);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone non supportato", e);
        }
    }

}
