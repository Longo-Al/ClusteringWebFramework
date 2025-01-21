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

public final class JsonSafeConverter{
    public static List<? extends ClusterableItem<?>> parseBlob(Blob b, String typeName) throws SQLException, ClassNotFoundException {        
        // Leggi il contenuto del blob
        try (InputStream inputStream = b.getBinaryStream()) {
            byte[] jsonBytes = inputStream.readAllBytes(); // Richiede Java 9+
            String jsonString = new String(jsonBytes); // Converti in stringa

            return ClusterableItem.fromJsonList(jsonString, typeName); 
        } catch (Exception e) {
            throw new SQLException("Errore durante il parsing del Blob: " + e.getMessage(), e);
        }
    }

    public static List<? extends ClusterableItem<?>> tryParsing(Blob bin_data, String type) throws JsonParseException, ClassNotFoundException, SQLException, IOException{
        InputStream inputStream = bin_data.getBinaryStream();
        byte[] jsonBytes = inputStream.readAllBytes(); // Richiede Java 9+
        String jsonString = new String(jsonBytes); // Converti in stringa
        return ClusterableItem.fromJsonList(jsonString, type);
    }

    public static Blob CreateBlob(Connection connection, List<? extends ClusterableItem<?>> data) throws SQLException {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("La lista di ClusterableItem non può essere null o vuota.");
        }

        // Serializza la lista in una stringa JSON
        Gson gson = new Gson();
        String json = gson.toJson(data);

        // Converti la stringa JSON in un array di byte
        byte[] jsonBytes = json.getBytes();

        // Crea un Blob utilizzando i byte JSON
        Blob blob = connection.createBlob();
        blob.setBytes(1, jsonBytes);

        return blob;
    }

    public static Blob CreateBlobfromJson(Connection connection, String json, String typeName) throws SQLException, JsonParseException, ClassNotFoundException {
        //tento la deserializzazione, se va a buon fine continuo altrimenti inoltro l'eccezione
        List<ClusterableItem<?>> parseVerify = ClusterableItem.fromJsonList(json, typeName);
        
        // Converti la stringa JSON in un array di byte
        json = parseVerify.toString();
        byte[] jsonBytes = json.getBytes();

        // Crea un Blob utilizzando i byte JSON
        Blob blob = connection.createBlob();
        blob.setBytes(1, jsonBytes);

        return blob;
    }

    public static Blob CreateBlobfromJson(Connection connection, byte[] jsonBytes, String typeName) throws SQLException, JsonParseException, ClassNotFoundException, UnsupportedEncodingException {
        String value = new String(jsonBytes, "UTF-8");
        ClusterableItem.fromJsonList(value, typeName);
        // Crea un Blob utilizzando i byte JSON
        Blob blob = connection.createBlob();
        blob.setBytes(1, jsonBytes);

        return blob;
    }

}