package Map.Server.src.database.Pojo;

import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import com.google.gson.JsonParseException;
import com.mysql.cj.exceptions.DataReadException;

import Map.Server.src.clustering.Interface.ClusterableItem;
import Map.Server.src.database.JsonSafeConverter;

/**
 * La classe <code>Dataset</code> rappresenta un dataset all'interno del sistema, 
 * contenente metadati, informazioni sui dati e metodi per l'interazione con il database.
 * 
 * @author Alex Longo
 */
public class Dataset {
    private Integer id;
    private String name;
    private String description;
    private Long size;
    private Integer MaxLevel;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String type;
    private Blob data;
    private String tags;

    public Dataset() {
        return;
    }

    /**
     * Costruttore della classe <code>Dataset</code>.
     * Crea un dataset a partire dal nome, descrizione, tipo, dati (in formato ClusterableItem), 
     * tag associati e la connessione al database.
     *
     * @param name Il nome del dataset.
     * @param description La descrizione del dataset.
     * @param type Il tipo del dataset.
     * @param data I dati del dataset, rappresentati come lista di oggetti <code>ClusterableItem</code>.
     * @param tags I tag associati al dataset.
     * @param con La connessione al database.
     * @throws SQLException Se si verifica un errore durante la creazione del Blob.
     */
    public Dataset(String name, String description, String type, List<? extends ClusterableItem<?>> data, String tags, Connection con) throws SQLException {
        this.name = name;
        this.description = description;
        this.type = type;
        this.data = JsonSafeConverter.CreateBlob(con, data);
        this.MaxLevel = data.size();
        this.tags = tags;
        this.size = this.data.length(); // Calcolo del peso in byte del JSON
    }

    /**
     * Costruttore della classe <code>Dataset</code> con Blob già disponibile.
     * Utilizzato per la rapida trasmissione tra frontend e API.
     *
     * @param name Il nome del dataset.
     * @param description La descrizione del dataset.
     * @param type Il tipo del dataset.
     * @param data Il Blob contenente i dati del dataset.
     * @param tags I tag associati al dataset.
     * @throws SQLException Se si verifica un errore durante l'elaborazione del Blob.
     */
    public Dataset(String name, String description, String type, Blob data, String tags) throws SQLException {
        this.name = name;
        this.description = description;
        this.type = type;
        this.data = data;
        this.tags = tags;
        this.size = this.data.length(); // Calcolo del peso in byte del JSON
    }

    /**
     * Costruttore della classe <code>Dataset</code> a partire da un array di byte binari.
     * Viene utilizzato per creare un dataset a partire da dati binari.
     *
     * @param name Il nome del dataset.
     * @param description La descrizione del dataset.
     * @param type Il tipo del dataset.
     * @param bin_data I dati binari.
     * @param tags I tag associati al dataset.
     * @param con La connessione al database.
     * @throws SQLException Se si verifica un errore durante l'elaborazione dei dati.
     * @throws JsonParseException Se si verifica un errore durante la deserializzazione dei dati.
     * @throws ClassNotFoundException Se si verifica un errore durante il caricamento delle classi.
     * @throws IOException Se si verifica un errore durante il parsing.
     */
    public Dataset(String name, String description, String type, byte[] bin_data, String tags, Connection con) throws SQLException, JsonParseException, ClassNotFoundException, IOException {
        this.name = name;
        this.description = description;
        this.type = type;
        List<? extends ClusterableItem<?>> data = JsonSafeConverter.ParseByte(bin_data, type);
        this.data = JsonSafeConverter.CreateBlob(con, data);
        this.MaxLevel = data.size();
        this.tags = tags;
        this.size = this.data.length(); // Calcolo del peso in byte del JSON
    }

    /**
     * Crea un oggetto <code>Dataset</code> a partire da un <code>ResultSet</code>.
     * Viene utilizzato per caricare un dataset dal database.
     *
     * @param resultSet Il <code>ResultSet</code> contenente i dati del dataset.
     * @return Un oggetto <code>Dataset</code> con i dati del <code>ResultSet</code>.
     * @throws SQLException Se si verifica un errore durante il recupero dei dati.
     */
    public static Dataset createFromResultSet(ResultSet resultSet) throws SQLException {
        if (Dataset.countRows(resultSet) == 1) {
            resultSet.next();
            Dataset dataset = new Dataset();

            dataset.id = resultSet.getInt("id");
            dataset.name = resultSet.getString("name");
            dataset.description = resultSet.getString("description");
            dataset.size = resultSet.getLong("size");
            dataset.MaxLevel = resultSet.getInt("MaxLevel");
            dataset.createdAt = resultSet.getTimestamp("created_at");
            dataset.updatedAt = resultSet.getTimestamp("updated_at");
            dataset.type = resultSet.getString("type");
            dataset.data = resultSet.getBlob("data");
            dataset.tags = resultSet.getString("tags");

            return dataset;
        } else {
            throw new SQLException("Can't load more than one ENTIRE dataset each time");
        }
    }

    // Getter e Setter
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getSize() {
        return size;
    }

    public Integer getMaxLevel() {
        return MaxLevel;
    }

    public void setMaxLevel(Integer MaxLevel) {
        this.MaxLevel = MaxLevel;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Blob getData() {
        return data;
    }

    public void setData(Blob data) throws SQLException {
        this.data = data;
        this.size = (long) data.length(); // Ricalcolo del peso in byte
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    //----------------------------------------------------------------------------

    /**
     * Metodo per generare una query di aggiornamento per il dataset nel database.
     * 
     * @param conn La connessione al database.
     * @param id L'ID del dataset da aggiornare.
     * @return Il <code>PreparedStatement</code> per l'aggiornamento del dataset.
     * @throws SQLException Se si verifica un errore durante la creazione della query.
     */
    public PreparedStatement generateUpdateQuery(Connection conn, int id) throws SQLException {
        String query = "UPDATE Datasets SET " +
                       "name = ?, " +
                       "description = ?, " +
                       "size = ?, " +
                       "MaxLevel = ?, " +
                       "type = ?, " +
                       "data = ?, " +
                       "tags = ?, " +
                       "updated_at = ? " +
                       "WHERE id = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        int index = 1;

        // Set dei parametri nel PreparedStatement
        stmt.setString(index++, name);
        stmt.setString(index++, description != null ? description : null);
        stmt.setLong(index++, size);
        stmt.setInt(index++, MaxLevel);
        stmt.setString(index++, type);
        stmt.setBlob(index++, data);
        stmt.setString(index++, tags != null ? tags : null);
        stmt.setTimestamp(index++, Timestamp.from(Instant.now()));
        stmt.setInt(index, id);

        return stmt;
    }

    /**
     * Metodo per generare una query di inserimento per un nuovo dataset nel database.
     * 
     * @param conn La connessione al database.
     * @return Il <code>PreparedStatement</code> per l'inserimento del nuovo dataset.
     * @throws SQLException Se si verifica un errore durante la creazione della query.
     * @throws DataReadException Se i dati del dataset non sono validi.
     */
    public PreparedStatement generateInsertQuery(Connection conn) throws SQLException, DataReadException {
        if (isValid()) {
            String query = "INSERT INTO Datasets (name, description, size, MaxLevel, type, data, tags) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            // Set dei parametri nel PreparedStatement
            stmt.setString(1, name);
            stmt.setString(2, description != null ? description : null);
            stmt.setLong(3, size);
            stmt.setInt(4, MaxLevel);
            stmt.setString(5, type);
            stmt.setBlob(6, data);
            stmt.setString(7, tags != null ? tags : null);
            return stmt;
        } else
            throw new DataReadException("Check the name and the content of your Dataset.");
    }

    /**
     * Metodo per recuperare le informazioni generali di tutti i dataset nel database.
     * 
     * @param conn La connessione al database.
     * @return Un <code>ResultSet</code> contenente le informazioni di tutti i dataset.
     * @throws SQLException Se si verifica un errore durante l'esecuzione della query.
     */
    public static ResultSet getInfosFromDb(Connection conn) throws SQLException {
        String query = "SELECT id, name, description, size, created_at, updated_at, type, tags FROM Datasets";
        PreparedStatement stmt = conn.prepareStatement(query);
        ResultSet result = stmt.executeQuery();
        return result;
    }

    /**
     * Metodo per recuperare un dataset dal database dato il suo ID.
     * 
     * @param conn La connessione al database.
     * @param id L'ID del dataset da recuperare.
     * @return Un <code>ResultSet</code> contenente i dati del dataset.
     * @throws SQLException Se si verifica un errore durante l'esecuzione della query.
     */
    public static ResultSet getbyId(Connection conn, long id) throws SQLException {
        String query = "SELECT * FROM Datasets WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, String.valueOf(id));
        ResultSet result = stmt.executeQuery();
        return result;
    }

    /**
     * Metodo per recuperare informazioni sui dataset filtrando per nome.
     * 
     * @param conn La connessione al database.
     * @param name Il nome del dataset da cercare.
     * @return Un <code>ResultSet</code> contenente i dataset che corrispondono al filtro.
     * @throws SQLException Se si verifica un errore durante l'esecuzione della query.
     */
    public static ResultSet getInfobyName(Connection conn, String name) throws SQLException {
        String query = "SELECT id, name, description, size, created_at, updated_at, type, tags FROM Datasets where name LIKE ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, '%' + name + '%');
        ResultSet result = stmt.executeQuery();
        return result;
    }

    /**
     * Metodo per recuperare informazioni sui dataset relativi al clustering.
     * 
     * @param conn La connessione al database.
     * @param id L'ID del dataset da recuperare.
     * @return Un <code>ResultSet</code> contenente le informazioni del dataset relative al clustering.
     * @throws SQLException Se si verifica un errore durante l'esecuzione della query.
     */
    public static ResultSet getClusterInfo(Connection conn, long id) throws SQLException {
        String query = "SELECT id, MaxLevel FROM Datasets WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setLong(1, id);
        ResultSet result = stmt.executeQuery();
        return result;
    }

    /**
     * Metodo per verificare la validità del dataset.
     * Un dataset è valido se i suoi dati sono corretti e non nulli.
     * 
     * @return <code>true</code> se il dataset è valido, <code>false</code> altrimenti.
     */
    public boolean isValid() {
        Boolean flag = false;
        try {
            if (this.data.length() > 0) {
                JsonSafeConverter.tryParsing(data, type);
                if (this.name != null) {
                    flag = true;
                }
            }
        } catch (Exception e) {
            System.out.println("data of " + this + " seems to be corrupted, investigate.");
        }
        return flag;
    }

    /**
     * Metodo per contare il numero di righe in un <code>ResultSet</code>.
     * 
     * @param resultSet Il <code>ResultSet</code> da cui contare le righe.
     * @return Il numero di righe nel <code>ResultSet</code>.
     * @throws SQLException Se si verifica un errore durante l'accesso al <code>ResultSet</code>.
     */
    private static int countRows(ResultSet resultSet) throws SQLException {
        int rowCount = 0;
        if (resultSet != null) {
            resultSet.last(); // Moves to the last row
            rowCount = resultSet.getRow(); // Gets the current row number (last row number)
            resultSet.beforeFirst(); // Moves back to the beginning of the ResultSet
        }
        return rowCount;
    }
}
