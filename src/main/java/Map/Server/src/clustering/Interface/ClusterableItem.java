package Map.Server.src.clustering.Interface;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Classe astratta che rappresenta un elemento clusterizzabile.
 * 
 * Fornisce metodi per valutare l'istanza e per serializzare/deserializzare oggetti da e verso JSON.
 * 
 * @param <T> Il tipo dell'elemento clusterizzabile.
 * @author Longo Alex
 */
public abstract class ClusterableItem<T> {

    /**
     * Calcola i valori valutativi per l'istanza corrente.
     * 
     * @return Una lista di valori numerici rappresentativi dell'istanza.
     */
    public abstract List<Double> evaluate();

    /**
     * Converte una lista di oggetti ClusterableItem in una rappresentazione JSON.
     * 
     * @param toConvert La lista di oggetti da convertire.
     * @return Una stringa JSON che rappresenta la lista di oggetti.
     */
    public final static String toJson(List<? extends ClusterableItem<?>> toConvert) {
        Gson gson = new Gson();
        return gson.toJson(toConvert);
    }

    /**
     * Deserializza una stringa JSON in una lista di oggetti di tipo ClusterableItem specificato.
     * 
     * @param json La stringa JSON da deserializzare.
     * @param typeName Il nome del tipo da utilizzare per la deserializzazione (presente in "Map.Server.item_types").
     * @param <T> Il tipo degli oggetti deserializzati.
     * @return Una lista di oggetti deserializzati di tipo T.
     * @throws ClassNotFoundException Se il tipo specificato non è stato trovato.
     * @throws JsonParseException Se si verifica un errore nel parsing del JSON.
     */
    public final static <T extends ClusterableItem<?>> List<T> fromJsonList(String json, String typeName) throws JsonParseException, ClassNotFoundException {
        try {
            // Trova la classe associata al nome
            @SuppressWarnings("unchecked")
            Class<T> clazz = (Class<T>) Class.forName("Map.Server.item_types." + typeName);
            // Creazione del TypeToken per la deserializzazione
            Type listType = TypeToken.getParameterized(List.class, clazz).getType();

            // Deserializzazione
            Gson gson = new Gson();
            return gson.fromJson(json, listType);
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundException("Il tipo richiesto non è stato trovato");
        } catch (JsonParseException e) {
            throw new JsonParseException("Errore nel parsing del JSON, si consiglia di leggere il disclaimer");
        }
    }
}
