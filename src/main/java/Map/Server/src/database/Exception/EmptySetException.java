package Map.Server.src.database.Exception;

/**
 * Eccezione che viene lanciata quando un insieme risulta vuoto.
 * Questa classe estende <code>Exception</code> e fornisce un costruttore per passare il messaggio di errore.
 * 
 * @author Alex Longo
 */
public class EmptySetException extends Exception {

    /**
     * Costruttore della <code>EmptySetException</code>.
     * Crea un'istanza dell'eccezione con un messaggio personalizzato.
     * 
     * @param message Il messaggio di errore che descrive il motivo per cui l'insieme è vuoto.
     */
    public EmptySetException(String message) {
        super(message);
    }
}
