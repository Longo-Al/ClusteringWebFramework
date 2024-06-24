package clustering;

import data.Data;

/**
 * Classe che rappresenta un cluster di dati.
 */
public class Cluster {
    private Integer[] clusteredData = new Integer[0];

    /**
     * Aggiunge un nuovo dato al cluster.
     * 
     * @param id identificatore del dato
     */
    public void addData(int id) {
        // controllo duplicati
        for (int i = 0; i < clusteredData.length; i++) {
            if (id == clusteredData[i]) {
                return;
            }
        }
        Integer[] clusteredDataTemp = new Integer[clusteredData.length + 1];
        System.arraycopy(clusteredData, 0, clusteredDataTemp, 0, clusteredData.length);
        clusteredData = clusteredDataTemp;
        clusteredData[clusteredData.length - 1] = id;
    }

    /**
     * Restituisce la dimensione del cluster.
     * 
     * @return numero di elementi nel cluster
     */
    public int getSize() {
        return clusteredData.length;
    }

    /**
     * Restituisce l'elemento all'indice specificato.
     * 
     * @param i indice dell'elemento
     * @return elemento all'indice specificato
     */
    public int getElement(int i) {
        return clusteredData[i];
    }

    /**
     * Crea una copia del cluster corrente.
     * 
     * @return copia del cluster
     */
    public Cluster createACopy() {
        Cluster copyC = new Cluster();
        for (int i = 0; i < getSize(); i++) {
            copyC.addData(clusteredData[i]);
        }
        return copyC;
    }

    /**
     * Crea un nuovo cluster che è la fusione dei due cluster preesistenti.
     * 
     * @param c cluster da unire
     * @return nuovo cluster risultante dalla fusione
     */
    public Cluster mergeCluster(Cluster c) {
        Cluster newC = new Cluster();
        for (int i = 0; i < getSize(); i++) {
            newC.addData(clusteredData[i]);
        }
        for (int i = 0; i < c.getSize(); i++) {
            newC.addData(c.clusteredData[i]);
        }
        return newC;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < clusteredData.length - 1; i++) {
            str.append(clusteredData[i]).append(",");
        }
        str.append(clusteredData[clusteredData.length - 1]);
        return str.toString();
    }

    public String toString(Data data) {
        StringBuilder str = new StringBuilder();
        for (Integer integer : clusteredData) {
            str.append("<").append(data.getExample(integer)).append(">");
        }
        return str.toString();
    }
}
