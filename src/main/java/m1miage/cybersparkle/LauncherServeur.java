package m1miage.cybersparkle;

import java.io.Console;

/**
 * Classe de lancement du serveur, elle écoute les commandes de l'utilisateur et les traite.
 */
public class LauncherServeur {

    private Serveur serveur;

    private boolean keepGoing = true;

    private final Console console = System.console();

    /**
     * Lance le programme, elle écoute les commandes de l'utilisateur et les traite. Elle s'arrête avec
     * l'instruction "quit"
     *
     * @throws Exception S'il y a une erreur lors du traitement de la commande
     */
    public void run() throws Exception {
        // écoute les commandes
        this.serveur = Serveur.getInstance();
        // écoute si le serveur s'arrête et lance la procédure d'arrêt des clients
        Runtime.getRuntime().addShutdownHook(new Thread(() -> serveur.shutDown()));

        while (keepGoing) {
            String commande = console != null ? console.readLine("$ ") : null;

            if (console == null || commande == null) {
                if (console == null) {
                    System.out.println("La console n'est pas disponible. Veuillez utiliser une autre méthode pour lire l'entrée utilisateur.");
                }
                break;
            }

            keepGoing = processCommand(commande.trim());
        }
        System.exit(0);
    }

    /**
     * Traite une commande de l'utilisateur
     *
     * @param cmd Commande à traiter
     * @return "true" si le programme doit continue à s'exécuter, "false" sinon
     * @throws Exception S'il y a une erreur lors du traitement de la commande
     */
    private boolean processCommand(String cmd) throws Exception {
        switch (cmd.trim().toLowerCase()) {
            case "quit" -> {
                return false;
            }
            case "cancel" -> serveur.cancelTask();
            case "status" -> System.out.println(serveur.showStatus());
            case "help" -> {
                System.out.println(" • status - display informations about connected workers");
                System.out.println(" • solve <d> - try to mine with given difficulty");
                System.out.println(" • cancel - cancel a task");
                System.out.println(" • help - describe available commands");
                System.out.println(" • quit - terminate pending work and quit");
            }
            default -> {
                if (cmd.startsWith("solve ")) {
                    String[] input = cmd.split(" ");
                    serveur.solve(input[1]);
                }
                else {
                    System.out.println();
                }
            }
        }
        return true;
    }

    /**
     * Lance le programme
     *
     * @param args Arguments de la ligne de commande de main
     * @throws Exception S'il y a une erreur lors du lancement du programme
     */
    public static void main(String[] args) throws Exception {
        new LauncherServeur().run();
    }

}
