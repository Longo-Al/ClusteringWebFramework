package Map.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mysql.cj.exceptions.DataReadException;

import Map.Server.src.database.DbAccess;
import Map.Server.src.database.JsonSafeConverter;
import Map.Server.src.database.Exception.DatabaseConnectionException;
import Map.Server.src.database.Pojo.Dataset;

/**
 * Servlet che gestisce le operazioni CRUD sui dataset. Permette di recuperare informazioni,
 * aggiornare e cancellare i dataset nel database.
 * 
 * @author Alex Longo
 */
public class DatasetServlet extends HttpServlet {
    /** Connessione al database */
    private static Connection connection;

    /**
     * Inizializza la connessione al database all'avvio del servlet.
     * 
     * @throws ServletException se non è possibile stabilire la connessione con il database
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
     * Gestisce la richiesta GET per recuperare informazioni sui dataset.
     * Se il path è vuoto, restituisce la lista dei dataset, altrimenti fornisce informazioni su un dataset specifico.
     * 
     * @param request la richiesta HTTP
     * @param response la risposta HTTP
     * @throws ServletException se un errore si verifica durante l'elaborazione della richiesta
     * @throws IOException se un errore si verifica nella lettura dei dati
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
    
        try (PrintWriter out = response.getWriter()) {
            if (pathInfo == null || pathInfo.equals("/")) {
                try (ResultSet rs = Dataset.getInfosFromDb(connection)) {
                    if (rs == null) {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        out.print("{\"error\":\"Failed to retrieve datasets\"}");
                        return;
                    }
                    out.print("[");
                    boolean first = true;
                    while (rs.next()) {
                        if (!first) out.print(",");
                        out.print("{" +
                                "\"id\":" + rs.getInt("id") + "," +
                                "\"name\":\"" + rs.getString("name") + "\"," +
                                "\"description\":\"" + rs.getString("description") + "\"," +
                                "\"size\":" + rs.getLong("size") + "," +
                                "\"created_at\":\"" + rs.getTimestamp("created_at") + "\"," +
                                "\"updated_at\":\"" + rs.getTimestamp("updated_at") + "\"," +
                                "\"type\":\"" + rs.getString("type") + "\"," +
                                "\"tags\":\"" + rs.getString("tags") + "\"}");
                        first = false;
                    }
                    out.print("]");
                }
            } else {
                String[] parts = pathInfo.split("/");
                if (parts.length == 2) {
                    int id = Integer.parseInt(parts[1]);
                    try (ResultSet rs = Dataset.getClusterInfo(connection, id)) {
                        if (rs == null || !rs.next()) {
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            out.print("{\"error\":\"Dataset not found\"}");
                            return;
                        }

                        int datasetId = rs.getInt("id");
                        int maxLevel = rs.getInt("MaxLevel");

                        out.print("{" +
                                "\"id\":" + datasetId + "," +
                                "\"MaxLevel\":" + maxLevel + "}");
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\":\"Invalid request: can't parse path\"}");
                }
            }
        } catch (SQLException | DataReadException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    /**
     * Gestisce la richiesta PUT per aggiornare un dataset esistente.
     * 
     * @param request la richiesta HTTP
     * @param response la risposta HTTP
     * @throws ServletException se un errore si verifica durante l'elaborazione della richiesta
     * @throws IOException se un errore si verifica nella lettura dei dati
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.split("/").length == 2) {
            try {
                int id = Integer.parseInt(pathInfo.split("/")[1]);
                String name = request.getParameter("name");
                String description = request.getParameter("description");
                String type = request.getParameter("type");
                String tags = request.getParameter("tags");
                byte[] data = request.getParameter("data").getBytes();
            
                Blob b = JsonSafeConverter.CreateBlobfromJson(connection, data, type);
                Dataset toupdate = new Dataset(name, description, type, b, tags);
                PreparedStatement stmt = toupdate.generateUpdateQuery(connection, id);
                //query di aggiornamento
                int rows = stmt.executeUpdate();
    
                // Verifica dell'esito dell'operazione
                if (rows > 0) {
                    response.getWriter().print("{\"message\":\"Dataset updated successfully\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().print("{\"error\":\"Dataset not found\"}");
                }
    
                // Chiusura delle risorse
                stmt.close();
            } catch (Exception e) {
                // Gestione degli errori
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().print("{\"error\":\"" + e.getMessage() + "\"}");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("{\"error\":\"Invalid request\"}");
        }
    }

    /**
     * Gestisce la richiesta DELETE per eliminare un dataset esistente.
     * 
     * @param request la richiesta HTTP
     * @param response la risposta HTTP
     * @throws ServletException se un errore si verifica durante l'elaborazione della richiesta
     * @throws IOException se un errore si verifica nella lettura dei dati
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.split("/").length == 2) {
            try {
                int id = Integer.parseInt(pathInfo.split("/")[1]);
                String query = "DELETE FROM Datasets WHERE id = ?";;
                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    stmt.setInt(1, id);
                    int rows = stmt.executeUpdate();
                    if (rows > 0) {
                        response.getWriter().print("{\"message\":\"Dataset deleted successfully\"}");
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        response.getWriter().print("{\"error\":\"Dataset not found\"}");
                    }
                }
            } catch (IOException | NumberFormatException | SQLException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().print("{\"error\":\"" + e.getMessage() + "\"}");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("{\"error\":\"Invalid request\"}");
        }
    }
}
