package Map.Server.src.database.Pojo;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import com.mysql.cj.exceptions.DataReadException;

import Map.Server.src.clustering.Interface.ClusterableItem;
import Map.Server.src.database.JsonSafeConverter;

public class Dataset {
    private Integer id;
    private String name;
    private String description;
    private Long size;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String type;
    private Blob data;
    private String tags;

    public Dataset(){
        return;
    }
    // Costruttore
    public Dataset(String name, String description, String type, List<? extends ClusterableItem<?>> data, String tags, Connection con) throws SQLException {
        this.name = name;
        this.description = description;
        this.type = type;
        this.data = JsonSafeConverter.CreateBlob(con, data);
        this.tags = tags;
        this.size = this.data.length(); // Calcolo del peso in byte del JSON 
    }

    //to use just for fast fordwarding frontend-to-api
    public Dataset(String name, String description, String type,Blob data, String tags) throws SQLException {
        this.name = name;
        this.description = description;
        this.type = type;
        this.data = data;
        this.tags = tags;
        this.size = this.data.length(); // Calcolo del peso in byte del JSON
    }

    public static Dataset createFromResultSet(ResultSet resultSet) throws SQLException {
        if (Dataset.countRows(resultSet) == 1) {
            resultSet.next();
            Dataset dataset = new Dataset();
            
            dataset.id = resultSet.getInt("id");
            dataset.name = resultSet.getString("name");
            dataset.description = resultSet.getString("description");
            dataset.size = resultSet.getLong("size");
            dataset.createdAt = resultSet.getTimestamp("created_at");
            dataset.updatedAt = resultSet.getTimestamp("updated_at");
            dataset.type = resultSet.getString("type");
            dataset.data = resultSet.getBlob("data");
            dataset.tags = resultSet.getString("tags");
            
            return dataset;
        }else{
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
    //
    //                    DB dataset macro
    //----------------------------------------------------------------------------

    // Metodo per generare una query di aggiornamento
    public PreparedStatement generateUpdateQuery(Connection conn,int id) throws SQLException {
        String query = "UPDATE Datasets SET " +
                       "name = ?, " +
                       "description = ?, " +
                       "size = ?, " +
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
        stmt.setString(index++, type);
        stmt.setBlob(index++, data);
        stmt.setString(index++, tags != null ? tags : null);
        stmt.setTimestamp(index++, Timestamp.from(Instant.now()));
        stmt.setInt(index, id);
      
        return stmt;
    }
    
    // Metodo per generare una query di inserimento
    public PreparedStatement generateInsertQuery(Connection conn) throws SQLException,DataReadException {
        if (isValid()) {
            String query = "INSERT INTO Datasets (name, description, size, type, data, tags) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            // Set dei parametri nel PreparedStatement
            stmt.setString(1, name);
            stmt.setString(2, description != null ? description : null);
            stmt.setLong(3, size);
            stmt.setString(4, type);
            stmt.setBlob(5, data);
            stmt.setString(6, tags != null ? tags : null);
            return stmt;
        }else
            throw new DataReadException("Check the name and the content of your Dataset.");
    }

    public static ResultSet getInfosFromDb(Connection conn) throws SQLException{
        String query = "SELECT id, name, description, size, created_at, updated_at, type, tags FROM Datasets";
        PreparedStatement stmt = conn.prepareStatement(query);
        ResultSet result = stmt.executeQuery();
        return result;
    }

    public static ResultSet getbyId(Connection conn, long id) throws SQLException{
        String query = "SELECT * FROM Datasets WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, String.valueOf(id));
        ResultSet result = stmt.executeQuery();
        return result;
    }

    public static ResultSet getInfobyName(Connection conn, String name) throws SQLException{
        String query = "SELECT id, name, description, size, created_at, updated_at, type, tags FROM Datasets where name LIKE ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, '%'+ name +'%');
        ResultSet result = stmt.executeQuery();
        return result;
    }


    public boolean isValid() {
        Boolean flag = false;
        try {
            if(this.data.length() > 0){
                JsonSafeConverter.tryParsing(data, type);
                if (this.name != null) {
                    flag = true;
                }
            }
        } catch (Exception e) {
            System.out.println("data of "+ this +" seems to be corrupted, investigate.");
        }
        return flag;
    }

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
