package Map.Server.src.clustering;

import java.util.TreeSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import Map.Server.src.clustering.Exceptions.InvalidClustersNumberException;
import Map.Server.src.clustering.Exceptions.InvalidSizeException;
import Map.Server.src.clustering.Interface.ClusterableItem;
import Map.Server.src.clustering.Serialization.ClusterSerializer;
import Map.Server.src.clustering.Serialization.ClusterSetSerializer;
import Map.Server.src.clustering.distance.ClusterDistance;

/**
 * Classe che rappresenta un insieme di cluster, 
 * implementata come una struttura {@link TreeSet} di oggetti {@link Cluster}.
 * Consente operazioni di fusione dei cluster, visualizzazione dei dati e serializzazione in JSON.
 *
 * @author Team MAP Que Nada
 */
public class ClusterSet extends TreeSet<Cluster> {

    /**
     * Costruttore di default che crea un insieme vuoto di cluster.
     */
    ClusterSet() {
        super();
    }

    /**
     * Fonde i due cluster più vicini all'interno di questo insieme, in base alla distanza
     * calcolata da un oggetto {@link ClusterDistance}.
     *
     * @param distance oggetto che calcola la distanza tra i cluster.
     * @param data la collezione di clusterabili su cui calcolare la distanza.
     * @return un nuovo {@link ClusterSet} con i cluster uniti.
     * @throws InvalidSizeException se le dimensioni dei cluster non coincidono.
     * @throws InvalidClustersNumberException se non ci sono abbastanza cluster da fondere.
     */
    public final ClusterSet mergeClosestClusters(ClusterDistance distance, ClusterableCollection<? extends ClusterableItem<?>> data) throws InvalidSizeException, InvalidClustersNumberException {
        if (this.size() <= 1)
            throw new InvalidClustersNumberException("Non ci sono abbastanza cluster da fondere");

        double minD = Double.MAX_VALUE;
        Cluster cluster1 = null;
        Cluster cluster2 = null;

        for (Cluster c1 : this) {
            for (Cluster c2 : this.tailSet(c1, false)) { // Considera solo i successivi per evitare doppi confronti
                try {
                    double d = distance.distance(c1, c2, data);
                    if (d < minD) {
                        minD = d;
                        cluster1 = c1;
                        cluster2 = c2;
                    }
                } catch (InvalidSizeException e) {
                    throw e;
                }
            }
        }

        if (cluster1 == null || cluster2 == null)
            throw new InvalidClustersNumberException("Errore nella selezione dei cluster da fondere");

        Cluster mergedCluster = cluster1.mergeCluster(cluster2, minD);
        ClusterSet finalClusterSet = new ClusterSet();
        for (Cluster c : this) {
            if (c != cluster1 && c != cluster2) {
                finalClusterSet.add(c);
            }
        }
        finalClusterSet.add(mergedCluster);

        return finalClusterSet;
    }

    /**
     * Restituisce una rappresentazione testuale dell'insieme di cluster.
     * La stringa mostra gli indici degli esempi raggruppati nei cluster.
     *
     * @return una stringa che rappresenta l'insieme di cluster.
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        int i = 0;
        for (Cluster c : this) {
            i++;
            if (c != null) str.append("cluster").append(i).append(":").append(c).append("\n");
        }
        return str.toString();
    }

    /**
     * Restituisce una rappresentazione testuale dell'insieme di cluster,
     * visualizzando gli esempi raggruppati nei cluster, utilizzando un dataset.
     *
     * @param data oggetto di classe {@link ClusterableCollection} che rappresenta il dataset su cui il clustering è calcolato.
     * @return una stringa che rappresenta gli esempi raggruppati nei cluster.
     */
    public String toString(ClusterableCollection<? extends ClusterableItem<?>> data) {
        StringBuilder str = new StringBuilder();
        int i = 0;
        for (Cluster c : this) {
            i++;
            if (c != null) str.append("cluster").append(i).append(":").append(c.toString(data)).append("\n");
        }
        return str.toString();
    }

    /**
     * Converte l'insieme di cluster in una stringa JSON utilizzando la libreria Gson.
     * I cluster vengono serializzati con l'ausilio di {@link ClusterSetSerializer} e {@link ClusterSerializer}.
     *
     * @param layer l'insieme di cluster da serializzare.
     * @param data la collezione di clusterabili su cui il clustering è stato calcolato.
     * @return una stringa JSON che rappresenta l'insieme di cluster.
     */
    public static String toJson(ClusterSet layer, ClusterableCollection<? extends ClusterableItem<?>> data) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ClusterSet.class, new ClusterSetSerializer(data))
                .registerTypeAdapter(Cluster.class, new ClusterSerializer(data))
                .create();
        return gson.toJson(layer);
    }
}
