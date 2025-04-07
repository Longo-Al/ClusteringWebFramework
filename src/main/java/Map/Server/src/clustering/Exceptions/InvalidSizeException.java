package Map.Server.src.clustering.Exceptions;

/**
 * Eccezione personalizzata lanciata quando la dimensione di un insieme
 * di cluster è inferiore a 1, condizione non valida per molte operazioni
 * di clustering.
 * 
 * @author Longo Alex
 */
public class InvalidSizeException extends Exception {

    /**
     * Costruisce una nuova eccezione {@code InvalidSizeException}
     * con il messaggio di errore specificato.
     *
     * @param message il messaggio che descrive l'errore
     */
    public InvalidSizeException(String message) {
        super(message);
    }
}
