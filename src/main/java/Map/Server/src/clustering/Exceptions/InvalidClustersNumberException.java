package Map.Server.src.clustering.Exceptions;

/**
 * Eccezione personalizzata lanciata quando il numero di cluster specificato
 * è minore di 1, ovvero non valido per un'operazione di clustering.
 *
 * @author Longo Alex
 */
public class InvalidClustersNumberException extends Exception {

    /**
     * Costruisce una nuova eccezione {@code InvalidClustersNumberException}
     * con il messaggio di errore specificato.
     *
     * @param msg il messaggio che descrive l'errore
     */
    public InvalidClustersNumberException(String msg) {
        super(msg);
    }
}
