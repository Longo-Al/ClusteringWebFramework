package Map.Server.src.database.Exception;

/** Eccezione lanciata quando manca un numero */
public class MissingNumberException extends Exception {
    /** Costruttore
     * @param message messaggio da visualizzare
     * */
    public MissingNumberException(String message) {
        super(message);
    }
}
