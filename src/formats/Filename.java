package formats;

import java.util.Arrays;

/**
 *  Comporte le nom du fichier sous le format du systeme.
 */
public class Filename {
    private final char[] filename = new char[40];

    /**
     * Créer le format à l'aide d'une chaine de caractere.
     * @param filename Nom du fichier.
     * @throws WrongFormatException Si la chaine ne respecte pas le format.
     */
    public Filename(String filename) throws WrongFormatException {
        parse(filename.trim());
    }

    private void parse(String filename) throws WrongFormatException {
        if (!filename.matches("([a-zA-Z0-9\\.]){1,40}")) {
            throw new WrongFormatException("bad filename format");
        }

        int cpt = 0;
        char[] filenameArray = filename.toCharArray();

        while (cpt < filenameArray.length) {
            this.filename[cpt] = filenameArray[cpt];
            ++cpt;
        }

        while (cpt < this.filename.length) {
            this.filename[cpt++] = (char)32;
        }
    }

    @Override
    public String toString() {
        String res = String.valueOf(this.filename);

        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Filename filename1 = (Filename) o;

        if (!Arrays.equals(filename, filename1.filename)) return false;

        return true;
    }

    /**
     * Retourne la chaîne de caractère sans les espaces superflues (s'ils
     * existent), créés par la mise en format de la chaîne.
     * @return la chaine sans espaces supplémentaire à la fin.
     */
    public String trimmedFilename() {
        return String.valueOf(this.filename).trim();
    }
}
