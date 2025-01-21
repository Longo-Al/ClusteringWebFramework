package Map.Server.src.server_core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.FileAlreadyExistsException;

import Map.Server.src.clustering.ClusterableCollection;
import Map.Server.src.clustering.HierachicalClusterMiner;
import Map.Server.src.clustering.Exceptions.InvalidClustersNumberException;
import Map.Server.src.clustering.Exceptions.InvalidDepthException;
import Map.Server.src.clustering.Exceptions.InvalidSizeException;
import Map.Server.src.distance.AverageLinkDistance;
import Map.Server.src.distance.ClusterDistance;
import Map.Server.src.distance.SingleLinkDistance;

/**
* Gestore client per gestire le connessioni con i client.
*/
class ServerOneClient extends Thread {
    private final Socket clientSocket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private ClusterableCollection<?> data; // Memorizza l'oggetto Data caricato

    /**
    * Costruttore per il gestore client.
     *
     * @param socket il socket del client
     * @throws IOException se si verifica un errore di I/O
     */
    public ServerOneClient(Socket socket) throws IOException {
        this.clientSocket = socket;
        this.out = new ObjectOutputStream(clientSocket.getOutputStream());
        this.in = new ObjectInputStream(clientSocket.getInputStream());
        this.start();
    }

    /**
     * Metodo che gestisce le richieste del client.
     */
    @Override
    public void run() {
        try {
            while (true) {
                int requestType = (int) in.readObject();

                switch (requestType) {
                    case 0:
                        // Carica dati dal database
                        
                        break;
                    case 1:
                        // Esegui clustering
                        handleClustering();
                        break;
                    case 2:
                        // Carica il dendrogram da file
                        handleLoadDendrogramFromFile();
                        break;
                    default:
                        out.writeObject("Tipo di richiesta non valido");
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("Disconnessione client: " + clientSocket);
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                clientSocket.close();
                out.close();
                in.close();
            } catch (IOException e) {
                System.err.println("Errore nella chiusura del socket o degli ObjectStream");
            }
        }
    }

    /**
     * Gestisce l'operazione di clustering.
     *
     * @throws IOException se si verifica un errore di I/O
     * @throws ClassNotFoundException se la classe non è trovata
     */
    private void handleClustering() throws IOException, ClassNotFoundException {
        if (data == null) {
            out.writeObject("Dati non caricati");
            return;
        }

        int depth = (int) in.readObject();
        int distanceType = (int) in.readObject();

        try {
            HierachicalClusterMiner clustering = new HierachicalClusterMiner(depth);
            ClusterDistance distance = distanceType == 1 ? new SingleLinkDistance() : new AverageLinkDistance();

            clustering.mine(data, distance);

            out.writeObject("OK");
            out.writeObject(clustering.toString(data));

            String fileName = (String) in.readObject();

            try {
                out.writeObject("Dendrogramma salvato correttamente.");
            } catch (FileAlreadyExistsException e) {
                out.writeObject("Errore. Il file esiste già: " + fileName);
            } catch (IOException e) {
                out.writeObject("Errore durante il salvataggio del dendrogramma: " + e.getMessage());
            }
        } catch (InvalidSizeException | InvalidClustersNumberException | InvalidDepthException | IllegalArgumentException e) {
            out.writeObject(e.getMessage());
        }
    }


    /**
     * Gestisce il caricamento del dendrogram da un file.
     *
     * @throws IOException se si verifica un errore di I/O
     * @throws ClassNotFoundException se la classe non è trovata
     */
    private void handleLoadDendrogramFromFile() throws IOException, ClassNotFoundException {
        String fileName = (String) in.readObject();
        try {
            HierachicalClusterMiner clustering = HierachicalClusterMiner.loadHierachicalClusterMiner(fileName);

            if (data == null) {
                out.writeObject("Dati non caricati");
                return;
            }

            if (clustering.getDepth() > data.size()) {
                out.writeObject("Numero di esempi maggiore della profondità del dendrogramma!");
            } else {
                out.writeObject("OK");
                out.writeObject(clustering.toString(data));
            }
        } catch (IOException | InvalidDepthException e) {
            out.writeObject(e.getMessage());
        }
    }
}

