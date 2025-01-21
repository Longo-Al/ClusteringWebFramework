package Map.Server.src.clustering.Exceptions;

/** Eccezione lanciata quando il numero di cluster è minore di 1 */
public class InvalidClustersNumberException extends Exception{
    /** Costruttore */
    public InvalidClustersNumberException(String msg) {
        super(msg);
    }
}
