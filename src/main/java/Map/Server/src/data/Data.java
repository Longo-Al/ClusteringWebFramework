package Map.Server.src.data;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import com.google.gson.Gson;
import Map.Server.src.database.*;

/**
 * Classe Data
 * Gestisce un dataset memorizzato in una tabella del database oppure come oggetto JSON nel database.
 */
public class Data {
    /** Lista di esempi */
    private final List<Example> data = new ArrayList<>(); // rappresenta il dataset

    /**
     * Costruttore
     * Crea un'istanza della classe Data leggendo i suoi esempi dalla tabella con nome tableName nel database.
     *
     * @param tableName Nome della tabella nel database
     * @throws NoDataException se la tabella è vuota.
     */
    public Data(String tableName) throws NoDataException {
        DbAccess dbAccess = new DbAccess();
        try {
            TableData tableData = new TableData(dbAccess);
            List<Example> examples = tableData.getDistinctTransazioni(tableName);
            this.data.addAll(examples);
        } catch (DatabaseConnectionException e) {
            throw new NoDataException("Errore di connessione al database: " + e.getMessage() + "\n");
        } catch (EmptySetException e) {
            throw new NoDataException("La tabella " + tableName + " è vuota: " + e.getMessage() + "\n");
        } catch (MissingNumberException e) {
            throw new NoDataException("Eccezione durante l'elaborazione dei dati: " + e.getMessage() + "\n");
        } catch (SQLException e) {
            throw new NoDataException("Errore SQL durante il recupero dei dati dalla tabella: " + e.getMessage() + "\n");
        }
    }

    /**
     * Costruttore vuoto per deserializzazione.
     */
    public Data() {}

    /**
     * Metodo getNumberOfExample
     * Restituisce il numero degli esempi memorizzati in data.
     *
     * @return numero di esempi nel dataset
     */
    public int getNumberOfExample() {
        return data.size();
    }

    /**
     * Metodo getExample
     * Restituisce l'elemento dell'istanza data in posizione exampleIndex.
     *
     * @param exampleIndex indice dell'elemento da restituire
     * @return elemento in posizione exampleIndex
     */
    public Example getExample(int exampleIndex) {
        return data.get(exampleIndex);
    }

    /**
     * Metodo iterator
     * Restituisce un iterator per scorrere gli elementi di data.
     *
     * @return iterator per scorrere gli elementi di data
     */
    public Iterator<Example> iterator() {
        return data.iterator();
    }

    /**
     * Metodo toString
     * Crea una stringa in cui memorizza gli esempi memorizzati nell’attributo data, opportunamente enumerati.
     *
     * @return stringa con gli esempi in data
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        Iterator<Example> iterator = iterator();
        int count = 0;

        while (iterator.hasNext()) {
            s.append(count++).append(":[").append(iterator.next().toString()).append("]\n");
        }

        return s.toString();
    }

    /**
     * Metodo toJSON
     * Serializza l'oggetto Data in formato JSON.
     *
     * @return stringa JSON rappresentante l'oggetto
     */
    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);  // Serializza l'oggetto in JSON
    }

    /**
     * Metodo statico fromJSON
     * Deserializza una stringa JSON in un oggetto Data.
     *
     * @param json stringa JSON
     * @return oggetto Data deserializzato
     */
    public static Data fromJSON(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Data.class);  // Deserializza il JSON in un oggetto
    }

    /**
     * Salva il dataset come JSON nel database.
     *
     * @param connection Connessione al database
     * @param name Nome identificativo del dataset
     * @throws SQLException in caso di errori SQL
     */
    public void saveToDatabase(Connection connection, String name) throws SQLException {
        String sql = "INSERT INTO datasets (name, description, data) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            // Serializza il dataset in JSON
            String json = this.toJSON();

            // Imposta i parametri della query
            ps.setString(1, name);                     // Nome del dataset
            ps.setString(2, "Serialized Data Object"); // Descrizione
            ps.setBytes(3, json.getBytes("UTF-8"));    // Dati come BLOB

            ps.executeUpdate(); // Esegue la query
        } catch (UnsupportedEncodingException e) {
            throw new SQLException("Errore durante la conversione del dataset in JSON", e);
        }
    }

    /**
     * Carica un dataset dal database, deserializzandolo da JSON.
     *
     * @param connection Connessione al database
     * @param name Nome identificativo del dataset
     * @return Oggetto Data deserializzato
     * @throws SQLException in caso di errori SQL
     */
    public static Data loadFromDatabase(Connection connection, String name) throws SQLException {
        String sql = "SELECT data FROM datasets WHERE name = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name); // Imposta il parametro per il nome del dataset

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    byte[] jsonBytes = rs.getBytes("data"); // Recupera i dati come BLOB
                    String json = new String(jsonBytes, "UTF-8"); // Converte i byte in una stringa JSON

                    return Data.fromJSON(json); // Deserializza il JSON in un oggetto Data
                } else {
                    throw new SQLException("Dataset con nome '" + name + "' non trovato nel database.");
                }
            }
        } catch (UnsupportedEncodingException e) {
            throw new SQLException("Errore durante la lettura del dataset dal database", e);
        }
    }
}
