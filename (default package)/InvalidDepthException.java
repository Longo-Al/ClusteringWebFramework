// Eccezione per gestire i casi in cui la profondità del dendrogramma è maggiore del numero di esempi nel dataset
public class InvalidDepthException extends IllegalArgumentException {
    public InvalidDepthException(String message) {
        super(message);
    }
}

// Eccezione per gestire i casi in cui si tenta di calcolare la distanza tra due esempi di dimensioni diverse
public class InvalidSizeException extends IllegalArgumentException {
    public InvalidSizeException(String message) {
        super(message);
    }
}
