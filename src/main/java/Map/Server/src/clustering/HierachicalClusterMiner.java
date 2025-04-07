package Map.Server.src.clustering;

import java.io.*;
import java.util.UUID;
import Map.Server.src.clustering.Exceptions.InvalidClustersNumberException;
import Map.Server.src.clustering.Exceptions.InvalidDepthException;
import Map.Server.src.clustering.Exceptions.InvalidSizeException;
import Map.Server.src.clustering.Interface.ClusterableItem;
import Map.Server.src.clustering.distance.ClusterDistance;

/**
 * La classe <code>HierachicalClusterMiner</code> è responsabile per il processo di clustering gerarchico
 * dei dati forniti. Utilizza un dendrogramma per rappresentare le operazioni di clustering.
 * 
 * @param <T> Tipo di oggetti clusterabili che estendono l'interfaccia <code>ClusterableItem</code>.
 * 
 * @author Alex Longo
 */
public class HierachicalClusterMiner<T extends ClusterableItem<?>> implements Serializable {
    private final ClusterableCollection<T> data;
    /** Dendrogramma che rappresenta la struttura gerarchica dei cluster */
    private final Dendrogram dendrogram;

    /**
     * Costruttore della classe <code>HierachicalClusterMiner</code>.
     * Crea un'istanza del miner per eseguire il clustering dei dati con una profondità definita.
     * 
     * @param data Il dataset di oggetti clusterabili da processare.
     * @param depth La profondità desiderata del dendrogramma.
     * @throws InvalidDepthException Se la profondità è inferiore a 1.
     */
    public HierachicalClusterMiner(ClusterableCollection<T> data, int depth) throws InvalidDepthException {
        this.data = data;
        this.dendrogram = new Dendrogram(depth);
    }

    /**
     * Restituisce la profondità del dendrogramma.
     * 
     * @return La profondità del dendrogramma.
     */
    public int getDepth() {
        return dendrogram.getDepth();
    }

    /**
     * Restituisce il risultato finale del clustering sotto forma di un insieme di cluster.
     * 
     * @return L'insieme di cluster risultante dal clustering.
     */
    public ClusterSet getResult() {
        return this.dendrogram.getResult();
    }

    /**
     * Esegue il processo di clustering gerarchico sui dati.
     * 
     * @param distance Interfaccia per il calcolo della distanza tra cluster.
     * @throws InvalidDepthException Se la profondità del dendrogramma è inferiore al numero di esempi.
     * @throws InvalidSizeException Se la dimensione del cluster è inferiore a 2.
     * @throws InvalidClustersNumberException Se il numero di cluster è inferiore a 2.
     */
    public void mine(ClusterDistance distance) throws InvalidDepthException, InvalidSizeException, InvalidClustersNumberException {
        if (getDepth() > data.size()) {
            throw new InvalidDepthException("Numero di esempi maggiore della profondità del dendrogramma!\n");
        }

        ClusterSet level0 = new ClusterSet();
        for (UUID uuid : data) {
            Cluster c = new Cluster();
            c.addData(uuid);
            level0.add(c);
        }

        dendrogram.put(0, level0);
        for (int i = 1; i < dendrogram.getMaxDepth(); i++) {
            ClusterSet nextlevel;
            try {
                nextlevel = dendrogram.get(i - 1).mergeClosestClusters(distance, data);
                dendrogram.put(i, nextlevel);
            } catch (InvalidSizeException | InvalidClustersNumberException e) {
                i = getDepth();
                throw e;
            }
        }
    }

    /**
     * Restituisce una rappresentazione testuale del dendrogramma.
     * 
     * @return Una stringa che rappresenta il dendrogramma.
     */
    public String toString() {
        return dendrogram.toString();
    }

    /**
     * Restituisce una rappresentazione dettagliata del dendrogramma.
     * 
     * @param data Il dataset di esempi da utilizzare per la rappresentazione.
     * @throws InvalidDepthException Se la profondità del dendrogramma è inferiore al numero di esempi.
     * @return Una stringa che rappresenta il dendrogramma in dettaglio.
     */
    public String toVerboseString() throws InvalidDepthException {
        return dendrogram.toString(data);
    }

    /**
     * Restituisce una rappresentazione del risultato finale in formato JSON.
     * 
     * @return Una stringa JSON che rappresenta il risultato del clustering.
     */
    public String toJson() {
        return ClusterSet.toJson(this.getResult(), this.data);
    }
}
