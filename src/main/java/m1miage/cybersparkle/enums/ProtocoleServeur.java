package m1miage.cybersparkle.enums;

/**
 * Énumération regroupant les instructions que le serveur peut envoyer au worker pour éviter les fautes de frappe.
 */
public enum ProtocoleServeur {
    /**
     * Instruction demandant au worker de s'identifier.
     */
    WHO_ARE_YOU("WHO_ARE_YOU_?"),

    /**
     * Instruction demandant au worker de fournir son mot de passe.
     */
    GIMME_PASSWORD("GIMME_PASSWORD"),

    /**
     * Instruction indiquant que la demande précédente a été acceptée.
     */
    OK("OK"),

    /**
     * Instruction indiquant que le serveur a bien reçu l'identification du worker.
     */
    HELLO_YOU("HELLO_YOU"),

    /**
     * Instruction indiquant que le mot de passe fourni par le worker est incorrect.
     */
    YOU_DONT_FOOL_ME("YOU_DONT_FOOL_ME"),

    /**
     * Instruction indiquant que la tâche a déjà été résolue par un autre worker.
     */
    SOLVED("SOLVED"),

    /**
     * Instruction demandant au worker de fournir son avancement sur la tâche.
     */
    PROGRESS("PROGRESS"),

    /**
     * Instruction indiquant que la tâche a été annulée par le serveur.
     */
    CANCELLED("CANCELLED"),

    /**
     * Instruction demandant au worker de se fermer proprement.
     */
    SHUTDOWN("SHUTDOWN"),

    /**
     * Instruction fournissant au worker le nonce à utiliser pour la tâche.
     */
    NONCE("NONCE"),

    /**
     * Instruction fournissant au worker les données à utiliser pour la tâche.
     */
    PAYLOAD("PAYLOAD"),

    /**
     * Instruction fournissant au worker la difficulté à atteindre pour la tâche.
     */
    SOLVE("SOLVE");

    /**
     * La chaîne de caractères représentant l'instruction.
     */
    public final String instruction;

    /**
     * Constructeur de l'énumération, qui associe une chaîne de caractères à une instruction.
     *
     * @param instruction la chaîne de caractères représentant l'instruction
     */
    ProtocoleServeur(String instruction) {
        this.instruction = instruction;
    }

}
