package m1miage.cybersparkle.enums;

/**
 * Énumération regroupant les instructions que le worker peut envoyer au serveur pour éviter les fautes de frappe.
 */
public enum ProtocoleWorker {
    /**
     * Instruction indiquant que le worker s'est bien identifié.
     */
    ITS_ME("ITS_ME"),

    /**
     * Instruction fournissant le mot de passe du worker au serveur.
     */
    PASSWD("PASSWD"),

    /**
     * Instruction indiquant que le worker est prêt à recevoir une tâche.
     */
    READY("READY"),

    /**
     * Instruction indiquant que le worker a trouvé une solution à la tâche.
     */
    FOUND("FOUND"),

    /**
     * Instruction indiquant que le worker est en train de tester une solution potentielle à la tâche.
     */
    TESTING("TESTING"),

    /**
     * Instruction indiquant que le worker est en train de se fermer proprement.
     */
    SHUTTING_DOWN("SHUTTING_DOWN"),

    /**
     * Instruction indiquant que le worker n'a pas trouvé de solution à la tâche.
     */
    NOPE("NOPE");

    /**
     * La chaîne de caractères représentant l'instruction.
     */
    public final String instruction;

    /**
     * Constructeur de l'énumération, qui associe une chaîne de caractères à une instruction.
     *
     * @param instruction la chaîne de caractères représentant l'instruction
     */
    ProtocoleWorker(String instruction) {
        this.instruction = instruction;
    }
}
