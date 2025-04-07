package Map.Server.src.database.Exception;

/**
 * Eccezione che viene lanciata quando non è possibile stabilire una connessione al database.
 * Questa classe estende <code>Exception</code> e fornisce un costruttore per passare il messaggio di errore.
 * 
 * @author Alex Longo
 */
public class DatabaseConnectionException extends Exception {

    /**
     * Costruttore della <code>DatabaseConnectionException</code>.
     * Crea un'istanza dell'eccezione con un messaggio personalizzato.
     * 
     * @param msg Il messaggio di errore che descrive il motivo della mancata connessione.
     */
    public DatabaseConnectionException(String msg) {
        super(msg);
    }
}
