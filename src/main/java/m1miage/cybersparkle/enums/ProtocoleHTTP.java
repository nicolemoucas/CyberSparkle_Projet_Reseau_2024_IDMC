package m1miage.cybersparkle.enums;

/**
 * Énumération représentant les différents types de protocoles HTTP utilisés pour les requêtes pour
 * éviter les fautes de frappe.
 */
public enum ProtocoleHTTP {
    /**
     * Le type de requête GET, qui est utilisé pour récupérer des données depuis un serveur.
     */
    GET("GET"),

    /**
     * Le type de requête POST, qui est utilisé pour envoyer des données à un serveur pour créer ou
     * mettre à jour une ressource.
     */
    POST("POST");

    /**
     * La chaîne de caractères représentant le type de protocole HTTP.
     */
    public final String type;

    /**
     * Constructeur de l'énumération, qui associe une chaîne de caractères à un type de protocole HTTP.
     *
     * @param type la chaîne de caractères représentant le type de protocole HTTP
     */
    ProtocoleHTTP(String type) {
        this.type = type;
    }
}