package Map.Server.src.clustering.Exceptions;

/**
 * Eccezione personalizzata lanciata quando si raggiunge il livello massimo
 * consentito durante un'operazione di clustering gerarchico o generazione
 * del dendrogramma.
 * 
 * Questa eccezione può essere utile per prevenire ricorsione infinita
 * o cicli di elaborazione troppo profondi.
 *
 * @author Longo Alex
 */
public class MaxLevelReachedException extends Exception {

    /**
     * Costruisce una nuova eccezione {@code MaxLevelReachedException}
     * con il messaggio di errore specificato.
     *
     * @param msg il messaggio che descrive l'errore
     */
    public MaxLevelReachedException(String msg) {
        super(msg);
    }
}
