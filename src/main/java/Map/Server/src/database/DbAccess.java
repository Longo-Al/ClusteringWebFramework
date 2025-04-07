package Map.Server.src.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import Map.Server.src.database.Exception.DatabaseConnectionException;

/**
 * La classe <code>DbAccess</code> gestisce l'accesso al database, fungendo da semaforo per la connessione,
 * permettendo il riutilizzo della connessione esistente.
 * Utilizza il pattern singleton per garantire che venga creata una sola connessione.
 * 
 * @author Alex Longo
 */
public class DbAccess {
    private static final String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
    private static final String DBMS = "jdbc:mysql";
    private static final String SERVER = "mysql";
    private static final String DATABASE = "MapDB";
    private static final int PORT = 3306;
    private static final String USER_ID = "MapUser";
    private static final String PASSWORD = "map";
    private static final int CONNECTTIMEOUT = 5000;
    private static final int SOCKETTIMEOUT = 5000;
    private static final String SERVERTIMEZONE = "UTC";
    
    private static Connection connection;
    
    /**
     * Costruttore della classe <code>DbAccess</code>.
     * Se la connessione non è ancora stata creata, la inizializza.
     * 
     * @throws DatabaseConnectionException Se non è possibile stabilire la connessione.
     */
    public DbAccess() throws DatabaseConnectionException {
        if (connection == null) {
            initConnection();
        }
    }

    /**
     * Costruttore della classe <code>DbAccess</code> con IP personalizzato.
     * Se la connessione non è ancora stata creata, la inizializza utilizzando l'IP fornito.
     * 
     * @param ip L'indirizzo IP del server del database.
     * @throws DatabaseConnectionException Se non è possibile stabilire la connessione.
     */
    public DbAccess(String ip) throws DatabaseConnectionException {
        if (connection == null) {
            initConnection(ip);
        }
    }

    /**
     * Inizializza la connessione al database se non esiste già.
     * 
     * @throws DatabaseConnectionException Se la connessione fallisce.
     */
    private static synchronized void initConnection() throws DatabaseConnectionException {
        if (connection == null) {
            try {
                Class.forName(DRIVER_CLASS_NAME);
                String connectionString = DBMS + "://" + SERVER + ":" + PORT + "/" + DATABASE +
                        "?user=" + USER_ID + "&password=" + PASSWORD + "&serverTimezone=" + SERVERTIMEZONE +
                        "&connectTimeout=" + CONNECTTIMEOUT + "&socketTimeout=" + SOCKETTIMEOUT;
                connection = DriverManager.getConnection(connectionString);
            } catch (ClassNotFoundException e) {
                throw new DatabaseConnectionException("[!] Driver not found: " + e.getMessage());
            } catch (SQLException e) {
                throw new DatabaseConnectionException("[!] Connection error: " + e.getMessage());
            }
        }
    }

    /**
     * Inizializza la connessione al database con un IP personalizzato se non esiste già.
     * 
     * @param ip L'indirizzo IP del server del database.
     * @throws DatabaseConnectionException Se la connessione fallisce.
     */
    private static synchronized void initConnection(String ip) throws DatabaseConnectionException {
        if (connection == null) {
            try {
                Class.forName(DRIVER_CLASS_NAME);
                String connectionString = DBMS + "://" + ip + ":" + PORT + "/" + DATABASE +
                        "?user=" + USER_ID + "&password=" + PASSWORD + "&serverTimezone=" + SERVERTIMEZONE +
                        "&connectTimeout=" + CONNECTTIMEOUT + "&socketTimeout=" + SOCKETTIMEOUT;
                connection = DriverManager.getConnection(connectionString);
            } catch (ClassNotFoundException e) {
                throw new DatabaseConnectionException("[!] Driver not found: " + e.getMessage());
            } catch (SQLException e) {
                throw new DatabaseConnectionException("[!] Connection error: " + e.getMessage());
            }
        }
    }

    /**
     * Restituisce la connessione al database, inizializzandola se necessario.
     * 
     * @return La connessione al database.
     * @throws DatabaseConnectionException Se la connessione fallisce.
     */
    public static synchronized Connection getConnection() throws DatabaseConnectionException {
        if (connection == null || isClosed()) {
            initConnection();
        }
        return connection;
    }

    /**
     * Restituisce la connessione al database con IP personalizzato, inizializzandola se necessario.
     * 
     * @param ip L'indirizzo IP del server del database.
     * @return La connessione al database.
     * @throws DatabaseConnectionException Se la connessione fallisce.
     */
    public static synchronized Connection getConnection(String ip) throws DatabaseConnectionException {
        if (connection == null || isClosed()) {
            initConnection(ip);
        }
        return connection;
    }

    /**
     * Chiude la connessione al database e la imposta su <code>null</code>.
     */
    public static synchronized void releaseConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                System.err.println("[!] Error closing connection: " + e.getMessage());
            }
        }
    }

    /**
     * Verifica se la connessione è chiusa o non valida.
     * 
     * @return <code>true</code> se la connessione è chiusa o nulla, <code>false</code> altrimenti.
     */
    private static boolean isClosed() {
        try {
            return connection == null || connection.isClosed();
        } catch (SQLException e) {
            return true;
        }
    }
}
