package m1miage.cybersparkle;

import m1miage.cybersparkle.enums.ProtocoleHTTP;
import m1miage.cybersparkle.exception.NoAccessWebServiceException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Classe permettant de se connecter au service web et faire des requêtes HTTP.
 */
public class ServerConnected {
    private static final String host = "projet-raizo-idmc.netlify.app";
    private static final int port = 443;
    private static final String authToken = "rec0R8W3Q6lj3Ed3G";

    // Oblige à appeler les méthodes en static
    private ServerConnected() {
    }

    /**
     * Génère la tâche à résoudre à partir d'une difficulté donnée.
     *
     * @param difficulte Difficultée de la tâche
     * @return Tâche à résoudre
     */
    public static String generate_work(String difficulte) {
        String response = null;
        try {
            response = request(ProtocoleHTTP.GET, "/.netlify/functions/generate_work?d=" + difficulte);
        } catch (NoAccessWebServiceException e) {
            throw new RuntimeException(e);
        }
        String[] strings = response.split("\"");
        return (strings.length >= 4) ? strings[3] : null;
    }

    /**
     * Valide une tâche avec une difficulté donnée et la solution trouvée.
     * @param difficulte Difficulté de la tâche
     * @param solution Solution trouvée pour la tâche
     * @return "true" si la solution est correcte, "false" sinon
     */
    public static boolean validate_work(String difficulte, SolutionDto solution) {
        String body = formatBody(difficulte, solution);
        System.out.println(body);
        HttpRequest request = buildValidateWorkRequest(body);

        return extractValidateWorkAnswer(request);
    }

    /**
     * Extrait la réponse de validation de la tâche à partir d'une requête HTTP donnée.
     *
     * @param request Requête HTTP à traiter
     * @return "vrai" si la solution est valide, "faux" sinon
     */
    private static boolean extractValidateWorkAnswer(HttpRequest request) {
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response);
            System.out.println(response.body());
            return response.statusCode() == 200 || response.statusCode() == 409;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Formate le corps de la requête HTTP pour valider une tâche
     *
     * @param difficulte Difficulté de la tâche
     * @param solution Solution trouvée pour la tâche
     * @return Corps de la requête HTTP en format JSON (String)
     */
    private static String formatBody(String difficulte, SolutionDto solution) {
        return "{\"d\": " + difficulte + ", \"n\": \"" + solution.getNonce() + "\", \"h\": \"" + solution.getHash() + "\"}";
    }

    /**
     * Construit une requête HTTP POST pour valider une tâche
     *
     * @param body corps de la requête HTTP en format JSON (String)
     * @return requête HTTP construite
     */
    private static HttpRequest buildValidateWorkRequest(String body) {
        return HttpRequest.newBuilder()
                .uri(URI.create("https://" + host + "/.netlify/functions/validate_work"))
                .header("Authorization", "Bearer " + authToken)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }

    /**
     * Fait une requête HTTP à partir de la méthode et l'endpoint donnés.
     * @param method Méthode HTTP à utiliser (GET, POST)
     * @param endpoint Endpoint de la requête HTTP
     * @return Réponse du serveur ou null si la requête échoue
     * @throws NoAccessWebServiceException Si l'accès au webservice est refusé
     */
    private static String request (
            ProtocoleHTTP method,
            String endpoint
            ) throws NoAccessWebServiceException {

        String res = null;

        try {

            // Connection au serveur
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket socket = (SSLSocket) factory.createSocket(host, port);

            // Requête HTTP
            // On doit laisser une ligne vide à la fin de la requête
            String request = method + " " + endpoint + " HTTP/1.1\r\n" +
                    "Host: " + host + "\r\n" +
                    "Authorization: Bearer " + authToken + "\r\n" +
                    "Connection: close\r\n\r\n";

            OutputStream os = socket.getOutputStream();
            os.write(request.getBytes());
            os.flush();

            // Lecture de la réponse du serveur
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
                if(line.startsWith("{")) {
                    res = line;
                }
            }

            // Fermeture de la connexion
            in.close();
            os.close();
            socket.close();

        } catch (IOException e) {
            throw new NoAccessWebServiceException(e.getMessage());
        }

        return res;
    }

}