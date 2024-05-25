package m1miage.cybersparkle;

import m1miage.cybersparkle.enums.ProtocoleWorker;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Classe représentant un thread de résolution de tâche pour un worker. La tâche consiste à trouver un
 * nonce qui, lorsqu'il est concaténé avec les données et haché avec SHA-256, produit un hachage avec
 * un nombre de zéros donné en tête (à gauche).
 */
public class WorkerSolvingThread extends Thread {
    private final int difficulty;
    private final int initialNonce;
    private String currentNonce = "";
    private final String data;
    private final int increase;
    private final OutputStreamWriter outputStream;
    private boolean shouldStop = false;

    /**
     * Constructeur de la classe WorkerSolvingThread.
     *
     * @param data Données à utiliser pour résoudre la tâche
     * @param initialNonce Nonce de départ
     * @param difficulty Difficulté de la tâche
     * @param increase Incrément à appliquer au nonce à chaque itération de la boucle de résolution
     * @param outputStream Flux de sortie vers lequel envoyer les messages de statut et de résolution
     */
    public WorkerSolvingThread(String data, int initialNonce, int difficulty, int increase, OutputStreamWriter outputStream) {
        this.data = data;
        this.initialNonce = initialNonce;
        this.difficulty = difficulty;
        this.increase = increase;
        this.outputStream = outputStream;
    }

    /**
     * Renvoie la valeur courante du nonce utilisé pour la résolution de la tâche.
     *
     * @return Valeur du nonce
     */
    public String getCurrentNonce() {
        return currentNonce;
    }

    /**
     * Permet d'arrêter la résolution de la tâche.
     *
     * @param shouldStop "true" si la résolution doit s'arrêter, "false" sinon
     */
    public void setShouldStop(boolean shouldStop) {
        this.shouldStop = shouldStop;
    }

    /**
     * Démarre la résolution du problème et envoie un message de statut ou de solution au serveur.
     */
    public void run() {
        System.out.println("start solving");
        SolutionDto solutionDto = solve(data, difficulty, initialNonce, increase);
        if (solutionDto != null) {
            sendToServeur(ProtocoleWorker.FOUND, solutionDto.hash + " " + solutionDto.nonce);
        }
        sendToServeur(ProtocoleWorker.READY, "");
    }

    /**
     * Essaye de résoudre le problème en utilisant une boucle qui incrémente le nonce et vérifie si
     * le hachage obtenu correspond aux critères de difficulté.
     *
     * @param string Données à utiliser pour résoudre la tâche
     * @param difficulty Difficulté de la tâche
     * @param initialNonce Nonce de départ
     * @param jump Incrément à appliquer au nonce à chaque itération
     * @return Solution sous forme d'objet SolutionDTO si le problème est résolu, null sinon.
     */
    SolutionDto solve(String string, int difficulty, final int initialNonce, int jump) {
        long nonce = initialNonce;
        byte[] nonceBytes = BigInteger.valueOf(nonce).toByteArray();
        this.currentNonce = toHex(nonceBytes);
        String hash = hash(string, nonceBytes);
        while (!checkHash(hash, difficulty) && !shouldStop) {
            nonce += jump;
            nonceBytes = BigInteger.valueOf(nonce).toByteArray();
            this.currentNonce = toHex(nonceBytes);
            hash = hash(string, nonceBytes);
        }

        return shouldStop ? null : new SolutionDto(Long.toHexString(nonce), hash);
    }

    /**
     * Méthode qui calcule le hachage d'une chaîne de caractères et un nonce avec SHA-256.
     *
     * @param str Chaîne de caractères à utiliser pour le hachage
     * @param nonce Nonce à utiliser pour le hachage
     * @return Hachage de la chaîne et le nonce
     */
    public static String hash(String str, byte[] nonce) {

        final MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        final byte[] data = str.getBytes(StandardCharsets.UTF_8);
        final byte[] payload = new byte[data.length + nonce.length];
        System.arraycopy(data, 0, payload, 0, data.length);
        System.arraycopy(nonce, 0, payload, data.length, nonce.length);
        byte[] hash = messageDigest.digest(payload);

        return toHex(hash);
    }

    /**
     * Convertit un tableau de bytes en une chaîne de caractères sous format hexadécimal
     *
     * @param buffer Tableau de bytes à convertir
     * @return Chaîne de caractères en hexadécimal
     */
    public static String toHex(byte[] buffer) {
        return HexFormat.of().formatHex(buffer);
    }

    /**
     * Vérifie si un hachage correspond aux critères de difficulté.
     *
     * @param hashed Hachage à vérifier
     * @param difficulty Difficulté de la tâche
     * @return "true" si le hachage correspond aux critères de difficulté, "false" sinon.
     */
    private boolean checkHash(String hashed, int difficulty) {
        return hashed.startsWith("0".repeat(difficulty));
    }

    /**
     * Envoie un message au serveur en utilisant le protocole spécifié. S'il y a une erreur le thread
     * va s'arrêter et au prochain message le client sera supprimé par la méthode de nettoyage.
     *
     * @param protocole Protocole à utiliser pour envoyer le message
     * @param message Message à envoyer
     */
    private void sendToServeur(ProtocoleWorker protocole, String message) {
        try {
            BufferedWriter output = new BufferedWriter(outputStream);
            Utils.sendMessage(output, protocole.instruction + " " + message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
