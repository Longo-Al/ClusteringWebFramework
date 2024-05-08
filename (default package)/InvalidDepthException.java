// Eccezione per gestire i casi in cui la profondità del dendrogramma è maggiore del numero di esempi nel dataset
public class InvalidDepthException extends IllegalArgumentException {
    public InvalidDepthException(String message) {
        super(message);
    }
}