package defaultpackage.exceptions;

/**
 * Eccezione lanciata quando la profondità è invalida.
 */
public class InvalidDepthException extends IllegalArgumentException {
    /**
     * Costruttore della classe InvalidDepthException.
     * 
     * @param message messaggio di errore
     */
    public InvalidDepthException(String message) {
        super(message);
    }
}
