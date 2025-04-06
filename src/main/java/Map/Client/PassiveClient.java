package Map.Client;

import java.io.*;
import java.net.*;

/**
 * Classe che rappresenta un client passivo che si connette a un server
 * e gestisce sia i messaggi dal server che l'input da tastiera dell'utente.
 *
 * Supporta comandi speciali ("MACRO:") per eseguire operazioni come download/upload file
 * e pulizia dello schermo.
 * 
 * @author Longo Alex
 */
public class PassiveClient {
    private Socket serverSocket;
    private DataInputStream serverIn;
    private DataOutputStream serverOut;
    private BufferedReader userIn;
    private PrintWriter userOut;
    private volatile boolean running = true;

    /**
     * Costruttore del client passivo.
     * 
     * @param host Indirizzo del server a cui connettersi
     * @param port Porta del server
     * @throws IOException se si verifica un errore durante la connessione
     */
    public PassiveClient(String host, int port) throws IOException {
        serverSocket = new Socket(host, port);
        serverIn = new DataInputStream(serverSocket.getInputStream());
        serverOut = new DataOutputStream(serverSocket.getOutputStream());
        userIn = new BufferedReader(new InputStreamReader(System.in));
        userOut = new PrintWriter(System.out, true);
    }

    /**
     * Avvia due thread per gestire l'input da server e da utente.
     */
    public void start() {
        new Thread(this::handleServerMessages).start();
        new Thread(this::handleUserInput).start();
    }

    /**
     * Gestisce i messaggi ricevuti dal server.
     * Se il messaggio è un comando speciale, lo esegue come macro.
     */
    private void handleServerMessages() {
        try {
            while (running) {
                String message = serverIn.readUTF();
                if (isSpecialCommand(message)) {
                    executeMacro(message);
                } else {
                    userOut.println(message);
                }
            }
        } catch (IOException e) {
            userOut.println("Qualcosa è andato storto: " + e.getMessage());
        } finally {
            stop();
        }
    }

    /**
     * Legge l'input da tastiera e lo invia al server.
     */
    private void handleUserInput() {
        try {
            String userInput;
            while (running && (userInput = userIn.readLine()) != null) {
                serverOut.writeUTF(userInput);
            }
        } catch (IOException e) {
            userOut.println("Errore nella lettura dell'input utente");
        } finally {
            stop();
        }
    }

    /**
     * Verifica se il messaggio ricevuto è un comando speciale (macro).
     * 
     * @param message Messaggio ricevuto dal server
     * @return true se è una macro, false altrimenti
     */
    private boolean isSpecialCommand(String message) {
        return message.startsWith("MACRO:");
    }

    /**
     * Esegue una macro ricevuta dal server.
     * 
     * @param command Comando macro da eseguire
     * @throws IOException se si verifica un errore durante l'esecuzione
     */
    private void executeMacro(String command) throws IOException {
        String macro = command.substring(6).trim().toUpperCase();
        switch (macro) {
            case "EXIT":
                userOut.println("Chiusura del client...");
                stop();
                break;

            case "LOAD_DATA":
                try {
                    LoadDataMacro(this);
                } catch (IOException e) {
                    userOut.println("Errore durante l'esecuzione della macro");
                }
                break;

            case "DOWNLOAD_DATA":
                try {
                    DownloadDataMacro(this);
                } catch (IOException e) {
                    userOut.println("Errore durante il download dei dati: " + e.getMessage());
                }
                break;

            case "CLEAR":
                screenClear();
                break;

            default:
                userOut.println("Macro non riconosciuta: " + macro);
        }
    }

    /**
     * Pulisce la console in base al sistema operativo.
     */
    private void screenClear() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            System.out.println("Errore nella pulizia della console: " + e.getMessage());
        }
    }

    /**
     * Macro per il download di un file dal server e salvataggio locale.
     * 
     * @param sh Riferimento all'istanza del client
     * @throws IOException se si verifica un errore nella ricezione o scrittura del file
     */
    public static void DownloadDataMacro(PassiveClient sh) throws IOException {
        String fileName = sh.serverIn.readUTF();
        long fileSize = sh.serverIn.readLong();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        long bytesReceived = 0;

        while (bytesReceived < fileSize) {
            int bytesRead = sh.serverIn.read(buffer, 0, (int)Math.min(buffer.length, fileSize - bytesReceived));
            if (bytesRead == -1) break;
            baos.write(buffer, 0, bytesRead);
            bytesReceived += bytesRead;
        }

        String content = baos.toString("UTF-8");

        String userFileName = fileName;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("ClusteringResult/" + userFileName))) {
            writer.write(content);
        }

        sh.serverOut.writeUTF("File salvato con successo come: " + userFileName);
    }

    /**
     * Macro per il caricamento di un file locale e invio al server.
     * 
     * @param sh Riferimento all'istanza del client
     * @throws IOException se il file non esiste o in caso di errore I/O
     */
    public static void LoadDataMacro(PassiveClient sh) throws IOException {
        String filePath = sh.serverIn.readUTF();

        File file = new File(filePath);

        if (!file.exists() || !file.isFile()) {
            sh.serverOut.writeUTF("Errore: Il file specificato non esiste.");
            return;
        }

        sh.serverOut.writeUTF(file.getName());
        sh.serverOut.writeLong(file.length());

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) > 0) {
                sh.serverOut.write(buffer, 0, bytesRead);
            }
        }
        sh.serverOut.flush();
        sh.serverOut.writeUTF("File inviato con successo.");
    }

    /**
     * Ferma il client e chiude la connessione.
     */
    private void stop() {
        running = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            // Ignora errori durante la chiusura
        }
        System.exit(0);
    }

    /**
     * Metodo main per avviare il client passivo.
     * 
     * @param args arg[0] = indirizzo IP (opzionale), arg[1] = porta (opzionale)
     */
    public static void main(String[] args) {
        String defaultAddress = "localhost";
        int defaultPort = 5000;

        String address = (args.length > 0) ? args[0] : defaultAddress;
        int port;

        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
                if (port < 0 || port > 65535) {
                    System.err.println("Numero di porta non valido: " + port);
                    return;
                }
            } catch (NumberFormatException e) {
                System.err.println("Formato della porta non valido: " + args[1]);
                return;
            }
        } else {
            port = defaultPort;
            System.out.println("Nessuna porta specificata, utilizzo la porta di default: " + defaultPort);
        }

        try {
            PassiveClient client = new PassiveClient(address, port);
            client.start();
        } catch (IOException e) {
            System.out.println("Errore di connessione: " + e.getMessage());
        }
    }
}
