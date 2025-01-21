package Map.Server.src.clustering.Interface;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public abstract class ClusterableItem<T> {

    /**
     * Calcola i valori valutativi per l'istanza data.
     * @return Una lista di valori numerici rappresentativi dell'istanza.
     */
    public abstract List<Double> evaluate();

    /**
     * Converte l'oggetto corrente in formato JSON.
     * @return Una stringa JSON che rappresenta l'oggetto.
     */

    public final static String toJson(List<? extends ClusterableItem<?>> toConvert) {
        Gson gson = new Gson();
        return gson.toJson(toConvert);
    }

    /**
     * Deserializza una stringa JSON in una lista di oggetti ClusterableItem<T>.
     * @param json La stringa JSON da deserializzare.
     * @param type Il tipo specifico da utilizzare per la lista.
     * @return Una lista di oggetti ClusterableItem<T> rappresentata dal JSON.
     * 
          * @throws ClassNotFoundException
          * @throws JsonParseException
          */
        public final static <T extends ClusterableItem<?>> List<T> fromJsonList(String json, String typeName) throws JsonParseException, ClassNotFoundException {
        // Trova la classe associata al nome
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) Class.forName("Map.Server.item_types."+typeName);

        // Creazione del TypeToken per la deserializzazione
        Type listType = TypeToken.getParameterized(List.class, clazz).getType();

        // Deserializzazione
        Gson gson = new Gson();
        return gson.fromJson(json, listType);
    }

}