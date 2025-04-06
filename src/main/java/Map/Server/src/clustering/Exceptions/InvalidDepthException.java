package Map.Server.src.clustering.Exceptions;

/**
 * Eccezione personalizzata lanciata quando la profondità specificata
 * per un dendrogramma è minore o uguale a zero, il che non è valido
 * per le operazioni di clustering gerarchico.
 *
 * @author Longo Alex
 */
public class InvalidDepthException extends Exception {

    /**
     * Costruisce una nuova eccezione {@code InvalidDepthException}
     * con il messaggio di errore specificato.
     *
     * @param msg il messaggio che descrive l'errore
     */
    public InvalidDepthException(String msg) {
        super(msg);
    }
}
