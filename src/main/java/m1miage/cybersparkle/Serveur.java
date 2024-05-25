package m1miage.cybersparkle;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Classe représentant un serveur de minage qui utilise un patron Singleton (une seule instance de serveur).
 */
public class Serveur {
    private static Serveur instance;

    String actualDifficulty = null;

    private ServerSocket serverSocket;
    /**
     * Liste des threads de connexion au serveur.
     */
    public List<ServerConnectionThread> clients;

    /**
     * Constructeur de la classe Serveur qui initialise la liste des clients et le socket du serveur.
     */
    private Serveur() {
        this.clients = new ArrayList<>();

        try {
            ServerSocket serverSocket = new ServerSocket(1337);
            new Thread(() -> {
                while (true) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        ServerConnectionThread serverConnectionThread = new ServerConnectionThread(clientSocket,
                                "12345");
                        clients.add(serverConnectionThread);
                        serverConnectionThread.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Méthode qui permet de récupérer l'instance unique de la classe Serveur. Si elle n'existe pas, elle
     * est créée.
     *
     * @return Instance unique de la classe Serveur
     */
    public static Serveur getInstance() {
        return instance != null ? instance : (instance = new Serveur());
    }

    /**
     * Méthode qui permet de lancer une nouvelle tâche de minage avec une difficulté donnée. Elle nettoie
     * la liste des clients et répartit la tâche entre les clients actifs.
     *
     * @param difficulte Difficulté de la tâche à résoudre
     */
    public void solve(String difficulte) {
        cleanClientPool();
        String data = ServerConnected.generate_work(difficulte);
        this.actualDifficulty = difficulte;
        int nonceJump = clients.size();
        IntStream.range(0, clients.size()).forEach(i -> clients.get(i).solve(difficulte, data, nonceJump, i));
    }

    /**
     * Valide une solution proposée par un client. Si elle est correcte la tâche est considérée comme résolue.
     *
     * @param solution Solution trouvée pour la tâche
     */
    public void validateWork(SolutionDto solution) {
        boolean correctSolution = ServerConnected.validate_work(this.actualDifficulty, solution);
        if (correctSolution) {
            taskSolved();
        }
    }

    /**
     * Affiche l'état actuel des clients connectés au serveur.
     *
     * @return Statut des clients
     */
    public String showStatus() {
        StringBuilder statusList = new StringBuilder();
        cleanClientPool();
        if (!clients.isEmpty()) {
            clients.forEach(client -> statusList.append(" * ").append(client.toString()).append(" ").append(client.getStatus()).append("\n"));
        } else {
            statusList.append("No clients connected.");
        }
        return statusList.toString();
    }

    /**
     * Arrête proprement le serveur en annulant les tâches en cours et informant les clients de se fermer.
     */
    public void shutDown() {
        // Arrêter les tâches des clients
        cancelTask();
        // Informer les clients de se fermer
        clients.forEach(ServerConnectionThread::shutdown);
    }

    /**
     * Annule la tâche en cours.
     */
    public void cancelTask() {
        cleanClientPool();
        clients.forEach(ServerConnectionThread::cancelTask);

    }

    /**
     * Informe les clients que la tâche a été résolue.
     */
    public void taskSolved() {
        cleanClientPool();
        clients.forEach(ServerConnectionThread::taskSolved);
    }

    /**
     * Méthode qui nettoie la liste des clients en supprimant ceux qui ne sont plus actifs.
     */
    private void cleanClientPool() {
        clients.removeIf(client -> !client.isAlive());
    }

}
