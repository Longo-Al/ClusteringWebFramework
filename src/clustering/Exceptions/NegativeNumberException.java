package clustering.Exceptions;
/**
 * Eccezione lanciata quando viene fornito un numero negativo non valido.
 */
public class NegativeNumberException extends IllegalArgumentException {
    /**
     * Costruttore della classe NegativeNumberException.
     * 
     * @param message messaggio di errore
     */
    public NegativeNumberException(String message) {
        super(message);
    }
}
