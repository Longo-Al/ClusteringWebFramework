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

public class DatasetServlet extends HttpServlet {
    private DbAccess dbAccess;
    @Override
    public void init() throws ServletException {
        try {
            dbAccess = new DbAccess();
        } catch (DatabaseConnectionException e) {
            throw new ServletException("Failed to initialize database connection", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            Connection connection = dbAccess.getConnection();
            if (pathInfo == null || pathInfo.equals("/")) {
                // Get all datasets (without data field)
                try (ResultSet rs = Dataset.getInfosFromDb(connection)) {
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
                // Get dataset by ID (with data field)
                String[] parts = pathInfo.split("/");
                if (parts.length == 2) {
                    int id = Integer.parseInt(parts[1]);
                    try (ResultSet rs = Dataset.getbyId(connection, id)) {
                        if (rs.next()) {
                            out.print("{" +
                                    "\"id\":" + rs.getInt("id") + "," +
                                    "\"name\":\"" + rs.getString("name") + "\"," +
                                    "\"description\":\"" + rs.getString("description") + "\"," +
                                    "\"size\":" + rs.getLong("size") + "," +
                                    "\"created_at\":\"" + rs.getTimestamp("created_at") + "\"," +
                                    "\"updated_at\":\"" + rs.getTimestamp("updated_at") + "\"," +
                                    "\"type\":\"" + rs.getString("type") + "\"," +
                                    "\"data\":\"" + new String(rs.getBytes("data")) + "\"," +
                                    "\"tags\":\"" + rs.getString("tags") + "\"}");
                        } else {
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            out.print("{\"error\":\"Dataset not found \"}");
                        }
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\":\"Invalid request: can't parse path \"}");
                }
                out.close();
            }
        } catch (SQLException | DatabaseConnectionException | DataReadException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Recupero dei parametri dalla request
            String name = request.getParameter("name");
            String description = request.getParameter("description");
            String type = request.getParameter("type");
            String tags = request.getParameter("tags");
            byte[] data = request.getParameter("data").getBytes();

            // Creazione della connessione al database
            Connection connection = dbAccess.getConnection();
            Blob b = JsonSafeConverter.CreateBlobfromJson(connection, data, type);
            Dataset toupdate = new Dataset(name, description, type, b, tags);
            PreparedStatement stmt = toupdate.generateInsertQuery(connection);
            // Esecuzione della query
            int rowsAffected = stmt.executeUpdate();

            // Controllo se l'inserimento è riuscito
            if (rowsAffected > 0) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().print("{\"message\":\"Dataset created successfully\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().print("{\"error\":\"Failed to create dataset\"}");
            }

            // Chiusura della connessione
            stmt.close();
            connection.close();
        } catch (Exception e) {
            // Gestione degli errori
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }


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
                
                // Creazione della connessione al database
                Connection connection = dbAccess.getConnection();
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
                connection.close();
    
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
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.split("/").length == 2) {
            try {
                int id = Integer.parseInt(pathInfo.split("/")[1]);
                String query = "DELETE FROM Datasets WHERE id = ?";
                Connection connection = dbAccess.getConnection();
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
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().print("{\"error\":\"" + e.getMessage() + "\"}");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("{\"error\":\"Invalid request\"}");
        }
    }

    @Override
    public void destroy() {
        try {
            dbAccess.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

