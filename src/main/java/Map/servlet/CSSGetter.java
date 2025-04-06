package Map.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * La classe CSSGetter gestisce le richieste GET per recuperare i file CSS dal server.
 * Essa legge il file CSS richiesto e lo invia come risposta HTTP. La classe verifica
 * anche la validità del nome del file e la sua esistenza prima di inviarlo.
 * 
 * @author Alex Longo
 */
public class CSSGetter extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /** Il percorso base per i file CSS nel contesto del servlet */
    private String basePath;

    /**
     * Inizializza il percorso base per i file CSS. Questo percorso è relativo alla
     * directory /css nella root del contesto del server.
     * 
     * @throws ServletException se il percorso base non può essere determinato
     */
    @Override
    public void init() throws ServletException {
        // Inizializza il BASE_PATH relativo alla directory /css nella root del contesto
        basePath = getServletContext().getRealPath("/css");
        if (basePath == null) {
            throw new ServletException("Base path for CSS files could not be determined.");
        }
    }

    /**
     * Gestisce le richieste HTTP GET per recuperare i file CSS. Il nome del file CSS
     * viene ottenuto dal path della richiesta. Il file viene letto e inviato come
     * risposta con il tipo di contenuto appropriato (text/css).
     * 
     * @param req la richiesta HTTP
     * @param resp la risposta HTTP
     * @throws ServletException se un errore si verifica durante l'elaborazione della richiesta
     * @throws IOException se un errore si verifica nella lettura del file
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Recupera il nome del file dal path
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // Se non è specificato un file, restituisci un errore 400 (Bad Request)
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "File name is required in the path.");
            return;
        }

        // Rimuove eventuali caratteri di "/" iniziali o finali
        String fileName = pathInfo.replaceFirst("^/+", "").replaceAll("/+$", "");

        // Verifica che il nome del file abbia un'estensione valida
        if (!fileName.endsWith(".css")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Only .css files are allowed.");
            return;
        }

        // Normalizza il percorso per evitare attacchi di directory traversal
        Path normalizedPath = Paths.get(basePath).resolve(fileName).normalize();

        // Verifica che il file sia all'interno della directory basePath
        if (!normalizedPath.startsWith(basePath)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied.");
            return;
        }

        // Verifica che il file esista e sia leggibile
        if (Files.exists(normalizedPath) && Files.isReadable(normalizedPath)) {
            // Imposta il tipo di contenuto corretto
            resp.setContentType("text/css");

            // Legge il contenuto del file e lo invia come risposta
            try (InputStream inputStream = Files.newInputStream(normalizedPath)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    resp.getOutputStream().write(buffer, 0, bytesRead);
                }
            }
        } else {
            // Se il file non esiste o non è leggibile, restituisci un errore 404 (Not Found)
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "CSS file not found: " + fileName);
        }
    }
}
