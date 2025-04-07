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
 * Servlet per la gestione dei file JavaScript richiesti dai client.
 * La servlet recupera i file JavaScript dalla directory /js e li restituisce come risposta.
 * 
 * @author Alex Longo
 */
public class JsGetter extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /** La directory base per i file JavaScript */
    private String basePath;

    /**
     * Inizializza la servlet e imposta il percorso della directory contenente i file JavaScript.
     * 
     * @throws ServletException se non è possibile determinare il percorso base
     */
    @Override
    public void init() throws ServletException {
        // Inizializza il BASE_PATH relativo alla directory /scripts nella root del contesto
        basePath = getServletContext().getRealPath("/js");
        if (basePath == null) {
            throw new ServletException("Base path for JavaScript files could not be determined.");
        }
    }

    /**
     * Gestisce la richiesta GET per recuperare un file JavaScript dalla directory /js.
     * Il nome del file viene estratto dal percorso della richiesta e il file viene letto e restituito come risposta.
     * 
     * @param req la richiesta HTTP
     * @param resp la risposta HTTP
     * @throws ServletException se si verifica un errore nel gestire la richiesta
     * @throws IOException se si verifica un errore nella lettura del file
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
        if (!fileName.endsWith(".js")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Only .js files are allowed.");
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
            resp.setContentType("application/javascript");

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
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "JavaScript file not found: " + fileName);
        }
    }
}
