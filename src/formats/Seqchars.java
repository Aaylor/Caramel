package formats;

/**
 * Classe représentant le corps d'un message utilisateur dans le système.
 */
public class Seqchars {
    public final char[] message;

    /**
     * Construit à partir d'une chaîne de caractère.
     * Vérifie aussi qu'il possède le bon encodage.
     * @param msg message utilisateur
     * @throws WrongFormatException si l'encodage n'est pas le bon
     */
    public Seqchars(String msg) throws WrongFormatException {
        if (!CharsetChecker.isCorrectlyEncoded(msg)) {
            throw new WrongFormatException("Badly encoded message");
        }

        message = msg.toCharArray();
    }

    public String toString() {
        return String.valueOf(message);
    }
}
