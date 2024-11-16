package Map.Server.src.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import Map.Server.src.database.DatabaseConnectionException;
import Map.Server.src.database.DbAccess;

public class MainDataTest {
    public static void main(String[] args) {
        DbAccess access = new DbAccess();
        try (Connection connection = access.getConnection()) {
            // Carica il dataset dal database
            Data basic = new Data("examples");
            basic.saveToDatabase(connection, "Standard dataset");
            Data loadedDataset = Data.loadFromDatabase(connection, "Standard dataset");

            System.out.println("Dataset caricato: " + loadedDataset.toString());
        } catch (SQLException | DatabaseConnectionException e) {
            e.printStackTrace();
        } catch (NoDataException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
    }
}