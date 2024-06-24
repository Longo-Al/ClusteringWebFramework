package defaultpackage.exceptions;
/**
 * Eccezione lanciata quando la dimensione è invalida.
 */
public class InvalidSizeException extends IllegalArgumentException {
    /**
     * Costruttore della classe InvalidSizeException.
     * 
     * @param message messaggio di errore
     */
    public InvalidSizeException(String message) {
        super(message);
    }
}
