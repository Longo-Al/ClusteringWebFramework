package Map.Server.src.database.Exception;

/**
 * Eccezione che viene lanciata quando manca un numero necessario.
 * Questa classe estende <code>Exception</code> e fornisce un costruttore per passare il messaggio di errore.
 * 
 * @author Alex Longo
 */
public class MissingNumberException extends Exception {

    /**
     * Costruttore della <code>MissingNumberException</code>.
     * Crea un'istanza dell'eccezione con un messaggio personalizzato.
     * 
     * @param message Il messaggio di errore che descrive la mancanza del numero.
     */
    public MissingNumberException(String message) {
        super(message);
    }
}
