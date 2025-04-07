package Map.Server.src.clustering.Serialization;

import com.google.gson.*;

import Map.Server.src.clustering.Cluster;
import Map.Server.src.clustering.ClusterableCollection;
import Map.Server.src.clustering.Interface.ClusterableItem;

import java.lang.reflect.Type;

/**
 * Serializer personalizzato per la classe {@link Cluster}.
 * 
 * Questo serializer gestisce la conversione di un oggetto Cluster in un formato JSON specifico,
 * includendo i dati clusterizzati, la distanza e i sotto-cluster.
 * 
 * @author Longo Alex
 */
public class ClusterSerializer implements JsonSerializer<Cluster> {

    /**
     * Collezione di oggetti clusterizzabili associata al Cluster da serializzare.
     */
    private final ClusterableCollection<? extends ClusterableItem<?>> pool;
    
    /**
     * Costruttore del serializer.
     * 
     * @param pool La collezione di elementi clusterizzabili da usare durante la serializzazione.
     */
    public ClusterSerializer(ClusterableCollection<? extends ClusterableItem<?>> pool){
        this.pool = pool;
    }

    /**
     * Serializza un oggetto {@link Cluster} in un {@link JsonElement}.
     * 
     * @param cluster L'istanza di Cluster da serializzare.
     * @param typeOfSrc Il tipo dell'oggetto sorgente (ignorato in questa implementazione).
     * @param context Il contesto di serializzazione.
     * @return Il {@link JsonElement} rappresentante il Cluster serializzato.
     */
    @Override
    public JsonElement serialize(Cluster cluster, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        // Convertiamo gli UUID in una stringa nel formato richiesto
        String clusteredDataString = "(" + cluster.toString(pool) + ")";
        clusteredDataString = clusteredDataString.replace("[", "(").replace("]", ")");
        jsonObject.addProperty("n", clusteredDataString);

        // Aggiungiamo la distanza, arrotondata a due decimali
        jsonObject.addProperty("d", (Math.round(cluster.getDistance() * 100.0) / 100.0));

        // Gestiamo i sotto-cluster
        JsonArray childrenArray = new JsonArray();
        if (cluster.getParent1() != null || cluster.getParent2() != null) {
            if (cluster.getParent1() != null) {
                childrenArray.add(context.serialize(cluster.getParent1()));
            }
            if (cluster.getParent2() != null) {
                childrenArray.add(context.serialize(cluster.getParent2()));
            }
        }
        jsonObject.add("c", childrenArray);

        return jsonObject;
    }
}
