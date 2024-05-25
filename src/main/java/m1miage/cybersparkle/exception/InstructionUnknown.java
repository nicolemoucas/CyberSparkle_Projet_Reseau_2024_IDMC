package m1miage.cybersparkle.exception;

/**
 * Exception personnalisée levée lorsqu'une instruction envoyée par le serveur ou le worker n'est
 * pas reconnue par le protocole.
 */
public class InstructionUnknown extends Exception {
    /**
     * Constructeur de l'exception InstructionUnknown qui prend en paramètre un message d'erreur à afficher.
     *
     * @param message le message d'erreur à afficher
     */
    public InstructionUnknown(String message) {
        super("Erreur : cette instruction est inconnue du protocole." + message);
    }
}
