package Map.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//TO INCLUDE FOR WAR
import Map.servlet.DatasetServlet;

public class indexservlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Imposta il tipo di contenuto della risposta come HTML
        response.setContentType("text/html");

        // Recupera il percorso del file HTML che desideri inviare
        String htmlFilePath = getServletContext().getRealPath("/index.html");

        // Crea un InputStream per leggere il file HTML
        try (InputStream in = new FileInputStream(new File(htmlFilePath));
             OutputStream out = response.getOutputStream()) {

            // Crea un buffer per leggere e scrivere i dati
            byte[] buffer = new byte[1024];
            int bytesRead;

            // Leggi dal file e scrivi nella risposta
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            // In caso di errore, imposta il codice di stato HTTP 500
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error loading HTML file: " + e.getMessage());
        }
    }
}