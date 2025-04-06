package Map.Server.src.clustering.Exceptions;

/**
 * Eccezione personalizzata lanciata quando non sono presenti dati 
 * da elaborare per l'algoritmo di clustering.
 * 
 * Questa condizione può verificarsi, ad esempio, se il dataset è vuoto
 * o non è stato caricato correttamente.
 * 
 * @author Longo Alex
 */
public class NoDataException extends Exception {

    /**
     * Costruisce una nuova eccezione {@code NoDataException}
     * con il messaggio di errore specificato.
     *
     * @param message il messaggio che descrive l'errore
     */
    public NoDataException(String message) {
        super(message);
    }
}
