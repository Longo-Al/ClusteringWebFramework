package Map.Server.src.server_core;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonParseException;

import Map.Server.src.clustering.ClusterableCollection;
import Map.Server.src.clustering.HierachicalClusterMiner;
import Map.Server.src.clustering.Interface.ClusterableItem;
import Map.Server.src.clustering.distance.AverageLinkDistance;
import Map.Server.src.clustering.distance.SingleLinkDistance;
import Map.Server.src.database.DbAccess;
import Map.Server.src.database.JsonSafeConverter;
import Map.Server.src.database.Exception.DatabaseConnectionException;
import Map.Server.src.database.Pojo.Dataset;

class ClientHandler extends Thread {
    private Socket clientSocket;
    private DataInputStream dis;
    private DataOutputStream dos;

    public ClientHandler(Socket socket) throws IOException {
        this.clientSocket = socket;
        this.dis = new DataInputStream(clientSocket.getInputStream());
        this.dos = new DataOutputStream(clientSocket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            askMacro("CLEAR");
            sendMenu();
            while (true) {
                String command = dis.readUTF();
                
                if (command.equalsIgnoreCase("bye")) {
                    break;
                }else {
                    switch (command) {
                        case "1":
                            handleDatasetLoading();
                            break;
                        case "2":
                            handleClustering();
                            break;
                        default:
                            sendMessage("Comando non valido");
                    }
                    askMacro("CLEAR");
                    sendMenu();
                }

            }
        } catch (IOException e) {
            System.err.println("Errore di comunicazione: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void sendMenu(){
        try{
            dos.writeUTF(
                "\n===== MENU =====\n" +
                "1. Carica un dataset\n" +
                "2. Clusterizza un dataset\n" +
                "Digita 'bye' per uscire\n" +
                "================\n" +
                "Inserisci comando: "
            );
            dos.flush();
        } catch(IOException e){
            sendError("Qualcosa è andato storto\n, il server ha rilevato questo errore:\n " + e.getLocalizedMessage(), true);
        }
    }

    private void handleDatasetLoading(){
        try {
            Connection con = DbAccess.getConnection();
            askMacro("CLEAR");
            sendMessage(getLoadDisclaimer());
            dis.readUTF();
            askMacro("CLEAR");
            sendMessage("===== CREA UN DATASET =====");
            sendMessage("Inserisci il nome del dataset:");
            String name = dis.readUTF();
            sendMessage("Inserisci il tipo dei dati nel dataset:");
            sendMessage("(IMPORTANTE: questo campo deve essere coerente con il nome della classe che estende ClusterableItem)");
            String type = dis.readUTF();
            sendMessage("Inserisci una descrizione del dataset(opzionale):");
            String description = dis.readUTF();
            sendMessage("Inserisci dei tag al dataset(opzionale):");
            String tags = dis.readUTF();
            sendMessage("Inserisci il filepath (Assoluto/Relativo) del file json: ");
            String path = dis.readUTF();

            askMacro("LOAD_DATA");
            dos.writeUTF(path);

            byte[] bin_data = handleFileUpload();
            
            String report = dis.readUTF();
            if(!report.contains("File inviato con successo")){
                throw new IOException("Il client non è riuscito a inviare il file");
            }

            Dataset toload = new Dataset(name, description, type, bin_data, tags, con);
            PreparedStatement stmt = toload.generateInsertQuery(con);
            ResultSet sqlresult;
            try {
                stmt.executeUpdate();
                sendMessage("Dataset creato con successo!");
            } catch (SQLException e) {
                boolean choosen = false;
                while (!choosen) {
                    sendError("Dataset già presente sul DB",false);
                    sendMessage("1) Rimpiazza il dataset esistente");
                    sendMessage("2) Annulla");
                    sendMessage("###############");
                    sendMessage("Scegli una opzione");
                    int option = Integer.parseInt(dis.readUTF());
                    switch (option) {
                        case 1:
                            try{
                                sqlresult = Dataset.getInfobyName(con, name);
                                sqlresult.next();
                                stmt = toload.generateUpdateQuery(con, sqlresult.getInt(1));
                                stmt.executeUpdate();
                                sendMessage("Dataset aggiornato con successo!");
                            } catch (SQLException e1) {
                                throw e1;
                            }finally{
                                choosen = true;
                            }
                            break;
                        case 2:
                                sendMessage("Operazione annullata");
                                choosen = true;
                            break;
                        default:
                            sendError("Scelta non valida", true);
                            break;
                    }
               }             
            }finally{
                dos.writeUTF("Premi invio per continuare");
                dis.readUTF();
            }
        } catch (IOException | JsonParseException | ClassNotFoundException | DatabaseConnectionException | SQLException e) {
            sendError("Qualcosa è andato storto,\n il server ha rilevato questo errore:\n " + e.getMessage(),true);
        }
    }

    private void handleClustering() {
        try {
            Connection con = DbAccess.getConnection();
            boolean exitClusteringMenu = false;
            while (!exitClusteringMenu) {
                askMacro("CLEAR");
                sendMessage("===== MENU CLUSTERIZZAZIONE =====");
                sendMessage("1) Mostra tutti i dataset");
                sendMessage("2) Cerca dataset per nome");
                sendMessage("3) Clusterizza un dataset");
                sendMessage("Digita 'exit' per tornare al menu principale");
                sendMessage("Inserisci comando: ");
                String choice = dis.readUTF();
    
                switch (choice) {
                    case "1":
                        showAllDatasets(con);
                        break;
                    case "2":
                        searchDatasetByName(con);
                        break;
                    case "3":
                        clusterizeDataset(con);
                        break;
                    case "exit":
                        exitClusteringMenu = true;
                        break;
                    default:
                        sendMessage("Comando non valido");
                }
            }
        } catch (Exception e) {
            sendError("Errore nella gestione del clustering: " + e.getMessage(),true);
        }
    }

    private void showAllDatasets(Connection con) {
        try {
            ResultSet rs = Dataset.getInfosFromDb(con);
            List<String> datasets = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                String type = rs.getString("type");
                datasets.add("ID: " + id + " - " + name + "\n" + "Type: " + type + "\n" + "Descrizione: "+ description +
                            "\n*******************************\n");
            }
            paginateDatasets(datasets);
        } catch (SQLException | IOException e) {
            sendError("Errore nel recupero dei dataset: " + e.getMessage(),true);
        }
    }

    private void searchDatasetByName(Connection con) {
        try {
            askMacro("CLEAR");
            sendMessage("Inserisci il nome da cercare:");
            String searchTerm = dis.readUTF();

            ResultSet rs = Dataset.getInfobyName(con, searchTerm);
            List<String> datasets = new ArrayList<>();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                String type = rs.getString("type");
                datasets.add("ID: " + id + " - " + name + "\n" + "Type: " + type + "\n" + "Descrizione: "+ description +
                            "\n*******************************\n");
            }

            paginateDatasets(datasets);
        } catch (SQLException | IOException e) {
            sendError("Errore nella ricerca dei dataset: " + e.getMessage(),true);
        }
    }

    private void paginateDatasets(List<String> datasets) throws IOException {
        int total = datasets.size();
        int pageSize = 10;
        int currentPage = 0;
        boolean exitPagination = false;
        String feedback = "";
        while (!exitPagination) {
            askMacro("CLEAR");
    
            int start = currentPage * pageSize;
            int end = Math.min(start + pageSize, total);
    
            sendMessage("=== Visualizzazione dataset (" + (start + 1) + "-" + end + " di " + total + ") ===");
            for (int i = start; i < end; i++) {
                sendMessage(datasets.get(i));
            }
    
            sendMessage("Utilizza 'd' per la pagina successiva, 'a' per la precedente, 'q' per uscire.");
            if(feedback != "")
                sendMessage(feedback);
            sendMessage("Inserisci una scelta: ");
    
            String input = dis.readUTF(); // dis = DataInputStream
            if (input == null || input.isEmpty()) {
                sendError("Nessun input rilevato.", true);
                feedback = "";
                continue;
            }
    
            char nav = Character.toLowerCase(input.charAt(0));
    
            switch (nav) {
                case 'd': // Prossima pagina
                    if ((currentPage + 1) * pageSize < total) {
                        currentPage++;
                        feedback = "";
                    } else {
                        feedback = "\nNon ci sono altre pagine.\n";
                    }
                    break;
    
                case 'a': // Pagina precedente
                    if (currentPage > 0) {
                        currentPage--;
                        feedback = "";
                    } else {
                        feedback = "\nSei già alla prima pagina.\n ";
                    }
                    break;
    
                case 'q':
                    exitPagination = true;
                    break;
    
                default:
                    feedback = "";
                    sendError("Input non valido o non riconosciuto.", true);
                    break;
            }
        }
    }
    
    private void clusterizeDataset(Connection con){
        try{
            Dataset downloaded = null;

            askMacro("CLEAR");
            sendMessage("===== CLUSTERIZZAZIONE DATASET =====");
            
            // Ottieni l'ID del dataset con validazione
            long datasetId = 0;
            while (true) {
                sendMessage("Inserisci l'ID del dataset da clusterizzare:");
                String inputId = dis.readUTF();
                try {
                    datasetId = Long.parseLong(inputId);
                    ResultSet rs = Dataset.getbyId(con, datasetId);
                    if (rs.isAfterLast()) {
                        throw new SQLException("Id non trovato");
                    }
                    downloaded = Dataset.createFromResultSet(rs);
                    break; // esci dal ciclo se l'ID è valido
                } catch (NumberFormatException e) {
                    sendError("Errore: L'ID deve essere un numero intero valido. Riprova.", true);
                    return;
                } catch (SQLException e){
                    sendError(e.getLocalizedMessage(), true );
                    return;
                } 
            }
            
            List<? extends ClusterableItem<?>> reloaded = JsonSafeConverter.parseBlob(downloaded.getData(), downloaded.getType());
            final ClusterableCollection<? extends ClusterableItem<?>> parsed = new ClusterableCollection<>(reloaded);

            sendMessage("Dataset caricato con successo.\n");

            // Seleziona il tipo di distanza con validazione
            int distancetype = 0;
            while (true) {
                sendMessage("Seleziona il tipo di distanza da applicare:");
                sendMessage("1) Average link distance");
                sendMessage("2) Single link distance");
                
                String distChoice = dis.readUTF();
                try {
                    distancetype = Integer.parseInt(distChoice);
                    if (distancetype == 1 || distancetype == 2) {
                        break; // esci se la scelta è valida
                    } else {
                        sendMessage("Errore: Opzione non valida. Seleziona 1 o 2.");
                    }
                } catch (NumberFormatException e) {
                    sendMessage("Errore: Devi scegliere un numero valido (1 o 2). Riprova.");
                }
            }

            ResultSet infos = Dataset.getClusterInfo(con, datasetId);
            infos.next();
            int maxDepth =  infos.getInt("MaxLevel");
            int depth;
            while (true) {
                sendMessage("Seleziona la profondità alla quale fermare il clustering"+"(MAX:"+maxDepth+"):");
                String depthChoice = dis.readUTF();
                try {
                    depth = Integer.parseInt(depthChoice);
                    if (depth > 0 && depth <= maxDepth) {
                        break; // esci se la scelta è valida
                    } else {
                        sendMessage("Inserisci una profondità valida.");
                    }
                } catch (NumberFormatException e) {
                    sendMessage("La profondità deve essere un numero intero compreso tra 1 e "+maxDepth);
                }
            }


            // Seleziona il tipo di output con validazione
            int outputType = 0;
            String outputChoice;
            while (true) {
                sendMessage("Seleziona il tipo di output desiderato:");
                sendMessage("1) Standard output (Per fini di test)");
                sendMessage("2) Come albero binario (Per l'utilizzo del frontend)");
                
                outputChoice = dis.readUTF();
                outputType =  Integer.parseInt(outputChoice);
                if (outputType == 1 || outputType == 2) {
                    switch (outputType) {
                        case 1:
                            outputChoice = "Standard output";
                            break;
                        case 2:
                            outputChoice ="Albero binario";
                            break;
                    }
                    break; // esci se la scelta è valida
                } else {
                    sendMessage("Errore: Opzione non valida. Seleziona 1 o 2.");
                }
            }
            
            sendMessage("Clusterizzazione avviata per il dataset " + datasetId + " con output tipo " + outputChoice);
            HierachicalClusterMiner<? extends ClusterableItem<?>> cm = new HierachicalClusterMiner<>(parsed, depth);
            
            if (distancetype == 1) 
                cm.mine(new AverageLinkDistance());
            else
                cm.mine(new SingleLinkDistance());
            
            String toSend;
            if (outputType == 1)
                toSend = cm.toVerboseString();
            else
                toSend = cm.toJson();

            byte[] contentBytes = toSend.getBytes("UTF-8");
            String generatedFileName = "cluster_result_" + datasetId + ".txt";

            // Macro per preparare il client a ricevere il file
            askMacro("DOWNLOAD_DATA");

            // Invio del file
            dos.writeUTF(generatedFileName);        // Nome file
            dos.writeLong(contentBytes.length);     // Dimensione
            dos.write(contentBytes);                // Contenuto
            dos.flush();
    
            String report = dis.readUTF();
            if (!report.contains("File salvato con successo")) {
                throw new Exception("Il client non ha confermato il salvataggio del file");
            }else
                dos.writeUTF(report);

            dis.readUTF();
        } catch(Exception e) {
            sendError("Qualcosa sembra essere andato storto: " + e.getMessage(), true);
        }
}

    private byte[] handleFileUpload() throws IOException{
        byte[] fileData = null;
        String fileName = dis.readUTF();

        if (fileName.contains("Errore: Il file specificato non esiste.")) {
            throw new IOException("Il file non esiste");
        }

        long fileSize = dis.readLong();
        
        fileData = new byte[(int) fileSize];
        dis.readFully(fileData);
        
        sendMessage("File " + fileName + " caricato con successo (" + fileSize + " bytes)");
        return fileData;
    }

    private void askMacro(String cmd){
        try {
            dos.writeUTF("MACRO:"+ cmd);
            dos.flush();
        } catch (IOException e) {
                System.err.println("Errore nell'invio del comando: " + e.getMessage());
        }
    }

    private void sendMessage(String message) {
        try {
            dos.writeUTF(message);
            dos.flush();
        } catch (IOException e) {
            System.err.println("Errore nell'invio del messaggio: " + e.getMessage());
        }
    }

    private void sendError(String error, boolean askEnter) {
        try {
            askMacro("CLEAR");
            dos.writeUTF("####-ERRORE-####");
            dos.writeUTF(error);
            dos.writeUTF("################");
            dos.flush();
    
            if (askEnter) {
                dos.writeUTF("Premi INVIO per continuare...");
                dos.flush();
    
                // Leggi e scarta la riga per evitare input residuo
                dis.readUTF(); // Aspetta che l'utente prema INVIO
            }
    
        } catch (IOException e) {
            System.err.println("Errore nell'invio del messaggio: " + e.getMessage());
        }
    }
    
    public String getLoadDisclaimer(){
        return
            "DISCLAIMER PER IL CARICAMENTO FILE JSON\n" +
            "\n" +
            "Al fine di garantire la scalabilità e l'efficienza del programma, i dati da clusterizzare devono essere caricati in formato JSON.\n" +
            "Il file JSON deve rispettare la seguente struttura:\n" +
            "\n" +
            "- Deve contenere un array di oggetti.\n" +
            "- Ogni oggetto deve essere codificato secondo il formato \"tipo oggetto\": {valore}, dove:\n" +
            "  - \"tipo oggetto\" è una stringa identificativa (es. \"example\").\n" +
            "  - {valore} è il dato associato.\n" +
            "\n" +
            "Un esempio di file JSON valido è il seguente:\n" +
            "\n" +
            "[\n" +
            "    {\n" +
            "        \"example\": [1.0, 2.0, 0.0]\n" +
            "    },\n" +
            "    {\n" +
            "      ...\n"+        
            "    }\n" +
            "    ...\n"+
            "]\n" +
            "\n" +
            "ATTENZIONE:\n" +
            "- È fondamentale rispettare il formato sopra indicato per evitare errori durante l'elaborazione.\n" +
            "- Ogni elemento dell'array deve contenere un solo oggetto chiave-valore.\n" +
            "- Il tipo di oggetto (es. \"example\") deve essere coerente all'interno di tutto il file.\n"+
            "\n Premi invio per continuare.\n";
    }

    private void closeConnection() {
        try {
            askMacro("EXIT");
            if (dis != null) dis.close();
            if (dos != null) dos.close();
            clientSocket.close();
            System.out.println("Connessione chiusa con: " + clientSocket.getRemoteSocketAddress());
        } catch (IOException e) {
            System.err.println("Errore nella chiusura della connessione: " + e.getMessage());
        }
    }
    
}