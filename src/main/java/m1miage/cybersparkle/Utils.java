package m1miage.cybersparkle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Classe utilitaire contenant les méthodes statiques pour la communication réseau.
 */
public class Utils {
    /**
     * Envoie un message sur le flux de sortie spécifié.
     *
     * @param output Flux de sortie sur lequel envoyer le message
     * @param message Message à envoyer
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de l'envoi du message
     */
    protected static void sendMessage(BufferedWriter output, String message) throws IOException {
        System.out.println("send :" + message);
        output.write(message);
        output.newLine();
        output.flush();
    }

    /**
     * Lit le prochain message sur le flux d'entrée spécifié.
     *
     * @param bufferedReader Flux d'entrée sur lequel lire le message
     * @return Prochain message lu sur le flux d'entrée
     * @throws IOException Si une erreur d'entrée/sortie se produit
     */
    protected static String readNextMessage(BufferedReader bufferedReader) throws IOException {
        String reader = bufferedReader.readLine();
        while (reader == null) {
            reader = bufferedReader.readLine();
        }
        System.out.println("Received: " + reader);
        return reader;
    }
}
