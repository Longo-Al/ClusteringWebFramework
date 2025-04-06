package Map.Server.src.server_core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * La classe MultiServer gestisce l'avvio e la gestione del server che accetta
 * connessioni multiple da parte di client. Utilizza il pattern Singleton per
 * garantire che ci sia una sola istanza del server.
 * 
 * @author Alex Longo
 */
public class MultiServer {
    /** La porta del server */
    private final int PORT;
    
    /** Riferimento all'istanza Singleton del server */
    private static MultiServer singleton = null;

    /**
     * Costruttore della classe MultiServer. Inizializza la porta e avvia il server
     * invocando il metodo run().
     * 
     * @param port la porta sulla quale il server deve ascoltare le connessioni.
     */
    private MultiServer(int port) {
        this.PORT = port;
        run();
    }

    /**
     * Metodo statico per creare l'istanza unica di MultiServer. Garantisce che
     * il server venga avviato solo una volta, mantenendo l'implementazione Singleton.
     * 
     * @param port la porta sulla quale avviare il server.
     */
    public static void instanceMultiServer(int port) {
        if (singleton == null) {
            singleton = new MultiServer(port);
        }
    }

    /**
     * Avvia il server, creando un oggetto ServerSocket che ascolta le connessioni
     * in ingresso sulla porta specificata. Ogni volta che un client si connette,
     * viene creato un nuovo thread (ClientHandler) per gestire la connessione.
     */
    private void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server avviato sulla porta: " + PORT);
            
            // Ciclo infinito per accettare continuamente nuove connessioni
            while (true) {
                try {
                    // Accetta una connessione in ingresso dal client
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Connessione accettata: " + clientSocket);
                    
                    // Crea e avvia un nuovo thread per gestire la connessione del client
                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    clientHandler.start();
                } catch (IOException e) {
                    // Gestione errori durante la gestione della connessione con il client
                    System.out.println("Errore nella gestione della connessione con il client.");
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            // Gestione errori durante l'avvio del server
            System.out.println("Errore nell'avvio del server.");
            e.printStackTrace();
        }
    }
}
