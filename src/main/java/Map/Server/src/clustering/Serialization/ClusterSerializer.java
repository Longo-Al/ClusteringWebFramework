package Map.Server.src.clustering.Serialization;

import com.google.gson.*;

import Map.Server.src.clustering.Cluster;
import Map.Server.src.clustering.ClusterableCollection;
import Map.Server.src.clustering.Interface.ClusterableItem;

import java.lang.reflect.Type;

public class ClusterSerializer implements JsonSerializer<Cluster> {

    private final ClusterableCollection<? extends ClusterableItem<?>> pool;
    
    public ClusterSerializer(ClusterableCollection<? extends ClusterableItem<?>> pool){
        this.pool = pool;
    }

    @Override
    public JsonElement serialize(Cluster cluster, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        // Convertiamo gli UUID in una stringa nel formato richiesto
        String clusteredDataString = "("+cluster.toString(pool)+")";
        clusteredDataString = clusteredDataString.replace("[", "(").replace("]", ")");
        jsonObject.addProperty("n", clusteredDataString);

        // Aggiungiamo la distanza
        jsonObject.addProperty("d", String.format("%.2f",cluster.getDistance()));

        // Gestiamo i sotto-cluster
        JsonArray childrenArray = new JsonArray();
        if (cluster.getParent1() != null || cluster.getParent2() != null) {
            if (cluster.getParent1() != null) {
                childrenArray.add(context.serialize(cluster.getParent1()));
            }
            if (cluster.getParent2() != null) {
                childrenArray.add(context.serialize(cluster.getParent2()));
            }
        } else {
            // Se non ha figli, lo mettiamo come array vuoto
            childrenArray = new JsonArray();
        }
        jsonObject.add("c", childrenArray);

        return jsonObject;
    }
}
