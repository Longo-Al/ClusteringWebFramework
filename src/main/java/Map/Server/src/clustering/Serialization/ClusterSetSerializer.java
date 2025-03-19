
package Map.Server.src.clustering.Serialization;

import com.google.gson.*;
import java.lang.reflect.Type;
import Map.Server.src.clustering.ClusterSet;
import Map.Server.src.clustering.ClusterableCollection;
import Map.Server.src.clustering.Interface.ClusterableItem;
import Map.Server.src.clustering.Cluster;

public class ClusterSetSerializer implements JsonSerializer<ClusterSet> {
    private final ClusterableCollection<? extends ClusterableItem<?>> pool;

    public ClusterSetSerializer(ClusterableCollection<? extends ClusterableItem<?>> pool){
        this.pool = pool;
    }

    @Override
    public JsonElement serialize(ClusterSet clusterSet, Type typeOfSrc, JsonSerializationContext context) {
        if (clusterSet.size() == 1) {
            // Se c'è un solo cluster, restituiamo direttamente il suo JSON
            return context.serialize(clusterSet.first());
        }

        JsonObject jsonObject = new JsonObject();

        StringBuilder nValue = new StringBuilder("[");
        // Unire tutti gli UUID di tutti i cluster in una stringa
        for (Cluster cluster : clusterSet) {
            nValue.append(cluster.toString(pool)+';');
        }
        nValue.setCharAt(nValue.length()-1, ']');
        String dataString = nValue.toString().replace("[", "(").replace("]", ")");
        jsonObject.addProperty("n", dataString);

        // Calcoliamo il delta delle distanze tra i cluster
        double minDistance = Double.MAX_VALUE;
        double maxDistance = Double.MIN_VALUE;
        for (Cluster cluster : clusterSet) {
            double d = cluster.getDistance();
            if (d < minDistance) minDistance = d;
            if (d > maxDistance) maxDistance = d;
        }
        double deltaDistance = maxDistance - minDistance;
        jsonObject.addProperty("d", String.format("%.2f",deltaDistance));

        // Aggiungiamo la lista dei cluster in "c"
        JsonArray clustersArray = new JsonArray();
        for (Cluster cluster : clusterSet) {
            clustersArray.add(context.serialize(cluster));
        }
        jsonObject.add("c", clustersArray);

        return jsonObject;
    }
}
