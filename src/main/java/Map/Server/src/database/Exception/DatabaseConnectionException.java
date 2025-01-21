package Map.Server.src.database.Exception;
/** Eccezione lanciata quando non è possibile stabilire una connessione al database */
public class DatabaseConnectionException extends Exception {
    /** Costruttore */
    public DatabaseConnectionException(String msg) {
        super(msg);
    }
}
