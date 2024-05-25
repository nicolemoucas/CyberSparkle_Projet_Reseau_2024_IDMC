package m1miage.cybersparkle.exception;

/**
 * Exception personnalisée levée lorsqu'il est impossible d'accéder au web service.
 */
public class NoAccessWebServiceException extends Exception {

    /**
     * Constructeur de l'exception NoAccessWebServiceException qui prend en paramètre un message d'erreur à afficher.
     *
     * @param message le message d'erreur à afficher
     */
    public NoAccessWebServiceException(String message) {
        super("Accès au web service impossible.\nVérifiez votre connexion internet.\n" + message);
    }

}
