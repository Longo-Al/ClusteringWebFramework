package Map.Server;


import Map.Server.src.server_core.MultiServer;

/**
 * Classe main del Server.
 */
public class Main {

    /**
     * Punto di partenza dell'applicazione lato server.
     *
     * @param args argomenti passati da terminale
     */
    public static void main(String[] args) {
        String ArgPort = System.getProperty("serverPort", "5000");;
        if (args.length == 0) {
            System.out.println("Nessun numero di porta inserito.");
            System.out.println("Utilizzo la porta di default: " + ArgPort);
        }

        int port = Integer.parseInt(ArgPort);
        try {
            
            if (port < 0 || port > 65535) {
                System.err.println("Numero di porta non valido: " + args[0]);
                System.exit(1);
            }
        } catch (NumberFormatException e) {
            System.err.println("Numero di porta non valido: " + args[0]);
            System.exit(1);
            return;
        }

        System.out.println("Server avviato sulla porta " + port);
        MultiServer.instanceMultiServer(port);
    }
}
