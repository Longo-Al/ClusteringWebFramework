package Map.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import Map.Server.src.clustering.ClusterableCollection;
import Map.Server.src.clustering.HierachicalClusterMiner;
import Map.Server.src.clustering.Exceptions.InvalidClustersNumberException;
import Map.Server.src.clustering.Exceptions.InvalidDepthException;
import Map.Server.src.clustering.Exceptions.InvalidSizeException;
import Map.Server.src.clustering.Interface.ClusterableItem;
import Map.Server.src.database.DbAccess;
import Map.Server.src.database.JsonSafeConverter;
import Map.Server.src.database.Exception.DatabaseConnectionException;
import Map.Server.src.database.Pojo.Dataset;
import Map.Server.src.clustering.distance.AverageLinkDistance;
import Map.Server.src.clustering.distance.ClusterDistance;
import Map.Server.src.clustering.distance.SingleLinkDistance;

/**
 * Servlet che gestisce l'operazione di clustering su un dataset.
 * La servlet esegue il clustering gerarchico su un dataset, in base ai parametri forniti
 * dalla richiesta HTTP, e restituisce il risultato in formato JSON.
 * 
 * @author Alex Longo
 */
public class MiningService extends HttpServlet {
    private static Connection connection;
    
    /**
     * Inizializza la servlet e stabilisce una connessione con il database.
     * 
     * @throws ServletException se la connessione al database non può essere stabilita
     */
    @Override
    public void init() throws ServletException {
        super.init();
        if (connection == null) {
            try {
                connection = DbAccess.getConnection();
            } catch (DatabaseConnectionException e) {
                throw new ServletException(e.getLocalizedMessage());
            }
        }
    }

    /**
     * Gestisce una richiesta GET per eseguire il clustering su un dataset specificato.
     * La servlet esegue il clustering utilizzando il metodo gerarchico, utilizzando
     * il tipo di distanza e la profondità forniti come parametri nella richiesta.
     * 
     * @param request la richiesta HTTP
     * @param response la risposta HTTP
     * @throws ServletException se si verifica un errore durante l'elaborazione della richiesta
     * @throws IOException se si verifica un errore durante la scrittura della risposta
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        // Estrai il path info dopo il mapping del servlet
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\":\"Missing dataset index in path\"}");
            return;
        }
        
        // Rimuove lo slash iniziale e ottiene l'indice
        String[] pathParts = pathInfo.split("/");
        if (pathParts.length < 2) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\":\"Invalid path format\"}");
            return;
        }
        
        String indiceDatasetStr = pathParts[1];
        String profonditaStr = request.getParameter("depth");
        String tipoDistanza = request.getParameter("distance_type");
        
        if (profonditaStr == null || tipoDistanza == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\":\"Missing parameters\"}");
            return;
        }
        
        try {
            int indiceDataset = Integer.parseInt(indiceDatasetStr);
            int profondita = Integer.parseInt(profonditaStr);
            
            Connection connection = DbAccess.getConnection();
            ResultSet sqlresult = Dataset.getbyId(connection, indiceDataset);
            if (!sqlresult.next()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write("{\"error\":\"Dataset not found\"}");
                return;
            }
            
            Dataset downloaded = Dataset.createFromResultSet(sqlresult);
            List<? extends ClusterableItem<?>> reloaded;
            try {
                reloaded = JsonSafeConverter.parseBlob(downloaded.getData(), downloaded.getType());
            } catch (ClassNotFoundException e) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write("{\"error\":\"Type cannot be recognized, check your data.\"}");
                return;
            }
            ClusterableCollection<? extends ClusterableItem<?>> reBasic = new ClusterableCollection<>(reloaded);
            
            HierachicalClusterMiner<? extends ClusterableItem<?>> miner = new HierachicalClusterMiner<>(reBasic, profondita);
            ClusterDistance distanceMetric;
            
            if ("average".equalsIgnoreCase(tipoDistanza)) {
                distanceMetric = new AverageLinkDistance();
            } else if ("single".equalsIgnoreCase(tipoDistanza)) {
                distanceMetric = new SingleLinkDistance();
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"error\":\"Invalid distance type\"}");
                return;
            }
            
            miner.mine(distanceMetric);
            
            String jsonResponse = miner.toJson();
            out.write(jsonResponse);
        } catch (SQLException | DatabaseConnectionException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\":\"Database error: " + e.getMessage() + "\"}");
        } catch (JsonParseException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\":\"JSON parsing error\"}");
        } catch (InvalidDepthException | InvalidSizeException | InvalidClustersNumberException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\":\"Clustering error: " + e.getMessage() + "\"}");
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\":\"Invalid number format\"}");
        }
    }
}
