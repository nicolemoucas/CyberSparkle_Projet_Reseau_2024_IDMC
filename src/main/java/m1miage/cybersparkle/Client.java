package m1miage.cybersparkle;

import m1miage.cybersparkle.enums.ProtocoleServeur;
import m1miage.cybersparkle.enums.ProtocoleWorker;
import m1miage.cybersparkle.exception.InstructionUnknown;

import java.io.*;
import java.net.Socket;

/**
 * Classe représentant un client qui se connecte à un serveur et résout les tâches envoyées par celui-ci.
 */
public class Client {
    private String ip;
    private String password;
    private Socket socket;

    /**
     * Constructeur de la classe Client.
     *
     * @param ip Adresse IP du serveur
     * @param password Mot de passe pour de connecter au serveur
     */
    public Client(String ip, String password) {
        this.ip = ip;
        this.password = password;
    }

    /**
     * Crée une instance de la classe Client avec l'adresse fournie en ligne de commande puis démarre l'exécution.
     *
     * @param args Arguments de ligne de commande pour la fonction main
     * @throws InstructionUnknown Si une instruction inconnue est reçue du serveur
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la connexion
     */
    public static void main(String[] args) throws InstructionUnknown, IOException {
        new Client(args.length == 0 ? "localhost" : args[0], "12345").run();
    }

    /**
     * Démarre l'exécution du client en se connectant au serveur et en écoutant les instructions envoyées
     * par celui-ci.
     *
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la connexion
     * @throws InstructionUnknown Si une instruction inconnue est reçue du serveur
     */
    private void run() throws IOException, InstructionUnknown {
        socket = connectToServer();
        System.out.println("Connected to server.");
        System.out.println(socket);
        InputStreamReader inputStream = new InputStreamReader(socket.getInputStream());

        listenToServer(inputStream);
    }

    /**
     * Méthode utilisée pour écouter le serveur et agir en fonction de ce qu'il dit.
     *
     * @param inputStream inputStream utilisé pour récupérer les instructions du serveur.
     * @throws IOException Si une erreur se produit lors de la lecture de l'inputStream
     * @throws InstructionUnknown Si une instruction inconnue est reçue du serveur
     */
    private void listenToServer(InputStreamReader inputStream) throws IOException, InstructionUnknown {
        WorkerSolvingThread workerSolvingThread = null;
        while (true) {
            System.out.println("Listening to server...");
            BufferedReader bufferedReader = new BufferedReader(inputStream);
            System.out.println("Waiting for message...");
            String reader = Utils.readNextMessage(bufferedReader);
            System.out.println(reader);
            String choice = reader.split(" ")[0];
            switch (getEnumValue(choice)) {
                case WHO_ARE_YOU -> sendToServeur(ProtocoleWorker.ITS_ME);
                case GIMME_PASSWORD -> sendToServeur(ProtocoleWorker.PASSWD, this.password);
                case HELLO_YOU -> sendToServeur(ProtocoleWorker.READY);
                case OK, YOU_DONT_FOOL_ME -> {}
                case SHUTDOWN -> shutdown(workerSolvingThread);
                case NONCE, PAYLOAD, SOLVE -> startSolving(reader, bufferedReader, workerSolvingThread);
                case SOLVED, CANCELLED -> stopWork(workerSolvingThread);
                case PROGRESS -> progress(workerSolvingThread);
                case null, default -> throw new InstructionUnknown(reader);
            }
        }
    }

    /**
     * Créé un nouveau thread pour résoudre la tâche avec les paramètres donnés.
     *
     * @param reader Message avec les paramètres de la tâche
     * @param bufferedReader BufferedReader pour lire les données supplémentaires envoyées par le serveur
     * @param workerSolvingThread le thread à démarrer pour résoudre la tâche
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la lecture des informations
     */
    private void startSolving(String reader, BufferedReader bufferedReader, WorkerSolvingThread workerSolvingThread) throws IOException {
        String start = null;
        String inc = null;
        String data = null;
        String difficulty = null;
        do {
            if (reader.split(" ")[0].equals(ProtocoleServeur.PAYLOAD.instruction)) {
                data = reader.split(" ")[1];
                System.out.println("data=" + data);
            }
            if (reader.split(" ")[0].equals(ProtocoleServeur.SOLVE.instruction)) {
                difficulty = reader.split(" ")[1];
                System.out.println("difficulty=" + difficulty);
            }
            if (reader.split(" ")[0].equals(ProtocoleServeur.NONCE.instruction)) {
                start = reader.split(" ")[1];
                System.out.println("start=" + start);
                inc = reader.split(" ")[2];
                System.out.println("inc=" + inc);
            }
            if (data == null || difficulty == null || start == null || inc == null) {
                reader = Utils.readNextMessage(bufferedReader);
            }
        }
        while (data == null || difficulty == null || start == null || inc == null);

        System.out.println("GO");
        workerSolvingThread = new WorkerSolvingThread(data, Integer.parseInt(start),
                Integer.parseInt(difficulty), Integer.parseInt(inc),
                new OutputStreamWriter(socket.getOutputStream()));
        workerSolvingThread.start();
    }

    /**
     * Envoie l'état d'avancement du thread au serveur
     *
     * @param workerSolvingThread Le thread de résolution de tâche
     */
    private void progress(WorkerSolvingThread workerSolvingThread) {
        if (workerSolvingThread != null && workerSolvingThread.isAlive()) {
            sendToServeur(ProtocoleWorker.TESTING, workerSolvingThread.getCurrentNonce());
        } else {
            sendToServeur(ProtocoleWorker.NOPE);
        }
    }

    /**
     * Arrête le thread de résolution de tâche s'il est en cours d'exécution
     *
     * @param workerSolvingThread Le thread à arrêter
     */
    private static void stopWork(WorkerSolvingThread workerSolvingThread) {
        if (workerSolvingThread != null && workerSolvingThread.isAlive()) {
            workerSolvingThread.setShouldStop(true);
        }
    }

    /**
     * Arrête le client et libère les ressources
     *
     * @param workerSolvingThread Le thread à arrêter
     */
    private void shutdown(WorkerSolvingThread workerSolvingThread) {
        // Envoyer une réponse de confirmation de fermeture au serveur
        sendToServeur(ProtocoleWorker.SHUTTING_DOWN);
        // Arrêter le thread de résolution des tâches s'il est en cours
        stopWork(workerSolvingThread);
        // Fermer les flux de communication et le socket
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Terminer le programme
        System.exit(0);
    }

    /**
     * Méthode utilisée pour récupérer la valeur d'énumération choix à partir de l'inputStream de listenToServer.
     *
     * @param value la valeur envoyée par le serveur
     * @return {ProtocoleServeur} la valeur de l'énumération ProtocoleServeur correspondante.
     */
    private ProtocoleServeur getEnumValue(String value) {
        for (ProtocoleServeur protocoleServeur : ProtocoleServeur.values()) {
            if (protocoleServeur.instruction.equals(value)) {
                return protocoleServeur;
            }
        }
        return null;
    }

    /**
     * Méthode utilisée pour se connecter au serveur.
     *
     * @return {Socket} un nouveau socket connecté au serveur.
     */
    private Socket connectToServer() {
        try {
            return new Socket(ip, 1337);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Envoie un message au serveur selon le protocole donné.
     *
     * @param protocole Protocole à utiliser pour envoyer le message
     */
    private void sendToServeur(ProtocoleWorker protocole) {
        sendToServeur(protocole, "");
    }

    /**
     * Envoie un message au serveur avec un protocole et un contenu donnés.
     *
     * @param protocole Protocole à utiliser pour envoyer le message
     * @param message Message à envoyer au serveur
     */
    private void sendToServeur(ProtocoleWorker protocole, String message) {
        try {
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            sendMessage(output, message.isEmpty() ? protocole.instruction : protocole.instruction + " " + message);
        } catch (IOException e) {
            //TODO si erreur, supprimer le socket de la liste des clients
            throw new RuntimeException(e);
        }
    }

    /**
     * Envoie un message au serveur avec un flux de sortie donné.
     * @param output Flux de sortir à utiliser pour envoyer le message
     * @param message Message à envoyer au serveur
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de l'envoi du message
     */
    private void sendMessage(BufferedWriter output, String message) throws IOException {
        System.out.println("send :" + message);
        output.write(message);
        output.newLine();
        output.flush();
    }
}
