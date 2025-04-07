package Map.Server.src.clustering;

import java.util.HashMap;
import Map.Server.src.clustering.Exceptions.InvalidDepthException;
import Map.Server.src.clustering.Interface.ClusterableItem;

/**
 * Classe che rappresenta un dendrogramma, un'alberatura gerarchica che modella la struttura
 * dei cluster a diversi livelli di profondità.
 * Ogni livello contiene un insieme di cluster che rappresentano una fase di fusione dei dati.
 *
 * @author Alex Longo
 */
public class Dendrogram extends HashMap<Integer, ClusterSet> {

    /** Profondità massima del dendrogramma */
    private final int MaxDepth;

    /** Flag che indica se il dendrogramma è completo o meno */
    private boolean isComplete;

    /**
     * Restituisce se il dendrogramma è completo o meno.
     *
     * @return {@code true} se il dendrogramma è completo, {@code false} altrimenti.
     */
    public boolean isComplete() {
        return this.isComplete;
    }

    /**
     * Imposta lo stato di completezza del dendrogramma.
     *
     * @param isComplete stato di completezza da impostare.
     */
    private void setComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    /**
     * Restituisce il risultato finale del dendrogramma, ovvero l'insieme di cluster all'ultima profondità
     * se il dendrogramma è completo.
     *
     * @return il cluster finale all'ultima profondità, o {@code null} se non è completo.
     */
    public ClusterSet getResult() {
        if (isComplete) {
            return this.get(this.MaxDepth - 1);
        }
        return null;
    }

    /**
     * Costruttore che inizializza il dendrogramma con una profondità specificata.
     *
     * @param depth profondità massima del dendrogramma.
     * @throws InvalidDepthException se la profondità è inferiore o uguale a zero.
     */
    Dendrogram(int depth) throws InvalidDepthException {
        super();
        if (depth <= 0) {
            throw new InvalidDepthException("Profondità non valida!\n");
        }
        this.MaxDepth = depth;
        setComplete(false);
    }

    /**
     * Aggiunge un livello di cluster nel dendrogramma. Se il numero di livelli raggiunge la profondità massima,
     * il dendrogramma viene marcato come completo.
     *
     * @param key livello del dendrogramma da inserire.
     * @param value insieme di cluster da associare al livello.
     * @return il precedente insieme di cluster associato al livello, o {@code null} se non esisteva.
     */
    @Override
    public ClusterSet put(Integer key, ClusterSet value) {
        ClusterSet output = null;
        if (this.size() < MaxDepth) {
            output = super.put(key, value);
            if (this.size() == MaxDepth) {
                setComplete(true);
                return value;
            }
        }
        return output;
    }

    /**
     * Restituisce la profondità corrente del dendrogramma.
     *
     * @return la profondità corrente del dendrogramma.
     */
    int getDepth() {
        return this.size();
    }

    /**
     * Restituisce la profondità massima del dendrogramma.
     *
     * @return la profondità massima del dendrogramma.
     */
    public int getMaxDepth() {
        return MaxDepth;
    }

    /**
     * Restituisce una rappresentazione testuale del dendrogramma, visualizzando ogni livello e il relativo insieme di cluster.
     *
     * @return una stringa che rappresenta l'intero dendrogramma.
     */
    public String toString() {
        StringBuilder v = new StringBuilder();
        for (Integer k : this.keySet()) {
            v.append("level").append(k.intValue() + 1).append(":\n").append(this.get(k)).append("\n");
        }
        return v.toString();
    }

    /**
     * Restituisce una rappresentazione testuale del dendrogramma, visualizzando ogni livello e il relativo insieme di cluster,
     * utilizzando il dataset fornito.
     *
     * @param data il dataset di esempio su cui il clustering è stato effettuato.
     * @return una stringa che rappresenta l'intero dendrogramma, utilizzando il dataset.
     */
    public String toString(ClusterableCollection<? extends ClusterableItem<?>> data) {
        StringBuilder v = new StringBuilder();
        for (Integer k : this.keySet()) {
            v.append("level").append(k.intValue() + 1).append(":\n").append(this.get(k).toString(data)).append("\n");
        }
        return v.toString();
    }
}
