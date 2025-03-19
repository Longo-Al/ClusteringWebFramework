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
 * classe ClusterSet
 * Implementa un insieme di cluster
 *
 * @author Team MAP Que Nada
 */
public class ClusterSet extends TreeSet<Cluster>{
	/**
	 * Costruttore
	 * crea un'istanza di classe ClusterSet di dimensione k
	 
	 * @param k dimensione dell'insieme di cluster
	 */
	ClusterSet(){
		super();
	}

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
	 * Metodo toString
	 * restituisce una stringa contenente gli indici degli esempi raggruppati nei cluster
	 *
	 * @return str stringa contenente gli indici degli esempi raggruppati nei cluster
	 */
	public String toString(){
		StringBuilder str= new StringBuilder();
		int i = 0;
		for(Cluster c: this){
			i++;
			if (c!=null) str.append("cluster").append(i).append(":").append(c).append("\n");
		}
		return str.toString();

	}

	/**
	 * Metodo toString
	 * restituisce una stringa contenente gli esempi raggruppati nei cluster
	 * @param <T>
	 *
	 * @param data oggetto di classe Data che modella il dataset su cui il clustering è calcolato
	 * @return str stringa contenente gli esempi raggruppati nei cluster
	 */
	public String toString(ClusterableCollection<? extends ClusterableItem<?>> data){
		StringBuilder str= new StringBuilder();
		int i = 0;
		for(Cluster c: this){
			i++;
			if (c!=null) str.append("cluster").append(i).append(":").append(c.toString(data)).append("\n");
		}
		return str.toString();

	}

	
	public static String toJson(ClusterSet layer, ClusterableCollection<? extends ClusterableItem<?>> data) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ClusterSet.class, new ClusterSetSerializer(data))
				.registerTypeAdapter(Cluster.class, new ClusterSerializer(data))
                .setPrettyPrinting()
                .create();
        return gson.toJson(layer);
    }

}
