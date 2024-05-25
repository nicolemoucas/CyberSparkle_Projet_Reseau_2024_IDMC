package m1miage.cybersparkle;

import m1miage.cybersparkle.enums.ProtocoleServeur;
import m1miage.cybersparkle.enums.ProtocoleWorker;

import java.io.*;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Stream;

import static m1miage.cybersparkle.enums.ProtocoleServeur.*;
import static m1miage.cybersparkle.enums.ProtocoleWorker.*;

/**
 * Classe qui représente un thread de connexion au serveur.
 */
public class ServerConnectionThread extends Thread {
    private final Socket clientSocket;

    private final String password;

    private boolean shouldContinue = true;

    private volatile String status = null;


    /**
     * Constructeur de la classe ServerConnectionThread qui créé un nouveau thread de connexion au serveur
     * pour le client donné.
     *
     * @param clientSocket Socket du client associé au thread
     * @param password Mot de passe pour authentifier le client
     */
    public ServerConnectionThread(Socket clientSocket, String password) {
        this.clientSocket = clientSocket;
        this.password = password;
    }


    /**
     * Renvoie une chaîne de caractères pour cet objet
     * @return Chaîne de caractères de l'objet
     */
    @Override
    public String toString() {
        return "ServerConnectionThread{" +
                "clientSocket=" + clientSocket +
                '}';
    }

    /**
     * Méthode exécutée par le thread de connexion au serveur qui gère la communication avec le client via
     * un socket.
     */
    public void run() {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            sendToClient(WHO_ARE_YOU);
            String reader = Utils.readNextMessage(bufferedReader);
            System.out.println("is ITS_ME = " + ITS_ME.instruction.equals(reader));
            if (!ITS_ME.instruction.equals(reader)) {
                clientSocket.close();
                return;
            }

            sendToClient(GIMME_PASSWORD);
            reader = Utils.readNextMessage(bufferedReader);
            String[] strings = reader.replace("\n", "").split(" ");
            if (!strings[1].equals(password)) {
                sendToClient(YOU_DONT_FOOL_ME);
                clientSocket.close();
                return;
            }
            sendToClient(HELLO_YOU);

            while (shouldContinue) {
                reader = Utils.readNextMessage(bufferedReader);
                String choice = reader.split(" ")[0];
                switch (getEnumValue(choice)) {
                    case READY -> sendToClient(OK);
                    case FOUND -> {
                        String nonce = reader.split(" ")[2];
                        String hash = reader.split(" ")[1];
                        Serveur.getInstance().validateWork(new SolutionDto(nonce, hash));
                    }
                    case TESTING, NOPE -> status = reader;
                    case null, default -> { /* ignore */ }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Renvoie le statut actuel du client. S'il ne répond pas pendant un délai donné une chaîne d'erreur
     * est renvoyée.
     *
     * @return Statut actuel du client ou chaîne d'erreur s'il ne répond pas
     */
    public String getStatus() {
        status = null;
        sendToClient(PROGRESS);
        Instant messageTimestamp = Instant.now();
        while (status == null) {
            if (!waitForStatus(messageTimestamp)) {
                return "Erreur : Impossible de communiquer avec le client :(";
            }
        }
        return status;

    }

    /**
     * Méthode qui permet de ne pas rester bloqués quand on demande le statut à un worker qui doit répondre
     * en 5 secondes.
     * @param messageTimestamp Instant du dernier message envoyé au client
     * @return "vrai" si le statut a été mis à jour à temps, "faux" sinon
     */
    private boolean waitForStatus(Instant messageTimestamp) {
        Thread.onSpinWait();
        return Duration.between(messageTimestamp, Instant.now()).toSeconds() <= 5;
    }

    /**
     * Ferme la connexion au client et arrête le thread de connexion au serveur.
     */
    public void shutdown() {
        sendToClient(SHUTDOWN);
        shouldContinue = false;
    }

    /**
     * Annule la tâche actuelle du client.
     */
    public void cancelTask() {
        sendToClient(CANCELLED);
    }

    /**
     * Indique au client que la tâche a été résolue.
     */
    public void taskSolved() {
        sendToClient(SOLVED);
    }

    /**
     * Envoie un message au client via le socket.
     *
     * @param protocole Protocole à utiliser pour envoyer le message
     */
    private void sendToClient(ProtocoleServeur protocole) {
        sendToClient(protocole, "");
    }

    /**
     * Envoie un message au client via le socket.
     *
     * @param protocole Protocole à utiliser pour envoyer le message
     * @param message Contenu du message
     */
    private void sendToClient(ProtocoleServeur protocole, String message) {
        try {
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            Utils.sendMessage(output, protocole.instruction + " " + message);
        } catch (IOException e) {
            //Le thread casse et au prochain message, la methode qui nettoie la liste des clients va le supprimer.
            throw new RuntimeException(e);
        }
    }

    /**
     * Demande au client de commencer à résoudre une nouvelle tâche.
     *
     * @param difficulte Difficulté de la tâche
     * @param data Données de la tâche
     * @param nonceJump Nombre de nonces à sauter à chaque itération
     * @param start Nonce de départ pour la tâche
     */
    public void solve(String difficulte, String data, int nonceJump, int start) {
        sendToClient(SOLVE, difficulte);
        sendToClient(PAYLOAD, data);
        sendToClient(NONCE, start + " " + nonceJump);
    }

    /**
     * Méthode utilisée pour récupérer la valeur d'énumération choix à partir de l'inputStream de listenToServer.
     *
     * @param value la valeur envoyée par le serveur
     * @return  la valeur de l'énumération ProtocoleServeur correspondante.
     */
    private static ProtocoleWorker getEnumValue(String value) {
        return Stream.of(ProtocoleWorker.values())
                .filter(pw -> pw.instruction.equals(value))
                .findFirst()
                .orElse(null);
    }
}
