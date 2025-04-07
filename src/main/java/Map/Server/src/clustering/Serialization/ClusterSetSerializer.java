package Map.Server.src.clustering.Serialization;

import com.google.gson.*;
import java.lang.reflect.Type;

import Map.Server.src.clustering.ClusterSet;
import Map.Server.src.clustering.ClusterableCollection;
import Map.Server.src.clustering.Interface.ClusterableItem;
import Map.Server.src.clustering.Cluster;

/**
 * Serializer personalizzato per la classe {@link ClusterSet}.
 * 
 * Questo serializer gestisce la conversione di un oggetto ClusterSet in un formato JSON,
 * rappresentando l'intera struttura di clustering in maniera compatta e leggibile.
 * 
 * @author Longo Alex
 */
public class ClusterSetSerializer implements JsonSerializer<ClusterSet> {

    /**
     * Collezione di oggetti clusterizzabili associata al ClusterSet da serializzare.
     */
    private final ClusterableCollection<? extends ClusterableItem<?>> pool;

    /**
     * Costruttore del serializer.
     * 
     * @param pool La collezione di elementi clusterizzabili da usare durante la serializzazione.
     */
    public ClusterSetSerializer(ClusterableCollection<? extends ClusterableItem<?>> pool){
        this.pool = pool;
    }

    /**
     * Serializza un oggetto {@link ClusterSet} in un {@link JsonElement}.
     * 
     * @param clusterSet L'istanza di ClusterSet da serializzare.
     * @param typeOfSrc Il tipo dell'oggetto sorgente (ignorato).
     * @param context Il contesto di serializzazione.
     * @return Il {@link JsonElement} rappresentante il ClusterSet serializzato.
     */
    @Override
    public JsonElement serialize(ClusterSet clusterSet, Type typeOfSrc, JsonSerializationContext context) {
        if (clusterSet.size() == 1) {
            // Se c'è un solo cluster, restituiamo direttamente il suo JSON
            return context.serialize(clusterSet.first());
        }

        JsonObject jsonObject = new JsonObject();
        
        // Nome generico per il set
        jsonObject.addProperty("n", "");

        // Calcoliamo il delta delle distanze tra i cluster
        double minDistance = Double.MAX_VALUE;
        double maxDistance = Double.MIN_VALUE;
        for (Cluster cluster : clusterSet) {
            double d = cluster.getDistance();
            if (d < minDistance) minDistance = d;
            if (d > maxDistance) maxDistance = d;
        }
        double deltaDistance = maxDistance - minDistance;
        jsonObject.addProperty("d", (Math.round(deltaDistance * 100.0) / 100.0));

        // Aggiungiamo la lista dei cluster in "c"
        JsonArray clustersArray = new JsonArray();
        for (Cluster cluster : clusterSet) {
            clustersArray.add(context.serialize(cluster));
        }
        jsonObject.add("c", clustersArray);

        return jsonObject;
    }
}
