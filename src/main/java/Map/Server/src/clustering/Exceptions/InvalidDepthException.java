package Map.Server.src.clustering.Exceptions;

/** Eccezione lanciata quando la profondità del dendrogramma è minore o uguale a 0 */
public class InvalidDepthException extends Exception{
    /** Costruttore */
    public InvalidDepthException(String msg) {
        super(msg);
    }
}
