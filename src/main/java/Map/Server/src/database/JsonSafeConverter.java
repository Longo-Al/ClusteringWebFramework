package Map.Server.src.database;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import Map.Server.src.clustering.Interface.ClusterableItem;

/**
 * La classe <code>JsonSafeConverter</code> fornisce metodi per la gestione sicura di dati JSON 
 * contenuti in oggetti <code>Blob</code> e per la conversione tra oggetti Java e JSON.
 * 
 * @author Alex Longo
 */
public final class JsonSafeConverter {

    /**
     * Converte un <code>Blob</code> in una lista di oggetti <code>ClusterableItem</code>.
     * 
     * @param b Il <code>Blob</code> contenente i dati JSON.
     * @param typeName Il nome del tipo di oggetto da deserializzare.
     * @return Una lista di oggetti <code>ClusterableItem</code> deserializzati dal JSON nel <code>Blob</code>.
     * @throws SQLException Se si verifica un errore durante l'accesso al <code>Blob</code>.
     * @throws ClassNotFoundException Se il tipo di oggetto specificato non è trovato.
     */
    public static List<? extends ClusterableItem<?>> parseBlob(Blob b, String typeName) throws SQLException, ClassNotFoundException {        
        try (InputStream inputStream = b.getBinaryStream()) {
            byte[] jsonBytes = inputStream.readAllBytes(); // Richiede Java 9+
            String jsonString = new String(jsonBytes); // Converti in stringa

            return ClusterableItem.fromJsonList(jsonString, typeName); 
        } catch (Exception e) {
            throw new SQLException("Errore durante il parsing del Blob: " + e.getMessage(), e);
        }
    }

    /**
     * Converte un array di byte in una lista di oggetti <code>ClusterableItem</code>.
     * 
     * @param bin_data I dati binari da deserializzare.
     * @param type Il nome del tipo di oggetto da deserializzare.
     * @return Una lista di oggetti <code>ClusterableItem</code> deserializzati dal byte array.
     * @throws JsonParseException Se si verifica un errore durante il parsing del JSON.
     * @throws ClassNotFoundException Se il tipo di oggetto specificato non è trovato.
     * @throws SQLException Se si verifica un errore SQL.
     * @throws IOException Se si verifica un errore durante la lettura dei dati.
     */
    public static List<? extends ClusterableItem<?>> ParseByte(byte[] bin_data, String type) throws JsonParseException, ClassNotFoundException, SQLException, IOException {
        String jsonString = new String(bin_data); // Converti in stringa
        return ClusterableItem.fromJsonList(jsonString, type);
    }

    /**
     * Prova a eseguire il parsing di un <code>Blob</code> in una lista di oggetti <code>ClusterableItem</code>.
     * 
     * @param bin_data Il <code>Blob</code> contenente i dati binari da deserializzare.
     * @param type Il nome del tipo di oggetto da deserializzare.
     * @return Una lista di oggetti <code>ClusterableItem</code> deserializzati dal <code>Blob</code>.
     * @throws JsonParseException Se si verifica un errore durante il parsing del JSON.
     * @throws ClassNotFoundException Se il tipo di oggetto specificato non è trovato.
     * @throws SQLException Se si verifica un errore SQL.
     * @throws IOException Se si verifica un errore durante la lettura dei dati.
     */
    public static List<? extends ClusterableItem<?>> tryParsing(Blob bin_data, String type) throws JsonParseException, ClassNotFoundException, SQLException, IOException {
        InputStream inputStream = bin_data.getBinaryStream();
        byte[] jsonBytes = inputStream.readAllBytes(); // Richiede Java 9+
        String jsonString = new String(jsonBytes); // Converti in stringa
        return ClusterableItem.fromJsonList(jsonString, type);
    }

    /**
     * Crea un <code>Blob</code> a partire da una lista di oggetti <code>ClusterableItem</code>.
     * 
     * @param connection La connessione al database.
     * @param data La lista di oggetti <code>ClusterableItem</code> da serializzare.
     * @return Un <code>Blob</code> contenente i dati serializzati in formato JSON.
     * @throws SQLException Se si verifica un errore durante la creazione del <code>Blob</code>.
     */
    public static Blob CreateBlob(Connection connection, List<? extends ClusterableItem<?>> data) throws SQLException {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("La lista di ClusterableItem non può essere null o vuota.");
        }

        Gson gson = new Gson();
        String json = gson.toJson(data); // Serializza la lista in una stringa JSON

        byte[] jsonBytes = json.getBytes(); // Converti la stringa in un array di byte

        // Crea un Blob utilizzando i byte JSON
        Blob blob = connection.createBlob();
        blob.setBytes(1, jsonBytes);

        return blob;
    }

    /**
     * Crea un <code>Blob</code> a partire da una stringa JSON e un nome di tipo.
     * 
     * @param connection La connessione al database.
     * @param json La stringa JSON da deserializzare.
     * @param typeName Il nome del tipo di oggetto da deserializzare.
     * @return Un <code>Blob</code> contenente i dati serializzati in formato JSON.
     * @throws SQLException Se si verifica un errore durante la creazione del <code>Blob</code>.
     * @throws JsonParseException Se si verifica un errore durante il parsing del JSON.
     * @throws ClassNotFoundException Se il tipo di oggetto specificato non è trovato.
     */
    public static Blob CreateBlobfromJson(Connection connection, String json, String typeName) throws SQLException, JsonParseException, ClassNotFoundException {
        List<ClusterableItem<?>> parseVerify = ClusterableItem.fromJsonList(json, typeName); // Verifica deserializzazione

        byte[] jsonBytes = json.getBytes(); // Converti la stringa in un array di byte

        // Crea un Blob utilizzando i byte JSON
        Blob blob = connection.createBlob();
        blob.setBytes(1, jsonBytes);

        return blob;
    }

    /**
     * Crea un <code>Blob</code> a partire da un array di byte JSON e un nome di tipo.
     * 
     * @param connection La connessione al database.
     * @param jsonBytes L'array di byte JSON da serializzare.
     * @param typeName Il nome del tipo di oggetto da deserializzare.
     * @return Un <code>Blob</code> contenente i dati serializzati in formato JSON.
     * @throws SQLException Se si verifica un errore durante la creazione del <code>Blob</code>.
     * @throws JsonParseException Se si verifica un errore durante il parsing del JSON.
     * @throws ClassNotFoundException Se il tipo di oggetto specificato non è trovato.
     * @throws UnsupportedEncodingException Se il formato di codifica dei byte non è supportato.
     */
    public static Blob CreateBlobfromJson(Connection connection, byte[] jsonBytes, String typeName) throws SQLException, JsonParseException, ClassNotFoundException, UnsupportedEncodingException {
        String value = new String(jsonBytes, "UTF-8");
        ClusterableItem.fromJsonList(value, typeName); // Verifica deserializzazione

        // Crea un Blob utilizzando i byte JSON
        Blob blob = connection.createBlob();
        blob.setBytes(1, jsonBytes);

        return blob;
    }
}
