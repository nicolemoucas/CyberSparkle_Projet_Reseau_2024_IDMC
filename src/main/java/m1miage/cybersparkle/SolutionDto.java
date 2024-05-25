package m1miage.cybersparkle;

/**
 * Classe de transfert des données (Data Transfer Object) pour une solution de hachage, elle contient
 * le nonce et le hachage correspondants.
 */
public class SolutionDto {
    String nonce;
    String hash;

    /**
     * Constructeur de la classe SolutionDto avec le nonce et le hachage spécifiés.
     *
     * @param nonce Nonce de la solution de hachage
     * @param hash Hachage de la solution de hachage
     */
    public SolutionDto(String nonce, String hash) {
        this.nonce = nonce;
        this.hash = hash;
    }

    /**
     * Renvoie le nonce de la solution de hachage.
     *
     * @return Nonce de la solution
     */
    public String getNonce() {
        return nonce;
    }

    /**
     * Renvoie le hachage de la solution de hachage.
     *
     * @return Hash de la solution
     */
    public String getHash() {
        return hash;
    }
}
